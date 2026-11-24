package com.rpgmanager.common.exception;

/**
 * Recurso inexistente (ou que não pertence à campanha informada). Vira HTTP 404.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource, Object id) {
        super("%s com id %s não encontrado(a)".formatted(resource, id));
    }
}
