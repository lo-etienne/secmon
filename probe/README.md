# Les probes

Ce module contient le code en commun pour toutes les implémentations 
de probes.

## Packages

|Package|Description|
|-------|-----------|
|be.flmr.secmon.probe.config|Classes qui lisent la configuration de l'implémentation de la probe|
|be.flmr.secmon.probe.net|Classes en rapport avec la connexion entre le daemon et la probe|
|be.flmr.secmon.probe.service|Classes en rapport avec la communication entre la probe et ses services|

## Configuration

Chaque implémentation de probe est configurée avec un fichier JSON avec
comme format:

```json
{
  "name": "probe http",
  "version": "1.0.0",
  "multicast_address" : "224.50.50.50",
  "multicast_port" : "60150",
  "aes_key": "aPdSgVkYp3s6v9y$B&E(H+MbQeThWmZq",
  "alive_interval": 10,
  "protocol": "https"
}
```

|Champ|Description|
|-----|-----------|
|name|Le nom de la probe (Deprécié mais requis)|
|version|La version de la probe (Déprécié mais requis)|
|multicast_address|L'adresse multicast à laquelle la probe envoie ses annonces|
|mutlicast_port|Le port multicast auquel la probe envoie ses annonces|
|aes_key|La clef AES/GCM pour l'encryption des messages entre le daemon et les probes|
|alive_interval|L'intervalle à laquelle la probe envoie ses messages de vie|
|protocol|Le protocol géré par la probe|