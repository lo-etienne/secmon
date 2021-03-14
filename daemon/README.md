# Le Monitor Daemon

Le monitor daemon s'occupe de la communication entre les clients et les
probes.

## Lancement

```shell
gradlew :daemon:run
```

## Configuration

N'oubliez pas d'éditer le 
[fichier de configuration](./build/resources/main/monitor.json)
avant de lancer le daemon

```json
{
  "name": "monitor",
  "version": "1.0.0",
  "multicast_address" : "224.50.50.50",
  "multicast_port" : "60150",
  "client_port" : "42069",
  "tls": "true",
  "aes_key": "aPdSgVkYp3s6v9y$B&E(H+MbQeThWmZq",
  "certificate_password": "group5",
  "certificate_path": "/home/florent/Documents/cours/reseau/secmon/daemon/src/main/resources/group5.monitor.p12",
  "probes": [
    "snmp1!snmp://public@192.168.128.38:161/1.3.6.1.4.1.2021.4.11.0!10000!99999999!120",
    "snmp2!snmp://public@192.168.128.38:161/1.3.6.1.4.1.2021.11.11.0!10!99999999!120",
    "http1!https://sensor.cg.helmo.be/api/get-temp/!5!35!60",
    "http2!https://sensor.cg.helmo.be/api/get-humidity/!0!80!60"
  ]
}
```

|Le champ|Description|
|------------|------------------|
|name|Le nom du moniteur (Deprécié mais requis)|
|version|La version du moniteur (Déprécié mais requis)|
|mutlicast_address|L'addresse à laquelle le moniteur écoute les annonces des probes|
|multicast_port|Le port auquel le moniteur écoute les annonces des probes|
|client_port|Le port du serveur auquel les clients peuvent se connecter|
|tls|Si le serveur doit être lancé en TLS ou non|
|aes_key|La clef AES/GCM pour l'encryption des messages entre le daemon et les probes|
|certificate_password|Le mot de passe du certificat précisé plus bas|
|certificate_path|Le chemin vers le certificat du serveur|
|probes|La configuration des services au lancement du monitor daemon|

## Quitter le programme

Pour quitter le programme, il faut entrer

```
quit
```

pendant l'exécution de ce dernier.