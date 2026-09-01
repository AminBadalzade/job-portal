FROM eclipse-temurin:21-jre

WORKDIR /app

# we give name app.jar to our jar file
COPY target/jobportal-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080


ENTRYPOINT ["java", "-jar", "app.jar"]