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

@DisplayName("API de Times")
class TimeControllerTest extends TesteBase {

    private static final String JSON_VALIDO = """
            { "nome": "FURIA", "pais": "Brasil", "ranking": 10, "anoFundacao": 2017 }
            """;

    @Test
    @DisplayName("POST cadastra time e retorna 201")
    void cadastrar() throws Exception {
        mvc.perform(post("/api/times").contentType(MediaType.APPLICATION_JSON).content(JSON_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nome").value("FURIA"));
    }

    @Test
    @DisplayName("POST com dados inválidos retorna 400 e mensagens por campo")
    void cadastrarInvalido() throws Exception {
        mvc.perform(post("/api/times").contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"nome\": \"\", \"pais\": \"Brasil\", \"ranking\": 0, \"anoFundacao\": 1800 }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.nome").value("O nome do time é obrigatório"))
                .andExpect(jsonPath("$.campos.ranking").exists())
                .andExpect(jsonPath("$.campos.anoFundacao").exists());
    }

    @Test
    @DisplayName("POST com JSON mal formatado retorna 400")
    void jsonInvalido() throws Exception {
        mvc.perform(post("/api/times").contentType(MediaType.APPLICATION_JSON).content("{ nome: "))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST com nome repetido retorna 409")
    void cadastrarDuplicado() throws Exception {
        timeRepository.save(new Time("FURIA", "Brasil", 10, 2017));
        mvc.perform(post("/api/times").contentType(MediaType.APPLICATION_JSON).content(JSON_VALIDO))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("GET lista os times ordenados pelo ranking")
    void listar() throws Exception {
        timeRepository.save(new Time("MIBR", "Brasil", 20, 2003));
        timeRepository.save(new Time("Vitality", "França", 1, 2013));
        mvc.perform(get("/api/times"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nome").value("Vitality"));
    }

    @Test
    @DisplayName("GET por ID retorna 200 ou 404")
    void buscarPorId() throws Exception {
        Time time = timeRepository.save(new Time("FURIA", "Brasil", 10, 2017));
        mvc.perform(get("/api/times/" + time.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pais").value("Brasil"));
        mvc.perform(get("/api/times/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Time com ID 99999 não encontrado"));
    }

    @Test
    @DisplayName("GET com ID não numérico retorna 400")
    void idInvalido() throws Exception {
        mvc.perform(get("/api/times/abc")).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT atualiza time existente e retorna 404 para inexistente")
    void atualizar() throws Exception {
        Time time = timeRepository.save(new Time("FURIA", "Brasil", 10, 2017));
        mvc.perform(put("/api/times/" + time.getId()).contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"nome\": \"FURIA Esports\", \"pais\": \"Brasil\", \"ranking\": 5, \"anoFundacao\": 2017 }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("FURIA Esports"))
                .andExpect(jsonPath("$.ranking").value(5));
        mvc.perform(put("/api/times/99999").contentType(MediaType.APPLICATION_JSON).content(JSON_VALIDO))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE remove time e retorna 204")
    void excluir() throws Exception {
        Time time = timeRepository.save(new Time("FURIA", "Brasil", 10, 2017));
        mvc.perform(delete("/api/times/" + time.getId())).andExpect(status().isNoContent());
        mvc.perform(get("/api/times/" + time.getId())).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE de time com jogadores retorna 409")
    void excluirComJogadores() throws Exception {
        Time time = timeRepository.save(new Time("FURIA", "Brasil", 10, 2017));
        jogadorRepository.save(new Jogador("KSCERATO", "Kaike Cerato", "Brasil", "Rifler", 1.1, time));
        mvc.perform(delete("/api/times/" + time.getId())).andExpect(status().isConflict());
    }
}
