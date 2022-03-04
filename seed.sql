INSERT INTO donnamis.addresses (building_number, street, postcode, commune)
VALUES ('23a', 'rue des paquerettes', '1420', 'Braine l''Alleud');

INSERT INTO donnamis.addresses (building_number, street, postcode, commune)
VALUES ('78', 'chaussée de Waterloo', '1410', 'Waterloo');

INSERT INTO donnamis.addresses (building_number, street, postcode, commune)
VALUES ('1000', 'avenue Louise', '1000', 'Bruxelles');

INSERT INTO donnamis.addresses (building_number, street, postcode, commune)
VALUES ('3', 'avenue Rombault', '1420', 'Braine l''Alleud');

INSERT INTO donnamis.members (username, lastname, firstname, status, role, password, id_addresse)
VALUES ('mehdi', 'Mehdi', 'Bouchbouk', 'valid', 'administrator', '$2a$10$v2MrdWQxqkSYAoHuAlX5lOL8mlHZtAhB0zMHU5gPfmY0iX8RT5Rfe', 1);

INSERT INTO donnamis.members (username, lastname, firstname, status, role, password, id_addresse)
VALUES ('tchoupi', 'Rayan', 'Abarkan', 'valid', 'member', '$2a$10$v2MrdWQxqkSYAoHuAlX5lOL8mlHZtAhB0zMHU5gPfmY0iX8RT5Rfe', 2);

INSERT INTO donnamis.members (username, lastname, firstname, status, role, password, id_addresse)
VALUES ('stef42', 'Stefan', 'Mircovici', 'denied', 'member', '$2a$10$v2MrdWQxqkSYAoHuAlX5lOL8mlHZtAhB0zMHU5gPfmY0iX8RT5Rfe', 3);

INSERT INTO donnamis.members (username, lastname, firstname, status, role, password, id_addresse)
VALUES ('npm i', 'Nicolas', 'Poppe', 'pending', 'member', '$2a$10$v2MrdWQxqkSYAoHuAlX5lOL8mlHZtAhB0zMHU5gPfmY0iX8RT5Rfe', 4);