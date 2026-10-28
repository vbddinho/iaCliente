# Somente CPU
docker run -d --name ollama -v ollama:/root/.ollama -p 11434:11434 ollama/ollama

# Com GPU NVIDIA (requer nvidia-container-toolkit)
docker run -d --gpus=all --name ollama -v ollama:/root/.ollama -p 11434:11434 ollama/ollama

docker exec -it ollama ollama pull llama3.1


| Configuração | Efeito |
|---|---|
| `OLLAMA_MAX_LOADED_MODELS=1` | Permite manter apenas um modelo carregado por vez. |
| `OLLAMA_NUM_PARALLEL=1` | Limita o processamento a uma solicitação simultânea por modelo. |
| `OLLAMA_KEEP_ALIVE=2m` | Mantém o modelo carregado por aproximadamente dois minutos após o uso, antes de descarregá-lo. |
| `mem_limit: 6g` | Limita a memória RAM disponível para o container. |
| `memswap_limit: 6g` | Impede que o container utilize memória swap adicional além do limite configurado. |


spring.ai.ollama.base-url=http://localhost:11434

# Baixa o modelo automaticamente se não existir (opcional se você já fez o ollama pull)
spring.ai.ollama.init.pull-model-strategy=when_missing

spring.ai.ollama.chat.options.model=llama3.1
spring.ai.ollama.chat.options.temperature=0.2
spring.ai.ollama.chat.options.num-ctx=8192

