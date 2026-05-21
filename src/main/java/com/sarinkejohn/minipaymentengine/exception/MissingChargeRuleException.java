package com.sarinkejohn.minipaymentengine.exception;

public class MissingChargeRuleException extends RuntimeException {
    public MissingChargeRuleException(String message) {
        super(message);
    }
}
