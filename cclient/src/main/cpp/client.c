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

struct client_socket *client_connect(char *hostname, char *port) 
{
    struct hostent *host = getbyhostname(hostname);
    struct client_socket cs;
    strncpy(cs.hostname, hostname, strlen(hostname));
    strncpy(cs.port, port, strlen(port));

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

    const int status = getaddrinfo(hostname, port, &hints, &addrs);
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

    sc.fd = sfd;

    sc.ctx = init_ctx();
    sc.ssl = SSL_new(sc.ctx);

    return sc;
}

void send(char* message, client_socket* cs) {

}

void receive(client_socket* cs) {

}