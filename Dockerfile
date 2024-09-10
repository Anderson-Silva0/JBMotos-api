# Etapa de construção
FROM maven:3.8.7-openjdk-17 AS build

# Defina o diretório de trabalho
WORKDIR /app

# Copie o pom.xml e baixe as dependências
COPY pom.xml .
RUN mvn dependency:go-offline

# Copie o código-fonte e construa o projeto
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa de execução
FROM openjdk:17-jdk-slim

# Defina o diretório de trabalho
WORKDIR /app

# Copie o JAR do estágio de construção
COPY --from=build /app/target/jbmotos-0.0.1-SNAPSHOT.jar app.jar

# Exponha a porta na qual a aplicação estará ouvindo
EXPOSE 8080

# Comando para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]