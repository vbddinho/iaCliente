package com.example.llmIa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;



@Component 
public class ClienteTools {

    private static final Logger log = LoggerFactory.getLogger(ClienteTools.class);
    private final ClienteRepository repository;
    
    public ClienteTools(ClienteRepository repository) {
        this.repository = repository;
    }

     /**
     * A description é o "contrato" que o LLM lê para decidir QUANDO chamar.
     * Seja explícito: só chamar com todos os campos reais, sem inventar.
     */
     @Tool(description = """
        Cadastra um novo cliente no sistema. Só chame esta ferramenta quando o usuário
        tiver informado TODOS os campos (cnpj, nome, rua, numero e cep).
        NUNCA invente ou presuma valores ausentes: pergunte ao usuário antes.
        """)
public String cadastrarCliente(
        @ToolParam(description = "CNPJ do cliente, 14 dígitos, com ou sem pontuação") String cnpj,
        @ToolParam(description = "Razão social ou nome do cliente") String nome,
        @ToolParam(description = "Nome da rua/logradouro") String rua,
        @ToolParam(description = "Número do endereço (texto, ex: '120' ou 'S/N')") String numero,
        @ToolParam(description = "CEP com 8 dígitos, com ou sem hífen") String cep) {

    log.info("Tool chamada: cnpj={}, nome={}, rua={}, numero={}, cep={}", cnpj, nome, rua, numero, cep);

    // Nunca confie cegamente nos parâmetros do LLM: normalize e valide em Java.
    String cnpjLimpo = apenasDigitos(cnpj);
    String cepLimpo  = apenasDigitos(cep);

    // O retorno de erro volta ao modelo, que o usa para pedir a correção ao usuário.
    if (cnpjLimpo.length() != 14) return "ERRO: CNPJ inválido (precisa ter 14 dígitos). Peça o CNPJ correto ao usuário.";
    if (cepLimpo.length() != 8)   return "ERRO: CEP inválido (precisa ter 8 dígitos). Peça o CEP correto ao usuário.";
    if (isBlank(nome) || isBlank(rua) || isBlank(numero))
        return "ERRO: campos obrigatórios ausentes. Pergunte ao usuário o que falta.";
    if (repository.existsByCnpj(cnpjLimpo)) 
    {
        Cliente cliente = repository.findByCnpj(cnpjLimpo).get();

        return atualizarCliente(cliente.getCnpj(), cliente.getNome(), cliente.getRua(), cliente.getRua(), cliente.getCep());
    }
        

    repository.save(new Cliente(cnpjLimpo,nome.trim(),rua,numero,cepLimpo));
    return "SUCESSO: cliente '%s' (CNPJ %s) cadastrado.".formatted(nome.trim(), cnpjLimpo);
}

@Tool(description="""
        Atualiza cliente existente na base de dados, só chame esta ferramenta quando o usuário informar um cnpj existente.
        NUNCA invente ou presuma valores ausentes: pergunte ao usuário antes.
        """)
public String atualizarCliente(
        @ToolParam(description = "CNPJ do cliente, 14 dígitos, com ou sem pontuação") String cnpj,
        @ToolParam(description = "Razão social ou nome do cliente") String nome,
        @ToolParam(description = "Nome da rua/logradouro") String rua,
        @ToolParam(description = "Número do endereço (texto, ex: '120' ou 'S/N')") String numero,
        @ToolParam(description = "CEP com 8 dígitos, com ou sem hífen") String cep) {
    log.info("Tool chamada: cnpj={}, nome={}, rua={}, numero={}, cep={}", cnpj, nome, rua, numero, cep);

    



    return "SUCESSO: cliente '%s' (CNPJ %s) atualizado.".formatted(nome.trim(), cnpj);
        }


private static String apenasDigitos(String s) { return s == null ? "" : s.replaceAll("\\D", ""); }
private static boolean isBlank(String s) { return s == null || s.isBlank(); }
}
