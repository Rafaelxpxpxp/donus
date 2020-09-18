# Donus

## Descrição

Projeto Donus reactivo e orientado a eventos.

## Tecnologias
- Java
- Kafka
- Webflux
- Redis
- Postgresql
- Docker
- Docker Compose

## Como iniciar

Na pasta do projeto execute os seguintes comandos após ter instaldo docker e docker-compose na sua máquina.

```
  mvn package

  docker-compose build .
  
  docker-compose up
```

*Obs: Verifique se todos os serviços subiram, caso não, execute docker-compose up novamente.*

## Urls importantes.

### Documentação das Apis Rest

**Swagger-ui local** 

http://localhost:9090/swagger-ui

**Swagger-ui docker**

http://localhost:8080/swagger-ui

### Painel de controle para o Kafka

**Control Center**

http://localhost:9021/

### Banco de dados

Utilizar DataGrip ou pgAdmin para acessar o banco

Campo         | Valor
------------- | -------------
*url:*        | localhost:5432/postgres
*user:*       | postgres
*password:*   | 123
