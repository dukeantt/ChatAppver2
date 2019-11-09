FROM openjdk:8-alpine

WORKDIR /usr/app

CMD javac -d ./out  ./src/*.java \
    && \
    java -cp ./out -Djava.rmi.server.codebase=out/ -Djava.rmi.server.hostname="172.17.0.2" -Djava.security.policy=server.policy ChatServer

EXPOSE 6000
