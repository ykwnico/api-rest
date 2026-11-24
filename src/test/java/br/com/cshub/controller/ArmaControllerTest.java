package br.com.cshub.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import br.com.cshub.TesteBase;
import br.com.cshub.entity.Arma;

@DisplayName("API de Armas")
class ArmaControllerTest extends TesteBase {

    private static final String JSON = """
            { "nome": "AK-47", "categoria": "Rifle", "preco": 2700, "lado": "TR", "dano": 36 }
            """;

    @Test
    @DisplayName("POST cadastra arma e retorna 201")
    void cadastrar() throws Exception {
        mvc.perform(post("/api/armas").contentType(MediaType.APPLICATION_JSON).content(JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.preco").value(2700));
    }

    @Test
    @DisplayName("POST com preço negativo e lado inválido retorna 400")
    void cadastrarInvalido() throws Exception {
        mvc.perform(post("/api/armas").contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"nome\": \"AWP\", \"categoria\": \"Sniper\", \"preco\": -1, \"lado\": \"Espectador\", \"dano\": 115 }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.preco").value("O preço não pode ser negativo"))
                .andExpect(jsonPath("$.campos.lado").value("O lado deve ser TR, CT ou Ambos"));
    }

    @Test
    @DisplayName("POST com nome repetido retorna 409")
    void duplicado() throws Exception {
        armaRepository.save(new Arma("AK-47", "Rifle", 2700, "TR", 36));
        mvc.perform(post("/api/armas").contentType(MediaType.APPLICATION_JSON).content(JSON))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("GET, PUT e DELETE de arma")
    void fluxoCompleto() throws Exception {
        Arma arma = armaRepository.save(new Arma("AK-47", "Rifle", 2500, "TR", 36));
        mvc.perform(get("/api/armas")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
        mvc.perform(get("/api/armas/" + arma.getId())).andExpect(status().isOk()).andExpect(jsonPath("$.nome").value("AK-47"));
        mvc.perform(put("/api/armas/" + arma.getId()).contentType(MediaType.APPLICATION_JSON).content(JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preco").value(2700));
        mvc.perform(delete("/api/armas/" + arma.getId())).andExpect(status().isNoContent());
        mvc.perform(get("/api/armas/" + arma.getId())).andExpect(status().isNotFound());
    }
}
