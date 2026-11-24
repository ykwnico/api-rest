package br.com.cshub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

// Configuração da documentação Swagger / OpenAPI
// Interface disponível em: http://localhost:8080/swagger-ui.html
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI csHubOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("CS Hub API")
                        .version("1.0.0")
                        .description("API REST para gerenciar times, jogadores, mapas, armas e partidas "
                                + "de campeonatos de Counter-Strike. Todas as rotas trabalham com JSON.")
                        .contact(new Contact().name("Equipe CS Hub")))
                .addServersItem(new Server().url("http://localhost:8080").description("Ambiente local"));
    }
}
