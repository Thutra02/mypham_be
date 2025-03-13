FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/datn.war app.war
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.war"]