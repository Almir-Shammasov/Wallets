FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/*.jar Wallets.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/Wallets.jar"]