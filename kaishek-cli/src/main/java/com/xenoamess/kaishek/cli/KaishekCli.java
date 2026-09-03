package com.xenoamess.kaishek.cli;

import com.xenoamess.kaishek.syntax.CstNode;
import com.xenoamess.kaishek.syntax.Parser;
import com.xenoamess.kaishek.syntax.ParseResult;
import com.xenoamess.kaishek.syntax.SyntaxKind;
import com.xenoamess.kaishek.validator.Validator;
import com.xenoamess.kaishek.profile.Ck3Profile11906;
import com.xenoamess.kaishek.zg361.OfflinePreflight;
import com.xenoamess.kaishek.zg361.Synthetic361Pipeline;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.*;
import java.util.*;
import java.util.stream.Stream;

/** Dependency-free batch entry point. It intentionally never loads Quarkus. */
public final class KaishekCli {
  private static final String VERSION = "0.1.0-cli";
  private static final String EXE_SHA = "2D00FF3101EF70B566F2FCBAE292F09263199C80E9DC8F139B82D7D96F83DB86";
  public static void main(String[] args) { System.exit(run(args, System.out, System.err)); }

  static int run(String[] args, PrintStream out, PrintStream err) {
    if (args.length == 0 || "--help".equals(args[0]) || "-h".equals(args[0])) { usage(out); return 0; }
    if ("--version".equals(args[0]) || "-V".equals(args[0])) { out.println(VERSION); return 0; }
    String command = args[0].toLowerCase(Locale.ROOT);
    try {
      return switch (command) {
        case "parse" -> parse(args, out);
        case "validate" -> validate(args, out);
        case "hash" -> hash(args, out);
        case "corpus" -> corpus(args, out);
        case "batch", "replay" -> batch(args, out);
        case "synthetic-361", "runtime-fixture" -> synthetic361(args, out);
        case "preflight" -> preflight(args, out);
        case "profile", "profiles" -> profile(args, out);
        default -> unsupported("command:" + command, out);
      };
    } catch (IllegalArgumentException e) { out.println(json("status", "ERROR", "error", e.getMessage())); return 2; }
      catch (IOException e) { out.println(json("status", "ERROR", "error", e.toString())); return 3; }
  }

  private static int parse(String[] a, PrintStream out) throws IOException {
    byte[] source = readInputBytes(a, 1); ParseResult p = Parser.parse(source);
    StringBuilder b = new StringBuilder("{\"status\":\"PARSED\",\"bytes\":").append(source.length)
      .append(",\"tokens\":").append(countNodes(p.document())).append(",\"blocks\":").append(countKind(p.document(), SyntaxKind.BLOCK))
      .append(",\"diagnostics\":[");
    for (int i=0;i<p.diagnostics().size();i++) {
      var x=p.diagnostics().get(i);
      if(i>0)b.append(',');
      // Offsets are byte offsets and therefore part of the numeric JSON
      // contract, not strings.  Keeping them numeric also lets callers sort
      // and compare diagnostics without a second coercion step.
      b.append("{\"code\":").append(q(x.code()))
          .append(",\"message\":").append(q(x.message()))
          .append(",\"offset\":").append(x.span().start()).append('}');
    }
    out.println(b.append("],\"roundTrip\":").append(!p.hasErrors()).append('}'));
    return p.hasErrors() ? 1 : 0;
  }
  private static int validate(String[] a, PrintStream out) throws IOException {
    Path inputPath = pathOption(a, 1);
    byte[] source = readInputBytes(a, 1);
    String profile = option(a, "--profile", "ck3-1.19.0.6");
    ParseResult p = Parser.parse(source);
    List<String> d = new ArrayList<>(); p.diagnostics().forEach(x -> d.add(x.code()));
    // Only syntax checks are currently certified; schema/profile semantics remain explicit.
    boolean known = profile.equals("ck3-1.19.0.6") || profile.equals("ck3-1.19.0.6-zg361");
    if (!known) { out.println(json("status","UNSUPPORTED","reason","profile-not-registered","profile",profile)); return 4; }
    List<String> semantic = new ArrayList<>();
    boolean semanticSupported = profile.equals("ck3-1.19.0.6") && inputPath != null;
    if (semanticSupported && !p.hasErrors()) {
      // The ParseResult overload intentionally includes syntax diagnostics for
      // library callers.  The CLI reports syntax and semantic counts
      // separately, so validate the already parsed document here.
      Validator.validate(p.document(), inputPath.toString(), new Ck3Profile11906())
          .forEach(x -> semantic.add(x.code()));
    }
    boolean invalid = p.hasErrors() || !semantic.isEmpty();
    String semStatus = semanticSupported ? "VALIDATED" : "UNSUPPORTED";
    String status = invalid ? "INVALID" : (semanticSupported ? "VALIDATED" : "UNSUPPORTED");
    out.println("{\"status\":\""+status+"\",\"profile\":"+q(profile)+",\"syntaxDiagnostics\":"+d.size()+",\"semanticDiagnostics\":"+semantic.size()+",\"semantic\":\""+semStatus+"\"}");
    // A supported semantic check must fail the process when it finds a
    // semantic diagnostic.  Previously only syntax diagnostics affected the
    // exit code, making an INVALID response appear successful to CI.
    // Syntax errors are invalid input regardless of whether a file path was
    // supplied.  Only a syntactically valid input whose semantic layer is not
    // available uses the explicit UNSUPPORTED exit code.
    return invalid ? 1 : (!semanticSupported ? 4 : 0);
  }
  private static int hash(String[] a, PrintStream out) throws IOException {
    Path path = pathOption(a, 1); if (path != null && Files.isDirectory(path)) return corpus(a,out);
    byte[] bytes = path == null ? readInputBytes(a,1) : Files.readAllBytes(path);
    out.println("{\"status\":\"OK\",\"algorithm\":\"SHA-256\",\"sha256\":"+q(sha(bytes))+",\"bytes\":"+bytes.length+"}"); return 0;
  }
  private static int corpus(String[] a, PrintStream out) throws IOException {
    boolean requireCorpus = hasFlag(a, "--require-corpus");
    // Preserve the historical positional-path behaviour for the optional
    // form.  Required mode resolves the token even before it exists so an
    // acceptance caller gets an explicit, machine-readable SKIP.
    Path root = requireCorpus ? corpusPathOption(a, 1) : pathOption(a,1);
    if (root==null) throw new IllegalArgumentException("corpus requires a directory");
    if (!Files.exists(root)) {
      if (requireCorpus) return unavailableCorpus(root, "corpus-root-absent", out);
      throw new IllegalArgumentException("not a directory: "+root);
    }
    if (!Files.isDirectory(root)) throw new IllegalArgumentException("not a directory: "+root);
     List<Path> files = new ArrayList<>();
     try(Stream<Path> s=Files.walk(root)){
       s.filter(Files::isRegularFile).filter(KaishekCli::isScript)
        .sorted(Comparator.comparing(p -> root.relativize(p).toString().replace('\\','/')))
        .forEach(files::add);
     }
     if (files.isEmpty() && requireCorpus)
       return unavailableCorpus(root, "corpus-empty", out);
     MessageDigest md = digest(); long bytes=0; int parsed=0, errors=0; List<String> errorFiles = new ArrayList<>();
     for(Path f:files){
       byte[] x=Files.readAllBytes(f); bytes+=x.length;
       String relative = root.relativize(f).toString().replace('\\','/');
       md.update(relative.getBytes(StandardCharsets.UTF_8)); md.update((byte)0); md.update(x);
       ParseResult p=Parser.parse(x); parsed++;
       if(p.hasErrors()){ errors++; errorFiles.add(relative); }
     }
     StringBuilder details = new StringBuilder("[");
     for (int i=0; i<errorFiles.size(); i++) { if (i > 0) details.append(','); details.append(q(errorFiles.get(i))); }
     out.println("{\"status\":\"OK\",\"root\":"+q(root.toString())+",\"files\":"+files.size()+",\"parsed\":"+parsed+",\"errors\":"+errors+",\"errorFiles\":"+details.append(']').toString()+",\"bytes\":"+bytes+",\"corpusSha256\":"+q(hex(md.digest()))+"}"); return errors==0?0:1;
  }

  /**
   * Execute a deterministic JSONL manifest through the same public CLI
   * commands.  This is intentionally a thin batch/replay layer: parsing,
   * validation and runtime semantics remain owned by their existing modules.
   * Each input line is an object with {@code id}, {@code command}, and either
   * an {@code args} string array or one of {@code text}/{@code file} plus an
   * optional {@code profile}.  One envelope is emitted per line so a failed
   * case never hides later results.
   */
  private static int batch(String[] a, PrintStream out) throws IOException {
    Path manifest = null;
    boolean stopOnError = false;
    for (int i = 1; i < a.length; i++) {
      String token = a[i];
      if ("--continue-on-error".equals(token)) { stopOnError = false; continue; }
      if ("--stop-on-error".equals(token)) { stopOnError = true; continue; }
      if ("--file".equals(token)) {
        if (++i >= a.length || a[i].isBlank()) throw new IllegalArgumentException("--file requires a value");
        manifest = Paths.get(a[i]); continue;
      }
      if (token.startsWith("--file=")) {
        String value = token.substring("--file=".length());
        if (value.isBlank()) throw new IllegalArgumentException("--file requires a value");
        manifest = Paths.get(value); continue;
      }
      if (token.startsWith("-")) throw new IllegalArgumentException("unknown option: " + token);
      if (manifest != null) throw new IllegalArgumentException("unexpected argument: " + token);
      manifest = Paths.get(token);
    }
    List<String> lines = manifest == null
        ? new String(System.in.readAllBytes(), StandardCharsets.UTF_8).lines().toList()
        : Files.readAllLines(manifest, StandardCharsets.UTF_8);
    int failures = 0;
    int malformed = 0;
    for (int index = 0; index < lines.size(); index++) {
      String line = lines.get(index);
      if (index == 0 && !line.isEmpty() && line.charAt(0) == '\ufeff') line = line.substring(1);
      line = line.trim();
      if (line.isEmpty() || line.startsWith("#")) continue;
      BatchRequest request;
      try {
        request = BatchRequest.parse(line);
      } catch (IllegalArgumentException e) {
        malformed++;
        out.println("{\"line\":" + (index + 1) + ",\"status\":\"ERROR\",\"error\":" + q(e.getMessage()) + "}");
        if (stopOnError) break;
        continue;
      }
      ByteArrayOutputStream captured = new ByteArrayOutputStream();
      int exit = run(request.commandArgs(), new PrintStream(captured), System.err);
      String result = captured.toString(StandardCharsets.UTF_8).trim();
      out.println("{\"id\":" + q(request.id()) + ",\"line\":" + (index + 1)
          + ",\"exitCode\":" + exit + ",\"result\":" + resultJson(result) + "}");
      if (exit != 0) {
        failures++;
        if (stopOnError) break;
      }
    }
    return malformed > 0 ? 2 : (failures > 0 ? 1 : 0);
  }

  /** Preserve the command's JSON object as a nested value in the envelope. */
  private static String resultJson(String result) {
    if (result != null && result.startsWith("{") && result.endsWith("}")) return result;
    return q(result == null ? "" : result);
  }

  /** Run the checked-in 014 parser → IR → finite-runtime fixture offline. */
  private static int synthetic361(String[] a, PrintStream out) throws IOException {
    Synthetic361Pipeline.Result result;
    if (a.length == 1) {
      result = Synthetic361Pipeline.runGenerated();
    } else {
      Path source = pathOption(a, 1);
      if (source == null || !Files.isRegularFile(source))
        throw new IllegalArgumentException("synthetic-361 requires --file PATH");
      result = Synthetic361Pipeline.run(Files.readAllBytes(source));
    }
    String status = result.execution().status().name();
    StringBuilder values = new StringBuilder("[");
    if (result.execution().value() != null) {
      List<?> list = result.execution().value();
      for (int i = 0; i < list.size(); i++) { if (i > 0) values.append(','); values.append(q(String.valueOf(list.get(i)))); }
    }
    values.append(']');
    out.println("{\"status\":" + q(status) + ",\"fixture\":\"zg361-synthetic-014\",\"synthetic\":true"
        + ",\"parsedDiagnostics\":" + result.parsed().diagnostics().size()
        + ",\"validationDiagnostics\":" + result.validation().size()
        + ",\"instructions\":" + result.program().instructions().size()
        + ",\"execution\":" + q(status)
        + ",\"values\":" + values
        + ",\"traceEntries\":" + result.execution().trace().entries().size()
        + (result.execution().reason().isBlank() ? "" : ",\"reason\":" + q(result.execution().reason())) + "}");
    return result.execution().isSuccess() ? 0 : 1;
  }

  /**
   * Run the deterministic parser/validator/fixture preflight.  This command
   * is intentionally side-effect free: it never starts CK3, loads a bridge,
   * opens MCP, or writes a save.  The single JSON object is suitable for a
   * parent CK3 acceptance runner to archive and gate before crossing its
   * launch boundary.
   */
  private static int preflight(String[] a, PrintStream out) throws IOException {
    Path root = null;
    String profile = OfflinePreflight.DEFAULT_PROFILE;
    String fixture = OfflinePreflight.DEFAULT_FIXTURE;
    String positionalRoot = null;
    for (int i = 1; i < a.length; i++) {
      String token = a[i];
      if (token == null) throw new IllegalArgumentException("null argument at index " + i);
      if ("--root".equals(token) || "--profile".equals(token) || "--fixture".equals(token)) {
        if (++i >= a.length || a[i] == null || a[i].isBlank() || a[i].startsWith("--"))
          throw new IllegalArgumentException(token + " requires a value");
        if ("--root".equals(token)) root = Paths.get(a[i]);
        else if ("--profile".equals(token)) profile = a[i];
        else fixture = a[i];
        continue;
      }
      if (token.startsWith("--root=")) {
        String value = token.substring("--root=".length());
        if (value.isBlank()) throw new IllegalArgumentException("--root requires a value");
        root = Paths.get(value); continue;
      }
      if (token.startsWith("--profile=")) {
        String value = token.substring("--profile=".length());
        if (value.isBlank()) throw new IllegalArgumentException("--profile requires a value");
        profile = value; continue;
      }
      if (token.startsWith("--fixture=")) {
        String value = token.substring("--fixture=".length());
        if (value.isBlank()) throw new IllegalArgumentException("--fixture requires a value");
        fixture = value; continue;
      }
      if (token.startsWith("-")) throw new IllegalArgumentException("unknown option: " + token);
      if (positionalRoot != null) throw new IllegalArgumentException("unexpected argument: " + token);
      positionalRoot = token;
    }
    if (root == null && positionalRoot != null) root = Paths.get(positionalRoot);
    OfflinePreflight.Report report = OfflinePreflight.run(
        new OfflinePreflight.Request(root, profile, fixture));
    out.println(report.toJson());
    return switch (report.status()) {
      case "GREEN" -> 0;
      case "UNSUPPORTED" -> 4;
      default -> 1;
    };
  }

  private record BatchRequest(String id, String[] commandArgs) {
    static BatchRequest parse(String line) {
      JsonObject object = JsonObject.parse(line);
      String id = object.string("id", null);
      if (id == null || id.isBlank()) throw new IllegalArgumentException("id is required");
      String command = object.string("command", null);
      if (command == null || command.isBlank()) throw new IllegalArgumentException("command is required");
      if (command.equalsIgnoreCase("batch") || command.equalsIgnoreCase("replay"))
        throw new IllegalArgumentException("nested batch/replay is not supported");
      List<String> args = object.array("args");
      boolean hasInput = object.has("text") || object.has("file") || object.has("profile");
      if (!args.isEmpty() && hasInput) throw new IllegalArgumentException("args cannot be combined with text/file/profile");
      if (args.isEmpty()) {
        String file = object.string("file", null);
        String text = object.string("text", null);
        if (file != null) { args = new ArrayList<>(List.of("--file", file)); }
        else if (text != null) { args = new ArrayList<>(List.of(text)); }
        else args = new ArrayList<>();
        String profile = object.string("profile", null);
        if (profile != null) { args.add("--profile"); args.add(profile); }
      }
      List<String> full = new ArrayList<>(); full.add(command); full.addAll(args);
      return new BatchRequest(id, full.toArray(String[]::new));
    }
  }

  /** Minimal dependency-free JSON object reader for the flat JSONL contract. */
  private static final class JsonObject {
    private final Map<String, Object> values;
    private JsonObject(Map<String, Object> values) { this.values = values; }
    boolean has(String key) { return values.containsKey(key); }
    String string(String key, String fallback) {
      Object value = values.get(key);
      if (value == null) return fallback;
      if (!(value instanceof String s)) throw new IllegalArgumentException(key + " must be a string");
      return s;
    }
    @SuppressWarnings("unchecked") List<String> array(String key) {
      Object value = values.get(key);
      if (value == null) return List.of();
      if (!(value instanceof List<?> list) || list.stream().anyMatch(x -> !(x instanceof String)))
        throw new IllegalArgumentException(key + " must be a string array");
      return List.copyOf((List<String>) list);
    }
    static JsonObject parse(String source) {
      if (source == null) throw new IllegalArgumentException("null JSON line");
      Cursor c = new Cursor(source); c.ws(); c.expect('{');
      Map<String, Object> map = new LinkedHashMap<>(); c.ws();
      if (c.peek('}')) { c.next(); c.ws(); c.end(); return new JsonObject(map); }
      while (true) {
        c.ws(); String key = c.string(); c.ws(); c.expect(':'); c.ws();
        if (map.put(key, c.value()) != null) throw new IllegalArgumentException("duplicate key: " + key);
        c.ws(); if (c.peek('}')) { c.next(); break; } c.expect(',');
      }
      c.ws(); c.end(); return new JsonObject(map);
    }
    private static final class Cursor {
      final String s; int i; Cursor(String s) { this.s = s; }
      void ws() { while (i < s.length() && Character.isWhitespace(s.charAt(i))) i++; }
      boolean peek(char x) { return i < s.length() && s.charAt(i) == x; }
      char next() { if (i >= s.length()) throw new IllegalArgumentException("unexpected end of JSON"); return s.charAt(i++); }
      void expect(char x) { if (next() != x) throw new IllegalArgumentException("expected '" + x + "'"); }
      void end() { if (i != s.length()) throw new IllegalArgumentException("trailing JSON data"); }
      String string() {
        expect('"'); StringBuilder b = new StringBuilder();
        while (i < s.length()) { char ch = next(); if (ch == '"') return b.toString();
          if (ch != '\\') { if (ch < 0x20) throw new IllegalArgumentException("control character in string"); b.append(ch); continue; }
          char esc = next(); switch (esc) {
            case '"' -> b.append('"'); case '\\' -> b.append('\\'); case '/' -> b.append('/');
            case 'b' -> b.append('\b'); case 'f' -> b.append('\f'); case 'n' -> b.append('\n');
            case 'r' -> b.append('\r'); case 't' -> b.append('\t');
            case 'u' -> { if (i + 4 > s.length()) throw new IllegalArgumentException("invalid unicode escape");
              String hex = s.substring(i, i + 4); try { b.append((char) Integer.parseInt(hex, 16)); }
              catch (NumberFormatException e) { throw new IllegalArgumentException("invalid unicode escape"); } i += 4; }
            default -> throw new IllegalArgumentException("invalid escape: " + esc);
          }
        }
        throw new IllegalArgumentException("unterminated string");
      }
      Object value() {
        if (peek('"')) return string();
        if (peek('[')) { next(); List<String> list = new ArrayList<>(); ws(); if (peek(']')) { next(); return list; }
          while (true) { ws(); list.add(string()); ws(); if (peek(']')) { next(); return list; } expect(','); }
        }
        throw new IllegalArgumentException("value must be string or string array");
      }
    }
  }
  /** Resolve a corpus path without requiring it to exist yet. */
  private static Path corpusPathOption(String[] a, int start) {
    String p=option(a,"--file",null);
    if(p!=null)return Paths.get(p);
    String candidate=positional(a,start);
    return candidate==null?null:Paths.get(candidate);
  }

  /** Emit an explicit non-passing result for a required unavailable corpus. */
  private static int unavailableCorpus(Path root, String reason, PrintStream out) {
    out.println("{\"status\":\"SKIP\",\"reason\":"+q(reason)+",\"required\":true,\"root\":"
      +q(root.toString())+",\"files\":0,\"parsed\":0,\"errors\":0,\"errorFiles\":[],\"bytes\":0,\"corpusSha256\":null}");
    // Required-mode SKIP is deliberately non-zero so CI cannot treat an
    // unavailable external corpus as passing evidence.
    return 1;
  }
  private static int profile(String[] a, PrintStream out) {
    String positionalId = positional(a, 1);
    String id=option(a,"--id", positionalId == null ? "" : positionalId);
    if (id.isEmpty()) { out.println("{\"status\":\"OK\",\"profiles\":[{\"id\":\"ck3-1.19.0.6\",\"game\":\"Crusader Kings III\",\"build\":\"1.19.0.6\",\"semantic\":\"static\"},{\"id\":\"ck3-1.19.0.6-zg361\",\"game\":\"Crusader Kings III + mod_zhongguo_style\",\"build\":\"1.19.0.6\",\"semantic\":\"schema-only\"}]}"); return 0; }
    if (!id.equals("ck3-1.19.0.6")&&!id.equals("ck3-1.19.0.6-zg361")) return unsupported("profile:"+id,out);
    String semantic = id.endsWith("-zg361") ? "schema-only" : "static";
    out.println("{\"status\":\"OK\",\"id\":"+q(id)+",\"game\":\"Crusader Kings III\",\"build\":\"1.19.0.6\",\"exeSha256\":"+q(EXE_SHA)+",\"semantic\":"+q(semantic)+",\"runtime\":\"UNSUPPORTED\"}"); return 0;
  }
  private static int unsupported(String reason, PrintStream out){out.println(json("status","UNSUPPORTED","reason",reason)); return 4;}
  private static String readInput(String[] a,int start)throws IOException{return new String(readInputBytes(a,start),StandardCharsets.UTF_8);}

  /**
   * Read an input argument in any option order.  A positional argument that
   * names an existing file is treated as a path; otherwise it is retained as
   * inline source text for the parse/hash convenience form.
   */
  private static byte[] readInputBytes(String[] a,int start)throws IOException{
    Path p=pathOption(a,start);
    if(p!=null)return Files.readAllBytes(p);
    String inline = positional(a,start);
    if(inline!=null)return inline.getBytes(StandardCharsets.UTF_8);
    return System.in.readAllBytes();
  }

  /** Resolve an explicit --file or an existing positional path. */
  private static Path pathOption(String[] a,int start){
    String p=option(a,"--file",null);
    if(p!=null)return Paths.get(p);
    String candidate=positional(a,start);
    if(candidate!=null){
      try { Path x=Paths.get(candidate); if(Files.exists(x))return x; }
      catch (InvalidPathException ignored) { /* inline source text */ }
    }
    return null;
  }

  /**
   * Return the first positional token after the command, skipping value
   * options.  Strict option handling prevents a misspelled/missing option
   * from silently switching a file operation to stdin.
   */
  private static String positional(String[] a,int start){
    boolean endOptions=false;
    String first=null;
    for(int i=start;i<a.length;i++){
      String token=a[i];
      if(token==null)throw new IllegalArgumentException("null argument at index "+i);
      if(endOptions || !token.startsWith("-")){
        if(first!=null)throw new IllegalArgumentException("unexpected argument: "+token);
        first=token;
        continue;
      }
      if("--".equals(token)){endOptions=true;continue;}
      String valueOption=valueOption(token);
      if(valueOption!=null){
        if(token.equals(valueOption)){
          if(++i>=a.length)throw new IllegalArgumentException(valueOption+" requires a value");
          String value=a[i];
          if(value==null||value.isBlank()||value.startsWith("--"))
            throw new IllegalArgumentException(valueOption+" requires a value");
        } else if(token.substring(valueOption.length()+1).isBlank()) {
          throw new IllegalArgumentException(valueOption+" requires a value");
        }
        continue;
      }
      if(isFlagOption(a, token)) continue;
      throw new IllegalArgumentException("unknown option: "+token);
    }
    return first;
  }

  /** Return an option value; supports both --key value and --key=value. */
  private static String option(String[] a,String key,String fallback){
    String found=null;
    String equalsPrefix=key+"=";
    for(int i=1;i<a.length;i++){
      String token=a[i];
      if(key.equals(token)){
        if(++i>=a.length)throw new IllegalArgumentException(key+" requires a value");
        String value=a[i];
        if(value==null||value.isBlank()||value.startsWith("--"))
          throw new IllegalArgumentException(key+" requires a value");
        found=value;
      }else if(token!=null&&token.startsWith(equalsPrefix)){
        String value=token.substring(equalsPrefix.length());
        if(value.isBlank())throw new IllegalArgumentException(key+" requires a value");
        found=value;
      }
    }
    return found==null?fallback:found;
  }

  /** Identify a supported value option while scanning positional arguments. */
  private static String valueOption(String token){
    for(String key:List.of("--file","--profile","--id"))
      if(token.equals(key)||token.startsWith(key+"="))return key;
    return null;
  }
  /** Identify a supported boolean option for the corpus command only. */
  private static boolean isFlagOption(String[] a, String token){
    return a.length > 0 && "corpus".equalsIgnoreCase(a[0]) && "--require-corpus".equals(token);
  }
  /** Return whether a boolean option was supplied in the argument vector. */
  private static boolean hasFlag(String[] a, String key){
    for (int i=1; i<a.length; i++) {
      if ("--".equals(a[i])) break;
      if (key.equals(a[i])) return true;
    }
    return false;
  }
  // The Phase 0 corpus contract is deliberately limited to Paradox script
  // sources.  Localization YAML has a different grammar and is reported by
  // a future localization adapter rather than being misclassified here.
  private static boolean isScript(Path p){String n=p.getFileName().toString().toLowerCase(Locale.ROOT);return n.endsWith(".txt")||n.endsWith(".gui");}
  private static MessageDigest digest(){try{return MessageDigest.getInstance("SHA-256");}catch(Exception e){throw new AssertionError(e);}}
  private static String sha(byte[] b){return hex(digest().digest(b));} private static String hex(byte[] b){StringBuilder s=new StringBuilder();for(byte x:b)s.append(String.format(Locale.ROOT,"%02x",x));return s.toString();}
  private static String q(String s){return "\""+s.replace("\\","\\\\").replace("\"","\\\"").replace("\r","\\r").replace("\n","\\n")+"\"";}
  private static String json(String... kv){StringBuilder b=new StringBuilder("{");for(int i=0;i<kv.length;i+=2){if(i>0)b.append(',');b.append(q(kv[i])).append(':').append(q(kv[i+1]));}return b.append('}').toString();}
  private static void usage(PrintStream o){o.println("kaishek-cli "+VERSION+" (pure Java; Quarkus is not started)\nUsage: parse|validate|hash|corpus|profile [--file PATH|PATH] [corpus: --require-corpus]\n       synthetic-361|runtime-fixture\n       preflight [--root PATH] [--profile ID] [--fixture ID]\n       batch|replay [--file MANIFEST.jsonl] [--stop-on-error]\nBatch lines: {\"id\":\"case\",\"command\":\"parse\",\"text\":\"x = 1\\n\"}\nPreflight is offline-only and never starts CK3; it returns one JSON report.\nFixtures include synthetic-361-014, appeal-014, ck3-calculated-value-014 (schema-only RED), ck3-g2-activity-type-schema-red-11906 (schema-only RED), ck3-war-days-trigger-11906, ck3-has-innovation-trigger-11906, ck3-has-cultural-tradition-trigger-11906, ck3-has-cultural-pillar-trigger-11906, ck3-has-cultural-parameter-trigger-11906, ck3-is-acclaimed-trigger-11906, ck3-can-be-acclaimed-trigger-11906, zg361-projects-metrics-postcondition-v1, and zg361-promotion-compensation-postcondition-v1.\nUnknown semantics return status UNSUPPORTED.");}

  private static int countNodes(CstNode n){int c=1; for(CstNode x:n.children()) c+=countNodes(x); return c;}
  private static int countKind(CstNode n,SyntaxKind k){int c=n.kind()==k?1:0; for(CstNode x:n.children()) c+=countKind(x,k); return c;}
}
