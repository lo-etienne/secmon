#include "../headers/client.h"
#include "../headers/packet.h"


SSL_CTX *init_ctx() 
{
    const SSL_METHOD *method = TLS_client_method();
    SSL_CTX *ctx = SSL_CTX_new(method);

    if (ctx == NULL) 
    {
        ERR_print_errors_fp(stderr);
    }
    return ctx;
}

struct client_socket *connect(char *hostname, char *port) 
{
    struct hostent *host = getbyhostname(hostname);

    if (host == NULL)
    {
        perror(hostname);
        exit(-1);
    }

    struct addrinfo hint, *addrs;
    memset(&hint, 0, sizeof(struct addrinfo));
    
}
