-- ==========================================================
-- CS Hub - dados iniciais (opcional)
-- A aplicação já insere estes dados automaticamente na primeira
-- execução (classe config/DadosIniciais). Use este script apenas
-- se preferir popular o banco manualmente.
-- ==========================================================

USE cs_hub;

INSERT INTO times (nome, pais, ranking, ano_fundacao) VALUES
('FURIA', 'Brasil', 12, 2017),
('Team Vitality', 'França', 1, 2013),
('Natus Vincere', 'Ucrânia', 3, 2009),
('MIBR', 'Brasil', 25, 2003);

INSERT INTO jogadores (nickname, nome_real, nacionalidade, funcao, rating, time_id) VALUES
('FalleN', 'Gabriel Toledo', 'Brasil', 'IGL', 1.02, 1),
('KSCERATO', 'Kaike Cerato', 'Brasil', 'Rifler', 1.15, 1),
('yuurih', 'Yuri Boian', 'Brasil', 'Entry', 1.08, 1),
('ZywOo', 'Mathieu Herbaut', 'França', 'AWPer', 1.30, 2),
('apEX', 'Dan Madesclaire', 'França', 'IGL', 0.95, 2),
('Aleksib', 'Aleksi Virolainen', 'Finlândia', 'IGL', 0.93, 3),
('b1t', 'Valerii Vakhovskyi', 'Ucrânia', 'Rifler', 1.06, 3),
('coldzera', 'Marcelo David', 'Brasil', 'Rifler', 1.01, NULL);

INSERT INTO mapas (nome, modo, pool_ativo, ano_lancamento) VALUES
('Mirage', 'Competitivo', 1, 2013),
('Inferno', 'Competitivo', 1, 2000),
('Nuke', 'Competitivo', 1, 2000),
('Ancient', 'Competitivo', 1, 2020),
('Dust II', 'Competitivo', 1, 2001),
('Vertigo', 'Competitivo', 0, 2017),
('Shoots', 'Wingman', 0, 2021);

INSERT INTO armas (nome, categoria, preco, lado, dano) VALUES
('Glock-18', 'Pistola', 200, 'TR', 30),
('USP-S', 'Pistola', 200, 'CT', 35),
('Desert Eagle', 'Pistola', 700, 'Ambos', 53),
('MAC-10', 'SMG', 1050, 'TR', 29),
('MP9', 'SMG', 1250, 'CT', 26),
('AK-47', 'Rifle', 2700, 'TR', 36),
('M4A1-S', 'Rifle', 2900, 'CT', 38),
('AWP', 'Sniper', 4750, 'Ambos', 115),
('Nova', 'Pesada', 1050, 'Ambos', 26);

INSERT INTO partidas (campeonato, data_partida, time_a_id, time_b_id, mapa_id, placar_a, placar_b) VALUES
('IEM Rio 2026', '2026-10-12', 1, 3, 1, 13, 9),
('IEM Rio 2026', '2026-10-13', 2, 1, 2, 13, 11),
('BLAST Premier', '2026-11-02', 4, 3, 3, 7, 13);
