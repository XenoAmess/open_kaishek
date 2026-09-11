package com.xenoamess.kaishek.profile;

import java.util.List;

/**
 * Static compatibility projection for the companion's portable vanilla-event assets.
 *
 * <p>The companion remains the canonical implementation and data owner. This class pins
 * only four offline, read-only MCP v1 envelopes and their current data revision
 * at {@link #ROOT_PROVIDER_COMMIT}; it neither copies evidence into open_kaishek
 * nor contacts CK3.</p>
 */
public final class VanillaEventPortableAssetCapabilityProfile {
    public static final String ROOT_PROVIDER_COMMIT =
            "ee185c4ecaa84240a0a0f6090e8fa4d825cc1ae4";
    public static final String ROOT_MCP_SERVER_SHA256 =
            "A67D98C4D3B452D09AF0B9A830E5E95BB58C31AF52B31AAA586423B18E4ED320";
    public static final String ROOT_DISCOVERY_SHA256 =
            "962966AD9AD18666F138E0B491F1FA68172CC149FC81261E0302534D8DF4A0D2";
    public static final String ROOT_PORTABLE_EVIDENCE_SHA256 =
            "FECDEC3F78349A2D25B24DC29AAF342D48893764375547171DE3D9F06545B29E";
    public static final String ROOT_SOURCE_INDEX_SHA256 =
            "959DF0ED0D704A10AC4D0CE2CC57ABD0C6FACC52EFD204EFF8F84210E552F5DF";
    public static final String ROOT_PORTABLE_EVIDENCE_MANIFEST_SHA256 =
            "BC843955BF8EAC8E0133386EFDD0D9DEF4DE794C50559E4F8C93EA6DFF9A5545";
    public static final String ROOT_SOURCE_INDEX_DATASET_SHA256 =
            "265EBCE989627D68C69DDEF178A7BC0EBE1DE846721E42584D2B8271D14E9CFD";

    public static final String LIST_KNOWLEDGE_TOOL_ID =
            "ck3_list_vanilla_event_knowledge_v1";
    public static final String LIST_EVIDENCE_TOOL_ID =
            "ck3_list_vanilla_event_evidence_v1";
    public static final String READ_EVIDENCE_TOOL_ID =
            "ck3_read_vanilla_event_evidence_v1";
    public static final String QUERY_SOURCE_PROVENANCE_TOOL_ID =
            "ck3_query_vanilla_event_source_provenance_v1";

    public static final String KNOWLEDGE_INDEX_SCHEMA =
            "xar.ck3.vanilla-event-knowledge-index";
    public static final String EVIDENCE_LIST_SCHEMA =
            "xar.ck3.vanilla-event-evidence-list";
    public static final String EVIDENCE_READ_SCHEMA =
            "xar.ck3.vanilla-event-evidence-read";
    public static final String SOURCE_PROVENANCE_SCHEMA =
            "xar.ck3.vanilla-event-source-provenance";
    public static final int SCHEMA_VERSION = 1;

    public static final String KNOWLEDGE_INDEX_SCHEMA_SHA256 =
            "E4FBA84B215803B56AB4D1E08AEF77F4DEBF4EB7D695914217D3510A821EF208";
    public static final String EVIDENCE_LIST_SCHEMA_SHA256 =
            "9B7B98DE0B60B7D9E31BB672B181378206554347668BF8B63B88CECADABFDE40";
    public static final String EVIDENCE_READ_SCHEMA_SHA256 =
            "EDECFB47292BD2630863A6197138CCB25CBF4975AF813D9FED1CEE51FABA3DC5";
    public static final String SOURCE_PROVENANCE_SCHEMA_SHA256 =
            "9E436E1A00F6B0C05EC54FEB7D28AE1A12780BAC98F3D080E11DE432AFA1D771";

    public static final List<String> LIST_KNOWLEDGE_REQUEST_FIELDS = List.of(
            "ck3_build", "query", "namespace", "evidence_class",
            "has_observations", "after_key", "limit");
    public static final List<String> LIST_EVIDENCE_REQUEST_FIELDS = List.of(
            "event_definition_key", "kind", "after_evidence_id", "limit", "ck3_build");
    public static final List<String> READ_EVIDENCE_REQUEST_FIELDS = List.of(
            "evidence_id", "offset", "max_bytes", "ck3_build");
    public static final List<String> QUERY_SOURCE_PROVENANCE_REQUEST_FIELDS = List.of(
            "key", "build");

    public static final CapabilityDescriptor LIST_KNOWLEDGE =
            new CapabilityDescriptor(
                    LIST_KNOWLEDGE_TOOL_ID,
                    "ck3-1.19.0.6-vanilla-event-knowledge-list-v1",
                    List.of(
                            "schema", "schema_version", "status", "ck3_build",
                            "ck3_exe_sha256", "query", "namespace", "evidence_class",
                            "has_observations", "after_key", "limit", "dataset_sha256",
                            "dataset_summary", "match_summary", "total_matches", "items",
                            "next_after_key", "unavailable_reason", "invalid_parameter"),
                    List.of(
                            "query_does_not_touch_gameplay_driver_or_ck3",
                            "stable_keyset_pagination_uses_after_key",
                            "filters_and_cursor_accept_no_host_path",
                            "dataset_identity_is_returned_on_available_results",
                            "unknown_mcp_arguments_are_rejected"),
                    true, true, false, false);

    public static final CapabilityDescriptor LIST_EVIDENCE =
            new CapabilityDescriptor(
                    LIST_EVIDENCE_TOOL_ID,
                    "ck3-1.19.0.6-vanilla-event-evidence-list-v1",
                    List.of(
                            "schema", "schema_version", "status", "ck3_build",
                            "ck3_exe_sha256", "event_definition_key", "kind",
                            "after_evidence_id", "limit", "dataset_sha256",
                            "total_matches", "evidence", "next_after_evidence_id",
                            "unavailable_reason", "invalid_parameter"),
                    List.of(
                            "query_does_not_touch_gameplay_driver_or_ck3",
                            "stable_keyset_pagination_uses_content_hash_cursor",
                            "response_exposes_only_repository_relative_logical_paths",
                            "bundle_root_and_blob_paths_are_not_request_or_response_fields",
                            "unknown_mcp_arguments_are_rejected"),
                    true, true, false, false);

    public static final CapabilityDescriptor READ_EVIDENCE =
            new CapabilityDescriptor(
                    READ_EVIDENCE_TOOL_ID,
                    "ck3-1.19.0.6-vanilla-event-evidence-read-v1",
                    List.of(
                            "schema", "schema_version", "status", "ck3_build",
                            "ck3_exe_sha256", "evidence_id", "sha256", "kind",
                            "media_type", "historical_artifact_may_contain_nonportable_locators",
                            "offset", "content_bytes", "total_bytes", "content_base64",
                            "eof", "unavailable_reason"),
                    List.of(
                            "query_does_not_touch_gameplay_driver_or_ck3",
                            "evidence_is_addressed_by_uncompressed_sha256",
                            "each_decoded_chunk_is_bounded_to_65536_bytes",
                            "bundle_integrity_is_verified_before_content_is_returned",
                            "caller_cannot_supply_bundle_root_or_file_path",
                            "unknown_mcp_arguments_are_rejected"),
                    true, true, false, false);

    public static final CapabilityDescriptor QUERY_SOURCE_PROVENANCE =
            new CapabilityDescriptor(
                    QUERY_SOURCE_PROVENANCE_TOOL_ID,
                    "ck3-1.19.0.6-vanilla-event-source-provenance-v1",
                    List.of(
                            "schema", "schema_version", "status", "key", "build",
                            "ck3_exe_sha256", "dataset_sha256", "namespace", "definition",
                            "caller_candidate_resolution", "caller_candidates",
                            "caller_candidates_are_lexical_only",
                            "caller_candidates_review_status", "unavailable_reason",
                            "invalid_parameter"),
                    List.of(
                            "query_does_not_touch_gameplay_driver_or_ck3",
                            "source_paths_are_repository_relative",
                            "source_files_are_sha256_bound_to_exact_build",
                            "caller_candidates_are_lexical_only_not_proven_call_edges",
                            "unsupported_build_is_typed_unavailable",
                            "unknown_mcp_arguments_are_rejected"),
                    true, true, false, false);

    private VanillaEventPortableAssetCapabilityProfile() { }
}
