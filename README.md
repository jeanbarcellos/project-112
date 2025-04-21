# Project 112 - Reactive X Imperative

Projeto para testes do WebFlux e WebClient

## Paradigmas

Resumo claro e direto sobre Programação Reativa (WebFlux) versus Programação Imperativa (Servlet - MVC tradicional) no contexto do Spring Boot. Incluí exemplos, motivações, orientações e explicações sobre os starters spring-boot-starter-web e spring-boot-starter-webflux.

### 🔁 Programação Reativa (Spring WebFlux)

#### 🧠 Conceito

A programação reativa é baseada no modelo assíncrono, orientado a eventos e não bloqueante. Usa menos threads e é eficiente para aplicações que lidam com alta concorrência e I/O intensivo, como chamadas externas, bancos reativos, etc.

#### 📦 Starter: spring-boot-starter-webflux

- Baseado no Reactor (com Mono, Flux)
- Usa Netty como servidor por padrão (mas pode usar Tomcat ou Jetty)
- Suporta WebClient (não bloqueante) ao invés de RestTemplate
- Projetado para end-to-end reatividade: controller, service, repositório, etc.

#### ✅ Quando Usar

Alta concorrência e escalabilidade são essenciais

Muitas operações assíncronas (ex: chamadas a APIs externas)

Usando banco de dados reativo (R2DBC, Mongo Reactive, etc)

Você tem domínio sobre todo o stack (para evitar .block())

#### 🚫 Quando Evitar

- A aplicação precisa consumir muitos serviços ou bancos bloqueantes
- A equipe não está confortável com o paradigma reativo
- Necessidade baixa de concorrência ou performance

#### 🧩 Exemplo

Endpoint:

```java
@GetMapping("/categorias")
public Flux<Category> getAll() {
    return categoryService.findAll(); // Flux<Category>
}
```

Client:

```java
public Flux<Category> findAll() {
    return webClient.get()
        .uri("/external-api/categories")
        .retrieve()
        .bodyToFlux(Category.class);
}

```

### 🧱 Programação Imperativa (Spring MVC)

#### 🧠 Conceito

Segue o modelo tradicional de programação sequencial/bloqueante. Cada requisição é tratada por uma thread que espera a resposta antes de continuar.

#### 📦 Starter: spring-boot-starter-web

- Baseado em Servlet API (com Tomcat como padrão)
- Usa RestTemplate para chamadas HTTP
- Suporta anotações tradicionais como @RestController, @Service, etc.

#### ✅ Quando Usar

- Aplicações tradicionais que usam JDBC ou chamadas bloqueantes
- Equipe já familiarizada com Spring MVC
- Complexidade e escala de requisições é média/baixa
- Integração com bibliotecas ou SDKs que são bloqueantes

#### 🚫 Quando Evitar

- Precisa de alta escalabilidade com poucas threads
- Operações I/O intensivas (como APIs externas)
- A arquitetura é voltada para reatividade (por ex, integra com Kafka, WebSocket, etc.)

#### 🧩 Exemplo

Endpoint:

```java
@GetMapping("/categorias")
public List<Category> getAll() {
    return categoryService.findAll(); // List<Category>
}
```

Client

```java
public List<Category> findAll() {
    return restTemplate.getForObject("/external-api/categories", List.class);
}
```

### ⚠️ Conflito entre Web e WebFlux

Se ambos os starters forem adicionados, o Spring Boot prioriza `spring-boot-starter-web` e a aplicação rodará no modo **Servlet**, ou seja, imperativo.

Se quiser usar WebFlux de verdade, evite adicionar o `spring-boot-starter-web`.

### 🚀 Dicas e Boas Práticas

| Situação                              | Melhor Escolha                              |
| ------------------------------------- | ------------------------------------------- |
| API tradicional com JDBC ou JPA       | `spring-boot-starter-web`                   |
| WebSocket, SSE, Streams               | `spring-boot-starter-webflux`               |
| Chamadas a muitos serviços externos   | `spring-boot-starter-webflux`               |
| Time quer simplicidade e legibilidade | `spring-boot-starter-web`                   |
| Projeto altamente escalável e moderno | `spring-boot-starter-webflux` (com cuidado) |

### 📝 Conclusão

| Característica       | WebFlux (Reativo)           | Web (Imperativo)         |
| -------------------- | --------------------------- | ------------------------ |
| Modelo               | Assíncrono e não bloqueante | Síncrono e bloqueante    |
| Threads              | Poucas, escalável           | Uma por requisição       |
| Tipos de retorno     | Mono<T>, Flux<T>            | T, List<T>               |
| Servidor             | Netty (padrão), Jetty       | Tomcat (padrão)          |
| Performance (escala) | Alta, se bem usado          | Boa, mas menos escalável |
| Complexidade         | Maior                       | Menor                    |
