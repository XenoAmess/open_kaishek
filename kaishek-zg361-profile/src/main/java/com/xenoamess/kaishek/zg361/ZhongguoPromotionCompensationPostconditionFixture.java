package com.xenoamess.kaishek.zg361;

import java.nio.charset.StandardCharsets;

/**
 * Parser/profile fixture for the #147 promotion to compensation receipt.
 *
 * <p>The cross-product business serial is deliberately separate from the T
 * and L/AE/AF case-kernel identities.  This fixture remains static-only and
 * does not execute or certify the native provider.</p>
 */
public final class ZhongguoPromotionCompensationPostconditionFixture {
    public static final String FIXTURE_ID =
            "zg361-promotion-compensation-postcondition-v1";
    public static final String ALIAS = "zg361-promotion-compensation";
    public static final String SOURCE_PATH =
            "common/scripted_effects/zg361_promotion_compensation_postcondition_fixture.txt";
    public static final String CAPABILITY_ID =
            ZhongguoBusinessPostconditionProfile.PROMOTION_COMPENSATION.id();

    private static final byte[] BOM = {(byte) 0xef, (byte) 0xbb, (byte) 0xbf};

    private ZhongguoPromotionCompensationPostconditionFixture() { }

    public static byte[] render() {
        String body = "# Static-only #147 promotion to compensation lineage fixture\n"
                + "zg361_promotion_compensation_postcondition_fixture = {\n"
                + "    set_variable = { name = zg361_pp_m147_receipt_serial value = var:zg361_pp_t_result_case }\n"
                + "    set_variable = { name = zg361_pp_m147_receipt_revision value = var:zg361_case_t_revision }\n"
                + "    if = {\n"
                + "        limit = {\n"
                + "            has_variable = zg361_pp_m147_receipt_serial\n"
                + "            has_variable = zg361_pp_m147_receipt_revision\n"
                + "            var:zg361_pp_m147_receipt_serial > 0\n"
                + "            var:zg361_pp_m147_receipt_revision = var:zg361_pp_m147_consumer_revision\n"
                + "            var:zg361_pp_m147_receipt_serial = var:zg361_comp_result_case\n"
                + "        }\n"
                + "        set_variable = { name = zg361_comp_promotion_receipt_choice_serial value = var:zg361_pp_m147_receipt_serial }\n"
                + "        set_variable = { name = zg361_comp_promotion_receipt_serial value = var:zg361_pp_m147_receipt_serial }\n"
                + "        set_variable = { name = zg361_comp_promotion_receipt_choice_revision value = var:zg361_pp_m147_receipt_revision }\n"
                + "        set_variable = { name = zg361_comp_promotion_receipt_revision value = var:zg361_comp_m082_visible_revision }\n"
                + "        set_variable = { name = zg361_comp_promotion_receipt_case value = var:zg361_comp_result_case }\n"
                + "    }\n"
                + "    if = {\n"
                + "        limit = {\n"
                + "            var:zg361_pp_m147_receipt_serial > 0\n"
                + "            var:zg361_comp_promotion_receipt_choice_serial > 0\n"
                + "            var:zg361_comp_promotion_receipt_serial > 0\n"
                + "            var:zg361_pp_m147_receipt_serial = var:zg361_comp_result_case\n"
                + "            var:zg361_comp_promotion_receipt_choice_serial = var:zg361_pp_m147_receipt_serial\n"
                + "            var:zg361_comp_promotion_receipt_serial = var:zg361_pp_m147_receipt_serial\n"
                + "            var:zg361_pp_m147_receipt_revision = var:zg361_pp_m147_consumer_revision\n"
                + "            var:zg361_comp_promotion_receipt_choice_revision = var:zg361_pp_m147_consumer_revision\n"
                + "            var:zg361_comp_promotion_receipt_revision > var:zg361_comp_promotion_receipt_choice_revision\n"
                + "        }\n"
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
