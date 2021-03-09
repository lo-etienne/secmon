#include "client.hpp"
#include <netdb.h>


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

struct client_socket *client_connect(char *hostname, char *port) 
{
    struct hostent *host = gethostbyname(hostname);
    struct client_socket *cs = (struct client_socket*) malloc(sizeof(struct client_socket));
    memset(cs, 0, sizeof(client_socket));
    cs->hostname = hostname;
    cs->port = port;

    if (host == NULL)
    {
        perror(hostname);
        exit(-1);
    }

    struct addrinfo hint, *addrs;
    memset(&hint, 0, sizeof(struct addrinfo));
    
    hint.ai_family = AF_UNSPEC;
    hint.ai_socktype = SOCK_STREAM;
    hint.ai_protocol = IPPROTO_TCP;

    int status = getaddrinfo(hostname, port, &hint, &addrs);
    if (status != 0)
    {
        fprintf(stderr, "%s: %s\n", hostname, gai_strerror(status));
    }

    int sfd, err;
    for (struct addrinfo *addr = addrs; addr != nullptr; addr = addr->ai_next)
    {
        sfd = socket(addrs->ai_family, addrs->ai_socktype, addrs->ai_protocol);

        if (sfd == -1)
        {
            err = errno;
            continue;
        }

        if (connect(sfd, addr->ai_addr, addr->ai_addrlen) == 0)
        {
            break;
        }

        err = errno;
        sfd = -1;
        close(sfd);
    }

    freeaddrinfo(addrs);

    if (sfd == -1)
    {
        fprintf(stderr, "%s: %s\n", hostname, strerror(err));
    } 

    cs->fd = sfd;

    cs->ctx = init_ctx();
    cs->ssl = SSL_new(cs->ctx);

    if (cs->ssl == nullptr)
    {
        fprintf(stderr, "SSL_new() failed\n");
        exit(-1);
    }

    SSL_set_fd(cs->ssl, cs->fd);
    status = SSL_connect(cs->ssl);
    if (status != 1)
    {
        SSL_get_error(cs->ssl, status);
        ERR_print_errors_fp(stderr);
        fprintf(stderr, "SSL_connect failed with SSL_get_error code %d\n", status);
        exit(-1);
    }

    return cs;
}

void client_send(char* message, client_socket* cs) {
    SSL_write(cs->ssl, message, strlen(message));
}

void client_receive(char* buff, int buff_len, client_socket* cs) {
    SSL_read(cs->ssl, buff, buff_len);
}

void client_free(client_socket* cs) {
    SSL_free(cs->ssl);
    close(cs->fd);
    SSL_CTX_free(cs->ctx);
}