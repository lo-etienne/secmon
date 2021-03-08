#include <errno.h>
#include <unistd.h>
#include <string.h>
#include <resolv.h>
#include <netdb.h>
#include <openssl/ssl.h>
#include <openssl/err.h>

SSL_CTX *init_ctx() 
{
    const SSL_METHOD *method = TLS_client_method();
    SSL_CTX *ctx = SSL_CTX_new(method);

    if (ctx == nullptr)
    {
        ERR_print_errors_fp(stderr);
    }
    return ctx;
}

int open_connection(const char *hostname, const char *port) 
{
    struct hostent *host;
    if ((host = gethostbyname(hostname)) == nullptr) 
    {
        perror(hostname);
        exit(EXIT_FAILURE);
    } 

    struct addrinfo hints = {0}, *addrs;
    hints.ai_family = AF_UNSPEC;
    hints.ai_socktype = SOCK_STREAM;
    hints.ai_protocol = IPPROTO_TCP;

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
    return sfd;
}

void display_certs(SSL *ssl) 
{
    X509 *cert = SSL_get_peer_certificate(ssl);
    if (cert != nullptr)
    {
        printf("Server certificates:\n");
        char *line = X509_NAME_oneline(X509_get_subject_name(cert), 0, 0);
        printf("Subject: %s\n", line);
        delete line;
        line = X509_NAME_oneline(X509_get_issuer_name(cert), 0, 0);
        printf("Issuer: %s\n", line);
        delete line;
        X509_free(cert);
    }
    else
    {
        printf("Info: No client certificates configured. \n");
    }
}

int main(int argc, char **argv) 
{
    SSL_CTX *ctx = init_ctx();
    SSL *ssl = SSL_new(ctx);
    if (ssl == nullptr) 
    {
        fprintf(stderr, "SSL_new() failed\n");
        exit(-1);
    }

    const int sfd = open_connection("localhost", argv[1]);
    SSL_set_fd(ssl, sfd);
    
    const int status = SSL_connect(ssl);
    if (status != 1)
    {
        SSL_get_error(ssl, status);
        ERR_print_errors_fp(stderr);
        fprintf(stderr, "SSL_connect failed with SSL_get_error code %d\n", status);
        exit(-1);
    }

    printf("Connected with %s encryption\n", SSL_get_cipher(ssl));
    display_certs(ssl);
    
    const char *str = "Hello world !";
    SSL_write(ssl, str, strlen(str));

    char *buf = (char*) malloc(sizeof(char) * 1024);
    SSL_read(ssl, buf, 1024);

    fprintf(stdout, "%s\n", buf);

    SSL_free(ssl);
    close(sfd);
    SSL_CTX_free(ctx);
    return 0;
}