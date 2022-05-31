CREATE DATABASE crm_db;

\c crm_db;

CREATE TYPE methods AS ENUM ('GET', 'POST', 'PUT', 'PATCH', 'DELETE');
CREATE TABLE tbl_activities (
	id SERIAL,
    method methods,
	url VARCHAR(1024) NOT NULL,
	PRIMARY KEY(id)
);
INSERT INTO tbl_activities (id, method,  url) VALUES
(1, 'GET', '/api/v1/users'),
(2, 'GET', '/api/v1/users/{id}'),
(3, 'POST', '/api/v1/users'),
(4, 'PUT', '/api/v1/users/{id}'),
(5, 'PATCH', '/api/v1/users/{id}'),
(6, 'DELETE', '/api/v1/users/{id}');


CREATE TYPE permissions AS ENUM ('VIEW', 'EDIT', 'DELETE', 'APPROVE');
CREATE TABLE tbl_permissions (
	id SERIAL,
	name permissions,
    enabled BOOLEAN,
	created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY(id)
);
INSERT INTO tbl_permissions (id, name, enabled) VALUES
(1, 'VIEW', true),
(2, 'EDIT', true),
(3, 'DELETE', true),
(4, 'APPROVE', true);


CREATE TABLE tbl_roles (
	id SERIAL,
	name VARCHAR(255) NOT NULL,
    enabled BOOLEAN,
	created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	UNIQUE (name),
	PRIMARY KEY(id)
);
INSERT INTO tbl_roles (id, name, enabled) VALUES
(1, 'SYSTEM_ADMIN', true),
(2, 'ROLE_MAKER', true),
(3, 'ROLE_CHECKER', true),
(4, 'ROLE_MANAGER', true);


CREATE TABLE tbl_role_permission_activities (
	role_id INT NOT NULL,
	permission_id INT NOT NULL,
	activity_id INT NOT NULL,
	PRIMARY KEY(role_id, permission_id, activity_id),
	CONSTRAINT tbl_role_permission_activities_fk1 FOREIGN KEY (role_id) REFERENCES tbl_roles(id),
	CONSTRAINT tbl_role_permission_activities_fk2 FOREIGN KEY (permission_id) REFERENCES tbl_permissions(id),
	CONSTRAINT tbl_role_permission_activities_fk3 FOREIGN KEY (activity_id) REFERENCES tbl_activities(id)
);
INSERT INTO tbl_role_permission_activities (role_id, permission_id, activity_id) VALUES
(1, 1, 1),
(1, 1, 2),
(1, 2, 3),
(1, 2, 4),
(1, 2, 5),
(1, 3, 6),
(4, 2, 3);


CREATE TABLE tbl_users (
	id SERIAL,
	username VARCHAR(50) NOT NULL,
	password VARCHAR(255) NOT NULL,
	created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY(id),
	UNIQUE(username)
);
INSERT INTO tbl_users (id, username, password) VALUES
(1, 'sysadmin', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6'),
(2, 'maker', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6'),
(3, 'checker', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6'),
(4, 'manager', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6');


CREATE TABLE tbl_user_roles (
	user_id INT NOT NULL,
	role_id INT NOT NULL,
    PRIMARY KEY(user_id, role_id),
	CONSTRAINT tbl_user_role_fk1 FOREIGN KEY (user_id) REFERENCES tbl_users(id),
	CONSTRAINT tbl_user_role_fk2 FOREIGN KEY (role_id) REFERENCES tbl_roles(id)
);
INSERT INTO tbl_user_roles (user_id, role_id) VALUES (1, 1),(4, 4);

