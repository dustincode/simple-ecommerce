FROM maven:3.8.3-openjdk-17
WORKDIR /simple-ecommerce
COPY . .
RUN mvn clean package -DskipTests
CMD mvn spring-boot:run
