package com.xenoamess.kaishek.operator;

import java.util.Map;

/** Deployment-provided MCP transport; open_kaishek does not own credentials or bootstrap. */
@FunctionalInterface
public interface OperatorMcpTransport {
    Map<String, Object> call(
            String endpoint,
            String toolId,
            Map<String, Object> arguments);
}
