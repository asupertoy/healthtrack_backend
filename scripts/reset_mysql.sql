-- DANGER: Drops and recreates the healthtrackdb schema.
-- Use ONLY on local development environment.
DROP DATABASE IF EXISTS healthtrackdb;
CREATE DATABASE healthtrackdb CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

