-- ==========================================================
-- CS Hub - script de criação do banco de dados (MySQL 8+)
-- Observação: a aplicação também cria/atualiza as tabelas sozinha
-- (spring.jpa.hibernate.ddl-auto=update). Este script serve como
-- documentação do modelo e alternativa para criar a base manualmente.
-- ==========================================================

CREATE DATABASE IF NOT EXISTS cs_hub
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE cs_hub;

-- Times (equipes)
CREATE TABLE IF NOT EXISTS times (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    nome          VARCHAR(60)  NOT NULL,
    pais          VARCHAR(40)  NOT NULL,
    ranking       INT          NOT NULL,
    ano_fundacao  INT          NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_times_nome UNIQUE (nome)
);

-- Jogadores (time_id pode ser nulo = free agent)
CREATE TABLE IF NOT EXISTS jogadores (
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    nickname       VARCHAR(30)  NOT NULL,
    nome_real      VARCHAR(80)  NOT NULL,
    nacionalidade  VARCHAR(40)  NOT NULL,
    funcao         VARCHAR(20)  NOT NULL,
    rating         DOUBLE       NOT NULL,
    time_id        BIGINT       NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_jogadores_nickname UNIQUE (nickname),
    CONSTRAINT fk_jogadores_time FOREIGN KEY (time_id) REFERENCES times (id)
);

-- Mapas
CREATE TABLE IF NOT EXISTS mapas (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    nome            VARCHAR(40)  NOT NULL,
    modo            VARCHAR(20)  NOT NULL,
    pool_ativo      BIT(1)       NOT NULL,
    ano_lancamento  INT          NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_mapas_nome UNIQUE (nome)
);

-- Armas
CREATE TABLE IF NOT EXISTS armas (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    nome       VARCHAR(40)  NOT NULL,
    categoria  VARCHAR(20)  NOT NULL,
    preco      INT          NOT NULL,
    lado       VARCHAR(10)  NOT NULL,
    dano       INT          NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_armas_nome UNIQUE (nome)
);

-- Partidas (dois times + um mapa)
CREATE TABLE IF NOT EXISTS partidas (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    campeonato    VARCHAR(80)  NOT NULL,
    data_partida  DATE         NOT NULL,
    time_a_id     BIGINT       NOT NULL,
    time_b_id     BIGINT       NOT NULL,
    mapa_id       BIGINT       NOT NULL,
    placar_a      INT          NOT NULL,
    placar_b      INT          NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_partidas_time_a FOREIGN KEY (time_a_id) REFERENCES times (id),
    CONSTRAINT fk_partidas_time_b FOREIGN KEY (time_b_id) REFERENCES times (id),
    CONSTRAINT fk_partidas_mapa   FOREIGN KEY (mapa_id)   REFERENCES mapas (id)
);
