## retrive, bodyToMono

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

📌 Comparação Rápida

| Método           | O que faz?                                              | Quando usar?                                |
| ---------------- | ------------------------------------------------------- | ------------------------------------------- |
| retrieve()       | Trata respostas 2xx automaticamente e converte o corpo. | ✅ Uso simples e direto.                    |
| bodyToMono()     | Converte a resposta HTTP para um objeto.                | ✅ Quando só precisa do corpo da resposta.  |
| exchangeToMono() | Dá acesso ao status HTTP, cabeçalho                     | ✅ Quando precisa tratar erros manualmente. |

---

## Diferença entre Mono e Flux

📌 Definições

- `Mono<T>` → Representa zero ou um elemento.
- `Flux<T>` → Representa zero, um ou múltiplos elementos.

Exemplos práticos

### Mono - Trabalhando com um único elemento

```java
import reactor.core.publisher.Mono;

public class MonoExample {
    public static void main(String[] args) {
        Mono<String> mono = Mono.just("Jean Barcellos");

        mono.subscribe(
            value -> System.out.println("Recebido: " + value), // onNext
            error -> System.err.println("Erro: " + error),     // onError
            () -> System.out.println("Concluído!")             // onComplete
        );
    }
}
```

📌 Saída:

```vbnet
Recebido: Jean Barcellos
Concluído!
```

### Flux - Trabalhando com múltiplos elementos

```java
import reactor.core.publisher.Flux;

public class FluxExample {
    public static void main(String[] args) {
        Flux<String> flux = Flux.just("Jean", "Carlos", "Barcellos");

        flux.subscribe(
            value -> System.out.println("Recebido: " + value),
            error -> System.err.println("Erro: " + error),
            () -> System.out.println("Concluído!")
        );
    }
}
```

📌 Saída:

```vbnet
Recebido: Jean
Recebido: Carlos
Recebido: Barcellos
Concluído!
```

---

Um paralelo que podemos fazer é que o Mono possui uma abstração similar à classe Optional <T> que temos no Java, e o Flux se aproxima mais de uma List<T>. Ambos são implementações criadas no Project Reactor da interface Publisher da especificação Reactive Streams.

---

https://medium.com/devspoint/project-reactor-um-pouco-sobre-mono-e-flux-6f5dbf09ac49

4 formas de criar um Mono.

- `just` -> que emitirá “apenas” o conteúdo passado por parâmetro.
- `empty` -> que cria um Publisher que não emite eventos, por não ter nenhum,
- `justOrEmpty` -> que acaba sendo uma junção dos 2 primeiros métodos, onde ele pode ter um conteúdo passado por parâmetro ou não.
- `defer` que possui uma sintaxe diferente, onde precisamos fornecer um Mono para ele.

---

Mono vs. Flux

Mono e Flux são implementações da interface Publisher . Em termos simples, podemos dizer que, quando estamos fazendo algo como um cálculo ou uma solicitação a um banco de dados ou serviço externo, e esperando no máximo um resultado, devemos usar Mono .

Quando esperamos vários resultados de nossa computação, banco de dados ou chamada de serviço externo, devemos usar o Flux .

Mono é mais relacionado à classe Optional em Java, pois contém 0 ou 1 valor, e Flux é mais relacionado à List , pois pode ter N valores.

---

🔥 Resumo Rápido

| Conceito                   | Mono                                          | Flux                            |
| -------------------------- | --------------------------------------------- | ------------------------------- |
| Elementos retornados       | 0 ou 1                                        | 0 a N                           |
| Uso comum                  | Busca única (findById())                      | Listagem (findAll())            |
| Operações de Transformação | map(), flatMap()                              | map(), flatMap(), filter()      |
| Tatamento de erro          | onErrorReturn(), onErrorResume(), doOnError() | onErrorContinue(), onErrorMap() |
