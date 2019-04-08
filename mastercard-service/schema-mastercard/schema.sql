-------------------------------------------------
-- create new user for the mastercard database --
CREATE ROLE mastercard_service WITH LOGIN PASSWORD 'mastercard_password';

-- allow the user mastercard_service to create the ne database
ALTER ROLE mastercard_service CREATEDB;

-- create a new database for the mastercard service
CREATE DATABASE mastercard_database WITH OWNER mastercard_service;

-- connect to the mastercard_database
\connect mastercard_database;

-- transaction status table
CREATE TABLE IF NOT EXISTS transaction_status(
  id VARCHAR (50) PRIMARY KEY,
  transaction_timestamp TIMESTAMP NOT NULL,
  status VARCHAR (10) NOT NULL
);

-- transaction table
CREATE TABLE IF NOT EXISTS transaction(
  id SERIAL PRIMARY KEY,
  correlation_id VARCHAR (50) REFERENCES transaction_status (id) ON UPDATE NO ACTION ON DELETE NO ACTION,
  mastercard_transaction_id VARCHAR (50) UNIQUE NOT NULL,
  transaction_amount decimal(12,2) NOT NULL,
  transaction_currency VARCHAR (3) NOT NULL,
  client_firstname VARCHAR (20) NOT NULL,
  client_surname VARCHAR (20) NOT NULL,
  client_card_number VARCHAR (50) NOT NULL
);

-- grant CRUD rights to mastercard_service user
GRANT SELECT, INSERT, UPDATE, DELETE
ON ALL TABLES IN SCHEMA public
TO mastercard_service;

--
grant all privileges on all sequences in schema public to mastercard_service;
