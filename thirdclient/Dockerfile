FROM openjdk:8-alpine

WORKDIR /usr/app

CMD javac -d ./out  ./src/*.java \
    && java -cp ./out -Djava.rmi.server.hostname="172.17.0.3" -Djava.security.policy=client.policy ThirdChatClient

EXPOSE 6000
