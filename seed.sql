INSERT INTO donnamis.members
    (username, lastname, firstname, status, role, password)
VALUES ('MarcDeBruxelles', 'Jacky', 'Marc', 'pending', 'member',
        '$2a$10$eMsxJ.LI75F.CIQXRXkrL.8k/Ss/ogrBCGo/8Ez8lJF99LdqmeKdm');

INSERT INTO donnamis.members
    (username, lastname, firstname, status, role, password)
VALUES ('AlbertDeBucarest', 'Dubous', 'Albert', 'valid', 'member',
        '$2a$10$tTjy7rCWdNsyQyIc8zoGrumHZvvGSOBwwf97J7PWMqoU5SVtR/0Fa');

INSERT INTO donnamis.members
    (username, lastname, firstname, status, role, password)
VALUES ('GaspardDePorto', 'Pepe', 'Gaspard', 'valid', 'administrator',
        '$2a$10$jTlPWXCbch1wjY34rQbjY.aoRxYr/Hq1MBeE2htTRQQ6q3lk5GpTm');

INSERT INTO donnamis.members
(username, lastname, firstname, status, role, password, refusal_reason)
VALUES ('JoshuaDeMadrid', 'Capri', 'Joshua', 'denied', 'member',
        '$2a$10$JxDzVtxFgNkecl.AxXgZUu75dU2iedqVk7Yzj2SDZkAsRvlqey4I2', 'Je ne te connais pas.');

INSERT INTO donnamis.addresses (id_member, building_number, street, postcode, commune, country)
VALUES (1, '23a', 'rue des paquerettes', '1420', 'Braine l''Alleud', 'Belgique');

INSERT INTO donnamis.addresses (id_member, building_number, street, postcode, commune, country)
VALUES (2, '78', 'chaussée de Waterloo', '1410', 'Waterloo', 'Belgique');

INSERT INTO donnamis.addresses (id_member, building_number, street, postcode, commune, country)
VALUES (3, '1000', 'avenue Louise', '1000', 'Bruxelles', 'Belgique');

INSERT INTO donnamis.addresses (id_member, building_number, street, postcode, commune, country)
VALUES (4, '3', 'avenue Rombault', '1420', 'Braine l''Alleud', 'Belgique');

INSERT INTO donnamis.types
    (type_name, is_default)
VALUES ('Accessoires pour animaux domestiques', TRUE);

INSERT INTO donnamis.types
    (type_name, is_default)
VALUES ('Accessoires pour voiture', TRUE);

INSERT INTO donnamis.types
    (type_name, is_default)
VALUES ('Décoration', TRUE);

INSERT INTO donnamis.types
    (type_name, is_default)
VALUES ('Jouets', TRUE);

INSERT INTO donnamis.types
    (type_name, is_default)
VALUES ('Literie', TRUE);

INSERT INTO donnamis.types
    (type_name, is_default)
VALUES ('Matériel de cuisine', TRUE);

INSERT INTO donnamis.types
    (type_name, is_default)
VALUES ('Matériel de jardinage', TRUE);

INSERT INTO donnamis.types
    (type_name, is_default)
VALUES ('Meuble', TRUE);

INSERT INTO donnamis.types
    (type_name, is_default)
VALUES ('Plantes', TRUE);

INSERT INTO donnamis.types
    (type_name, is_default)
VALUES ('Produits cosmétiques', TRUE);

INSERT INTO donnamis.types
    (type_name, is_default)
VALUES ('Vélo, trottinette', TRUE);

INSERT INTO donnamis.types
    (type_name, is_default)
VALUES ('Vêtements', TRUE);

INSERT INTO donnamis.objects
    (id_type, description, status, id_offeror)
VALUES (4, 'Une fusée tintin', 'interested', 2);

INSERT INTO donnamis.offers
    (date, time_slot, id_object)
VALUES (NOW(), 'Je suis disponible le dimanche soir à partir de 17h', 1);

INSERT INTO donnamis.objects
    (id_type, description, status, id_offeror)
VALUES (4, 'Jouet lego', 'interested', 2);

INSERT INTO donnamis.offers
    (date, time_slot, id_object)
VALUES (NOW(), 'Disponible à chaque heure de la semaine', 2);