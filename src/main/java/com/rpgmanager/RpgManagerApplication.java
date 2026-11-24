package com.rpgmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

/**
 * Ponto de entrada da API de gerenciamento de campanhas de RPG de mesa.
 *
 * <p>{@code VIA_DTO} serializa {@code Page<T>} num JSON estável
 * ({@code content} + {@code page}), em vez de expor o {@code PageImpl} interno do Spring.</p>
 */
@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class RpgManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RpgManagerApplication.class, args);
    }
}
