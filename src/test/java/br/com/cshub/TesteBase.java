package br.com.cshub;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import br.com.cshub.repository.ArmaRepository;
import br.com.cshub.repository.JogadorRepository;
import br.com.cshub.repository.MapaRepository;
import br.com.cshub.repository.PartidaRepository;
import br.com.cshub.repository.TimeRepository;

// Base dos testes: sobe a aplicação com H2 e limpa o banco antes de cada teste
@SpringBootTest
@AutoConfigureMockMvc
public abstract class TesteBase {

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected TimeRepository timeRepository;
    @Autowired
    protected JogadorRepository jogadorRepository;
    @Autowired
    protected MapaRepository mapaRepository;
    @Autowired
    protected ArmaRepository armaRepository;
    @Autowired
    protected PartidaRepository partidaRepository;

    @BeforeEach
    void limparBanco() {
        // Ordem importa por causa das chaves estrangeiras
        partidaRepository.deleteAll();
        jogadorRepository.deleteAll();
        timeRepository.deleteAll();
        mapaRepository.deleteAll();
        armaRepository.deleteAll();
    }
}
