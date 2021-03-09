#include <regex>

#include "regex_def.hpp"
#include "client.hpp"

void help()
{
    printf("Syntaxe: cclient [host] [port] [add-service|list-service|state-service] [augmented_url|rien|id]\n");
}

//    0      1       2       3        4
// [path] [host] [port] [command] [parameter]

int main(int argc, char **argv)
{
    char *host, *command, *parameter, *port;

    std::regex host_regex = std::regex(HOST);
    std::regex command_regex = std::regex("(add\\-service|list\\-service|state\\-service)");
    std::regex parameter_regex = std::regex("(" AUGMENTED_URL "|" ID ")");
    std::regex port_regex = std::regex(PORT);

    if (argc < 4)
    {
        fprintf(stderr, "Erreur dans la syntaxe de la commande: pas assez d'arguments.\n");
        help();
        exit(-1);
    }

    for (int i = 1; i < argc; ++i)
    {
        if (i == 1)
        {
            host = argv[i];
            if (!std::regex_match(host, host_regex))
            {
                fprintf(stderr, "Erreur dans la syntaxe de la commande: le host n'est pas valide.\n");
                help();
                exit(-1);
            }
        }

        if (i == 2)
        {
            port = argv[i];
            if (!std::regex_match(port, port_regex))
            {
                fprintf(stderr, "Erreur dans la syntaxe de la commande: la commande n'est pas valide.\n");
                help();
                exit(-1);
            }
        }

        if (i == 3)
        {
            command = argv[i];
            if (!std::regex_match(command, command_regex))
            {
                fprintf(stderr, "Erreur dans la syntaxe de la commande: la commande n'est pas valide.\n");
                help();
                exit(-1);
            }
        }

        if (i == 4)
        {
            parameter = argv[i];
            if (!strcmp(command, "list-service"))
            {
                fprintf(stderr, "Erreur dans la syntaxe de la commande: il manque un paramètre !\n");
                help();
                exit(-1);
            }
        }
    }

    struct client_socket *client = client_connect(host, port);
    char buffer[1024];
    memset(buffer, 0, 1024 * sizeof(char));

    if (!strcmp(command, "add-service"))
    {
        char *send_buff = (char *)malloc((9 + strlen(parameter)) * sizeof(char));
        memset(send_buff, 0, (9 + strlen(parameter)) * sizeof(char));
        int param_length = strlen(parameter);

        strncpy(send_buff, "ADDSRV ", 7);
        strncpy(send_buff + 7, parameter, param_length);
        strncpy(send_buff + 7 + param_length, "\r\n", 2);

        client_send(send_buff, client);
        free(send_buff);
    }
    else if (!strcmp(command, "list-service"))
    {
        client_send("LISTSRV\r\n", client);
    }
    else if (!strcmp(command, "state-service"))
    {
        char *send_buff = (char *)malloc((9 + strlen(parameter)) * sizeof(char));
        memset(send_buff, 0, (9 + strlen(parameter)) * sizeof(char));
        int param_length = strlen(parameter);

        strncpy(send_buff, "STATEREQ ", 9);
        strncpy(send_buff + 9, parameter, param_length);
        strncpy(send_buff + 9 + param_length, "\r\n", 2);

        client_send(send_buff, client);
        free(send_buff);
    }

    client_receive(buffer, 1024, client);

    printf("%s\n", buffer);

    client_free(client);

    return 0;
}