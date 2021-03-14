# Le client JAVA

Le client JAVA est un client qui fonctionne avec les arguments de
programmes passés par ligne de commande.

## Lancement

```shell
gradlew :client:run --args="[-hV] [-p=<port>] <host> <typeService> <parameterService>"
 
Console for interation with daemon
      <host>               host
      <typeService>        add-service | list-service | state-service
      <parameterService>
  -h, --help               Show this help message and exit.
  -p, --port=<port>        Port
  -V, --version            Print version information and exit.
```

## Arguments de lancement

### Ajouter un service

Cette commande permet d'ajouter un service dans la configuration du daemon
à l'adresse `<host>`

```shell
gradlew :client:run --args="[-hV] [-p=<port>] <host> add-service <serviceId>"
```

### Lister les différents services

Cette commande permet de lister tous les services enregistrés dans le
dameon à l'adresse `<host>`

```shell
gradlew :client:run --args="[-hV] [-p=<port>] <host> list-service"
```

### Demander le statut d'un service

Cette commande permet d'envoyer une requête pour demander le statut 
d'un service `<serviceId>` enregistré dans le daemon à l'adresse 
`<host>`

```shell
gradlew :client:run --args="[-hV] [-p=<port>] <host> state-service <serviceId>"
```