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



INSERT INTO donnamis.members (username, lastname, firstname, status, role, password)
VALUES ('caro', 'Line', 'Caroline', 'denied', 'member',
        '$2a$10$bBuNIaHXv2Rznvgr89gTOui9z1hTCWhNEWSLUNzEkCU9tR7KIPPKG');

INSERT INTO donnamis.addresses
    (id_member, building_number, unit_number, street, postcode, commune, country)
VALUES (1, '11', 'B1', 'Rue de l''Eglise', '4987', 'Stoumont', 'Belgique');

INSERT INTO donnamis.members (username, lastname, firstname, status, role, password)
VALUES ('achil', 'Ile', 'Achille', 'pending', 'member',
        '$2a$10$bBuNIaHXv2Rznvgr89gTOui9z1hTCWhNEWSLUNzEkCU9tR7KIPPKG');

INSERT INTO donnamis.addresses (id_member, building_number, street, postcode, commune, country)
VALUES (2, '7', 'Rue de Renkin', '4800', 'Verviers', 'Belgique');

INSERT INTO donnamis.members (username, lastname, firstname, status, role, password)
VALUES ('bazz', 'Ile', 'Basile', 'valid', 'member',
        '$2a$10$bBuNIaHXv2Rznvgr89gTOui9z1hTCWhNEWSLUNzEkCU9tR7KIPPKG');

INSERT INTO donnamis.addresses
    (id_member, building_number, unit_number, street, postcode, commune,country)
VALUES (3, '6', 'A103', 'Rue Haute Folie', '4800', 'Verviers', 'Belgique');

INSERT INTO donnamis.members (username, lastname, firstname, status, role, password)
VALUES ('bri', 'Lehmann', 'Brigitte', 'valid', 'administrator',
        '$2a$10$UJg.Xn2dVv78cuNN0/e/1.my5aCISCt1W1hN.JaLSmDqcqhE4vpGK');

INSERT INTO donnamis.addresses (id_member, building_number, street, postcode, commune, country)
VALUES (4, '13', 'Haut-Vinâve', '4845', 'Jalhay', 'Belgique');



INSERT INTO donnamis.objects (id_type, description, status, id_offeror, image)
VALUES (3, 'Décorations de Noël de couleur rouge', 'available', 3, 'img\objects\1.jpeg');
INSERT INTO donnamis.offers (date, time_slot, id_object)
VALUES ('2022-03-21', 'Mardi de 17h à 22h', 1);

INSERT INTO donnamis.objects (id_type, description, status, id_offeror, image)
VALUES (3, 'Cadre représentant un chien noir sur un fond noir.', 'available', 3, 'img\objects\2.jpeg');
INSERT INTO donnamis.offers (date, time_slot, id_object)
VALUES ('2022-03-25', 'Lundi de 18h à 22h', 2);

INSERT INTO donnamis.objects (id_type, description, status, id_offeror)
VALUES (8, 'Ancien bureau d’écolier.', 'available', 4);
INSERT INTO donnamis.offers (date, time_slot, id_object)
VALUES ('2022-03-25', 'Tous les jours de 15h à 18h', 3);

INSERT INTO donnamis.objects (id_type, description, status, id_offeror)
VALUES (8, 'Objet recu par lehmann', 'given', 1);
INSERT INTO donnamis.offers (date, time_slot, id_object)
VALUES ('2022-03-25', 'Tous les jours de 15h à 18h', 4);

INSERT INTO donnamis.interests (availability_date, status, id_member, id_object)
VALUES (NOW(), 'received', 4, 4);