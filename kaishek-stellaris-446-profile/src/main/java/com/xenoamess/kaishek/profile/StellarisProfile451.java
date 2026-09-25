package com.xenoamess.kaishek.profile;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/** Static-only 4.5.1 slice for the byte-identical vanilla Cetana portrait file. */
public final class StellarisProfile451 implements KaishekProfile {
    public static final String ID = "stellaris-4.5.1";
    public static final String GAME_VERSION = "4.5.1";
    public static final String EXE_SHA256 =
            "6FE06709F265E726722DC23F617C5FC4E5557629E2D43FE312016ABA547C83E4";

    private static final Set<String> PORTRAIT_WORDS = Set.of(
            "portraits", "synth_queen", "cetana_mammalian", "cetana_reptilian",
            "cetana_aquatic", "cetana_lithoid", "cetana_plantoid",
            "cetana_molluscoid", "cetana_avian", "cetana_empty",
            "cetana_robot", "texturefile", "greeting_sound");
    private static final Map<String, OpcodeSpec> OPCODES =
            new StellarisProfile446().opcodes().entrySet().stream()
                    .filter(entry -> PORTRAIT_WORDS.contains(entry.getKey()))
                    .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));

    @Override public String id() { return ID; }
    @Override public String gameVersion() { return GAME_VERSION; }
    @Override public String executableSha256() { return EXE_SHA256; }
    @Override public OpcodeSpec opcode(String name) {
        return name == null ? null : OPCODES.get(name);
    }
    @Override public Set<String> allowedStructuralKeys() { return Set.of(); }
    @Override public Map<String, OpcodeSpec> opcodes() { return OPCODES; }

    @Override
    public ScriptDomain domainForPath(String sourcePath) {
        if (sourcePath == null) return ScriptDomain.UNKNOWN;
        String normalized = sourcePath.replace('\\', '/').toLowerCase(Locale.ROOT);
        String suffix = "gfx/portraits/portraits/21_portraits_cybernetics_synthqueen.txt";
        return normalized.equals(suffix) || normalized.endsWith("/" + suffix)
                ? ScriptDomain.PORTRAITS : ScriptDomain.UNKNOWN;
    }
}
