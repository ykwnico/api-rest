package br.com.cshub.config;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import br.com.cshub.entity.Arma;
import br.com.cshub.entity.Jogador;
import br.com.cshub.entity.Mapa;
import br.com.cshub.entity.Partida;
import br.com.cshub.entity.Time;
import br.com.cshub.repository.ArmaRepository;
import br.com.cshub.repository.JogadorRepository;
import br.com.cshub.repository.MapaRepository;
import br.com.cshub.repository.PartidaRepository;
import br.com.cshub.repository.TimeRepository;

// Insere dados iniciais na primeira execução (somente se as tabelas estiverem vazias).
// Pode ser desligado com app.seed.enabled=false
@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class DadosIniciais implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DadosIniciais.class);

    private final TimeRepository timeRepository;
    private final JogadorRepository jogadorRepository;
    private final MapaRepository mapaRepository;
    private final ArmaRepository armaRepository;
    private final PartidaRepository partidaRepository;

    public DadosIniciais(TimeRepository timeRepository, JogadorRepository jogadorRepository,
                         MapaRepository mapaRepository, ArmaRepository armaRepository,
                         PartidaRepository partidaRepository) {
        this.timeRepository = timeRepository;
        this.jogadorRepository = jogadorRepository;
        this.mapaRepository = mapaRepository;
        this.armaRepository = armaRepository;
        this.partidaRepository = partidaRepository;
    }

    @Override
    public void run(String... args) {
        if (timeRepository.count() > 0 || mapaRepository.count() > 0 || armaRepository.count() > 0) {
            return; // banco já possui dados
        }
        log.info("Banco vazio: inserindo dados iniciais do CS Hub");

        // Times
        Time furia = timeRepository.save(new Time("FURIA", "Brasil", 12, 2017));
        Time vitality = timeRepository.save(new Time("Team Vitality", "França", 1, 2013));
        Time navi = timeRepository.save(new Time("Natus Vincere", "Ucrânia", 3, 2009));
        Time mibr = timeRepository.save(new Time("MIBR", "Brasil", 25, 2003));

        // Jogadores
        jogadorRepository.save(new Jogador("FalleN", "Gabriel Toledo", "Brasil", "IGL", 1.02, furia));
        jogadorRepository.save(new Jogador("KSCERATO", "Kaike Cerato", "Brasil", "Rifler", 1.15, furia));
        jogadorRepository.save(new Jogador("yuurih", "Yuri Boian", "Brasil", "Entry", 1.08, furia));
        jogadorRepository.save(new Jogador("ZywOo", "Mathieu Herbaut", "França", "AWPer", 1.30, vitality));
        jogadorRepository.save(new Jogador("apEX", "Dan Madesclaire", "França", "IGL", 0.95, vitality));
        jogadorRepository.save(new Jogador("Aleksib", "Aleksi Virolainen", "Finlândia", "IGL", 0.93, navi));
        jogadorRepository.save(new Jogador("b1t", "Valerii Vakhovskyi", "Ucrânia", "Rifler", 1.06, navi));
        jogadorRepository.save(new Jogador("coldzera", "Marcelo David", "Brasil", "Rifler", 1.01, null));

        // Mapas
        Mapa mirage = mapaRepository.save(new Mapa("Mirage", "Competitivo", true, 2013));
        Mapa inferno = mapaRepository.save(new Mapa("Inferno", "Competitivo", true, 2000));
        Mapa nuke = mapaRepository.save(new Mapa("Nuke", "Competitivo", true, 2000));
        mapaRepository.save(new Mapa("Ancient", "Competitivo", true, 2020));
        mapaRepository.save(new Mapa("Dust II", "Competitivo", true, 2001));
        mapaRepository.save(new Mapa("Vertigo", "Competitivo", false, 2017));
        mapaRepository.save(new Mapa("Shoots", "Wingman", false, 2021));

        // Armas
        armaRepository.save(new Arma("Glock-18", "Pistola", 200, "TR", 30));
        armaRepository.save(new Arma("USP-S", "Pistola", 200, "CT", 35));
        armaRepository.save(new Arma("Desert Eagle", "Pistola", 700, "Ambos", 53));
        armaRepository.save(new Arma("MAC-10", "SMG", 1050, "TR", 29));
        armaRepository.save(new Arma("MP9", "SMG", 1250, "CT", 26));
        armaRepository.save(new Arma("AK-47", "Rifle", 2700, "TR", 36));
        armaRepository.save(new Arma("M4A1-S", "Rifle", 2900, "CT", 38));
        armaRepository.save(new Arma("AWP", "Sniper", 4750, "Ambos", 115));
        armaRepository.save(new Arma("Nova", "Pesada", 1050, "Ambos", 26));

        // Partidas
        partidaRepository.save(new Partida("IEM Rio 2026", LocalDate.of(2026, 10, 12), furia, navi, mirage, 13, 9));
        partidaRepository.save(new Partida("IEM Rio 2026", LocalDate.of(2026, 10, 13), vitality, furia, inferno, 13, 11));
        partidaRepository.save(new Partida("BLAST Premier", LocalDate.of(2026, 11, 2), mibr, navi, nuke, 7, 13));
    }
}
