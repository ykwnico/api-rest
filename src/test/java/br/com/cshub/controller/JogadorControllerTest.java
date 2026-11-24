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
import br.com.cshub.entity.Jogador;
import br.com.cshub.entity.Time;

@DisplayName("API de Jogadores")
class JogadorControllerTest extends TesteBase {

    private String json(Long timeId) {
        String time = timeId == null ? "null" : "{ \"id\": " + timeId + " }";
        return """
                { "nickname": "FalleN", "nomeReal": "Gabriel Toledo", "nacionalidade": "Brasil",
                  "funcao": "IGL", "rating": 1.05, "time": %s }
                """.formatted(time);
    }

    @Test
    @DisplayName("POST cadastra jogador vinculado a um time")
    void cadastrarComTime() throws Exception {
        Time time = timeRepository.save(new Time("FURIA", "Brasil", 10, 2017));
        mvc.perform(post("/api/jogadores").contentType(MediaType.APPLICATION_JSON).content(json(time.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nickname").value("FalleN"))
                .andExpect(jsonPath("$.time.nome").value("FURIA"));
    }

    @Test
    @DisplayName("POST cadastra jogador sem time (free agent)")
    void cadastrarSemTime() throws Exception {
        mvc.perform(post("/api/jogadores").contentType(MediaType.APPLICATION_JSON).content(json(null)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.time").doesNotExist());
    }

    @Test
    @DisplayName("POST com time inexistente retorna 400")
    void cadastrarTimeInexistente() throws Exception {
        mvc.perform(post("/api/jogadores").contentType(MediaType.APPLICATION_JSON).content(json(999L)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("O time informado (ID 999) não existe"));
    }

    @Test
    @DisplayName("POST com função e rating inválidos retorna 400")
    void cadastrarInvalido() throws Exception {
        mvc.perform(post("/api/jogadores").contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"nickname\": \"x\", \"nomeReal\": \"y\", \"nacionalidade\": \"z\", \"funcao\": \"Goleiro\", \"rating\": 5 }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.funcao").exists())
                .andExpect(jsonPath("$.campos.rating").value("O rating deve ser no máximo 3.0"));
    }

    @Test
    @DisplayName("GET lista e busca jogador por ID")
    void listarEBuscar() throws Exception {
        Jogador j = jogadorRepository.save(new Jogador("ZywOo", "Mathieu Herbaut", "França", "AWPer", 1.3, null));
        mvc.perform(get("/api/jogadores")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
        mvc.perform(get("/api/jogadores/" + j.getId())).andExpect(status().isOk())
                .andExpect(jsonPath("$.funcao").value("AWPer"));
        mvc.perform(get("/api/jogadores/12345")).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT transfere jogador para outro time")
    void atualizar() throws Exception {
        Time time = timeRepository.save(new Time("FURIA", "Brasil", 10, 2017));
        Jogador j = jogadorRepository.save(new Jogador("FalleN", "Gabriel Toledo", "Brasil", "IGL", 1.0, null));
        mvc.perform(put("/api/jogadores/" + j.getId()).contentType(MediaType.APPLICATION_JSON).content(json(time.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.time.id").value(time.getId()));
    }

    @Test
    @DisplayName("DELETE remove jogador e retorna 404 se não existir")
    void excluir() throws Exception {
        Jogador j = jogadorRepository.save(new Jogador("FalleN", "Gabriel Toledo", "Brasil", "IGL", 1.0, null));
        mvc.perform(delete("/api/jogadores/" + j.getId())).andExpect(status().isNoContent());
        mvc.perform(delete("/api/jogadores/" + j.getId())).andExpect(status().isNotFound());
    }
}
