# Database Design

## Overview
The database name should be __DailyNUS__ with the following tables inside:
- Users
- 

## Create Tables
Run the following SQL query to set up all the tables.

- users<br>

The _users_ table is the central table. It includes the _Id_ column, which becomes the unique user identifier (UID). It also includes the create time, username, password, email, name, nus_username, nus_password, year, faculty, major.

```
CREATE TABLE users (
	id int AUTO_INCREMENT PRIMARY KEY,
	create_time TIMESTAMP,
	username varchar(50) NOT NULL,
	password varchar(150) NOT NULL,
	email varchar(200) NOT NULL,
	person_name varchar(100),
	nus_username varchar(50),
	nus_password varchar(150),
	year int,
	faculty varchar(70),
	major varchar(50)
)
```

- user_modules<br>

The _user_modules_ tables stores the modules each user (student) takes. It stores the module codes and 