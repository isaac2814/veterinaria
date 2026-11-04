# ==============================================================================
# Etapa 1: Compilación (Build stage)
# ==============================================================================
FROM eclipse-temurin:25-jdk-noble AS build
WORKDIR /app

# Instalar Maven para compilar de forma limpia
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Cachear dependencias copiando solo pom.xml primero
COPY pom.xml .
RUN mvn dependency:go-offline -B || true

# Copiar el código fuente y empaquetar el JAR
COPY src ./src
RUN mvn clean package -DskipTests

# ==============================================================================
# Etapa 2: Imagen ligera de ejecución (Runtime stage)
# ==============================================================================
FROM eclipse-temurin:25-jre-noble
WORKDIR /app

# Copiar el artefacto generado
COPY --from=build /app/target/*.jar app.jar

# Configurar variables de puerto (Fly.io asigna PORT=8080)
ENV PORT=8080
EXPOSE 8080

# Iniciar la aplicación
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
