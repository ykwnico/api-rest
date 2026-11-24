package br.com.cshub;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// Gera o arquivo docs/openapi.json a partir da própria API (documentação sempre atualizada)
@DisplayName("Documentação OpenAPI")
class OpenApiExportTest extends TesteBase {

    @Test
    @DisplayName("GET /v3/api-docs gera o JSON da especificação")
    void exportarEspecificacao() throws Exception {
        String json = mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        Path destino = Path.of("docs", "openapi.json");
        Files.createDirectories(destino.getParent());
        Files.writeString(destino, json, StandardCharsets.UTF_8);
    }
}
