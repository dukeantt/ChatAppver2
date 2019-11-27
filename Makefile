SERVER_IMG_NAME = chatapp

server_up:
	docker run -v `pwd`/chatserver/:/usr/app --rm $(SERVER_IMG_NAME)

server_run:
	docker run -it -v `pwd`/chatserver/:/usr/app --rm $(SERVER_IMG_NAME) /bin/sh

server_build:
	docker build --tag $(SERVER_IMG_NAME) ./chatserver

client_up:
	docker run -v `pwd`/thirdclient/:/usr/app --rm $(SERVER_IMG_NAME)

client_run:
	docker run -it -v `pwd`/thirdclient/:/usr/app --rm $(SERVER_IMG_NAME) /bin/sh

client_build:
	docker build --tag $(SERVER_IMG_NAME) ./thirdclient

client_up2:
	docker run -v `pwd`/fourthclient/:/usr/app --rm $(SERVER_IMG_NAME)

client_run2:
	docker run -it -v `pwd`/fourthclient/:/usr/app --rm $(SERVER_IMG_NAME) /bin/sh

client_build2:
	docker build --tag $(SERVER_IMG_NAME) ./fourthclient

stop_all_job:
	docker ps -q | xargs -t docker kill