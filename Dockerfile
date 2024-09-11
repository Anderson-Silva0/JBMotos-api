# Etapa de construção
FROM ubuntu:latest AS build

# Atualiza e instala OpenJDK 17 e Maven
RUN apt-get update && \
    apt-get install -y openjdk-17-jdk maven

# Define o diretório de trabalho
WORKDIR /app

# Copia todos os arquivos do projeto para o contêiner
COPY . .

# Executa o Maven para construir o projeto
RUN mvn clean install

# Etapa de execução
FROM openjdk:17-jdk-slim

# Define o diretório de trabalho
WORKDIR /app

# Expõe a porta que a aplicação usará
EXPOSE 8080

# Copia o JAR construído da etapa de construção
COPY --from=build /app/target/jbmotos-0.0.1-SNAPSHOT.jar app.jar

# Comando para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]