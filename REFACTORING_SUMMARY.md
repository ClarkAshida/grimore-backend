# Refatoração do Banco de Dados - Grimore Backend

## 📋 Resumo das Alterações

Esta refatoração atualizou o modelo de dados para seguir a nova especificação do banco de dados.

---

## 🔄 Alterações por Entidade

### 1. **STUDENT (Estudante)**

#### ❌ Campos Removidos:
- `university_name` - Nome da universidade
- `course_name` - Nome do curso
- `current_semester` - Semestre atual

#### ✅ Campos Adicionados:
- `active` (Boolean) - Status ativo/inativo
- `created_at` (LocalDateTime) - Data de criação
- `updated_at` (LocalDateTime) - Data de atualização

#### 📁 Arquivos Modificados:
- `V1__create_students_table.sql`
- `Student.java` (model)
- `CreateStudentDTO.java`
- `StudentDTO.java`

---

### 2. **DISCIPLINE (Disciplina)**

#### ❌ Campos Removidos:
- `nature` (ENUM) - Natureza da disciplina (OBLIGATORY/OPTIONAL)
- `semester` (Integer) - Semestre
- `status` (ENUM) - Status (ACTIVE/PASSED/FAILED/LOCKED)
- `total_hours` (ENUM) - Carga horária antiga
- `absences_count` (Integer) - Contador de faltas em aulas
- `class_schedules` (String) - Horários das aulas

#### ✅ Campos Adicionados/Modificados:
- `schedule_code` (String) - Código de horário UFRN (ex: `246N12`)
  - **Validação**: `^[1-7]+[MVN][1-6]+$`
  - Dias: 1-7 (Dom-Sáb)
  - Turno: M (Matutino), V (Vespertino), N (Noturno)
  - Slots: 1-6
- `color_hex` (String) - Cor em hexadecimal (ex: `#6366F1`)
  - **Validação**: `^#[0-9A-Fa-f]{6}$`
- `workload_hours` (ENUM) - Nova carga horária
  - Valores: H30, H45, H60, H75, H90, H120
  - Cada valor inclui limite de faltas (25%)
- `absences_hours` (Integer) - Contador de faltas em horas
- `active` (Boolean) - Status ativo/inativo
- `created_at` (LocalDateTime) - Data de criação
- `updated_at` (LocalDateTime) - Data de atualização

#### 📁 Arquivos Modificados:
- `V2__create_disciplines_table.sql`
- `Discipline.java` (model)
- `CreateDisciplineDTO.java`
- `DisciplineDTO.java`
- `TotalHours.java` → **Renomeado para** `WorkloadHours.java`

#### 📊 Tabela de Carga Horária e Limites de Faltas:

| Carga Horária | Total de Horas | Limite de Faltas (25%) |
|---------------|----------------|------------------------|
| H30           | 30h            | 9h                     |
| H45           | 45h            | 14h                    |
| H60           | 60h            | 18h                    |
| H75           | 75h            | 23h                    |
| H90           | 90h            | 27h                    |
| H120          | 120h           | 36h                    |

---

### 3. **TASK (Tarefa)**

#### ❌ Campos Removidos:
- `description` (String) - Descrição da tarefa
- `task_type` → Renomeado para `type`
- `status` (ENUM) - Status (TODO/IN_PROGRESS/DONE/STAND_BY)
- `priority` (ENUM) - Prioridade (LOW/MEDIUM/HIGH)
- `grade_weight` (Double) - Peso da nota
- `grade_obtained` (Double) - Nota obtida

#### ✅ Campos Adicionados/Modificados:
- `type` (ENUM) - Tipo da tarefa com valores em português
  - **PROVA** (antiga: EXAM)
  - **TRABALHO** (antiga: HOMEWORK)
  - **SEMINARIO** (antiga: SEMINAR)
  - **LISTA** (nova)
  - **PROJETO** (antiga: PROJECT)
  - **OUTRO** (antiga: OTHER)
- `completed` (Boolean) - Status de conclusão
- `created_at` (LocalDateTime) - Data de criação
- `updated_at` (LocalDateTime) - Data de atualização

#### 📁 Arquivos Modificados:
- `V3__create_tasks_table.sql`
- `Task.java` (model)
- `CreateTaskDTO.java`
- `TaskDTO.java`
- `TaskType.java` (enum - valores atualizados)

---

## 🗑️ ENUMs Removidos

Os seguintes ENUMs foram completamente removidos por não serem mais necessários:

1. **`DisciplineNature.java`** (OBLIGATORY/OPTIONAL)
2. **`DisciplineStatus.java`** (ACTIVE/PASSED/FAILED/LOCKED)
3. **`TaskStatus.java`** (TODO/IN_PROGRESS/DONE/STAND_BY)
4. **`TaskPriority.java`** (LOW/MEDIUM/HIGH)

---

## 🔗 Relacionamentos Mantidos

Os relacionamentos entre as entidades permanecem os mesmos:

```
STUDENT (1) ----< (N) DISCIPLINE
DISCIPLINE (1) ----< (N) TASK
```

- Um estudante pode ter várias disciplinas (1:N)
- Uma disciplina pertence a apenas um estudante (N:1)
- Uma disciplina pode ter várias tarefas (1:N)
- Uma tarefa pertence a apenas uma disciplina (N:1)

---

## ✅ Validações Importantes

### 1. Código da Disciplina
```regex
^[A-Z]{3}[0-9]{4}$
```
**Exemplos válidos**: `IMD1012`, `ABC1234`

### 2. Código de Horário UFRN
```regex
^[1-7]+[MVN][1-6]+$
```
**Exemplos válidos**:
- ✅ `246N12` - Segunda/Quarta/Sexta, Noturno, slots 1-2
- ✅ `35M34` - Terça/Quinta, Matutino, slots 3-4
- ✅ `7V1` - Sábado, Vespertino, slot 1

**Exemplos inválidos**:
- ❌ `abc123` - Letras onde deveria ter números
- ❌ `246X12` - X não é turno válido (apenas M/V/N)
- ❌ `246N78` - Slots 7 e 8 não existem (apenas 1-6)

### 3. Cor Hexadecimal
```regex
^#[0-9A-Fa-f]{6}$
```
**Exemplos válidos**: `#6366F1`, `#FF5733`, `#00AA00`

---

## 🚀 Status da Compilação

✅ **Projeto compilado com sucesso!**

```bash
./mvnw compile -DskipTests
```

Resultado: **BUILD SUCCESS**

---

## 📝 Notas Importantes

1. **Migrações de Banco de Dados**: As migrações Flyway foram atualizadas. Se você já possui dados no banco, será necessário:
   - Fazer backup dos dados existentes
   - Recriar o banco de dados
   - Ou criar uma nova migração para transformar os dados antigos

2. **Controllers e Services**: Os controllers e services ainda não implementados não foram afetados pela refatoração.

3. **Repositories**: Os repositories estão como interfaces vazias e funcionarão normalmente com Spring Data JPA.

4. **Timestamps Automáticos**: Os campos `created_at` e `updated_at` são gerenciados automaticamente pelo Hibernate usando `@CreationTimestamp` e `@UpdateTimestamp`.

---

## 🎯 Próximos Passos Recomendados

1. ✅ Testar a aplicação com o novo modelo
2. ✅ Implementar a lógica de validação de carga horária vs faltas
3. ✅ Adicionar endpoints para gerenciamento de faltas
4. ✅ Implementar soft delete usando o campo `active`
5. ✅ Criar testes unitários para as validações de regex

---

**Data da Refatoração**: 11 de dezembro de 2025
**Status**: Concluído ✅

