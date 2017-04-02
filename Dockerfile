FROM openjdk:8-jre-alpine

COPY . /usr/src/myapp
WORKDIR /usr/src/myapp
RUN gradlew installDist

CMD ["build/install/bot/bin/bot"]