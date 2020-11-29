# cognitive-exercises

Web App with games/exercises that might train cognitive skills. Users need to sign up to play. Scores are stored in db and users may compare themselves with others.
Currently there is one game available - memory.

Backend - Spring, frontend - Angular 6, database - MySQL. Db user configuration is stored in database_configure.sql. 
Example data can be loaded by running the app with appropriate profile set. User authentication is handled in Spring using JWT. 
Secret is stored in properties as the app is in dev phase.  
