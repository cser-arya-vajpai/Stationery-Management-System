#Tells how to create a container for my application.


#Every docker image starts with FROM. 
#Create a container using Ubuntu 22.04 with Java 17 Runtime already installed.
FROM eclipse-temurin:17-jre-jammy   
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

#Volume creates a special storage location. /tmp is temporary folder. Spring boot sometimes creates temporary files. This gives it a place to keep them.
VOLUME /tmp

#Means COPY file from my computer and into the Docker image.
COPY target/config-server-1.0.0.jar app.jar


#Run this command automatically when container starts. Means, launch java, run a jar file. The file copied earlier: /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]


#FROM = start with this existing image. 
#eclipse-temurin = Java distribution. Java provided by eclipse Foundation. Docker says "Give me a machine that already has Java installed."
#17 -> java 17, since project uses java17
#jre -> Java Runtim Environment. Used to run code . Jar is already compiled so u only need to Run application.
#jammy -> refers to Ubuntu 22.04. This is the OS inside the container. 