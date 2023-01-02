       DROP TABLE IF EXISTS sushi; 
        CREATE TABLE sushi (  
            id INT AUTO_INCREMENT PRIMARY KEY,  
            name VARCHAR(30),  
            time_to_make INT DEFAULT NULL  
        );  

        DROP TABLE IF EXISTS sushi_order;  
        CREATE TABLE sushi_order (  
            id INT AUTO_INCREMENT PRIMARY KEY,  
            status_id INT NOT NULL,  
            sushi_id INT NOT NULL,  
            createdAt TIMESTAMP NOT NULL default CURRENT_TIMESTAMP  
        );  

        DROP TABLE IF EXISTS status;
        CREATE TABLE status (
            id INT AUTO_INCREMENT PRIMARY KEY,
            name VARCHAR(30) NOT NULL
        );
        DROP TABLE IF EXISTS order_action;
        CREATE TABLE order_action (
            id INT AUTO_INCREMENT PRIMARY KEY,
            order_id INT NOT NULL,
            code INT NOT NULL,
            timeat TIMESTAMP NOT NULL default CURRENT_TIMESTAMP
        );