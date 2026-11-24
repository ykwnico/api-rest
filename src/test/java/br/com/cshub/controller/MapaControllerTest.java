package br.com.cshub.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import br.com.cshub.TesteBase;
import br.com.cshub.entity.Mapa;
import br.com.cshub.entity.Partida;
import br.com.cshub.entity.Time;

@DisplayName("API de Mapas")
class MapaControllerTest extends TesteBase {

    private static final String JSON = """
            { "nome": "Mirage", "modo": "Competitivo", "poolAtivo": true, "anoLancamento": 2013 }
            """;

    @Test
    @DisplayName("POST cadastra mapa e retorna 201")
    void cadastrar() throws Exception {
        mvc.perform(post("/api/mapas").contentType(MediaType.APPLICATION_JSON).content(JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.poolAtivo").value(true));
    }

    @Test
    @DisplayName("POST sem campos obrigatórios retorna 400")
    void cadastrarInvalido() throws Exception {
        mvc.perform(post("/api/mapas").contentType(MediaType.APPLICATION_JSON).content("{ \"modo\": \"Deathmatch\" }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.nome").exists())
                .andExpect(jsonPath("$.campos.modo").value("O modo deve ser Competitivo, Wingman ou Casual"))
                .andExpect(jsonPath("$.campos.poolAtivo").exists());
    }

    @Test
    @DisplayName("GET, PUT e DELETE de mapa")
    void fluxoCompleto() throws Exception {
        Mapa mapa = mapaRepository.save(new Mapa("Vertigo", "Competitivo", true, 2017));
        mvc.perform(get("/api/mapas")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
        mvc.perform(get("/api/mapas/" + mapa.getId())).andExpect(status().isOk());
        mvc.perform(put("/api/mapas/" + mapa.getId()).contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"nome\": \"Vertigo\", \"modo\": \"Competitivo\", \"poolAtivo\": false, \"anoLancamento\": 2017 }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.poolAtivo").value(false));
        mvc.perform(delete("/api/mapas/" + mapa.getId())).andExpect(status().isNoContent());
        mvc.perform(get("/api/mapas/" + mapa.getId())).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE de mapa usado em partida retorna 409")
    void excluirEmUso() throws Exception {
        Mapa mapa = mapaRepository.save(new Mapa("Mirage", "Competitivo", true, 2013));
        Time a = timeRepository.save(new Time("FURIA", "Brasil", 10, 2017));
        Time b = timeRepository.save(new Time("MIBR", "Brasil", 20, 2003));
        partidaRepository.save(new Partida("Teste", LocalDate.now(), a, b, mapa, 13, 5));
        mvc.perform(delete("/api/mapas/" + mapa.getId())).andExpect(status().isConflict());
    }
}
