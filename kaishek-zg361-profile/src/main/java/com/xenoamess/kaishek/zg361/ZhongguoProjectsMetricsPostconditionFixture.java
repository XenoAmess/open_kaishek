package com.xenoamess.kaishek.zg361;

import java.nio.charset.StandardCharsets;

/**
 * Parser/profile fixture for the CP #026 to Phase-3 #229 receipt lineage.
 *
 * <p>The source is a compact, authored projection of the companion
 * generators.  It proves only that the exact variable operations can be
 * parsed and validated; it does not execute CK3 or certify the native query.</p>
 */
public final class ZhongguoProjectsMetricsPostconditionFixture {
    public static final String FIXTURE_ID =
            "zg361-projects-metrics-postcondition-v1";
    public static final String ALIAS = "zg361-projects-metrics";
    public static final String SOURCE_PATH =
            "common/scripted_effects/zg361_projects_metrics_postcondition_fixture.txt";
    public static final String CAPABILITY_ID =
            ZhongguoBusinessPostconditionProfile.PROJECTS_METRICS.id();

    private static final byte[] BOM = {(byte) 0xef, (byte) 0xbb, (byte) 0xbf};

    private ZhongguoProjectsMetricsPostconditionFixture() { }

    public static byte[] render() {
        String body = "# Static-only CP #026 to Phase-3 #229 lineage fixture\n"
                + "zg361_projects_metrics_postcondition_fixture = {\n"
                + "    if = {\n"
                + "        limit = { NOT = { has_variable = zg361_cp_contribution_receipt_cursor } }\n"
                + "        set_variable = { name = zg361_cp_contribution_receipt_cursor value = 0 }\n"
                + "    }\n"
                + "    change_variable = { name = zg361_cp_contribution_receipt_cursor add = 1 }\n"
                + "    set_variable = { name = zg361_cp_m26_contribution_receipt_id value = var:zg361_cp_contribution_receipt_cursor }\n"
                + "    set_variable = { name = zg361_cp_m26_contribution_receipt_revision value = var:zg361_case_e_revision }\n"
                + "    if = {\n"
                + "        limit = {\n"
                + "            has_variable = zg361_cp_m26_contribution_receipt_id\n"
                + "            has_variable = zg361_cp_m26_contribution_receipt_revision\n"
                + "            var:zg361_cp_m26_contribution_receipt_id > 0\n"
                + "            var:zg361_cp_m26_contribution_receipt_revision > 0\n"
                + "        }\n"
                + "        set_variable = { name = zg361_p3_project_source_contribution_receipt_id value = var:zg361_cp_m26_contribution_receipt_id }\n"
                + "        set_variable = { name = zg361_p3_project_source_contribution_receipt_revision value = var:zg361_cp_m26_contribution_receipt_revision }\n"
                + "        set_variable = { name = zg361_p3_m229_source_contribution_receipt_id value = var:zg361_p3_project_source_contribution_receipt_id }\n"
                + "        set_variable = { name = zg361_p3_m229_source_contribution_receipt_revision value = var:zg361_p3_project_source_contribution_receipt_revision }\n"
                + "        set_variable = { name = zg361_p3_m229_metrics_revision value = var:zg361_case_aa_revision }\n"
                + "        set_variable = { name = zg361_p3_m229_dictionary_key_code value = var:zg361_p3_metric_dictionary_owner }\n"
                + "    }\n"
                + "}\n";
        return withBom(body);
    }

    private static byte[] withBom(String body) {
        byte[] encoded = body.getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[BOM.length + encoded.length];
        System.arraycopy(BOM, 0, result, 0, BOM.length);
        System.arraycopy(encoded, 0, result, BOM.length, encoded.length);
        return result;
    }
}
