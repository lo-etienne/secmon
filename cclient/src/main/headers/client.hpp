#include <errno.h>
#include <unistd.h>
#include <string.h>
#include <resolv.h>
#include <openssl/ssl.h>
#include <openssl/err.h>

struct client_socket 
{
    int fd;
    SSL *ssl;
    SSL_CTX *ctx;
    char* hostname;
    char* port;
};

struct client_socket *client_connect(char*, char*);
void client_send(char*, client_socket*);
void client_receive(char*, int, client_socket*);
void client_free(client_socket*);
