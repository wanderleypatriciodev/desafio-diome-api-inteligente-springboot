# API Inteligente de Orçamento com Spring AI

API de orçamento que recebe comandos de voz, transforma o áudio em texto, usa um modelo de linguagem para entender a intenção e executa funções reais da aplicação (criar transação, listar por categoria e consultar saldo).

## O que o projeto faz

O fluxo principal é:

1. O cliente envia um arquivo de áudio para `POST /transactions/ai`.
2. O áudio é transcrito em texto (Speech-to-Text).
3. O `ChatClient` interpreta o comando e escolhe uma ferramenta (`@Tool`).
4. A ferramenta chama um caso de uso: persistir transação, listar por categoria ou calcular saldo.
5. A resposta em texto é convertida em áudio (Text-to-Speech) e devolvida em MP3.

Os mesmos casos de uso também estão expostos em endpoints REST, para consulta e cadastro sem áudio.

## Tecnologias

- Java 21
- Spring Boot
- Spring AI (ChatClient, Tool Calling, Transcription e Speech)
- OpenAI (chat, Whisper e TTS)
- Spring Data JPA
- MySQL (via Docker Compose)
- Gradle
- JUnit 5

## Como executar

Pré-requisitos: Java 21, Docker e uma chave da OpenAI.

No PowerShell:

```powershell
$env:OPENAI_API_KEY="sua_chave_aqui"
cd 05-spring-ai
.\gradlew.bat bootRun
```

O Spring Boot sobe o MySQL definido em `compose.yml` automaticamente (plugin Docker Compose).

## Endpoints

| Método | Caminho | Descrição |
| --- | --- | --- |
| `POST` | `/transactions` | Cria uma transação (JSON) |
| `GET` | `/transactions/{category}` | Lista transações da categoria (`GROCERIES`, `PHARMA`, `AUTO`) |
| `GET` | `/transactions/balance` | Retorna o saldo total em centavos |
| `POST` | `/transactions/ai` | Envia áudio (`multipart/form-data`, campo `file`) e recebe MP3 |

Exemplo de criação:

```http
POST /transactions
Content-Type: application/json

{
  "description": "Compra no mercado",
  "category": "GROCERIES",
  "amount": 4590
}
```

`amount` é sempre em **centavos** (R$ 45,90 = `4590`).

Consulta de saldo:

```http
GET /transactions/balance
```

Resposta:

```json
{
  "totalAmountInCents": 4590
}
```

## Melhoria implementada

Além do fluxo das aulas, este projeto ganhou duas evoluções pequenas e alinhadas ao desafio:

1. **Nova consulta financeira + Tool Calling**  
   Caso de uso `CalculateTotalBalanceUseCase`, registrado como ferramenta `calculate-total-balance`.  
   A IA consegue responder perguntas como “qual é o meu saldo?”.  
   O mesmo cálculo está em `GET /transactions/balance`.

2. **Validação antes de salvar**  
   `PersistTransactionUseCase` recusa descrição vazia, valor menor ou igual a zero e categoria nula.  
   Erros de validação voltam HTTP 400.

O prompt do sistema foi atualizado para orientar a IA a usar a ferramenta de saldo e a falar valores em reais para a pessoa.

## Como testar o fluxo principal

Testes unitários dos casos de uso (não precisam de OpenAI nem Docker):

```powershell
cd 05-spring-ai
.\gradlew.bat test --tests dio.budgeting.application.*
```

Fluxo REST (com a aplicação no ar):

1. Criar uma transação com `POST /transactions`.
2. Consultar `GET /transactions/GROCERIES`.
3. Consultar `GET /transactions/balance`.
4. Tentar criar transação com `amount: 0` e conferir o erro 400.

Fluxo com IA (precisa de `OPENAI_API_KEY`):

1. Enviar um áudio em português no campo `file` de `POST /transactions/ai`.
2. Exemplos de fala: “Gastei 45 reais no mercado” ou “Qual é o meu saldo?”.
3. A API devolve um MP3 com a resposta.

Os testes de integração com OpenAI (`OpenAi*IT`, `ToolCallingIT`) só rodam quando a variável `OPENAI_API_KEY` está definida.

## O que foi aprendido

- Tool Calling liga o modelo a casos de uso reais, sem misturar regra de negócio no controller.
- Áudio entra como transcrição e sai como TTS, mas o núcleo continua sendo persistência e consulta.
- Uma evolução pequena (saldo + validação) já mostra o ciclo completo: domínio, caso de uso, ferramenta, endpoint, teste e documentação.

## Estrutura

- `src/main/java/dio/budgeting/domain` — modelo e contrato do repositório
- `src/main/java/dio/budgeting/application` — casos de uso usados pelo REST e pelas tools
- `src/main/java/dio/budgeting/infrastructure` — HTTP, JPA e integração com Spring AI

## Referências

- [Spring AI](https://docs.spring.io/spring-ai/reference/index.html)
- [Trilha DIO Spring Boot](https://github.com/digitalinnovationone/dio-spring-boot-learning-track)
