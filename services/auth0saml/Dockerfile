FROM tomcat:9.0.50-jdk8-openjdk-slim

COPY ./build/libs/auth0saml.jar /app.jar

ENTRYPOINT ["java","-jar","/app.jar"]