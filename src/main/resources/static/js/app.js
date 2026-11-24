// ==========================================================
// Lógica da interface: troca de abas, formulário, listagem,
// edição e exclusão. Tudo atualiza a tela sem recarregar a página.
// ==========================================================

// Estado atual da tela
const estado = {
    entidade: 'times',  // aba selecionada
    registros: [],      // registros carregados da API
    editandoId: null    // ID do registro em edição (null = novo cadastro)
};

// Atalhos para os elementos da página
const el = {
    abas: document.querySelectorAll('.aba'),
    tituloForm: document.getElementById('titulo-form'),
    form: document.getElementById('formulario'),
    campos: document.getElementById('campos'),
    erroForm: document.getElementById('erro-form'),
    btnSalvar: document.getElementById('btn-salvar'),
    btnCancelar: document.getElementById('btn-cancelar'),
    tituloLista: document.getElementById('titulo-lista'),
    contador: document.getElementById('contador'),
    busca: document.getElementById('busca'),
    btnAtualizar: document.getElementById('btn-atualizar'),
    estadoLista: document.getElementById('estado'),
    tabela: document.getElementById('tabela'),
    cabecalho: document.getElementById('tabela-cabecalho'),
    corpo: document.getElementById('tabela-corpo'),
    dialogo: document.getElementById('dialogo'),
    dialogoTexto: document.getElementById('dialogo-texto'),
    toasts: document.getElementById('toasts')
};

const config = () => ENTIDADES[estado.entidade];

// ---------- Mensagens rápidas ----------
function toast(mensagem, tipo = 'sucesso') {
    const div = document.createElement('div');
    div.className = `toast ${tipo === 'erro' ? 'toast--erro' : ''}`;
    div.textContent = mensagem;
    el.toasts.appendChild(div);
    setTimeout(() => div.remove(), 4000);
}

// ---------- Formulário ----------

// Monta os campos do formulário conforme a entidade selecionada
async function montarFormulario() {
    const cfg = config();
    el.campos.innerHTML = cfg.campos.map((c) => {
        const obrig = c.obrigatorio ? ' <span class="obrigatorio">*</span>' : '';
        const id = `campo-${c.nome}`;
        let controle;

        if (c.tipo === 'select') {
            controle = `<select id="${id}" name="${c.nome}" ${c.obrigatorio ? 'required' : ''}>
                <option value="">Selecione...</option>
                ${c.opcoes.map((o) => `<option value="${esc(o)}">${esc(o)}</option>`).join('')}
            </select>`;
        } else if (c.tipo === 'booleano') {
            controle = `<select id="${id}" name="${c.nome}" required>
                <option value="">Selecione...</option>
                <option value="true">Sim</option>
                <option value="false">Não</option>
            </select>`;
        } else if (c.tipo === 'relacao') {
            controle = `<select id="${id}" name="${c.nome}" ${c.obrigatorio ? 'required' : ''} disabled>
                <option value="">Carregando...</option>
            </select>`;
        } else {
            const attrs = [
                c.max ? `maxlength="${c.max}"` : '',
                c.min !== undefined ? `min="${c.min}"` : '',
                c.maxNum !== undefined ? `max="${c.maxNum}"` : '',
                c.passo ? `step="${c.passo}"` : '',
                c.obrigatorio ? 'required' : ''
            ].join(' ');
            controle = `<input id="${id}" name="${c.nome}" type="${c.tipo}" ${attrs}>`;
        }

        const dica = c.dica ? `<small class="campo__dica">${esc(c.dica)}</small>` : '';
        return `<div class="campo" data-campo="${c.nome}">
            <label for="${id}">${esc(c.rotulo)}${obrig}</label>
            ${controle}${dica}
        </div>`;
    }).join('');

    await carregarOpcoesRelacoes();
}

// Preenche os selects de relacionamento (ex.: lista de times para o jogador)
async function carregarOpcoesRelacoes() {
    const relacoes = config().campos.filter((c) => c.tipo === 'relacao');
    const cache = {};

    for (const c of relacoes) {
        const select = el.form.elements[c.nome];
        try {
            cache[c.recurso] = cache[c.recurso] || await api.listar(c.recurso);
            const itens = cache[c.recurso];
            const vazio = c.obrigatorio ? 'Selecione...' : c.textoVazio;
            select.innerHTML = `<option value="">${esc(vazio)}</option>` +
                itens.map((i) => `<option value="${i.id}">${esc(c.textoOpcao(i))}</option>`).join('');
            select.disabled = false;
            if (c.obrigatorio && itens.length === 0) {
                select.innerHTML = `<option value="">Nenhum registro cadastrado</option>`;
            }
        } catch (erro) {
            select.innerHTML = '<option value="">Falha ao carregar opções</option>';
        }
    }
}

// Lê os valores do formulário e monta o JSON esperado pela API
function lerFormulario() {
    const dados = {};
    for (const c of config().campos) {
        const valor = el.form.elements[c.nome].value.trim();
        if (c.tipo === 'number') {
            dados[c.nome] = valor === '' ? null : Number(valor);
        } else if (c.tipo === 'booleano') {
            dados[c.nome] = valor === '' ? null : valor === 'true';
        } else if (c.tipo === 'relacao') {
            dados[c.nome] = valor === '' ? null : { id: Number(valor) };
        } else {
            dados[c.nome] = valor;
        }
    }
    return dados;
}

// Coloca os dados de um registro no formulário (modo edição)
function preencherFormulario(registro) {
    for (const c of config().campos) {
        const campo = el.form.elements[c.nome];
        const valor = registro[c.nome];
        if (c.tipo === 'relacao') {
            campo.value = valor ? valor.id : '';
        } else if (c.tipo === 'booleano') {
            campo.value = valor === null || valor === undefined ? '' : String(valor);
        } else {
            campo.value = valor ?? '';
        }
    }
}

// Validação no navegador antes de enviar (a API valida de novo)
function validarNoNavegador() {
    const erros = {};
    for (const c of config().campos) {
        const campo = el.form.elements[c.nome];
        if (!campo.checkValidity()) {
            if (campo.validity.valueMissing) erros[c.nome] = 'Campo obrigatório';
            else if (campo.validity.rangeUnderflow) erros[c.nome] = `O valor mínimo é ${c.min}`;
            else if (campo.validity.rangeOverflow) erros[c.nome] = `O valor máximo é ${c.maxNum}`;
            else if (campo.validity.stepMismatch) erros[c.nome] = 'Use no máximo duas casas decimais';
            else erros[c.nome] = 'Valor inválido';
        }
    }
    return erros;
}

function mostrarErrosCampos(erros) {
    for (const [nome, mensagem] of Object.entries(erros)) {
        const bloco = el.campos.querySelector(`[data-campo="${nome}"]`);
        if (!bloco) continue;
        bloco.classList.add('invalido');
        const span = document.createElement('small');
        span.className = 'campo__erro';
        span.textContent = mensagem;
        bloco.appendChild(span);
    }
}

function limparErros() {
    el.erroForm.hidden = true;
    el.campos.querySelectorAll('.campo').forEach((b) => b.classList.remove('invalido'));
    el.campos.querySelectorAll('.campo__erro').forEach((s) => s.remove());
}

function mostrarErroForm(mensagem) {
    el.erroForm.textContent = mensagem;
    el.erroForm.hidden = false;
}

// Volta o formulário para o modo "novo cadastro"
function resetarFormulario() {
    estado.editandoId = null;
    el.form.reset();
    limparErros();
    el.tituloForm.textContent = `Nov${config().genero} ${config().singular}`;
    el.btnSalvar.textContent = 'Cadastrar';
    el.btnCancelar.hidden = true;
}

// Envio do formulário: POST para novo, PUT para edição
async function salvar(evento) {
    evento.preventDefault();
    limparErros();

    const errosLocais = validarNoNavegador();
    if (Object.keys(errosLocais).length > 0) {
        mostrarErrosCampos(errosLocais);
        mostrarErroForm('Corrija os campos destacados antes de continuar.');
        return;
    }

    const dados = lerFormulario();
    const cfg = config();
    el.btnSalvar.disabled = true;
    el.btnSalvar.textContent = 'Salvando...';

    try {
        if (estado.editandoId) {
            await api.atualizar(estado.entidade, estado.editandoId, dados);
            toast(`${capitalizar(cfg.singular)} atualizad${cfg.genero} com sucesso.`);
        } else {
            await api.criar(estado.entidade, dados);
            toast(`${capitalizar(cfg.singular)} cadastrad${cfg.genero} com sucesso.`);
        }
        resetarFormulario();
        await carregarLista();
    } catch (erro) {
        mostrarErrosCampos(erro.campos || {});
        mostrarErroForm(erro.message);
    } finally {
        el.btnSalvar.disabled = false;
        if (el.btnSalvar.textContent === 'Salvando...') {
            el.btnSalvar.textContent = estado.editandoId ? 'Salvar alterações' : 'Cadastrar';
        }
    }
}

// ---------- Listagem ----------

async function carregarLista() {
    const cfg = config();
    el.tituloLista.firstChild.textContent = `${cfg.plural} cadastrad${cfg.genero}s `;
    el.contador.textContent = '';
    el.tabela.hidden = true;
    el.estadoLista.className = 'estado';
    el.estadoLista.innerHTML = '<span class="carregando">Carregando registros...</span>';

    try {
        estado.registros = await api.listar(estado.entidade);
        renderizarTabela();
    } catch (erro) {
        el.estadoLista.className = 'estado estado--erro';
        el.estadoLista.innerHTML = `<p>${esc(erro.message)}</p>
            <button class="btn btn--secundario" type="button" onclick="carregarLista()">Tentar novamente</button>`;
    }
}

function renderizarTabela() {
    const cfg = config();
    const termo = el.busca.value.trim().toLowerCase();

    // Filtro simples pelo texto exibido nas colunas
    const filtrados = estado.registros.filter((r) => {
        if (!termo) return true;
        return cfg.colunas.map((c) => String(c.valor(r)).replace(/<[^>]+>/g, ''))
            .join(' ').toLowerCase().includes(termo);
    });

    el.contador.textContent = `(${estado.registros.length})`;

    if (estado.registros.length === 0) {
        el.tabela.hidden = true;
        el.estadoLista.className = 'estado';
        el.estadoLista.innerHTML = `<p>Nenhum${cfg.genero === 'a' ? 'a' : ''} ${cfg.singular} cadastrad${cfg.genero} ainda.</p>
            <p>Use o formulário ao lado para adicionar o primeiro registro.</p>`;
        return;
    }
    if (filtrados.length === 0) {
        el.tabela.hidden = true;
        el.estadoLista.className = 'estado';
        el.estadoLista.innerHTML = `<p>Nenhum resultado para "${esc(termo)}".</p>`;
        return;
    }

    el.estadoLista.innerHTML = '';
    el.cabecalho.innerHTML = cfg.colunas.map((c) => `<th>${esc(c.rotulo)}</th>`).join('') + '<th><span class="sr-only">Ações</span></th>';
    el.corpo.innerHTML = filtrados.map((r) => `
        <tr>
            ${cfg.colunas.map((c) => `<td data-label="${esc(c.rotulo)}">${c.valor(r)}</td>`).join('')}
            <td class="col-acoes">
                <button class="btn btn--secundario btn--pequeno" type="button" data-acao="editar" data-id="${r.id}">Editar</button>
                <button class="btn btn--secundario btn--pequeno" type="button" data-acao="excluir" data-id="${r.id}">Excluir</button>
            </td>
        </tr>`).join('');
    el.tabela.hidden = false;
}

// ---------- Edição e exclusão ----------

async function editar(id) {
    const cfg = config();
    limparErros();
    try {
        // Busca o registro atualizado pelo ID (GET /api/{recurso}/{id})
        const registro = await api.buscar(estado.entidade, id);
        estado.editandoId = id;
        preencherFormulario(registro);
        el.tituloForm.textContent = `Editando ${cfg.singular} #${id}`;
        el.btnSalvar.textContent = 'Salvar alterações';
        el.btnCancelar.hidden = false;
        el.form.scrollIntoView({ behavior: 'smooth', block: 'start' });
        el.form.elements[cfg.campos[0].nome].focus();
    } catch (erro) {
        toast(erro.message, 'erro');
        if (erro.status === 404) carregarLista();
    }
}

function confirmar(texto) {
    return new Promise((resolve) => {
        el.dialogoTexto.textContent = texto;
        el.dialogo.returnValue = '';
        el.dialogo.showModal();
        el.dialogo.addEventListener('close', () => resolve(el.dialogo.returnValue === 'confirmar'), { once: true });
    });
}

async function excluir(id) {
    const cfg = config();
    const registro = estado.registros.find((r) => r.id === id);
    const nome = registro ? cfg.descricao(registro) : `#${id}`;

    if (!(await confirmar(`Deseja realmente excluir ${cfg.genero} ${cfg.singular} "${nome}"? Esta ação não pode ser desfeita.`))) {
        return;
    }
    try {
        await api.excluir(estado.entidade, id);
        toast(`${capitalizar(cfg.singular)} excluíd${cfg.genero} com sucesso.`);
        if (estado.editandoId === id) resetarFormulario();
        await carregarLista();
    } catch (erro) {
        toast(erro.message, 'erro');
    }
}

// ---------- Troca de abas ----------

async function trocarEntidade(nome) {
    estado.entidade = nome;
    el.abas.forEach((a) => a.classList.toggle('ativa', a.dataset.entidade === nome));
    el.busca.value = '';
    await montarFormulario();
    resetarFormulario();
    carregarLista();
}

function capitalizar(texto) {
    return texto.charAt(0).toUpperCase() + texto.slice(1);
}

// ---------- Eventos ----------
el.abas.forEach((aba) => aba.addEventListener('click', () => trocarEntidade(aba.dataset.entidade)));
el.form.addEventListener('submit', salvar);
el.btnCancelar.addEventListener('click', resetarFormulario);
el.btnAtualizar.addEventListener('click', carregarLista);
el.busca.addEventListener('input', () => estado.registros.length && renderizarTabela());

// Delegação de eventos para os botões de cada linha
el.corpo.addEventListener('click', (e) => {
    const botao = e.target.closest('button[data-acao]');
    if (!botao) return;
    const id = Number(botao.dataset.id);
    if (botao.dataset.acao === 'editar') editar(id);
    if (botao.dataset.acao === 'excluir') excluir(id);
});

// Inicia na aba de times
trocarEntidade('times');
