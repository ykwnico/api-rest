// ==========================================================
// Comunicação com a API REST usando fetch
// ==========================================================

// Mesmo endereço do servidor que entrega a página.
// Se abrir o HTML por outro servidor, troque para "http://localhost:8080/api"
const API_BASE = window.location.protocol.startsWith('http') ? '/api' : 'http://localhost:8080/api';

// Erro padronizado para a interface saber o que mostrar
class ErroApi extends Error {
    constructor(mensagem, status = 0, campos = {}) {
        super(mensagem);
        this.status = status;   // 0 = falha de conexão
        this.campos = campos;   // erros por campo (validação)
    }
}

// Faz a requisição e trata os retornos da API
async function requisicao(caminho, opcoes = {}) {
    let resposta;
    try {
        resposta = await fetch(API_BASE + caminho, {
            headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
            ...opcoes
        });
    } catch (e) {
        // Servidor desligado ou sem rede
        throw new ErroApi('Não foi possível conectar à API. Verifique se o servidor está em execução.');
    }

    // 204 No Content (ex.: exclusão) não tem corpo
    if (resposta.status === 204) {
        return null;
    }

    let corpo = null;
    try {
        corpo = await resposta.json();
    } catch (e) {
        corpo = null;
    }

    if (!resposta.ok) {
        const mensagem = (corpo && corpo.mensagem) || `Erro ${resposta.status} ao processar a requisição.`;
        throw new ErroApi(mensagem, resposta.status, (corpo && corpo.campos) || {});
    }
    return corpo;
}

// Atalhos para cada método HTTP
const api = {
    listar: (recurso) => requisicao(`/${recurso}`),
    buscar: (recurso, id) => requisicao(`/${recurso}/${id}`),
    criar: (recurso, dados) => requisicao(`/${recurso}`, { method: 'POST', body: JSON.stringify(dados) }),
    atualizar: (recurso, id, dados) => requisicao(`/${recurso}/${id}`, { method: 'PUT', body: JSON.stringify(dados) }),
    excluir: (recurso, id) => requisicao(`/${recurso}/${id}`, { method: 'DELETE' })
};
