#include <errno.h>
#include <unistd.h>
#include <string.h>
#include <resolv.h>
#include <netdb.h>
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
void send(char*, client_socket*);
void receive(client_socket*);
