CREATE TYPE mission_status AS ENUM ('pending', 'active', 'complete');

CREATE TYPE mission_priority AS ENUM ('low', 'medium', 'high');

CREATE TYPE mission_category AS ENUM (
	'rescue',
	'assassination',
	'intel_gathering',
	'monitoring',
	'neutralization',
	'et_response',
	'recovery'
);


CREATE TABLE hero (
	hero_id SERIAL PRIMARY KEY,
	hero_name VARCHAR(100) NOT NULL
);

CREATE TABLE mission (
	mission_id SERIAL PRIMARY KEY,
	title VARCHAR(100) NOT NULL,
	description TEXT,
	status mission_status NOT NULL DEFAULT 'pending',
	priority mission_priority NOT NULL DEFAULT 'medium',
	category mission_category NOT NULL,
	hero_id INTEGER REFERENCES hero (hero_id),
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO hero (hero_name) VALUES
	('Clint Barton'),
	('Natasha Romanoff'),
	('Phil Coulson'),
	('Maria Hill'),
	('Daisy Johnson'),
	('Melinda May'),
	('Leo Fitz'),
	('Jemma Simmons'),
	('Grant Ward'),
	('Sharon Carter');

SELECT * FROM hero;