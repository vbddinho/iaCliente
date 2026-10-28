package com.example.llmIa;

/**
 * Contrato de retorno das ferramentas. O modelo lê o prefixo do status para decidir o próximo passo.
 */
public record ResultadoOperacao(Status status, String mensagem) {

    public enum Status {
        SUCESSO,
        ERRO,
        JA_EXISTE,
        NAO_ENCONTRADO
    }

    public static ResultadoOperacao sucesso(String mensagem) {
        return new ResultadoOperacao(Status.SUCESSO, mensagem);
    }

    public static ResultadoOperacao erro(String mensagem) {
        return new ResultadoOperacao(Status.ERRO, mensagem);
    }

    public static ResultadoOperacao jaExiste(String mensagem) {
        return new ResultadoOperacao(Status.JA_EXISTE, mensagem);
    }

    public static ResultadoOperacao naoEncontrado(String mensagem) {
        return new ResultadoOperacao(Status.NAO_ENCONTRADO, mensagem);
    }

    public String texto() {
        return status.name() + ": " + mensagem;
    }
}
