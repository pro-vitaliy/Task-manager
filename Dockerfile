FROM gradle:8.5-jdk21 AS build

WORKDIR /

COPY . .

RUN ./gradlew build -x test -PactiveProfile=production

FROM openjdk:21-jdk-slim

WORKDIR /

COPY --from=build /build/libs/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=production

CMD ["java", "-jar", "app.jar"]
