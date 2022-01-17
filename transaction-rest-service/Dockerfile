FROM adoptopenjdk:11-jre-hotspot
COPY target/transaction-rest-service-1.0.0.jar /usr/src/
WORKDIR /usr/src/
CMD java -Xmx32m -jar transaction-rest-service-1.0.0.jar
