-- CodeQuest Test Data Script
-- Run this in MySQL to create test users for the application

USE codyquest;

-- Insert a test user
INSERT INTO users (nom_complet, email, password, role, age, niveau_info, bio, education, experience, is_admin, is_banned, formation, created_at, xp)
VALUES (
    'Test User',
    'test@example.com',
    '$2a$12$0PrxrV/Ks6gB3FqKnT0m.eHzHJRXXv1sQZmpkCHH1X8uW1K7Ai8J2',  -- Password: 'test123' (pre-hashed with BCrypt)
    'Student',
    25,
    'Intermediate',
    'A test user for the CodeQuest application',
    'Computer Science',
    '2 years',
    0,
    0,
    'Full-Stack Development',
    NOW(),
    1500
);

-- Also create an admin test user
INSERT INTO users (nom_complet, email, password, role, age, niveau_info, bio, education, experience, is_admin, is_banned, formation, created_at, xp)
VALUES (
    'Admin User',
    'admin@example.com',
    '$2a$12$0PrxrV/Ks6gB3FqKnT0m.eHzHJRXXv1sQZmpkCHH1X8uW1K7Ai8J2',  -- Password: 'test123' (pre-hashed with BCrypt)
    'Admin',
    30,
    'Expert',
    'Admin user for system management',
    'Computer Science',
    '5 years',
    1,
    0,
    'Enterprise Development',
    NOW(),
    5000
);

SELECT * FROM users;
