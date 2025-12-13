# Grimore Backend

Grimore ajuda a organizar sua rotina acadêmica na UFRN — disciplinas, tarefas e atividades em um só lugar.

## 📋 Sobre o Projeto

Grimore é um backend em Spring Boot voltado à gestão acadêmica via API REST e, sobretudo, um projeto para aprender e praticar novas tecnologias como Java 21, Spring Boot 4.0.0, Spring Data JPA, Spring Security com JWT, Flyway, MapStruct, Lombok, PostgreSQL 16, Testcontainers, Docker, OpenAPI/Swagger, Prometheus, Grafana e CI/CD com GitHub Actions.

## 🛠 Stack Tecnológica

- **Java 21**
- **Spring Boot 4.0.0**
- **Spring Data JPA**
- **PostgreSQL 16**
- **Flyway** (Migrations)
- **MapStruct** (Object Mapping)
- **Lombok**
- **Maven**

## 📦 Estrutura do Projeto

```
src/main/java/com/grimore/
├── controller/       # Endpoints REST (Student, Discipline, Task)
├── dto/             # Data Transfer Objects (Request/Response)
├── enums/           # Enumerações (TaskType, WorkloadHours)
├── exception/       # Exception Handlers globais
├── mapper/          # MapStruct Mappers
├── model/           # Entidades JPA
├── repository/      # Repositories Spring Data
├── security/        # Configurações de segurança
└── service/         # Lógica de negócio
```
