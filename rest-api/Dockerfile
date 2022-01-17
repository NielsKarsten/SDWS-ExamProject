FROM adoptopenjdk:11-jre-hotspot
COPY target/rest-api-1.0.0.jar /usr/src/
WORKDIR /usr/src/
CMD java -Xmx32m -jar rest-api-1.0.0.jar
