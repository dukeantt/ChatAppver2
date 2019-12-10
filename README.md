# ChatAppver2
## Requirements
- JDK and JRE
- Docker and Docker compose
- IDE to run java application

## Preparation
Create folder out in ChatAppver2/chatclient/ <br/>
Create folder out in ChatAppver2/thirdclient/ <br/>
Create folder out in ChatAppver2/chatserver/ <br/>

### Database: chatapp.sql
#### Start mysql
Open folder ChatAppver2 in terminal <br/>
Run command docker-compose up -d (sudo docker-compose up -d) <br/>

#### Mysql information
user : 	  root  <br/>
password: 1  <br/>
host :    10.5.0.6  <br/>
database name: chatapp  <br/>

### Step to start server
Open folder ChatAppver2 in terminal <br/>
Step 1: use command "make server_build" (sudo make server_build) <br/>
Step 2: use command "make server_run" (sudo make server_run) <br/>
Step 3: use command "./runserver" <br/>

### Step to start client
Step 1: Open folder chatclient with your IDE <br/>
Step 2: Run the application with IDE <br/>

### Step to start another client
Step 1: Open thirdclient folder with IDE <br/>
Step 2: Open thirdclient folder in your terminal <br/>
Step 3: Use command "make client_build" (sudo make client_build) <br/>
Step 4: use command "make client_run" (sudo make client_run) <br/>
Step 5: Use command "javac -d ./out/ ./src/*.java" to compile the code <br/>
Step 6: Run the file ChatClientGui.class in thirdclient/out/ folder <br/>



