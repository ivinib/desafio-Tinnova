# API de gerenciamento de veiculos(PT-BR)

Esse projeto implementa a RESTful API para gerenciar veiculos, com as funções basicas do CRUD, filtragem, geração de relatório e autenticação

## Como rodar a aplicação

É possivel de rodar a aplicação de duas formas, localmente e com Docker

### Pré-requisitos

*   **Java Development Kit (JDK) 21**
*   **Apache Maven 3.x**
*   **Docker** (Apenas se for rodar com Docker)

### 1. Rodando localmente

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/ivinib/desafio-Tinnova.git
    cd desafio-Tinnova
    ```
2.  **Faz o build do projeto:**
    ```bash
    mvn clean install
    ```
3.  **Inicia o servidor Redis:**
    Para o funcionamento da parte de cache da cotação do docker, é necessario ter uma instância do Redis rodando. O jeito mais fácil é com o seguinte comando no Docker:
    ```bash
    docker run --name some-redis -p 6379:6379 -d redis:7-alpine
    ```
    Ou o cache pode ser desabilitado com essa propriedade: `spring.cache.type=none` adicionada no arquivo `application.properties`.
4.  **Rodando a aplicação Spring Boot:**
    ```bash
    mvn spring-boot:run
    ```
    A aplicação vai iniciar no link: `http://localhost:8080`.

### 2. Rodando com Docker (recomendado)

Esse metodo vai fazer o build da aplicação com os recursos necessarios dentro de um container Docker com um comando

1.  **Tenha o Docker instalado e configurado** na sua maquina.
2.  **Vá para o diretorio padrão do projeto** onde `Dockerfile` e `docker-compose.yml` estão localizados.
3.  **Faça o build e inicie os serviços:**
    ```bash
    docker-compose up --build -d
    ```
    *   `--build`: Faz o build da imagem Docker para a aplicação
    *   `-d`: Roda os containers em segundo plano.
        Depois de finalizar o build e inicialização, a aplicação estará disponivel no link: `http://localhost:8080`.

4.  **Para parar os serviços:**
    ```bash
    docker-compose down
    ```

### Rodando os testes automatizados

Comando para executar os testes automatizados:

```bash
mvn test
```

### Front end ###

Um front-end basico em Angular para testar a funcionalidade dessa aplicação pode ser encontrada no repositorio: `https://github.com/ivinib/desafio-Tinnova-front`.

### Swagger ###
Com a aplicação rodando o link para a documentação do Swagger: `http://localhost:8080/swagger-ui/index.html`.

---
#Vehicle Management API( EN - US)

This project implements a RESTful API for managing vehicles, including features for CRUD operations, filtering, reporting, and secure authentication.

## How to Run the Application

You have two main options to run this application: locally or using Docker Compose.

### Prerequisites

*   **Java Development Kit (JDK) 21**
*   **Apache Maven 3.x**
*   **Docker** (if running with Docker Compose)

### 1. Running Locally

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/ivinib/desafio-Tinnova.git
    cd desafio-Tinnova
    ```
2.  **Build the project:**
    ```bash
    mvn clean install
    ```
3.  **Start a Redis server:**
    If you intend to use the caching functionality, you need a running Redis instance. The easiest way is via Docker:
    ```bash
    docker run --name some-redis -p 6379:6379 -d redis:7-alpine
    ```
    Alternatively, you can disable caching by adding `spring.cache.type=none` to your `application.properties` file.
4.  **Run the Spring Boot application:**
    ```bash
    mvn spring-boot:run
    ```
    The application will start on `http://localhost:8080`.

### 2. Running with Docker (Recommended)

This method will build your application's Docker image and start both the application and a Redis container with a single command.

1.  **Ensure Docker is running** on your machine.
2.  **Navigate to the project root directory** where `Dockerfile` and `docker-compose.yml` are located.
3.  **Build and start the services:**
    ```bash
    docker-compose up --build -d
    ```
    *   `--build`: Builds the Docker image for your application.
    *   `-d`: Runs the containers in detached mode (in the background).
        The application will be accessible at `http://localhost:8080`.

4.  **To stop the services:**
    ```bash
    docker-compose down
    ```

### 1. Running Unit and Integration Tests

To execute all the tests defined in the project:

```bash
mvn test
```

### Front end ###

A basic Angular front-end to test the application can be found in the repository: `https://github.com/ivinib/desafio-Tinnova-front`.

### Swagger ###
Com a aplicação rodando o link para a documentação do Swagger: `http://localhost:8080/swagger-ui/index.html`.
---