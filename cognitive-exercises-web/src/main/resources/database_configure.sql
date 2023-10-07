CREATE DATABASE cognitive_exercises_dev;

CREATE USER 'cognitive_exercises_dev_user'@'localhost' IDENTIFIED BY 'user';
CREATE USER 'cognitive_exercises_dev_admin'@'%' IDENTIFIED BY 'admin';

GRANT SELECT ON cognitive_exercises_dev.* to 'cognitive_exercises_dev_user'@'localhost';
GRANT INSERT ON cognitive_exercises_dev.* to 'cognitive_exercises_dev_user'@'localhost';
GRANT DELETE ON cognitive_exercises_dev.* to 'cognitive_exercises_dev_user'@'localhost';
GRANT UPDATE ON cognitive_exercises_dev.* to 'cognitive_exercises_dev_user'@'localhost';

GRANT SELECT ON cognitive_exercises_dev.* to 'cognitive_exercises_dev_admin'@'%';
GRANT INSERT ON cognitive_exercises_dev.* to 'cognitive_exercises_dev_admin'@'%';
GRANT DELETE ON cognitive_exercises_dev.* to 'cognitive_exercises_dev_admin'@'%';
GRANT UPDATE ON cognitive_exercises_dev.* to 'cognitive_exercises_dev_admin'@'%';
GRANT CREATE ON cognitive_exercises_dev.* to 'cognitive_exercises_dev_admin'@'%';
GRANT ALTER ON cognitive_exercises_dev.* to 'cognitive_exercises_dev_admin'@'%';
GRANT EXECUTE ON cognitive_exercises_dev.* to 'cognitive_exercises_dev_admin'@'%';
GRANT CREATE VIEW ON cognitive_exercises_dev.* to 'cognitive_exercises_dev_admin'@'%';
GRANT DROP ON cognitive_exercises_dev.* to 'cognitive_exercises_dev_admin'@'%';
GRANT REFERENCES ON cognitive_exercises_dev.* to 'cognitive_exercises_dev_admin'@'%';