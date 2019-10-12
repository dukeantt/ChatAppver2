SERVER_IMG_NAME = chatapp

server_up:
	docker run -v `pwd`/chatserver/:/usr/app --rm $(SERVER_IMG_NAME)

server_run:
	docker run -it -v `pwd`/chatserver/:/usr/app --rm $(SERVER_IMG_NAME) /bin/sh

server_build:
	docker build --tag $(SERVER_IMG_NAME) ./chatserver
