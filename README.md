Desafio API - Gestão de VeículosAPI REST para gerenciamento de veículos, contendo conversão de moedas em tempo real, relatórios gerenciais estruturados e segurança baseada em niveis de acesso.

Tecnologias 
Utilizadas
 -Java 21 & Spring Boot 3.2+
 -Spring Security & JWT (Auth0)
 -Spring Data JPA & Banco de Dados H2
 -Lombok & Jakarta Validation
 -OpenAPI 3 (Swagger UI)
 -JUnit 5, Mockito & JaCoCo

Como Rodar a Aplicação
Pré-requisitos
Java 21 instalado.
Maven 3.9+ configurado.

No terminal executa esse comando: mvn clean spring-boot:run
A aplicação já inicia com 4 registros no banco de dados para testes e validações

Para rodar os testes, executa o seguinte comando no terminal: mvn clean test

Com a aplicação rodando, o link de acesso a documentação do Swagger é: http://localhost:8080/swagger-ui/index.html

Credenciais para testes manuais

Acesso de administrador: admin / admin123
Acesso de usuario: user / user123
