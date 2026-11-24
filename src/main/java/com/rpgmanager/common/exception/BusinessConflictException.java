package com.rpgmanager.common.exception;

/**
 * Violação de regra de negócio que conflita com o estado atual (ex.: número de sessão repetido). Vira HTTP 409.
 */
public class BusinessConflictException extends RuntimeException {

    public BusinessConflictException(String message) {
        super(message);
    }
}
