Ce repertoire contient ce fichier readme ainsi qu'un répèrtoire Ringo, qui est un répèrtoire au format généré par Eclipse, utilisable donc directement en important ce répèrtoire dans Eclipse, Le fichiers sources donc diponible dans Ringo/src/Ringo

Ce projet contient 15 classes Java qui permettent l'implémentation du protocole Ringo : 

	- La classe Application, qui est une classe abstraite qui définit des méthodes abstraites donc chaque application devra hériter, les classes qui en hérite devront ajouter les méthodes exec et traiter, qui permettent de gérer la récéption et l'envoi des messages d'application

	- La classe AppDiff qui est une application, qui hérite donc de la classe ci dessus, et qui permet de gérer l'application de difussion de message.

	- La classe AppTransfert, qui est une application et qui permet de gérer le transfert de fichier entre entités.

	- Le classe Dests, qui permet juste de déclarer un objet "Destinataire" avec comme attribut un ip et un port. La gestion des destinataires se fait donc via une list de Dests

	- La classe Entite, qui est la classe centrale du projet, qui contient les méthodes d'insertion, de duplication, et qui permet d'initialiser et de modifier en fonction une liste de destinataires, d'anneau, et qui permet la gestion des applications aux travers de plusieurs boolean.

	- la classe Ring, qui permet de déclarer un objet "Anneau", avec comme attribut l'ip de multi difussion et le port de multi difussion

	- la classe ServiceEnvoiUDP, qui est un service qui se lance dans un nouveau Thread et qui permet d'intéragir avec l'entrée standard pour envoyer des messages sur l'anneau

	- la classe ServiceRecvUDP, qui est un service qui se lance dans un nouveau Thread et qui permet d'écouter sur son port d'écoute UDP, afin de traiter les messages recu 

	- la classe ServiceMulticast, qui est un service qui se lance dans un nouveau Thread et qui permet d'écouter les messages recu en multi diffusion sur l'anneau. 

	- La classe ServiceTCP, qui est un service qui se lance dans un nouveau Thread et qui permet d'établir une connexion sur le port TCP pour l'insertion ou la duplication sur un anneau

	- 5 classes contenant un main (entite1, entite2, entite3, entite4, entite5) pour éxécuter le protocole.

Le programme se lance donc d'abord avec la classe entite1, puisque c'est la seul qui génère un nouvel anneau pour pouvoir s'y connecter, chaque main demande d'abord si on souhaite le mode verbeux ou non.
La classe entite2 est une classe qui s'insère sur l'anneau, au lancement il faut préciser si on souhaite le mode verbeux, l'ip de l'entité a laquelle on veut se connecter pour s'insérer, ainsi que son port TCP, si l'entité 1 est connecté, le protocole d'insertion s'éxécute avec les messages correspondant. 
La classe entite3 fais la même chose que l'entité3, on choisir si on veut s'insérer entre l'entite1 et l'entite2 ou l'entite2 et l'entite1
Idem pour l'entite4
Enfin l'entite5 est une entité qui va créer un nouvel anneau et qui va faire de l'entite choisis une entité doubleur