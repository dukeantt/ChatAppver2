FROM openjdk:8-alpine

WORKDIR /usr/app

CMD javac -d ./out  ./src/*.java \
    && java -cp ./out -Djava.rmi.server.hostname="172.17.0.4" -Djava.security.policy=client.policy ChatClientGui

EXPOSE 6000
