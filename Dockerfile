FROM java:8-jre
VOLUME /tmp
ADD target/configservice-0.0.1-SNAPSHOT.jar /app.jar
# All Spring Boot components expose 8888 as the container java debug port
EXPOSE 8888
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=dev","-jar","/app.jar"]