README

Backend jar file for docker is "cognitive-exercises-web.jar" created with "mvn clean package", it already contains 
data module as its dependency.

After running "docker compose up" one needs to enter backend centos container by using
"docker exec -it {container_name} bash" and run the cognitive-exercises app by using one of the scripts:
	- run-cog-ex-dev-mysql.sh
	- run-cog-ex-dev-mysql-bootstrap.sh (to put sample data into db)
	
On the first run, before running "run-cog-ex-dev-mysql-bootstrap.sh" one should prepare database:
	- CREATE DATABASE cognitive_exercises_dev;
	- create user "cognitive_exercises_dev_admin" with password "admin" and full DDL & DML access

Files:
	- docker-entrypoint.sh
	- wait-for-it.sh
Are not used, they were left for educational purposes. Using them allows docker container to wait for 
another container to be ready - port of the other service needs to be set as environment variable. In our
case it could be useful to wait for db as spring app will shut down when no connection can be achieved on start.
This was not used as cognitive exercises app needs to be run twice with different profiles (to introduce
data to datatabase on the first run), so the best solution turned out to be to not run anything on container start.



	
