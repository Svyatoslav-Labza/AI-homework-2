-- Drop tables if they exist (in reverse order of dependencies)
DROP TABLE IF EXISTS auth_users CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS addresses CASCADE;
DROP TABLE IF EXISTS companies CASCADE;
DROP TABLE IF EXISTS geo CASCADE;

-- Create geo table
CREATE TABLE geo (
    id SERIAL PRIMARY KEY,
    lat VARCHAR(20) NOT NULL,
    lng VARCHAR(20) NOT NULL
);

-- Create addresses table
CREATE TABLE addresses (
    id SERIAL PRIMARY KEY,
    street VARCHAR(100) NOT NULL,
    suite VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    zipcode VARCHAR(20) NOT NULL,
    geo_id INTEGER REFERENCES geo(id) ON DELETE SET NULL
);

-- Create companies table
CREATE TABLE companies (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    catch_phrase VARCHAR(200) NOT NULL,
    bs VARCHAR(200) NOT NULL
);

-- Create users table
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    username VARCHAR(50),
    email VARCHAR(100) NOT NULL,
    address_id INTEGER REFERENCES addresses(id) ON DELETE SET NULL,
    phone VARCHAR(50),
    website VARCHAR(100),
    company_id INTEGER REFERENCES companies(id) ON DELETE SET NULL
);

-- Create auth_users table
CREATE TABLE auth_users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT auth_users_email_unique UNIQUE (email)
); 