const form = document.querySelector('.js-form');
const campo = document.querySelector('.js-mensagem');
const lista = document.querySelector('.js-mensagens');
const botaoEnviar = document.querySelector('.js-enviar');
const botaoNova = document.querySelector('.js-nova-conversa');
const erro = document.querySelector('.js-erro');

const CHAVE_CONVERSA = 'assistente.conversationId';
let conversationId = sessionStorage.getItem(CHAVE_CONVERSA);

form.addEventListener('submit', enviar);
botaoNova.addEventListener('click', novaConversa);
campo.addEventListener('keydown', (event) => {
    if (event.key === 'Enter' && !event.shiftKey) {
        event.preventDefault();
        form.requestSubmit();
    }
});

async function enviar(event) {
    event.preventDefault();
    const mensagem = campo.value.trim();
    if (!mensagem) {
        return;
    }

    limparErro();
    adicionarMensagem(mensagem, 'usuario');
    campo.value = '';
    definirCarregando(true);

    try {
        const resposta = await fetch('/api/assistente/chat', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ conversationId, mensagem })
        });

        if (!resposta.ok) {
            throw new Error('Não consegui falar com o assistente.');
        }

        const dados = await resposta.json();
        conversationId = dados.conversationId;
        sessionStorage.setItem(CHAVE_CONVERSA, conversationId);
        adicionarMensagem(dados.resposta || '(sem resposta)', 'ia');
    } catch (falha) {
        adicionarMensagem('Não recebi resposta. Tente de novo.', 'ia');
        mostrarErro(falha.message);
    } finally {
        definirCarregando(false);
        campo.focus();
    }
}

function adicionarMensagem(texto, autor) {
    const artigo = document.createElement('article');
    artigo.className = `mensagem mensagem--${autor}`;
    const paragrafo = document.createElement('p');
    paragrafo.textContent = texto;
    artigo.appendChild(paragrafo);
    lista.appendChild(artigo);
    lista.scrollTop = lista.scrollHeight;
}

function novaConversa() {
    conversationId = null;
    sessionStorage.removeItem(CHAVE_CONVERSA);
    lista.replaceChildren();
    adicionarMensagem('Nova conversa. Diga o que deseja cadastrar.', 'ia');
    limparErro();
    campo.focus();
}

function definirCarregando(carregando) {
    botaoEnviar.disabled = carregando;
    botaoNova.disabled = carregando;
    campo.disabled = carregando;
    botaoEnviar.textContent = carregando ? 'Pensando...' : 'Enviar';
}

function mostrarErro(mensagem) {
    erro.hidden = false;
    erro.textContent = mensagem;
}

function limparErro() {
    erro.hidden = true;
    erro.textContent = '';
}
