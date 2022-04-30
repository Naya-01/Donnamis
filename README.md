# DONNAMIS Projet AE groupe 12

# Comment installer l'application ?

- Si vous ne l'avez pas fait, vous pouvez cloner le repo pour initier votre
  application : `git clone https://gitlab.vinci.be/6i2-cae/2021-2022/projet-ae-groupe-12.git`

## Base de données

Le back-end requiert l'utilisation d'une base de donnée. Pour configurer votre base de données nous
vous fournissons un script dans le repository nommé "init" qui permet d'établir les tables ainsi qu'
un autre script nommé "seed" qui permet de remplir ces tables.

## Utilisation d'un fichier de configuration

- Attention pour utiliser notre Application vous devez spécifier un accès pour la base de donnée qui
  comprendra les même tables
- Pour se faire, vous devez créer un fichier .properties à la racine du repository et y mettre les
  données suivantes

```shell
BaseUri = <Server IP>:<Port>
dbUrl = <DatabaseURL>
dbUser = <DatabaseUsername>
dbPassword = <DatabasePassword>
JWTSecret = <JWTSecret>
ImagePath= <Path> Example : C:\\Server\\data\\
```

## Comment utiliser le back-end ?

- Il faut d'abord démarrer le back-end

Vous retrouverez dans le chemin suivant un fichier Main.java

```
projet-ae-groupe-12/src/main/java/be/vinci/pae/main
```

Vous devez démarrer ce fichier une fois que le fichier properties sera défini

## Installation des dépendances et démarrage du front-end :

A présent il faut démarrer le front-end comme ceci :

```shell
cd projet-ae-groupe-12/frontend
npm i # (equivalent de npm install)
npm start
```

### Vous utiliserez l'adresse ip du serveur et le port associé pour explorer les routes du back-end.

### Concernant le front-end vous utiliserez l'adresse IP du serveur et le port 3000