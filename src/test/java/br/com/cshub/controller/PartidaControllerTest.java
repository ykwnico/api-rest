package br.com.cshub.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import br.com.cshub.TesteBase;
import br.com.cshub.entity.Mapa;
import br.com.cshub.entity.Partida;
import br.com.cshub.entity.Time;

@DisplayName("API de Partidas")
class PartidaControllerTest extends TesteBase {

    private Time furia;
    private Time navi;
    private Mapa mirage;

    @BeforeEach
    void dados() {
        furia = timeRepository.save(new Time("FURIA", "Brasil", 10, 2017));
        navi = timeRepository.save(new Time("NAVI", "Ucrânia", 3, 2009));
        mirage = mapaRepository.save(new Mapa("Mirage", "Competitivo", true, 2013));
    }

    private String json(Long timeA, Long timeB, Long mapa, int placarA, int placarB) {
        return """
                { "campeonato": "IEM Rio", "data": "2026-10-12",
                  "timeA": { "id": %d }, "timeB": { "id": %d }, "mapa": { "id": %d },
                  "placarA": %d, "placarB": %d }
                """.formatted(timeA, timeB, mapa, placarA, placarB);
    }

    @Test
    @DisplayName("POST registra partida com times e mapa completos na resposta")
    void cadastrar() throws Exception {
        mvc.perform(post("/api/partidas").contentType(MediaType.APPLICATION_JSON)
                        .content(json(furia.getId(), navi.getId(), mirage.getId(), 13, 9)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.timeA.nome").value("FURIA"))
                .andExpect(jsonPath("$.timeB.nome").value("NAVI"))
                .andExpect(jsonPath("$.mapa.nome").value("Mirage"))
                .andExpect(jsonPath("$.data").value("2026-10-12"));
    }

    @Test
    @DisplayName("POST com o mesmo time nos dois lados retorna 400")
    void mesmoTime() throws Exception {
        mvc.perform(post("/api/partidas").contentType(MediaType.APPLICATION_JSON)
                        .content(json(furia.getId(), furia.getId(), mirage.getId(), 13, 9)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("O time A e o time B devem ser diferentes"));
    }

    @Test
    @DisplayName("POST com mapa inexistente retorna 400")
    void mapaInexistente() throws Exception {
        mvc.perform(post("/api/partidas").contentType(MediaType.APPLICATION_JSON)
                        .content(json(furia.getId(), navi.getId(), 9999L, 13, 9)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST sem campos obrigatórios retorna 400 com mensagens")
    void camposObrigatorios() throws Exception {
        mvc.perform(post("/api/partidas").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.campeonato").exists())
                .andExpect(jsonPath("$.campos.timeA").value("O time A é obrigatório"))
                .andExpect(jsonPath("$.campos.mapa").exists());
    }

    @Test
    @DisplayName("GET, PUT e DELETE de partida")
    void fluxoCompleto() throws Exception {
        Partida p = partidaRepository.save(new Partida("IEM Rio", LocalDate.of(2026, 10, 12), furia, navi, mirage, 13, 9));
        mvc.perform(get("/api/partidas")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
        mvc.perform(get("/api/partidas/" + p.getId())).andExpect(status().isOk());
        mvc.perform(put("/api/partidas/" + p.getId()).contentType(MediaType.APPLICATION_JSON)
                        .content(json(furia.getId(), navi.getId(), mirage.getId(), 16, 14)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placarA").value(16));
        mvc.perform(delete("/api/partidas/" + p.getId())).andExpect(status().isNoContent());
        mvc.perform(get("/api/partidas/" + p.getId())).andExpect(status().isNotFound());
    }
}
