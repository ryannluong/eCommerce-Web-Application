CREATE SCHEMA idm;

CREATE TABLE idm.token_status
(
    id      INT         NOT NULL PRIMARY KEY,
    value   VARCHAR(32) NOT NULL
);

CREATE TABLE idm.user_status
(
    id      INT         NOT NULL PRIMARY KEY,
    value   VARCHAR(32) NOT NULL
);

CREATE TABLE idm.role
(
    id          INT             NOT NULL PRIMARY KEY,
    name        VARCHAR(32)     NOT NULL,
    description VARCHAR(128)    NOT NULL,
    precedence  INT             NOT NULL
);

CREATE TABLE idm.user
(
    id              INT         NOT NULL PRIMARY KEY AUTO_INCREMENT,
    email           VARCHAR(32) NOT NULL UNIQUE,
    user_status_id  INT         NOT NULL,
    salt            CHAR(8)     NOT NULL,
    hashed_password CHAR(88)    NOT NULL,
    FOREIGN KEY (user_status_id) REFERENCES idm.user_status (id)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE idm.refresh_token
(
    id              INT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    token           CHAR(36)  NOT NULL UNIQUE,
    user_id         INT       NOT NULL,
    token_status_id INT       NOT NULL,
    expire_time     TIMESTAMP NOT NULL,
    max_life_time   TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES idm.user (id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (token_status_id) REFERENCES idm.token_status (id)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE idm.user_role
(
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES idm.user (id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES idm.role (id)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE idm.student
(
    id         INT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(256) NOT NULL,
    last_name  VARCHAR(256) NOT NULL,
    year       INT          NOT NULL,
    gpa        DECIMAL      NOT NULL
);