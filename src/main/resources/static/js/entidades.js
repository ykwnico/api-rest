// ==========================================================
// Configuração de cada entidade: campos do formulário e colunas da tabela.
// O app.js usa estas definições para montar a tela de forma genérica.
// ==========================================================

// Escapa texto antes de inserir no HTML (evita injeção de código)
function esc(valor) {
    return String(valor ?? '').replace(/[&<>"']/g, (c) => ({
        '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
    }[c]));
}

function formatarData(iso) {
    if (!iso) return '';
    const [ano, mes, dia] = iso.split('-');
    return `${dia}/${mes}/${ano}`;
}

function seloLado(lado) {
    const classe = lado === 'CT' ? 'selo--ct' : lado === 'TR' ? 'selo--tr' : '';
    return `<span class="selo ${classe}">${esc(lado)}</span>`;
}

const ENTIDADES = {
    times: {
        singular: 'time',
        plural: 'Times',
        genero: 'o',
        descricao: (t) => t.nome,
        campos: [
            { nome: 'nome', rotulo: 'Nome do time', tipo: 'text', obrigatorio: true, max: 60 },
            { nome: 'pais', rotulo: 'País', tipo: 'text', obrigatorio: true, max: 40 },
            { nome: 'ranking', rotulo: 'Posição no ranking', tipo: 'number', obrigatorio: true, min: 1, maxNum: 500 },
            { nome: 'anoFundacao', rotulo: 'Ano de fundação', tipo: 'number', obrigatorio: true, min: 1999, maxNum: 2100 }
        ],
        colunas: [
            { rotulo: '#', valor: (t) => `<b>${t.ranking}º</b>` },
            { rotulo: 'Time', valor: (t) => esc(t.nome) },
            { rotulo: 'País', valor: (t) => esc(t.pais) },
            { rotulo: 'Fundação', valor: (t) => t.anoFundacao }
        ]
    },

    jogadores: {
        singular: 'jogador',
        plural: 'Jogadores',
        genero: 'o',
        descricao: (j) => j.nickname,
        campos: [
            { nome: 'nickname', rotulo: 'Nickname', tipo: 'text', obrigatorio: true, max: 30 },
            { nome: 'nomeReal', rotulo: 'Nome real', tipo: 'text', obrigatorio: true, max: 80 },
            { nome: 'nacionalidade', rotulo: 'Nacionalidade', tipo: 'text', obrigatorio: true, max: 40 },
            { nome: 'funcao', rotulo: 'Função', tipo: 'select', obrigatorio: true,
              opcoes: ['AWPer', 'Entry', 'IGL', 'Lurker', 'Rifler', 'Suporte'] },
            { nome: 'rating', rotulo: 'Rating 2.0', tipo: 'number', obrigatorio: true, min: 0, maxNum: 3, passo: '0.01',
              dica: 'Média de desempenho, de 0.00 a 3.00' },
            { nome: 'time', rotulo: 'Time atual', tipo: 'relacao', recurso: 'times', obrigatorio: false,
              textoVazio: 'Sem time (free agent)', textoOpcao: (t) => t.nome }
        ],
        colunas: [
            { rotulo: 'Nickname', valor: (j) => `<b>${esc(j.nickname)}</b>` },
            { rotulo: 'Nome', valor: (j) => esc(j.nomeReal) },
            { rotulo: 'País', valor: (j) => esc(j.nacionalidade) },
            { rotulo: 'Função', valor: (j) => `<span class="selo">${esc(j.funcao)}</span>` },
            { rotulo: 'Rating', valor: (j) => Number(j.rating).toFixed(2) },
            { rotulo: 'Time', valor: (j) => j.time ? esc(j.time.nome) : '<span class="selo selo--nao">Free agent</span>' }
        ]
    },

    partidas: {
        singular: 'partida',
        plural: 'Partidas',
        genero: 'a',
        descricao: (p) => `${p.timeA.nome} x ${p.timeB.nome} (${p.campeonato})`,
        campos: [
            { nome: 'campeonato', rotulo: 'Campeonato', tipo: 'text', obrigatorio: true, max: 80 },
            { nome: 'data', rotulo: 'Data', tipo: 'date', obrigatorio: true },
            { nome: 'timeA', rotulo: 'Time A', tipo: 'relacao', recurso: 'times', obrigatorio: true, textoOpcao: (t) => t.nome },
            { nome: 'placarA', rotulo: 'Rounds do time A', tipo: 'number', obrigatorio: true, min: 0, maxNum: 50 },
            { nome: 'timeB', rotulo: 'Time B', tipo: 'relacao', recurso: 'times', obrigatorio: true, textoOpcao: (t) => t.nome },
            { nome: 'placarB', rotulo: 'Rounds do time B', tipo: 'number', obrigatorio: true, min: 0, maxNum: 50 },
            { nome: 'mapa', rotulo: 'Mapa', tipo: 'relacao', recurso: 'mapas', obrigatorio: true, textoOpcao: (m) => m.nome }
        ],
        colunas: [
            { rotulo: 'Data', valor: (p) => formatarData(p.data) },
            { rotulo: 'Campeonato', valor: (p) => esc(p.campeonato) },
            { rotulo: 'Confronto', valor: (p) => {
                const a = p.placarA > p.placarB ? 'vencedor' : '';
                const b = p.placarB > p.placarA ? 'vencedor' : '';
                return `<span class="${a}">${esc(p.timeA.nome)}</span> x <span class="${b}">${esc(p.timeB.nome)}</span>`;
            } },
            { rotulo: 'Placar', valor: (p) => `<span class="placar"><b>${p.placarA}</b> : <b>${p.placarB}</b></span>` },
            { rotulo: 'Mapa', valor: (p) => esc(p.mapa.nome) }
        ]
    },

    mapas: {
        singular: 'mapa',
        plural: 'Mapas',
        genero: 'o',
        descricao: (m) => m.nome,
        campos: [
            { nome: 'nome', rotulo: 'Nome do mapa', tipo: 'text', obrigatorio: true, max: 40 },
            { nome: 'modo', rotulo: 'Modo de jogo', tipo: 'select', obrigatorio: true, opcoes: ['Competitivo', 'Wingman', 'Casual'] },
            { nome: 'poolAtivo', rotulo: 'Está no pool ativo?', tipo: 'booleano', obrigatorio: true },
            { nome: 'anoLancamento', rotulo: 'Ano de lançamento', tipo: 'number', obrigatorio: true, min: 1999, maxNum: 2100 }
        ],
        colunas: [
            { rotulo: 'Mapa', valor: (m) => `<b>${esc(m.nome)}</b>` },
            { rotulo: 'Modo', valor: (m) => esc(m.modo) },
            { rotulo: 'Pool ativo', valor: (m) => m.poolAtivo
                ? '<span class="selo selo--sim">Sim</span>' : '<span class="selo selo--nao">Não</span>' },
            { rotulo: 'Lançamento', valor: (m) => m.anoLancamento }
        ]
    },

    armas: {
        singular: 'arma',
        plural: 'Armas',
        genero: 'a',
        descricao: (a) => a.nome,
        campos: [
            { nome: 'nome', rotulo: 'Nome da arma', tipo: 'text', obrigatorio: true, max: 40 },
            { nome: 'categoria', rotulo: 'Categoria', tipo: 'select', obrigatorio: true, opcoes: ['Pistola', 'SMG', 'Rifle', 'Sniper', 'Pesada'] },
            { nome: 'preco', rotulo: 'Preço ($)', tipo: 'number', obrigatorio: true, min: 0, maxNum: 10000 },
            { nome: 'lado', rotulo: 'Lado', tipo: 'select', obrigatorio: true, opcoes: ['TR', 'CT', 'Ambos'] },
            { nome: 'dano', rotulo: 'Dano base', tipo: 'number', obrigatorio: true, min: 1, maxNum: 500 }
        ],
        colunas: [
            { rotulo: 'Arma', valor: (a) => `<b>${esc(a.nome)}</b>` },
            { rotulo: 'Categoria', valor: (a) => esc(a.categoria) },
            { rotulo: 'Preço', valor: (a) => `$${a.preco}` },
            { rotulo: 'Lado', valor: (a) => seloLado(a.lado) },
            { rotulo: 'Dano', valor: (a) => a.dano }
        ]
    }
};
