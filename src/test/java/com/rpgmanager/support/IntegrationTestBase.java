package com.rpgmanager.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.lifecycle.Startables;

/**
 * Base dos testes de integração: PostgreSQL e MongoDB reais em containers (mesmas versões do
 * docker-compose), ligados ao Spring via {@code @ServiceConnection}.
 *
 * <p>Padrão "singleton container": os containers sobem uma única vez, no carregamento da classe, e ficam
 * ativos até o fim da JVM de testes (o Ryuk, do Testcontainers, remove tudo ao final). Isso é necessário
 * porque o Spring reaproveita o mesmo contexto entre classes de teste; se cada classe subisse containers
 * próprios ({@code @Testcontainers} + {@code @Container}), o contexto em cache continuaria apontando para
 * as portas dos containers já desligados.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
public abstract class IntegrationTestBase {

    @ServiceConnection
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

    @ServiceConnection
    static final MongoDBContainer mongo = new MongoDBContainer("mongo:8.0");

    static {
        // Sobe os dois em paralelo
        Startables.deepStart(postgres, mongo).join();
    }

    @Autowired
    protected MockMvc mockMvc;
}
