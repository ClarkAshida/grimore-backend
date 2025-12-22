# 🔮 Grimore Backend

> *Organize sua vida acadêmica com um toque de mágica.*

O **Grimore Backend** é uma API REST desenvolvida em Java e Spring Boot focado em auxiliar a gestão da vida universitária na UFRN. Combina design minimalista com lógica de negócio robusta para resolver os principais problemas do estudante: configurar horários, lembrar prazos e controlar faltas.

## ✨ Funcionalidades

- Gestão inteligente de disciplinas com validação automática de conflitos de horário (SIGAA).
- Importação automática de horários com IA a partir de comprovante de matrícula em PDF.
- Cadastro de atividades (provas, trabalhos, tarefas) com datas e status.
- Controle de faltas com cálculo de limites de reprovação pela carga horária real.
- Autenticação segura com JWT e Spring Security.
- Documentação interativa via OpenAPI/Swagger.

## 🛠 Stack Tecnológica

### Core
- **Java 21**
- **Spring Boot 3.5.9**
- **PostgreSQL 16**

### Arquitetura & Design
- **Spring Data JPA** - Camada de persistência simplificada
- **Spring Web** - Construção de APIs RESTful
- **Spring IA** - Integração com serviços de IA
- **MapStruct** - Mapeamento automático de DTOs
- **Lombok** - Redução de boilerplate
- **Flyway** - Versionamento de migrations

### Segurança & Qualidade
- **Spring Security + JWT** - Autenticação stateless
- **Bean Validation** - Validações declarativas
- **OpenAPI/Swagger** - Documentação interativa da API

## 📦 Estrutura do Projeto

```
src/main/java/com/grimore/
├── controller/       # Endpoints REST
├── dto/             # Data Transfer Objects (Request/Response)
├── enums/           # Enumerações 
├── exception/       # Exception Handlers globais
├── mapper/          # MapStruct Mappers
├── model/           # Entidades JPA
├── repository/      # Repositories Spring Data
├── security/        # Configurações de segurança
├── service/         # Lógica de negócio
├── config/          # Configurações gerais
└── util/         # Utilitários de negócio
```

## 🎓 Objetivo Educacional

Este projeto é, sobretudo, uma **jornada de aprendizado prático** em desenvolvimento backend com Java e Spring Boot. Ele visa:
- Aplicar conceitos avançados de arquitetura de software.
- Integrar IA para resolver problemas do mundo real.
- Desenvolver habilidades em segurança, validação e documentação de APIs.
- Criar uma base sólida para futuros projetos acadêmicos e profissionais.
- Oferecer uma ferramenta útil para a comunidade estudantil da UFRN.

## 🔮 Próximos Encantamentos

- [ ] **Agendamento via Chat (NLP)**: IA que estrutura eventos em linguagem natural
- [ ] **Microsserviços**: Dividir o backend em serviços independentes com mensageria RabbitMQ
- [ ] **Containers & Orquestração**: Docker + Kubernetes para deploy escalável
- [ ] **Testes Automatizados**: Cobertura completa com JUnit e Mockito
- [ ] **Monitoramento & Logs**: Integração com ELK Stack ou Prometheus/Grafana
- [ ] **CI/CD**: GitHub Actions para automação de build e deploy
- [ ] **Deploy na Nuvem**: AWS (EC2, S3, RDS)
- [ ] **Frontend**: Aplicação web com React ou Angular

---

## Créditos

Feito por: [ClarkAshida](https://github.com/ClarkAshida)


