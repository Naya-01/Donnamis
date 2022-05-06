-----------
-- TYPES --
-----------

-- 1
INSERT INTO donnamis.types
    (type_name, is_default)
VALUES ('Accessoires pour animaux domestiques', TRUE);

-- 2
INSERT INTO donnamis.types
    (type_name, is_default)
VALUES ('Accessoires pour voiture', TRUE);

-- 3
INSERT INTO donnamis.types
    (type_name, is_default)
VALUES ('Décoration', TRUE);

-- 4
INSERT INTO donnamis.types
    (type_name, is_default)
VALUES ('Jouets', TRUE);

-- 5
INSERT INTO donnamis.types
    (type_name, is_default)
VALUES ('Literie', TRUE);

-- 6
INSERT INTO donnamis.types
    (type_name, is_default)
VALUES ('Matériel de cuisine', TRUE);

-- 7
INSERT INTO donnamis.types
    (type_name, is_default)
VALUES ('Matériel de jardinage', TRUE);

-- 8
INSERT INTO donnamis.types
    (type_name, is_default)
VALUES ('Meuble', TRUE);

-- 9
INSERT INTO donnamis.types
    (type_name, is_default)
VALUES ('Plantes', TRUE);

-- 10
INSERT INTO donnamis.types
    (type_name, is_default)
VALUES ('Produits cosmétiques', TRUE);

-- 11
INSERT INTO donnamis.types
    (type_name, is_default)
VALUES ('Vélo, trottinette', TRUE);

-- 12
INSERT INTO donnamis.types
    (type_name, is_default)
VALUES ('Vêtements', TRUE);

-------------
-- MEMBERS --
-------------

-- 1
INSERT INTO donnamis.members (username, lastname, firstname, status, role, password, version)
VALUES ('caro', 'Line', 'Caroline', 'valid', 'administrator',
        '$2a$10$/uN1A3Rfob0gxtLFV8yGQOFsy6ToYjvhxy5P51bGGUAX/RAsBa7oy', 1);
INSERT INTO donnamis.addresses
(id_member, building_number, unit_number, street, postcode, commune, version)
VALUES (1, '11', 'B1', 'Rue de l''Eglise', '4987', 'Stoumont', 1);

-- 2
INSERT INTO donnamis.members (username, lastname, firstname, status, role, password, refusal_reason,
                              version)
VALUES ('achil', 'Ile', 'Achille', 'denied', 'member',
        '$2a$10$/uN1A3Rfob0gxtLFV8yGQOFsy6ToYjvhxy5P51bGGUAX/RAsBa7oy',
        'L''application n''est pas encore ouverte à tous.', 1);
INSERT INTO donnamis.addresses (id_member, building_number, street, postcode, commune, version)
VALUES (2, '7', 'Rue de Renkin', '4800', 'Verviers', 1);

-- 3
INSERT INTO donnamis.members (username, lastname, firstname, status, role, password, version)
VALUES ('bazz', 'Ile', 'Basile', 'valid', 'member',
        '$2a$10$/uN1A3Rfob0gxtLFV8yGQOFsy6ToYjvhxy5P51bGGUAX/RAsBa7oy', 1);
INSERT INTO donnamis.addresses
(id_member, building_number, unit_number, street, postcode, commune, version)
VALUES (3, '6', 'A103', 'Rue Haute Folie', '4800', 'Verviers', 1);

-- 4
INSERT INTO donnamis.members (username, lastname, firstname, status, role, password, version)
VALUES ('bri', 'Lehmann', 'Brigitte', 'valid', 'administrator',
        '$2a$10$5lWQsfBTm87t/R7Tjwr.zesVRxlUN7oFvXWf6dDslAw3CFoaQc2Im', 1);
INSERT INTO donnamis.addresses (id_member, building_number, street, postcode, commune, version)
VALUES (4, '13', 'Haut-Vinâve', '4845', 'Jalhay', 1);

-- 5
INSERT INTO donnamis.members (username, lastname, firstname, status, role, password, version)
VALUES ('theo', 'Ile', 'Théophile', 'valid', 'member',
        '$2a$10$7cIBy/uMmQOeglZxCn98reEq6pT0uRYkeQbTEuXQsO76RhCxOxAWK', 1);
INSERT INTO donnamis.addresses (id_member, building_number, street, postcode, commune, version)
VALUES (5, '7', 'Rue de Renkin', '4800', 'Verviers', 1);

-- 6
INSERT INTO donnamis.members (username, lastname, firstname, status, role, password, refusal_reason,
                              version)
VALUES ('emi', 'Ile', 'Emile', 'denied', 'member',
        '$2a$10$BjuXhbdg.l.y91T5Aq63tu5WcqFYfQGFBOwf9ejrp04bFJCMZ1YP6',
        'L’application n’est pas encore ouverte à tous.', 1);
INSERT INTO donnamis.addresses (id_member, building_number, street, postcode, commune, version)
VALUES (6, '47', 'Rue de Verviers', '4000', 'Liège', 1);

-- 7
INSERT INTO donnamis.members (username, lastname, firstname, status, role, password, refusal_reason,
                              version)
VALUES ('cora', 'Line', 'Coralie', 'denied', 'member',
        '$2a$10$0TRcjXOO.nyFi2GG0Z4qjufzJ3nmNZ.hO.1f5hF4Dxlxj0OtS/Nru',
        'Vous devez encore attendre quelques jours.', 1);
INSERT INTO donnamis.addresses (id_member, building_number, unit_number, street, postcode, commune,
                                version)
VALUES (7, '789', 'Bis', 'Rue du salpêtré', '1040', 'Bruxelles', 1);

-- 8
INSERT INTO donnamis.members (username, lastname, firstname, status, role, password, version)
VALUES ('charline', 'Line', 'Charles', 'pending', 'member',
        '$2a$10$1Q9jtDODzRG5gQD8QQ5gUuv1F3Er8yxxSVpzy4kcN6tg1taS0KP0a', 1);
INSERT INTO donnamis.addresses (id_member, building_number, unit_number, street, postcode, commune,
                                version)
VALUES (8, '45', 'Ter', 'Rue des Minières', '4800', 'Verviers', 1);


-------------
-- OBJECTS --
-------------

-- 1
INSERT INTO donnamis.objects (id_type, description, status, id_offeror, image, version)
VALUES (3, 'Décorations de Noël de couleur rouge', 'cancelled', 3, 'img\objects\1.png', 1);
INSERT INTO donnamis.offers (date, time_slot, id_object, version, status)
VALUES ('2022-03-21 00:00:00', 'Mardi de 17h à 22h', 1, 1, 'cancelled');

-- 2
INSERT INTO donnamis.objects (id_type, description, status, id_offeror, image, version)
VALUES (3, 'Cadre représentant un chien noir sur un fond noir.', 'available', 3,
        'img\objects\2.jpg', 1);
INSERT INTO donnamis.offers (date, time_slot, id_object, version, status)
VALUES ('2022-03-25 01:00:00', 'Lundi de 18h à 22h', 2, 1, 'available');

-- 3
INSERT INTO donnamis.objects (id_type, description, status, id_offeror, version, image)
VALUES (8, 'Ancien bureau d’écolier.', 'interested', 4, 1, 'img\objects\3.JPG');
INSERT INTO donnamis.offers (date, time_slot, id_object, version, status)
VALUES ('2022-03-25 02:00:00', 'Tous les jours de 15h à 18h', 3, 1, 'interested');
INSERT INTO donnamis.interests (availability_date, status, id_member, id_object, version,
                                notification_date, send_notification, be_called)
VALUES ('2022-05-16', 'published', 5, 3, 1, NOW(), true, false);
INSERT INTO donnamis.interests (availability_date, status, id_member, id_object, version,
                                notification_date, send_notification, be_called)
VALUES ('2022-05-17', 'published', 3, 3, 1, NOW(), true, false);

-- 4
INSERT INTO donnamis.objects (id_type, description, status, id_offeror, version, image)
VALUES (7, 'brouette à deux roues à l’avant. Améliore la stabilité et ne fatigue pas le dos',
        'interested', 5, 1, 'img\objects\4.jpg');
INSERT INTO donnamis.offers (date, time_slot, id_object, version, status)
VALUES ('2022-03-28 04:00:00', 'Tous les matins avant 11h30', 4, 1, 'interested');
INSERT INTO donnamis.interests (availability_date, status, id_member, id_object, version,
                                notification_date, send_notification, be_called)
VALUES (NOW(), 'published', 4, 4, 1, NOW(), true, false);
INSERT INTO donnamis.interests (availability_date, status, id_member, id_object, version,
                                notification_date, send_notification, be_called)
VALUES (NOW(), 'published', 3, 4, 1, NOW(), true, false);
INSERT INTO donnamis.interests (availability_date, status, id_member, id_object, version,
                                notification_date, send_notification, be_called)
VALUES (NOW(), 'published', 1, 4, 1, NOW(), true, false);

-- 5
INSERT INTO donnamis.objects (id_type, description, status, id_offeror, version)
VALUES (7, 'Scie sur perche Gardena', 'available', 5, 1);
INSERT INTO donnamis.offers (date, time_slot, id_object, version, status)
VALUES ('2022-03-28 05:00:00', 'Tous les matins avant 11h30', 5, 1, 'available');

-- 6
INSERT INTO donnamis.objects (id_type, description, status, id_offeror, version, image)
VALUES (8, 'Table jardin et deux chaises en bois', 'available', 5, 1, 'img\objects\6.jpg');
INSERT INTO donnamis.offers (date, time_slot, id_object, version, status)
VALUES ('2022-03-29 06:00:00', 'En semaine, de 20h à 21h', 6, 1, 'available');

-- 7
INSERT INTO donnamis.objects (id_type, description, status, id_offeror, version, image)
VALUES (8, 'Table bistro', 'available', 5, 1, 'img\objects\7.jpg');
INSERT INTO donnamis.offers (date, time_slot, id_object, version, status)
VALUES ('2022-03-30 07:00:00', 'Lundi de 18h à 20h', 7, 1, 'available');

-- 8
INSERT INTO donnamis.objects (id_type, description, status, id_offeror, version, image)
VALUES (8, 'Table bistro ancienne de couleur bleue', 'interested', 1, 1, 'img\objects\8.jpg');
INSERT INTO donnamis.offers (date, time_slot, id_object, version, status)
VALUES ('2022-04-14 08:00:00', 'Samedi en journée', 8, 1, 'interested');
INSERT INTO donnamis.interests (availability_date, status, id_member, id_object, version,
                                notification_date, send_notification, be_called)
VALUES ('2022-05-14', 'published', 5, 8, 1, NOW(), true, false);
INSERT INTO donnamis.interests (availability_date, status, id_member, id_object, version,
                                notification_date, send_notification, be_called)
VALUES ('2022-05-14', 'published', 4, 8, 1, NOW(), true, false);

-- 9
INSERT INTO donnamis.objects (id_type, description, status, id_offeror, version, image)
VALUES (4, 'Tableau noir pour enfant', 'assigned', 5, 1, 'img\objects\9.jpg');
INSERT INTO donnamis.offers (date, time_slot, id_object, version, status)
VALUES ('2022-04-14 09:00:00', 'Lundi de 18h à 20h', 9, 1, 'assigned');
INSERT INTO donnamis.interests (availability_date, status, id_member, id_object, version,
                                notification_date, send_notification, be_called)
VALUES ('2022-05-16', 'assigned', 1, 9, 1, NOW(), true, false);


-- 10
INSERT INTO donnamis.objects (id_type, description, status, id_offeror, version, image)
VALUES (3, 'Cadre cottage naïf', 'interested', 5, 1, 'img\objects\10.jpg');
INSERT INTO donnamis.offers (date, time_slot, id_object, version, status)
VALUES ('2022-04-21 10:00:00', 'Lundi de 18h30 à 20h', 10, 1, 'interested');
INSERT INTO donnamis.interests (availability_date, status, id_member, id_object, version,
                                notification_date, send_notification, be_called)
VALUES (NOW(), 'published', 1, 10, 1, NOW(), true, false);
INSERT INTO donnamis.interests (availability_date, status, id_member, id_object, version,
                                notification_date, send_notification, be_called)
VALUES (NOW(), 'published', 3, 10, 1, NOW(), true, false);
INSERT INTO donnamis.interests (availability_date, status, id_member, id_object, version,
                                notification_date, send_notification, be_called)
VALUES (NOW(), 'published', 4, 10, 1, NOW(), true, false);

-- 11
INSERT INTO donnamis.objects (id_type, description, status, id_offeror, version, image)
VALUES (6, 'Tasse de couleur claire rose & mauve', 'interested', 5, 1, 'img\objects\11.jpg');
INSERT INTO donnamis.offers (date, time_slot, id_object, version, status)
VALUES ('2022-04-21 11:00:00', 'Lundi de 18h30 à 20h', 11, 1, 'interested');
INSERT INTO donnamis.interests (availability_date, status, id_member, id_object, version,
                                notification_date, send_notification, be_called)
VALUES ('2022-05-16', 'published', 1, 11, 1, NOW(), true, false);
INSERT INTO donnamis.interests (availability_date, status, id_member, id_object, version,
                                notification_date, send_notification, be_called)
VALUES ('2022-05-16', 'published', 3, 11, 1, NOW(), true, false);

-- 12
INSERT INTO donnamis.objects (id_type, description, status, id_offeror, version, image)
VALUES (9, 'Pâquerettes dans pots rustiques', 'assigned', 1, 1, 'img\objects\12.jpg');
INSERT INTO donnamis.offers (date, time_slot, id_object, version, status)
VALUES ('2022-04-21 12:00:00', 'Lundi de 16h à 17h', 12, 1, 'assigned');
INSERT INTO donnamis.interests (availability_date, status, id_member, id_object, version,
                                notification_date, send_notification, be_called)
VALUES ('2022-05-16', 'assigned', 3, 12, 1, NOW(), true, false);

-- 13
INSERT INTO donnamis.objects (id_type, description, status, id_offeror, version, image)
VALUES (9, 'Pots en grès pour petites plantes', 'available', 1, 1, 'img\objects\13.jpg');
INSERT INTO donnamis.offers (date, time_slot, id_object, version, status)
VALUES ('2022-04-21 13:00:00', 'Lundi de 16h à 17h', 13, 1, 'available');