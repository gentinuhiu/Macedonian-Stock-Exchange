FROM openjdk:17-jdk-slim

RUN apt-get update && apt-get install -y \
    python3 \
    python3-pip \
    maven \
    && apt-get clean

WORKDIR /app

COPY pom.xml /app/

RUN mvn dependency:resolve

COPY src /app/src

COPY requirements.txt /app/

RUN pip3 install -r requirements.txt

RUN mvn clean package -DskipTests

EXPOSE 9090

ENTRYPOINT ["java", "-jar", "/app/target/tech_prototype-0.0.1-SNAPSHOT.jar"]