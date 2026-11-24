package com.rpgmanager.common.exception;

/**
 * Requisição bem formada que viola uma regra de domínio (ex.: ficha de D&D numa campanha de
 * Ordem Paranormal). Vira HTTP 422.
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
