# 🔮 Grimore Backend

> *Organize sua vida acadêmica com um toque de mágica.*

O **Grimore** é uma aplicação web projetada para eliminar a fricção da gestão universitária na UFRN. Combinando um design lúdico e minimalista com um backend robusto, o sistema resolve os três maiores problemas do estudante: preguiça de configurar horários, esquecimento de prazos e descontrole de faltas.

## ✨ Funcionalidades

- **📚 Gestão Inteligente de Disciplinas**: Sistema de validação automática de conflitos de horário baseado nos códigos do SIGAA.
- **🗓 Configuração Automática de Horários**: Importe seu comprovante de matrícula em PDF e gere seu cronograma semanal instantaneamente
- **📝 Cadastro de Atividades:** Criação e organização de tarefas acadêmicas com datas e status de conclusão
- **⏰ Controle de Faltas Automatizado**: Lógica de negócios que calcula limites de reprovação baseados na carga horária real de cada disciplina
- **📋 Gerenciamento de Tarefas**: Organize provas, trabalhos e atividades com controle de prazos
- **🔐 Autenticação Segura**: JWT com Spring Security para proteção de dados
- 

## 🛠 Stack Tecnológica

### Core
- **Java 21** - Última versão LTS com records, pattern matching e text blocks
- **Spring Boot 4.0.0** - Framework enterprise-ready
- **PostgreSQL 16** - Banco de dados relacional robusto

### Arquitetura & Design
- **Spring Data JPA** - Camada de persistência simplificada
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
└── util/         # Utilitários de negócio
```

## 🎓 Objetivo Educacional

Este projeto é, sobretudo, uma **jornada de aprendizado prático** em desenvolvimento backend moderno:
- Arquitetura em camadas com separação de responsabilidades
- Padrões de projeto (Repository, Service, DTO)
- Testes automatizados com Testcontainers
- Documentação viva com OpenAPI
- Observabilidade com métricas e logs estruturados
- CI/CD com pipelines automatizados

## 🔮 Próximos Encantamentos

- [ ] **PDF Parsing Inteligente**: Setup do semestre via upload do comprovante de matrícula
- [ ] **Agendamento via Chat (NLP)**: IA que estrutura eventos em linguagem natural
- [ ] **Notificações Inteligentes**: Alertas de prazos e limites de faltas
- [ ] **Dashboard Analytics**: Visualização de desempenho e progresso

---

## Créditos

Feito por: [ClarkAshida](https://github.com/ClarkAshida)


