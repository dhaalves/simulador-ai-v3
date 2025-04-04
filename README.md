# Simulador de Aposentadoria

O Simulador de Aposentadoria é uma aplicação moderna e profissional desenvolvida com Quarkus para auxiliar servidores públicos a calcular o tempo de contribuição e simular diferentes cenários de aposentadoria de acordo com as regras previdenciárias vigentes.

## Funcionalidades

- Cadastro de usuários com dados pessoais e profissionais
- Gerenciamento de períodos de serviço (averbação de tempo)
- Conversão de tempo especial (insalubre) para tempo comum
- Simulação de diferentes regras de aposentadoria:
  - Regra permanente
  - Regras de transição (pontos e pedágio)
  - Regras especiais (professor, policial, insalubridade)
- Cálculo de percentual de benefício e valor estimado
- Histórico de simulações

## Tecnologias

- Quarkus 3.x
- Java 21
- Hibernate ORM com Panache
- RESTEasy Reactive
- PostgreSQL
- SmallRye OpenAPI e Swagger UI
- MicroProfile

## Requisitos

- JDK 21
- Maven 3.8.6+
- PostgreSQL 13+

## Configuração do Banco de Dados

Antes de executar a aplicação, é necessário criar o banco de dados:

```sql
CREATE DATABASE simulador_aposentadoria;
CREATE DATABASE simulador_aposentadoria_test;
```

As tabelas serão criadas automaticamente pela aplicação.

## Execução em Modo de Desenvolvimento

Para executar a aplicação em modo de desenvolvimento, use o comando:

```shell
./mvnw quarkus:dev
```

A aplicação estará disponível em http://localhost:8080

A interface Swagger UI estará disponível em http://localhost:8080/swagger para testar as APIs.

## Estrutura da API

### Usuários
- `GET /api/usuarios` - Lista todos os usuários
- `GET /api/usuarios/{id}` - Busca um usuário pelo ID
- `POST /api/usuarios` - Cria um novo usuário
- `PUT /api/usuarios/{id}` - Atualiza um usuário existente
- `DELETE /api/usuarios/{id}` - Remove um usuário

### Períodos de Serviço
- `GET /api/periodos-servico/usuario/{id}` - Lista períodos de serviço de um usuário
- `POST /api/periodos-servico` - Adiciona um novo período de serviço
- `PUT /api/periodos-servico/{id}` - Atualiza um período de serviço
- `DELETE /api/periodos-servico/{id}` - Remove um período de serviço
- `POST /api/periodos-servico/{id}/converter-tempo` - Converte tempo especial
- `GET /api/periodos-servico/usuario/{id}/tempo-total` - Calcula tempo total de serviço

### Simulador
- `GET /api/simulador` - Testa se o serviço está disponível
- `POST /api/simulador/executar` - Executa uma simulação de aposentadoria
- `GET /api/simulador/usuario/{id}/simulacoes` - Lista simulações de um usuário
- `GET /api/simulador/simulacao/{id}` - Busca uma simulação pelo ID

## Empacotamento e Execução da Aplicação

A aplicação pode ser empacotada usando:

```shell
./mvnw package
```

O arquivo `quarkus-run.jar` será gerado no diretório `target/quarkus-app/`.

A aplicação pode ser executada com:

```shell
java -jar target/quarkus-app/quarkus-run.jar
```

## Criando um Executável Nativo

É possível criar um executável nativo utilizando GraalVM:

```shell
./mvnw package -Dnative
```

Ou, se não tiver o GraalVM instalado, você pode executar a compilação em um container:

```shell
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

O executável nativo pode ser executado com:

```shell
./target/simulador-aposentadoria-1.0.0-runner
```

## Próximos Passos

- Implementação de autenticação JWT
- Interface de usuário com React ou Angular
- Geração de relatórios em PDF
- Dashboard com estatísticas de simulações
- Notificações por email para prazos de aposentadoria
