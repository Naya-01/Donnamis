# Boilerplate pour vos applications modernes utilisant Webpack ainsi qu'un router
## How to use ?
- Si vous ne l'avez pas fait, vous pouvez cloner le repo associé au boilerplate pour initier votre application : `git clone https://github.com/e-vinci/js-router-boilerplate.git` ou `git clone https://github.com/e-vinci/js-router-boilerplate.git nom-de-votre-projet` pour créer votre projet nommé `nom-de-votre-projet`.
- Une fois le clone fait, Git vous a créé un repo local de votre projet ; n'hésitez pas à ajouter une `remote` pour pointer vers votre Web repository.
- Si vous avez cloné votre projet au sein d'un repo existant, Git ne trackera pas ce nouveau projet ; pour vous assurer que Git traque votre projet, vous devez effacer le répertoire `.git` se trouvant dans votre nouveau projet. N'hésitez pas aussi à effacer `.gitignore` se trouvant dans votre nouveau projet.
- Installation des dépendances et démarrage du boilerplate : 
```shell
cd nom-de-votre-projet # (le nom donné au répertoire de votre projet)
npm i # (equivalent de npm install)
npm start
```
## How to ? Ajout d'un package
- Installation d'un package : `npm i nomDuPackage`
Pour plus d'info sur un package, ou pour trouver un package traitant d'un sujet qui vous intéresse : https://www.npmjs.com
- Modification du code pour l'utiliser, au sein de `/src/index.js` (ou tout autre module .js) : chargement de la librairie soit via `import` (ou `require`) du package. Généralement, les instructions d'installation et d'utilisation d'un package sont données sur le site de https://www.npmjs.com.
- Si quelqu'un souhaite installer et exécuter ce projet, la gestion des dépendances est très simple : copie du répertoire du projet (sans `node_modules`), `npm instal`, `npm start`. Il n'y a donc pas de librairies à gérer manuellement pour reprendre le projet d'un tiers.

## Utilisation d'assets (images, fonts, CSS...)
- Attention, quand vous utilisez des assets au sein de votre application, comme des images, l'URL d'un asset après le build de votre application n'est pas la même qu'avant le build. 
- Pour bien gérer les URL au sein de votre JavaScript, vous devez d'abord importer vos assets. Voici un exemple pour ajouter une image dynamiquement au sein d'un footer : 
```javascript
import logo from '.src/img/icon.png';
const footerPhoto = new Image(); // or document.createElement('img');
footerPhoto.src = logo;
footerPhoto.height = 50;
const footer = document.querySelector("footer");
footer.appendChild(footerPhoto);
```
- Plus d'information sur la gestion des assets via Webpack : https://webpack.js.org/guides/asset-management/ 

## Tout savoir sur le Routeur offert dans ce boilerplate
- Le rôle du nouveau routeur `/Components/Router/Router.js` sera d'implémenter ces fonctions :
    - Routage lors d’un clic sur un élément de la barre de navigation via `navbarWrapper.addEventListener("click",...)` :
        - Appel du composant associé à l’élément cliqué (et auto-render du composant)
        - Affichage dans le browser de l’URL associée à l’élément cliqué
        - Garder l’URL dans l’historique
    - Routage lors du chargement du frontend (ou lors d'un refresh) via `navbarWrapper.addEventListener("load",...)` :
    appel du composant associé à l’URL en cours : par exemple, si c'est "/", la HomePage sera appelée.
    - Routage lors de l’utilisation de l’historique du browser via `navbarWrapper.addEventListener("popstate",...)` : appel du composant associé à l’URL se trouvant dans la pile gérant le "state" du browser (l'historique)
    - Routage lors de redirection via la méthode `Redirect(uri)` :
        - Appel du composant associé à la redirection (et auto-render du composant)
        - Affichage dans le browser de l’URL associée à l’élément redirigé
        - Garder l’URL dans l’historique
- La configuration des routes est actuellement à faire au sein de `/Components/Router/Router.js`. Voici un exemple de configuration :
```js
const routes = {
  "/": HomePage,
  "/login": LoginPage,
  "/register": RegisterPage,
  "/logout": Logout,
};
```
- NB : Cette configuration pourrait être externalisée dans un fichier de configuration afin de rendre le code plus générique.
- La `HomePage` n'est pas chargé dans `index.js.` C'est le rôle du Router de charger la bonne page en se focalisant sur l'URL.
- De plus, il faut appeler le `Router` au sein de `index.js`

## Redirection d'une page vers une autre
- La page doit faire appel à la fonction `Redirect` du `Router` pour rediriger vers une autre page. Dans ce boilerplate, un example est fourni pour la `NewPage` qui redirige vers la `HomePage` lors d'un clic sur un bouton.
- Vous pouvez utiliser la fonction `Redirect` du `Router` en fonction de n'importe quel type d'événement : lors du succès de l'authentification, lors d'un clic sur un élément....

## Conclusion
- L'utilisation de ce boilerplate permet d'avoir un serveur de développement hyper performant, de développer avec beaucoup de confort, de faciliter la structure d'un projet, de gérer les dépendances, de transformer les assets de manière centralisée, ...

# Resources
- favicon : https://upload.wikimedia.org/wikipedia/commons/3/3e/AIGA_information.svg, AIGA (American Institute of Graphic Arts)