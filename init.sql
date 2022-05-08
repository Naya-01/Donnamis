DROP SCHEMA IF EXISTS donnamis CASCADE;

CREATE SCHEMA donnamis;

CREATE TABLE donnamis.members
(
    id_member      SERIAL PRIMARY KEY,
    username       VARCHAR(50)  NOT NULL,
    lastname       VARCHAR(50)  NOT NULL,
    firstname      VARCHAR(50)  NOT NULL,
    status         VARCHAR(15)  NOT NULL,
    role           VARCHAR(15)  NOT NULL,
    phone_number   VARCHAR(50)  NULL,
    password       CHAR(60)     NOT NULL,
    refusal_reason VARCHAR(50)  NULL,
    image          VARCHAR(100) NULL,
    version        INTEGER      NOT NULL
);

CREATE TABLE donnamis.addresses
(
    id_member       INTEGER REFERENCES donnamis.members (id_member),
    unit_number     VARCHAR(15) NULL,
    building_number VARCHAR(15) NOT NULL,
    street          VARCHAR(50) NOT NULL,
    postcode        VARCHAR(15) NOT NULL,
    commune         VARCHAR(50) NOT NULL,
    version         INTEGER     NOT NULL,
    PRIMARY KEY (id_member)
);

CREATE TABLE donnamis.types
(
    id_type    SERIAL PRIMARY KEY,
    type_name  VARCHAR(50) NOT NULL,
    is_default BOOLEAN     NOT NULL
);

CREATE TABLE donnamis.objects
(
    id_object   SERIAL PRIMARY KEY,
    id_type     INTEGER REFERENCES donnamis.types (id_type)     NOT NULL,
    description VARCHAR(100)                                    NOT NULL,
    status      VARCHAR(50)                                     NOT NULL,
    image       VARCHAR(100)                                    NULL,
    id_offeror  INTEGER REFERENCES donnamis.members (id_member) NOT NULL,
    version     INTEGER                                         NOT NULL
);

CREATE TABLE donnamis.ratings
(
    rating    INTEGER                                         NOT NULL,
    comment   VARCHAR(200)                                    NOT NULL,
    id_member INTEGER REFERENCES donnamis.members (id_member) NOT NULL,
    id_object INTEGER REFERENCES donnamis.objects (id_object) NOT NULL,
    PRIMARY KEY (id_object)
);

CREATE TABLE donnamis.interests
(
    availability_date DATE                                            NOT NULL,
    status            VARCHAR(15)                                     NOT NULL,
    id_member         INTEGER REFERENCES donnamis.members (id_member) NOT NULL,
    id_object         INTEGER REFERENCES donnamis.objects (id_object) NOT NULL,
    send_notification BOOLEAN                                         NOT NULL,
    notification_date TIMESTAMP                                       NOT NULL,
    be_called         BOOLEAN                                         NOT NULL,
    version           INTEGER                                         NOT NULL,
    PRIMARY KEY (id_object, id_member)
);

CREATE TABLE donnamis.offers
(
    id_offer  SERIAL PRIMARY KEY,
    date      TIMESTAMP                                       NOT NULL,
    time_slot VARCHAR(200)                                    NOT NULL,
    status    VARCHAR(200)                                    NOT NULL,
    id_object INTEGER REFERENCES donnamis.objects (id_object) NOT NULL,
    version   INTEGER                                         NOT NULL
);