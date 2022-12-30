CREATE TABLE users
(
    user_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT UNIQUE,
    password TEXT,
    comment TEXT
);

CREATE TABLE manufacturers
(
    manufacturer_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT UNIQUE
);

CREATE TABLE categories
(
    category_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT
);

CREATE TABLE models
(
    model_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    uuid TEXT UNIQUE,
    name TEXT,
    year INT,
    manufacturer_id BIGINT REFERENCES manufacturers(manufacturer_id)
);

CREATE TABLE models_categories
(
    model_category_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    model_id BIGINT REFERENCES models(model_id) ON DELETE CASCADE,
    category_id BIGINT REFERENCES categories(category_id) ON DELETE CASCADE,
    UNIQUE(model_id, category_id)
);

INSERT INTO users(name, password, comment)
VALUES ('root', '$2a$12$v3DzMxIX2A2fYjywtDJfGuZfHny6IkM/y8C7htyRQ3d0y1pG9cQeW', 'Default administrator account');