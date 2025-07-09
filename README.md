# Sistema de Gerenciamento de Curso

Por: Bianca de Oliveira Durgante, Davi Lopes Lemos e Thálys Lemos Correa.

Este software permite ao discente configurar, gerenciar e acompanhar a integralização de seu curso de forma organizada e personalizada.

## Como Usar

### 1. Cadastro Inicial
Preencha os seguintes dados:
- Nome do curso
- Matrícula do discente
- E-mail do discente

### 2. Configuração do Curso
Acesse a aba **"Configurar Curso"** e adicione os componentes curriculares:

#### Disciplinas
- Nome
- Semestre
- Carga horária
- Nota
- Período

#### Atividades Curriculares Extras
- Nome
- Semestre
- Carga horária exigida
- Carga horária cumprida
- Período

#### CCCGs (Componentes Curriculares Complementares de Graduação)
- Nome
- Semestre
- Carga horária exigida
- Carga horária cumprida
- Nota
- Período

> **Importante:** a **carga horária total exigida de CCCGs** deve ser informada manualmente no label localizado **acima da tabela de CCCGs**.

### 3. Gerenciamento de Horas
Na aba **"Gerenciar Horas"**:
- Marque as **disciplinas** que estão **matriculadas ou concluídas**.
- **Não é necessário marcar CCCGs ou Atividades Curriculares Extras**.

### 4. Pré-Requisitos
Na aba **"Visualizar Pré-requisitos"**, você pode:
- Indicar as relações de pré-requisitos entre atividades.
- Verificar quais atividades estão disponíveis para cursar.

### 5. Integralização
Na aba **"Integralização"**, é possível:
- Acompanhar o progresso da integralização do curso.
- Adicionar requisitos de integralização.
- Inserir novas Disciplinas, CCCGs e Atividades Curriculares Extras.

---

## Conveniências e Recomendações

- O nome do arquivo salvo seguirá o padrão: `[NOME_DO_CURSO]_[MATRICULA].txt`
- Evite utilizar:
  - Espaços
  - Acentos
  - Caracteres especiais (como `ç`) no nome do curso
- Os dados são salvos no formato `.txt` para facilitar edição e backup manual.


