package com.example.llmIa;

import org.springframework.stereotype.Service;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;

@Service 
public class AssistenteCadastroService {

    private static final String SYSTEM_PROMPT = """
            Você é um assistente de cadastros de um sistema interno. Responda sempre em português do Brasil, de forma curta.

            Tipos de cadastro disponíveis: 1) Cliente  (no momento, apenas este).

            REGRAS:
            - Se o usuário disser que quer fazer um cadastro sem especificar o tipo, pergunte o que deseja
              cadastrar e apresente as opções disponíveis.
            - Ao escolher 'Cliente', colete UM dado por vez, nesta ordem: CNPJ, nome, rua, número, CEP.
            - Se o usuário já informou alguns dados na mesma mensagem (ex: "Cadastre o cliente CNPJ ..."),
              aproveite-os e pergunte apenas o que falta.
            - Quando tiver TODOS os dados, chame a ferramenta cadastrarCliente.
            - NUNCA invente valores. NUNCA chame a ferramenta com campos faltando.
            - Se a ferramenta retornar ERRO, explique o problema ao usuário e peça a correção.
            - Se o registro já existe, pergunte ao usuário se deseja atualizar os dados.
            - Após coletar os dados do cadastro, confirme os dados com o usuário e pergunte se está correto.
            - se estiver correto, confirme o cadastro e pergunte se deseja algo mais.
            - Se houver algum alteração peça para o usuário informar os dados corretos e chame a ferramenta cadastrarCliente novamente.
            
            """;

    private final ChatClient chatClient;

    public AssistenteCadastroService(ChatClient.Builder builder, ClienteTools clienteTools) {

        // Memória por conversa: o LLM é stateless, então o histórico precisa ser reenviado a cada chamada.
        ChatMemory memory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .maxMessages(20)
                .build();

        this.chatClient = builder
                .defaultSystem(SYSTEM_PROMPT)
                .defaultTools(clienteTools)   // registra os métodos @Tool
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(memory).build())
                .build();
    }

    public String conversar(String conversationId, String mensagem) {
        return chatClient.prompt()
                .user(mensagem)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()      // Spring AI executa o loop: modelo -> tool_call -> método Java -> resultado -> modelo
                .content();
    }
    
}
