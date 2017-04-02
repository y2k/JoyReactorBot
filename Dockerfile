FROM openjdk:8-jdk-alpine

COPY . /usr/src/myapp
WORKDIR /usr/src/myapp
RUN ./gradlew --no-daemon installDist

CMD ["build/install/bot/bin/bot"]