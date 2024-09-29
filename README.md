# Instruções para Executar o Projeto

## Requisitos

- **Docker**: É necessário ter o Docker instalado em sua máquina.
  - Você pode baixar e instalar o Docker a partir do site oficial: [Docker Desktop](https://www.docker.com/products/docker-desktop).

- **Docker Compose**: O Docker Compose geralmente vem incluído com o Docker Desktop. Verifique se está disponível executando `docker-compose --version` no terminal.

## Passos para Executar o Projeto

Siga os passos abaixo para configurar e executar o projeto:

> **Importante:**
>
> - **Primeira vez que rodar:** Se a aplicação não rodar na primeira vez que executar o docker compose, tente rodar o comando uma segunda vez


### 1. Construir e Iniciar os Contêineres

Utilize o Docker Compose para construir e iniciar os contêineres da aplicação e do banco de dados:

```bash
docker-compose up -d
```

### 2. Verificar o Health Check da Aplicação

Aguarde alguns instantes para que a aplicação seja inicializada completamente. Em seguida, verifique o health check para garantir que a aplicação está funcionando corretamente:

- **URL do Health Check**:

  ```
  http://localhost:8080/actuator/health
  ```

  ```bash
  curl http://localhost:8080/actuator/health
  ```

- **Resposta Esperada**:

  ```json
  {
    "status": "UP"
  }
  ```

### 3. Acessar a Documentação da API (OpenAPI/Swagger)

A documentação interativa da API está disponível através do Swagger UI.

- **URL da Documentação**:

  ```
  http://localhost:8080/swagger-ui.html
  ```


### 4. Executar Exemplos de Chamadas à API

Você pode testar a API usando os exemplos abaixo.

#### Exemplo 1: Transação de CASH

Envia uma transação de tipo `CASH`.

- **Comando CURL**:

  ```bash
  curl --location 'http://localhost:8080/transactions' \
  --header 'Content-Type: application/json' \
  --data '{
      "account": "123",
      "totalAmount": 10.00,
      "mcc": "9999",
      "merchant": "PAG*JoseDaSilva          RIO DE JANEI BR"
  }'
  ```

- **Explicação**:

  - **Endpoint**: `/transactions` (método POST)
  - **Parâmetros**:
    - `account`: Identificador da conta (ex: "123").
    - `totalAmount`: Valor total da transação (ex: 10.00).
    - `mcc`: Código de categoria do comerciante (ex: "9999").
    - `merchant`: Nome do comerciante (ex: "PAG*JoseDaSilva          RIO DE JANEI BR").

#### Exemplo 2: Transação de MEAL

Envia uma transação de tipo `MEAL`.

- **Comando CURL**:

  ```bash
  curl --location 'http://localhost:8080/transactions' \
  --header 'Content-Type: application/json' \
  --data '{
      "account": "123",
      "totalAmount": 100.00,
      "mcc": "5811",
      "merchant": "PADARIA DO ZE               SAO PAULO BR"
  }'
  ```

- **Explicação**:

  - **Endpoint**: `/transactions` (método POST)
  - **Parâmetros**:
    - `account`: Identificador da conta (ex: "123").
    - `totalAmount`: Valor total da transação (ex: 100.00).
    - `mcc`: Código de categoria do comerciante (ex: "5811").
    - `merchant`: Nome do comerciante (ex: "PADARIA DO ZE               SAO PAULO BR").

### 8. Verificar as Respostas da API

As respostas da API indicarão o resultado do processamento da transação.

- **Códigos de Resposta**:
  - `"00"`: Transação aprovada.
  - `"51"`: Saldo insuficiente.
  - `"07"`: Erro no processamento.

- **Exemplo de Resposta**:

  ```json
  {
    "code": "00"
  }
  ```

### 9. Consultar os Dados no Banco de Dados (Opcional)

Se desejar, você pode verificar os dados armazenados no banco de dados.

#### Acessar o Banco de Dados MySQL

Você pode acessar o banco de dados MySQL executando o seguinte comando:

```bash
docker exec -it mysql_db mysql -ucaju -ptestegbvcaju teste_gbv_caju_db
```

## Informações Adicionais

- **Tecnologias Utilizadas**:

  - Java 17
  - Spring Boot
  - JPA/Hibernate
  - MySQL
  - Docker e Docker Compose
  - OpenAPI/Swagger

- **Endpoints Principais**:

  - `/transactions` (POST): Processa novas transações.
  - `/actuator/health` (GET): Verifica o status da aplicação.
  - `/swagger-ui.html`: Acessa a documentação interativa da API.

---
