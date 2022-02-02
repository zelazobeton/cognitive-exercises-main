# cognitive-exercises-main

cognitive-exercises-main is a service responsible mostly for storing user data and managing requests to keycloak authorization server that are not done directly from frontend. It uses mysql database.

## About cognitive-exercises app in general

cognitive-exercises is a microservice architecture web app with games/exercises that might train cognitive skills. Users need to sign up to play. Scores are stored in db and users may 
compare themselves with others. Currently there is one game available - memory.

## Running in docker 

Docker compose file is not to be run on its own. It should be copied into docker compose file that runs all cognitive-exercises microservices. 

Backend jar file for docker is "cognitive-exercises-web.jar" created with "mvn clean package", it already contains 
data module as its dependency.

After running "docker compose up" one needs to enter backend centos container by using
"docker exec -it {container_name} bash" and run the cognitive-exercises-main app by using one of the scripts:
  *  run-cog-ex-dev-mysql-bootstrap.sh (to put sample data into db)
  *  run-cog-ex-dev-mysql.sh (running after sample data is put)
	
On the first run, before running "run-cog-ex-dev-mysql-bootstrap.sh" one should prepare database:
  *  CREATE DATABASE cognitive_exercises_dev;
  *  create user "cognitive_exercises_dev_admin" with password "admin" and full DDL & DML access
