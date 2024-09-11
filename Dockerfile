# Etapa de construção
FROM maven:3.9.4-openjdk-17 AS build

# Define o diretório de trabalho
WORKDIR /app

# Copia os arquivos do projeto para o contêiner
COPY . .

# Executa a construção do projeto
RUN mvn clean install -DskipTests

# Etapa de execução
FROM openjdk:17-jdk-slim

# Define o diretório de trabalho
WORKDIR /app

# Copia o JAR construído da etapa de construção
COPY --from=build /app/target/jbmotos-0.0.1-SNAPSHOT.jar app.jar

# Expõe a porta em que a aplicação vai rodar
EXPOSE 8080

# Comando para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]