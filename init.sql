DROP SCHEMA IF EXISTS donnamis CASCADE ;

CREATE SCHEMA donnamis;

CREATE TABLE donnamis.adresses (
    id_adresse  SERIAL      PRIMARY KEY,
    boite       VARCHAR(15) NULL,
    numero      VARCHAR(15) NOT NULL,
    rue         VARCHAR(50) NOT NULL,
    code_postal VARCHAR(15) NOT NULL,
    commune     VARCHAR(50) NOT NULL
);

CREATE TABLE donnamis.membres (
    id_membre       SERIAL      PRIMARY KEY,
    pseudo          VARCHAR(50) NOT NULL,
    nom             VARCHAR(50) NOT NULL,
    prenom          VARCHAR(50) NOT NULL,
    etat            VARCHAR(15) NOT NULL,
    role            VARCHAR(15) NOT NULL,
    telephone       VARCHAR(50) NULL,
    id_adresse      INTEGER     REFERENCES donnamis.adresses(id_adresse) NOT NULL,
    raison_refus    VARCHAR(50) NULL
);

CREATE TABLE donnamis.types (
    id_type         SERIAL      PRIMARY KEY,
    type            VARCHAR(50) NOT NULL,
    est_par_defaut  BOOLEAN     NOT NULL
);

CREATE TABLE donnamis.objets (
    id_objet    SERIAL          PRIMARY KEY,
    id_type     INTEGER         REFERENCES donnamis.types (id_type) NOT NULL,
    description VARCHAR(100)    NOT NULL,
    etat        VARCHAR(10)     NOT NULL,
    image       BYTEA           NULL,
    id_offreur  INTEGER         REFERENCES donnamis.membres (id_membre) NOT NULL
);

CREATE TABLE donnamis.notes (
    score       INTEGER         NOT NULL,
    commentaire VARCHAR(100)    NOT NULL,
    id_membre   INTEGER         REFERENCES donnamis.membres (id_membre) NOT NULL,
    id_objet    INTEGER         REFERENCES donnamis.objets (id_objet) NOT NULL,
    PRIMARY KEY (id_objet, id_membre)
);

CREATE TABLE donnamis.interets (
    date_disponibilite  DATE        NOT NULL,
    etat                VARCHAR(15) NOT NULL,
    id_membre           INTEGER     REFERENCES donnamis.membres (id_membre) NOT NULL,
    id_objet            INTEGER     REFERENCES donnamis.objets (id_objet) NOT NULL,
    PRIMARY KEY (id_objet, id_membre)
);

CREATE TABLE donnamis.offres (
    id_offre        SERIAL      PRIMARY KEY,
    date            DATE        NOT NULL,
    plage_horaire   VARCHAR(50) NOT NULL,
    id_objet        INTEGER     REFERENCES donnamis.objets (id_objet) NOT NULL
);