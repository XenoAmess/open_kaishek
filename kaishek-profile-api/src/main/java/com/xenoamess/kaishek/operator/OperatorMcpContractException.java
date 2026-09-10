package com.xenoamess.kaishek.operator;

/** Raised when a target-side operator MCP response violates the pinned contract. */
public final class OperatorMcpContractException extends RuntimeException {
    public OperatorMcpContractException(String message) {
        super(message);
    }
}
