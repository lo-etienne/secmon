# Secmon

Secmon (abbréviation pour Secure Monitor) est une solution 
comprenant trois programmes afin de tracker les états d'un service sur
Internet.

## IntelliJ

Si vous importez le projet via IntelliJ, la plupart des commandes à
exécuter sont abrégées par l'IDE, notemment avec les "run configurations"
ou bien le plugin gradle intégré à l'IDE

## Le build

Pour build le projet, il suffit d'exécuter

```shell
gradlew build
```

**// ! \ Il faut build le projet pour pouvoir l'exécuter**

### [Le module CORE](./core/README.md)

Le module core contient du code en commun avec tout les autres modules.
Il est impossible de compiler les autres modules sans référencer le
CORE.

### Les clients

Les clients servent d'interface entre l'utilisateur de le monitor daemon
afin d'effectuer des requêtes de sondage de services et de récupérer
ce qui a été sondé.

Il existe deux implémentations du client:

* [Le client JAVA](./client/README.md)
* [Le client C++](./cclient/README.md)

### [Le monitor daemon](./daemon/README.md)

Le monitor daemon permet de lier les requêtes du client et le sondage
des probes. C'est lui qui s'occupe de rediriger les requêtes d'un
certain protocol vers la probe concernée

### [La probe](./probe/README.md)

La probe est un programme qui sonde différents services communiqués par
le monitor daemon. Lorsque la valeur change, la probe communique via
multicast que le service a changé de valeur.

Il existe trois implémentation des probes, chacune avec un protocol
applicatif différent:

| Probe                                       | Protocol applicatif |
|---------------------------------------------|---------------------|
| [La probe HTTP](./probehttp/README.md)      | (HTTPS: 443)        |
| [La probe SNMP](./probesnmp/README.md)      | (SNMP: 116)         |
| [La probe Minecraft](./probemc/README.md)   | (Minecraft: 25565)  |


---

Credits:
* RAEYMAECKERS Florent
* BEAUME Robin
* ETIENNE Loïc
* PRODHOMME Maxime