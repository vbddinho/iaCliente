package com.example.llmIa;

/**
 * Ponto de extensão de um tipo de cadastro no assistente.
 * O próximo cadastro é um {@code @Component} com métodos {@code @Tool} que implementa esta interface.
 * A regra de negócio fica no service daquele cadastro. Este contrato só descreve o fluxo ao modelo.
 */
public interface CadastroAssistido {

    String nome();

    String instrucoes();
}
