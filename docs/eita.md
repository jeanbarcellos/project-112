### 1️⃣ retrieve()

O método .retrieve() executa a requisição e permite que você manipule a resposta.

Ele é um atalho para processamento padrão, evitando código boilerplate.

📌 Exemplo de uso:

```java
WebClient webClient = WebClient.create("https://api.externa.com");

Mono<String> response = webClient.get()
    .uri("/dados")
    .retrieve()
    .bodyToMono(String.class);
```

🔍 O que acontece aqui?

- `.retrieve()` → Envia a requisição e trata respostas 200 OK automaticamente.
- `.bodyToMono(String.class)` → Converte o corpo da resposta para um `Mono<String>`.

📌 Observação:

Se o servidor retornar um erro (ex: 404 Not Found ou 500 Internal Server Error), o retrieve() lança uma WebClientResponseException automaticamente.

---

### 2️⃣ bodyToMono()

Este método converte a resposta da requisição em um objeto reativo `Mono<T>`.

O que é um `Mono<T>`?
🔹 Representa uma resposta única, como um JSON convertido para um objeto.

📌 Exemplo:

```java
Mono<DadosResponse> response = webClient.get()
    .uri("/dados")
    .retrieve()
    .bodyToMono(DadosResponse.class);
```

🔍 O que acontece aqui?

- A resposta JSON será desserializada para a classe `DadosResponse`.
- Como `Mono<T>` representa um único valor, usamos block() para obter o resultado de forma síncrona (apenas para testes!):

```java
DadosResponse resultado = response.block(); // Bloqueia até a resposta chegar!
```

📌 Evite usar `.block()` em produção, pois ele bloqueia a thread e quebra o modelo reativo.

---

### 3️⃣ exchangeToMono()

Esse método é mais avançado que retrieve(). Ele permite tratar toda a resposta HTTP, incluindo o código de status, cabeçalhos e corpo.

📌 Diferente de retrieve(), ele não lança exceções automaticamente!

Você tem que lidar com os erros manualmente.

📌 Exemplo de uso:

```java
Mono<DadosResponse> response = webClient.get()
    .uri("/dados")
    .exchangeToMono(clientResponse -> {
        if (clientResponse.statusCode().is2xxSuccessful()) {
            return clientResponse.bodyToMono(DadosResponse.class);
        } else {
            return Mono.error(new RuntimeException("Erro ao buscar dados"));
        }
    });
```

🔍 O que acontece aqui?

- `.exchangeToMono(clientResponse -> { ... })` → Permite acessar o status HTTP.
- Se a resposta for **200 OK**, processamos o corpo normalmente (`bodyToMono`).
- Se for um erro (`400` ou `500`), retornamos um Mono.error() para tratamento manual.
