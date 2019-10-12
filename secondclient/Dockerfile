FROM openjdk:8-alpine

WORKDIR /usr/app

CMD javac -d ./out  ./src/*.java \
    && java -cp ./out -Djava.rmi.server.hostname="172.17.0.2" -Djava.security.policy=client.policy SecondChatClient

EXPOSE 6000
