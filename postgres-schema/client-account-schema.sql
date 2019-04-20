-------------------------------------------------
-- create new user for the mastercard database --
CREATE ROLE client_account_service WITH LOGIN PASSWORD 'client_account_password';

-- create a new database for the mastercard service
CREATE DATABASE client_account_database WITH OWNER client_account_service;

-- connect to the mastercard_database
\connect client_account_database;

-- client account table
CREATE TABLE IF NOT EXISTS account(
  id SERIAL PRIMARY KEY,
  balance decimal(12,2) NOT NULL,
  currency VARCHAR (3) NOT NULL,
  card_number VARCHAR (50) UNIQUE NOT NULL
);

-- transaction status table
CREATE TABLE IF NOT EXISTS transaction_status(
  id VARCHAR (50) PRIMARY KEY,
  transaction_timestamp TIMESTAMP NOT NULL,
  status VARCHAR (10) NOT NULL
);

-- grant CRUD rights to mastercard_service user
GRANT SELECT, INSERT, UPDATE, DELETE
ON ALL TABLES IN SCHEMA public
TO client_account_service;

-- grant this too
grant all privileges on all sequences in schema public to client_account_service;
