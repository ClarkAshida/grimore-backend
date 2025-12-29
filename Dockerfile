# ==== BUILD STAGE ====
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

COPY . .
RUN mvn -q -DskipTests package \
  && JAR="$(ls -1 target/*.jar | grep -v '\.original$' | head -n 1)" \
  && cp "$JAR" /app/app.jar

# ==== RUNTIME STAGE ====
FROM eclipse-temurin:21-jre
WORKDIR /app

USER root
RUN apt-get update \
  && apt-get install -y --no-install-recommends curl wget \
  && rm -rf /var/lib/apt/lists/*

RUN useradd -m appuser
USER appuser

COPY --from=build /app/app.jar /app/app.jar

EXPOSE 8080
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
