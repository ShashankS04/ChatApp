CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    avatar_url VARCHAR(500),
    status VARCHAR(20) DEFAULT 'OFFLINE',
    last_seen TIMESTAMP,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS roles (
    id serial NOT NULL,
    name varchar(20) check (name in ('ROLE_USER','ROLE_MODERATOR','ROLE_ADMIN')),
    primary key (id)
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id bigint NOT NULL,
    role_id integer NOT NULL,
    primary key (user_id, role_id)
);

CREATE TABLE IF NOT EXISTS refresh_tokens (
    id bigserial not null,
    expiry_date timestamp(6) with time zone not null,
    token varchar(255) not null,
    user_id bigint,
    primary key (id)
);

CREATE TABLE IF NOT EXISTS otps (
    id bigserial not null,
    otp varchar(255),
    expiration_time timestamp(6) with time zone,
    user_id bigint,
    primary key (id)
);

INSERT INTO roles(name) VALUES('ROLE_USER');
INSERT INTO roles(name) VALUES('ROLE_MODERATOR');
INSERT INTO roles(name) VALUES('ROLE_ADMIN');