# Etapa de construção
FROM openjdk:17-oracle AS build

# Instala Maven
RUN apt-get update && apt-get install -y maven

# Define o diretório de trabalho
WORKDIR /app

# Copia os arquivos do projeto para o contêiner
COPY . .

# Executa a construção do projeto
RUN mvn clean install -DskipTests

# Etapa de execução
FROM openjdk:17-oracle

# Define o diretório de trabalho
WORKDIR /app

# Copia o JAR construído da etapa de construção
COPY --from=build /app/target/jbmotos-0.0.1-SNAPSHOT.jar app.jar

# Expõe a porta em que a aplicação vai rodar
EXPOSE 8080

# Comando para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]