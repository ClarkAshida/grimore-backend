## 🚀 TODO - Roadmap para Produção

### 🔐 1. Autenticação e Autorização

#### 1.1 Spring Security + JWT
- [X] Configurar `SecurityFilterChain` com autenticação stateless
- [X] Implementar geração e validação de JWT tokens
    - [X] Adicionar dependência `jjwt` (Java JWT)
    - [X] Criar `JwtTokenProvider` para gerar tokens
    - [X] Criar `JwtAuthenticationFilter` para validar tokens
    - [X] Configurar tempo de expiração e secret key
- [ ] Criar endpoints de autenticação
    - [X] `POST /api/auth/register` - Cadastro de usuário
    - [X] `POST /api/auth/login` - Login e geração de token
    - [X] `POST /api/auth/refresh` - Refresh token
    - [X] `POST /api/auth/logout` - Invalidar token
    - [ ] `POST /api/auth/forgot-password` - Iniciar recuperação de senha
    - [ ] `POST /api/auth/reset-password` - Resetar senha com token

#### 1.2 Controle de Acesso (RBAC)
- [X] Criar enum `Role` (STUDENT, ADMIN)
- [X] Adicionar roles à entidade `Student`
- [ ] Implementar anotações `@PreAuthorize` nos controllers
- [ ] Configurar hierarquia de roles
- [ ] Proteger endpoints administrativos

#### 1.3 OAuth2 / Social Login
- [ ] Integrar login com Google
- [ ] Configurar OAuth2 Client
- [ ] Implementar fluxo de registro via social login

#### 1.4 Segurança Adicional
- [X] Implementar proteção CSRF para endpoints relevantes
- [ ] Configurar CORS adequadamente
- [ ] Adicionar rate limiting por IP/usuário
- [ ] Implementar bloqueio de conta após tentativas falhas
- [ ] Criar auditoria de login (logs de acesso)
- [X] Adicionar password encryption com BCrypt
- [X] Criar política de senhas fortes
- [ ] Implementar recuperação de senha via email

#### 1.5 Implementar Exception Handling Global e Custom Exceptions
- [] Criar `GlobalExceptionHandler` com `@ControllerAdvice`
- [] Definir exceptions customizadas:
  - [] `ResourceNotFoundException`
  - [] `UnauthorizedException`
  - [] `ForbiddenException`
  - [] `BadRequestException`
  - [] `ConflictException`
  - [] `InternalServerErrorException`
  - [] `ValidationException`
  - [] `AuthenticationException`
  - [] `TokenExpiredException`
  - [] `RateLimitExceededException`
  - [] `EmailAlreadyExistsException`
  - [] `UsernameAlreadyExistsException`
- [X] Mapear exceptions para responses HTTP adequados
- [X] Adicionar logging de erros
- [ ] Criar estrutura de resposta de erro consistente
- [ ] Implementar testes para exception handling
- [ ] Documentar erros na API (Swagger)

---

### 📄 2. Paginação, Ordenação e Filtros

#### 2.1 Paginação
- [ ] Adicionar `Pageable` aos métodos de listagem
- [ ] Retornar `Page<T>` nos controllers
- [ ] Criar `PagedResponseDTO` customizado
- [ ] Implementar metadata de paginação (totalPages, totalElements, etc)
- [ ] Configurar tamanho máximo de página
- [ ] Adicionar parâmetros: `page`, `size`, `sort`

#### 2.2 Ordenação Avançada
- [ ] Permitir múltiplos campos de ordenação
- [ ] Validar campos de ordenação permitidos
- [ ] Implementar ordenação case-insensitive
- [ ] Criar enum com campos ordenáveis por entidade

#### 2.3 Filtros Dinâmicos
- [ ] Implementar Specification API do Spring Data JPA
- [ ] Criar `StudentFilter`, `DisciplineFilter`, `TaskFilter`
- [ ] Adicionar filtros por:
    - [ ] Status (ativo/inativo)
    - [ ] Data de criação (range)
    - [ ] Busca textual (nome, código, descrição)
    - [ ] Relacionamentos (ex: tarefas por disciplina)
- [ ] Implementar query builder dinâmico
- [ ] Adicionar suporte a operadores: `eq`, `like`, `gt`, `lt`, `in`

#### 2.4 Busca Full-Text
- [ ] Integrar PostgreSQL Full-Text Search
- [ ] Criar índices GIN para busca textual
- [ ] Implementar busca fuzzy (aproximada)
- [ ] Adicionar ranking de resultados

---

### 🔗 3. HATEOAS

#### 3.1 Implementação Básica
- [ ] Adicionar dependência `spring-boot-starter-hateoas`
- [ ] Estender DTOs de `RepresentationModel`
- [ ] Adicionar links `self` em todas as responses
- [ ] Criar `ModelAssembler` para cada entidade

#### 3.2 Hipermídia Avançada
- [ ] Adicionar links relacionados (ex: Student -> Disciplines)
- [ ] Implementar navegação entre recursos
- [ ] Adicionar affordances (ações disponíveis)
- [ ] Criar endpoint raiz com descoberta de APIs (`/api`)
- [ ] Implementar HAL (Hypertext Application Language)
- [ ] Adicionar links de paginação (first, last, next, prev)

#### 3.3 Documentação de Links
- [ ] Documentar todos os tipos de links disponíveis
- [ ] Criar profile links
- [ ] Adicionar templates de URL

---

### 🧪 4. Testes

#### 4.1 Testes Unitários
- [ ] Configurar JUnit 5 e Mockito
- [ ] Testar Services (100% de cobertura)
    - [ ] `StudentService`
    - [ ] `DisciplineService`
    - [ ] `TaskService`
    - [ ] `AuthService` (quando implementado)
- [ ] Testar Mappers
- [ ] Testar Validações customizadas
- [ ] Testar Exception Handlers
- [ ] Testar lógica de negócio complexa
- [ ] Usar `@ExtendWith(MockitoExtension.class)`

#### 4.2 Testes de Integração
- [ ] Configurar Testcontainers para PostgreSQL
- [ ] Testar Repositories com banco real
- [ ] Testar Controllers com `@WebMvcTest`
- [ ] Testar fluxos completos com `@SpringBootTest`
- [ ] Testar migrations do Flyway
- [ ] Testar transações e rollback
- [ ] Criar fixtures e dados de teste reutilizáveis
- [ ] Testar relacionamentos JPA

#### 4.3 Testes E2E (End-to-End)
- [ ] Configurar REST Assured
- [ ] Testar fluxos de usuário completos:
    - [ ] Cadastro → Login → CRUD de Disciplinas → CRUD de Tarefas
    - [ ] Autenticação e autorização
    - [ ] Upload e download de arquivos
- [ ] Testar diferentes perfis de usuário
- [ ] Testar cenários de erro e edge cases

#### 4.4 Testes de Performance
- [ ] Configurar JMH (Java Microbenchmark Harness)
- [ ] Testar endpoints sob carga com Gatling
- [ ] Identificar gargalos de performance
- [ ] Testar queries N+1
- [ ] Benchmark de operações críticas

#### 4.5 Testes de Segurança
- [ ] Testar endpoints protegidos sem autenticação
- [ ] Testar acesso com roles incorretos
- [ ] Testar SQL Injection
- [ ] Testar XSS
- [ ] Validar rate limiting

#### 4.6 Cobertura de Código
- [ ] Configurar JaCoCo
- [ ] Estabelecer meta de 80%+ de cobertura
- [ ] Gerar relatórios HTML de cobertura
- [ ] Integrar cobertura no CI/CD
- [ ] Criar quality gates

---

### 📚 5. Documentação da API

#### 5.1 OpenAPI/Swagger
- [ ] Adicionar dependência `springdoc-openapi-starter-webmvc-ui`
- [ ] Configurar Swagger UI (`/swagger-ui.html`)
- [ ] Adicionar anotações `@Operation` nos endpoints
- [ ] Documentar todos os parâmetros com `@Parameter`
- [ ] Documentar responses com `@ApiResponse`
- [ ] Adicionar exemplos de requisição/resposta
- [ ] Configurar esquemas de segurança (JWT)
- [ ] Customizar aparência do Swagger UI

#### 5.2 Documentação Complementar
- [ ] Criar arquivo `API.md` com guia de uso
- [ ] Documentar fluxos de autenticação
- [ ] Adicionar collection do Postman/Insomnia
- [ ] Criar exemplos de curl para cada endpoint
- [ ] Documentar códigos de erro e suas causas
- [ ] Criar diagrama de arquitetura
- [ ] Documentar variáveis de ambiente

#### 5.3 API Versioning
- [ ] Implementar versionamento de API (v1, v2)
- [ ] Escolher estratégia: URL, Header ou Accept header
- [ ] Documentar política de deprecation
- [ ] Manter compatibilidade entre versões

---

### 🐳 6. Containerização e Orquestração

#### 6.1 Dockerfile
- [ ] Criar `Dockerfile` multi-stage
    - [ ] Stage 1: Build com Maven
    - [ ] Stage 2: Runtime com JRE slim
- [ ] Otimizar layers para cache
- [ ] Usar imagem base Alpine para menor tamanho
- [ ] Configurar non-root user
- [ ] Adicionar health check no container
- [ ] Configurar timezone

#### 6.2 Docker Compose Completo
- [ ] Adicionar serviço da aplicação ao docker-compose
- [ ] Configurar networks entre serviços
- [ ] Adicionar volumes para persistência
- [ ] Configurar variáveis de ambiente
- [ ] Adicionar serviço Redis (cache)
- [ ] Adicionar serviço Nginx (reverse proxy)
- [ ] Configurar depends_on e health checks
- [ ] Criar profiles (dev, test, prod)

#### 6.3 Kubernetes
- [ ] Criar manifests K8s:
    - [ ] Deployment
    - [ ] Service
    - [ ] ConfigMap
    - [ ] Secret
    - [ ] Ingress
    - [ ] HorizontalPodAutoscaler
- [ ] Configurar probes (liveness, readiness)
- [ ] Implementar rolling updates
- [ ] Configurar resource limits

#### 6.4 Helm Charts
- [ ] Criar Helm chart para a aplicação
- [ ] Parametrizar valores
- [ ] Criar templates reutilizáveis

---

### 📊 7. Observabilidade e Monitoramento

#### 7.1 Spring Boot Actuator
- [ ] Expor endpoints úteis:
    - [ ] `/actuator/health` (com detalhes)
    - [ ] `/actuator/info`
    - [ ] `/actuator/metrics`
    - [ ] `/actuator/env`
    - [ ] `/actuator/loggers`
    - [ ] `/actuator/threaddump`
    - [ ] `/actuator/heapdump`
- [ ] Criar custom health indicators
- [ ] Adicionar build info
- [ ] Proteger endpoints sensíveis

#### 7.2 Métricas com Micrometer
- [ ] Configurar Micrometer registry
- [ ] Adicionar métricas customizadas:
    - [ ] Contadores de requisições por endpoint
    - [ ] Tempo de resposta por operação
    - [ ] Taxa de erro
    - [ ] Métricas de negócio (tasks criadas, etc)
- [ ] Configurar tags para dimensões

#### 7.3 Prometheus
- [ ] Adicionar dependência `micrometer-registry-prometheus`
- [ ] Expor endpoint `/actuator/prometheus`
- [ ] Criar `prometheus.yml` com scrape config
- [ ] Configurar alertas básicos
- [ ] Adicionar service discovery

#### 7.4 Grafana
- [ ] Criar container Grafana no docker-compose
- [ ] Configurar datasource Prometheus
- [ ] Criar dashboards:
    - [ ] JVM Metrics (heap, threads, GC)
    - [ ] HTTP Metrics (requests, latency, errors)
    - [ ] Database Metrics (connections, queries)
    - [ ] Business Metrics
- [ ] Configurar alertas visuais
- [ ] Exportar dashboards como JSON

#### 7.5 Logging Estruturado
- [ ] Configurar Logback com JSON layout
- [ ] Adicionar correlation IDs
- [ ] Implementar MDC (Mapped Diagnostic Context)
- [ ] Configurar níveis de log por package
- [ ] Adicionar log de auditoria
- [ ] Integrar com ELK Stack (Elasticsearch, Logstash, Kibana)

#### 7.6 Distributed Tracing
- [ ] Adicionar Spring Cloud Sleuth / Micrometer Tracing
- [ ] Integrar com Zipkin ou Jaeger
- [ ] Rastrear chamadas entre serviços
- [ ] Adicionar spans customizados

#### 7.7 APM (Application Performance Monitoring)
- [ ] Integrar New Relic / DataDog / Dynatrace
- [ ] Configurar alertas de performance
- [ ] Monitorar transações críticas

---

### ⚡ 8. Performance e Otimização

#### 8.1 Caching
- [ ] Adicionar dependência Spring Cache
- [ ] Configurar Redis como cache provider
- [ ] Implementar cache em consultas frequentes:
    - [ ] `@Cacheable` em findById
    - [ ] `@CacheEvict` em updates/deletes
    - [ ] `@CachePut` quando necessário
- [ ] Configurar TTL por cache
- [ ] Implementar cache warming
- [ ] Criar estratégia de invalidação
- [ ] Adicionar métricas de hit/miss rate

#### 8.2 Otimização de Queries
- [ ] Identificar e corrigir N+1 queries
- [ ] Usar `@EntityGraph` para fetch estratégico
- [ ] Implementar fetch JOIN quando necessário
- [ ] Criar índices no banco de dados
- [ ] Usar projeções para queries específicas
- [ ] Implementar query hints
- [ ] Habilitar second-level cache do Hibernate

#### 8.3 Connection Pooling
- [ ] Configurar HikariCP adequadamente
- [ ] Ajustar pool size (min/max)
- [ ] Configurar connection timeout
- [ ] Monitorar pool com métricas

#### 8.4 Async Processing
- [ ] Configurar `@EnableAsync`
- [ ] Criar operações assíncronas para tarefas pesadas
- [ ] Configurar ThreadPoolExecutor
- [ ] Implementar processamento em background

#### 8.5 Rate Limiting
- [ ] Implementar rate limiting com Bucket4j
- [ ] Configurar limites por endpoint
- [ ] Adicionar rate limiting por usuário
- [ ] Retornar headers `X-RateLimit-*`
- [ ] Criar diferentes tiers (free, premium)

#### 8.6 Compressão
- [ ] Habilitar GZIP compression
- [ ] Configurar threshold de compressão
- [ ] Comprimir responses grandes

#### 8.7 Database Partitioning
- [ ] Avaliar particionamento de tabelas grandes
- [ ] Implementar archiving de dados antigos
- [ ] Criar estratégia de retenção de dados

---

### 🔄 9. CI/CD

#### 9.1 GitHub Actions
- [ ] Criar workflow `.github/workflows/ci.yml`:
    - [ ] Build com Maven
    - [ ] Executar testes unitários
    - [ ] Executar testes de integração
    - [ ] Gerar relatório de cobertura
    - [ ] Upload para SonarCloud/SonarQube
    - [ ] Build Docker image
    - [ ] Push para Docker Hub/GHCR
- [ ] Criar workflow de deployment
- [ ] Configurar matriz de versões Java
- [ ] Adicionar cache de dependências Maven

#### 9.2 Quality Gates
- [ ] Configurar SonarQube/SonarCloud
- [ ] Estabelecer thresholds:
    - [ ] Cobertura mínima: 80%
    - [ ] Duplicação máxima: 3%
    - [ ] Code smells: 0
    - [ ] Bugs críticos: 0
- [ ] Bloquear merge em falha de quality gate
- [ ] Configurar análise de segurança (SAST)

#### 9.3 Dependency Management
- [ ] Configurar Dependabot
- [ ] Automatizar updates de segurança
- [ ] Escanear vulnerabilidades com Snyk
- [ ] Criar política de atualização

#### 9.4 Semantic Versioning
- [ ] Implementar versionamento automático
- [ ] Gerar CHANGELOG automaticamente
- [ ] Criar tags Git em releases
- [ ] Seguir padrão SemVer

#### 9.5 Ambientes
- [ ] Configurar pipelines para múltiplos ambientes:
    - [ ] Development (auto-deploy em push)
    - [ ] Staging (testes E2E automáticos)
    - [ ] Production (aprovação manual)
- [ ] Criar branches protegidos
- [ ] Configurar deploy previews para PRs

---

### ☁️ 10. Deploy e Infraestrutura

#### 10.1 AWS EC2
- [ ] Provisionar instância EC2
- [ ] Configurar Security Groups
- [ ] Instalar Docker na instância
- [ ] Configurar SSH keys
- [ ] Setup de Nginx como reverse proxy
- [ ] Configurar SSL/TLS com Let's Encrypt
- [ ] Implementar auto-scaling (opcional)
- [ ] Configurar Elastic IP

#### 10.2 AWS RDS
- [ ] Migrar PostgreSQL para RDS
- [ ] Configurar Multi-AZ para alta disponibilidade
- [ ] Setup de backups automáticos
- [ ] Configurar read replicas
- [ ] Implementar connection pooling via RDS Proxy

#### 10.3 AWS S3
- [ ] Configurar bucket para uploads de arquivos
- [ ] Implementar upload direto para S3
- [ ] Configurar políticas de acesso (IAM)
- [ ] Adicionar CloudFront para CDN

#### 10.4 AWS Secrets Manager
- [ ] Migrar secrets para AWS Secrets Manager
- [ ] Configurar rotação automática de credenciais
- [ ] Integrar aplicação com Secrets Manager

#### 10.5 Terraform (Infrastructure as Code)
- [ ] Criar módulos Terraform para:
    - [ ] VPC e networking
    - [ ] EC2 instances
    - [ ] RDS
    - [ ] S3 buckets
    - [ ] Security groups
    - [ ] Load balancers
- [ ] Configurar remote state no S3
- [ ] Criar workspaces por ambiente

#### 10.6 Alternativas de Deploy
- [ ] Avaliar AWS ECS/Fargate
- [ ] Avaliar AWS Elastic Beanstalk
- [ ] Avaliar Railway/Render (PaaS)
- [ ] Avaliar Digital Ocean App Platform

---

### 📧 11. Notificações e Comunicação

#### 11.1 Email
- [ ] Integrar JavaMailSender
- [ ] Configurar SMTP (AWS SES, SendGrid)
- [ ] Criar templates de email (Thymeleaf)
- [ ] Implementar emails:
    - [ ] Confirmação de cadastro
    - [ ] Recuperação de senha
    - [ ] Notificações de tarefas
    - [ ] Relatórios periódicos
- [ ] Configurar fila de emails (async)
- [ ] Implementar retry em falhas

#### 11.2 Notificações Push
- [ ] Integrar Firebase Cloud Messaging
- [ ] Criar sistema de preferências de notificação
- [ ] Implementar notificações em tempo real

#### 11.3 WebSockets
- [ ] Adicionar Spring WebSocket
- [ ] Implementar notificações real-time
- [ ] Criar sistema de eventos

---

### 📁 12. Upload e Armazenamento de Arquivos

#### 12.1 Upload de Arquivos
- [ ] Criar endpoint de upload `POST /api/files`
- [ ] Validar tipo e tamanho de arquivo
- [ ] Implementar upload para sistema local (dev)
- [ ] Implementar upload para S3 (prod)
- [ ] Gerar URLs assinadas para download
- [ ] Criar relacionamento Arquivo -> Task

#### 12.2 Processamento de Arquivos
- [ ] Implementar preview de imagens (thumbnails)
- [ ] Validar e sanitizar uploads
- [ ] Escanear vírus (ClamAV)
- [ ] Implementar compressão de imagens

---

### 🔍 13. Auditoria e Compliance

#### 13.1 Auditoria de Entidades
- [ ] Implementar `@EntityListeners` com Envers
- [ ] Criar tabelas de auditoria
- [ ] Rastrear quem/quando criou/modificou
- [ ] Implementar campos: `createdBy`, `createdAt`, `updatedBy`, `updatedAt`
- [ ] Criar endpoint para consultar histórico

#### 13.2 LGPD/GDPR
- [ ] Implementar exportação de dados do usuário
- [ ] Criar funcionalidade de exclusão de conta
- [ ] Adicionar consent management
- [ ] Criar política de privacidade
- [ ] Implementar anonimização de dados

#### 13.3 Logs de Auditoria
- [ ] Registrar ações críticas:
    - [ ] Login/Logout
    - [ ] Alterações de senha
    - [ ] Acessos a dados sensíveis
    - [ ] Operações administrativas
- [ ] Criar tabela de audit_logs
- [ ] Implementar retenção de logs

---

### 🧩 14. Integrações Externas

#### 14.1 APIs Externas
- [ ] Integrar com SIGAA (Sistema acadêmico UFRN)
- [ ] Criar adaptadores para APIs de terceiros
- [ ] Implementar circuit breaker com Resilience4j
- [ ] Adicionar retry policies
- [ ] Implementar fallbacks

#### 14.2 Webhooks
- [ ] Criar sistema de webhooks para eventos
- [ ] Permitir registro de URLs de callback
- [ ] Implementar assinatura de payloads
- [ ] Criar retry mechanism

---

### 🌐 15. Internacionalização (i18n)

- [ ] Configurar `MessageSource`
- [ ] Criar arquivos de mensagens (pt_BR, en_US)
- [ ] Internacionalizar mensagens de erro
- [ ] Internacionalizar validações
- [ ] Adicionar header `Accept-Language`
- [ ] Suportar múltiplos locales

---

### 🧪 16. Testes de Carga e Stress

- [ ] Configurar Gatling
- [ ] Criar cenários de carga:
    - [ ] 100 usuários simultâneos
    - [ ] 1000 requisições/segundo
    - [ ] Picos de tráfego
- [ ] Identificar limites da aplicação
- [ ] Criar baseline de performance
- [ ] Automatizar testes no CI/CD

---

### 📱 17. API Mobile-First

#### 17.1 Otimizações Mobile
- [ ] Criar endpoints específicos para mobile
- [ ] Implementar GraphQL (alternativa a REST)
- [ ] Otimizar payload de responses
- [ ] Criar SDK para clientes mobile

#### 17.2 Offline-First
- [ ] Implementar ETags para caching
- [ ] Suportar conditional requests
- [ ] Criar estratégia de sincronização

---

### 🔧 18. DevOps e Ferramentas

#### 18.1 Scripts de Automação
- [ ] Criar scripts para setup local
- [ ] Automatizar geração de dados de teste
- [ ] Criar script de backup do banco
- [ ] Automatizar restore de backups

#### 18.2 Makefile
- [ ] Criar Makefile com comandos úteis:
    - [ ] `make build`
    - [ ] `make test`
    - [ ] `make run`
    - [ ] `make docker-up`
    - [ ] `make deploy`

#### 18.3 Pre-commit Hooks
- [ ] Configurar Husky/Lefthook
- [ ] Executar testes antes de commit
- [ ] Validar formatação de código
- [ ] Executar linters

---

### 📊 19. Analytics e Métricas de Negócio

- [ ] Criar dashboard de métricas:
    - [ ] Usuários ativos
    - [ ] Tarefas criadas por dia
    - [ ] Disciplinas mais populares
    - [ ] Taxa de conclusão de tarefas
- [ ] Implementar event tracking
- [ ] Integrar com Google Analytics
- [ ] Criar relatórios automáticos

---

### 🎨 20. Melhorias de Arquitetura

#### 20.1 Clean Architecture
- [ ] Refatorar para camadas bem definidas
- [ ] Separar domain models de DTOs
- [ ] Implementar use cases
- [ ] Aplicar princípios SOLID

#### 20.2 Event-Driven Architecture
- [ ] Implementar eventos de domínio
- [ ] Configurar Spring Events
- [ ] Integrar RabbitMQ/Kafka
- [ ] Criar event sourcing para auditoria

#### 20.3 Microservices (Futuro)
- [ ] Identificar bounded contexts
- [ ] Separar serviços:
    - [ ] Auth Service
    - [ ] Student Service
    - [ ] Task Service
- [ ] Implementar API Gateway
- [ ] Configurar service discovery (Eureka/Consul)

---

### 🛡️ 21. Backup e Disaster Recovery

- [ ] Configurar backups automáticos diários
- [ ] Testar procedimento de restore
- [ ] Implementar backup incremental
- [ ] Criar plano de disaster recovery
- [ ] Documentar RTO e RPO
- [ ] Configurar replicação geográfica

---

### 📜 22. Compliance e Licenciamento

- [ ] Adicionar licença ao projeto (MIT, Apache 2.0)
- [ ] Criar CONTRIBUTING.md
- [ ] Adicionar CODE_OF_CONDUCT.md
- [ ] Verificar licenças de dependências
- [ ] Criar política de segurança (SECURITY.md)

---

## 🚦 Como Usar Este Roadmap

1. **Priorize**: Nem tudo precisa ser feito imediatamente. Comece por:
    - Autenticação JWT
    - Testes
    - Documentação (Swagger)
    - Docker
    - CI/CD básico