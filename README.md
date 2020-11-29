# cognitive-exercises

Web App with games/exercises that might train cognitive skills. Users need to sign up to play. Scores are stored in db and users may compare themselves with others.
Currently there is one game available - memory.

Backend - Spring, frontend - Angular 6. Both in the same repo but in separate directories. Backend commit messages start with "BE:", frontend with "FE:". 
Database - MySQL. Db user configuration is stored in database_configure.sql. Example data can be loaded by running the app with appropriate profile set. Games and user data (avatars, images) are stored in files. Database stores URL addresses to be used when fetching images. User authentication is handled in Spring using JWT. Secret is stored in properties as the app is in dev phase.  
