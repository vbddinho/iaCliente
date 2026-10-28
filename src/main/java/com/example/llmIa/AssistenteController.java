package com.example.llmIa;

import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/assistente")
public class AssistenteController {

    public record ChatRequest(String conversationId, String mensagem) {}
    public record ChatResponse(String conversationId, String resposta) {}

    private final AssistenteCadastroService service;

    public AssistenteController(AssistenteCadastroService service) {
        this.service = service;
    }

     @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest req) {
        // Se o cliente não enviar um id, inicia uma nova conversa.
        String id = (req.conversationId() == null || req.conversationId().isBlank())
                ? UUID.randomUUID().toString() : req.conversationId();
        return new ChatResponse(id, service.conversar(id, req.mensagem()));
    }
    
}
