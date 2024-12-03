ALTER TABLE smart_city.users
    ADD password VARCHAR(255) NULL;

ALTER TABLE smart_city.users
    ADD `role` VARCHAR(255) NULL;

ALTER TABLE smart_city.users
    DROP COLUMN password_hash;

ALTER TABLE smart_city.users
    MODIFY created_at datetime NULL;

ALTER TABLE smart_city.users
    MODIFY email VARCHAR(255);

ALTER TABLE smart_city.users
    MODIFY email VARCHAR(255) NULL;

ALTER TABLE smart_city.users
    MODIFY full_name VARCHAR(255);

ALTER TABLE smart_city.users
    MODIFY updated_at datetime NULL;

ALTER TABLE smart_city.users
    MODIFY username VARCHAR(255);

ALTER TABLE smart_city.users
    MODIFY username VARCHAR(255) NULL;