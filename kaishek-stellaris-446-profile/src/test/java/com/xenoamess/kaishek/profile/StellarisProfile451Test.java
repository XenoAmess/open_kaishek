package com.xenoamess.kaishek.profile;

import com.xenoamess.kaishek.syntax.Parser;
import com.xenoamess.kaishek.validator.Diagnostic;
import com.xenoamess.kaishek.validator.Validator;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StellarisProfile451Test {
    private final StellarisProfile451 profile = new StellarisProfile451();
    private static final String PATH =
            "gfx/portraits/portraits/21_portraits_cybernetics_synthqueen.txt";

    @Test
    void onlyByteIdenticalCetanaPortraitShapeIsInScope() {
        assertEquals("stellaris-4.5.1", profile.id());
        assertEquals("4.5.1", profile.gameVersion());
        assertEquals("6FE06709F265E726722DC23F617C5FC4E5557629E2D43FE312016ABA547C83E4",
                profile.executableSha256());
        assertEquals(ScriptDomain.PORTRAITS, profile.domainForPath("mod\\" + PATH.replace('/', '\\')));
        assertEquals(ScriptDomain.UNKNOWN, profile.domainForPath("common/decisions/workplace.txt"));
        assertNull(profile.opcode("add_deposit"));
        assertEquals(13, profile.opcodes().size());
    }

    @Test
    void productionShapeValidatesAndMutationsFailClosed() {
        StringBuilder source = new StringBuilder("portraits = {\n");
        for (String key : List.of("synth_queen", "cetana_mammalian", "cetana_reptilian",
                "cetana_aquatic", "cetana_lithoid", "cetana_plantoid",
                "cetana_molluscoid", "cetana_avian", "cetana_empty", "cetana_robot")) {
            source.append(key).append(" = { texturefile = ")
                    .append("\"gfx/models/portraits/xenoamess_cetana_laoda_portrait.dds\" ")
                    .append("greeting_sound = \"tox_portrait_01\" }\n");
        }
        String valid = source.append("}\n").toString();
        assertTrue(errors(valid, PATH).isEmpty());

        String invalid = valid.replace("cetana_mammalian", "cetana_mamalian")
                .replaceFirst("texturefile", "texturfile")
                .replace("gfx/models/portraits/xenoamess_cetana_laoda_portrait.dds\" greeting_sound",
                        "gfx/event_pictures/not_a_portrait.dds\" greeting_sound");
        List<Diagnostic> rejected = errors(invalid, PATH);
        assertTrue(rejected.stream().anyMatch(d -> d.code().equals("UNKNOWN_OPCODE")));
        assertTrue(rejected.stream().anyMatch(d -> d.code().equals("STELLARIS_PORTRAIT_REQUIRED")));
        assertTrue(rejected.stream().anyMatch(d -> d.code().equals("STELLARIS_PORTRAIT_FIELD_REQUIRED")));
        assertTrue(rejected.stream().anyMatch(d -> d.code().equals("INVALID_SCALAR_VALUE")));
        assertTrue(errors(valid, "common/decisions/21_portraits_cybernetics_synthqueen.txt")
                .stream().anyMatch(d -> d.code().equals("UNKNOWN_DIRECTORY")));
    }

    private List<Diagnostic> errors(String source, String path) {
        return Validator.validate(Parser.parse(source.getBytes(StandardCharsets.UTF_8)), path, profile)
                .stream().filter(d -> d.severity() == Diagnostic.Severity.ERROR).toList();
    }
}
