# La probe SNMP

La probe SNMP s'occupe de sonder des services SNMP et de renvoyer leur
statut

## Lancement

```shell
gradlew :probesnmp:run
```

N'oubliez pas d'éditer le
[fichier de configuration](./build/resources/main/probe.conf.json)
avant de lancer la probe

(c.f. le [README.md](../probe/README.md) du module probe pour des informations sur la
configuration de la probe)

## Quitter le programme

Pour quitter le programme, il faut entrer

```
quit
```

pendant l'exécution de ce dernier.