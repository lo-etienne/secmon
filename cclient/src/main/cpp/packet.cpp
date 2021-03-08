#include "packet.hpp"

void display_add_service_response_ok(char* str) {
    std::regex regex(ADD_SERVICE_RESP_OK_REGEX);
    std::regex opt(OPTIONAL_MESSAGE);

    if(std::regex_match(str, regex)) {
        std::smatch match;

        if(std::regex_search(str, match, opt)) {
            printf("Envoi OK : %s\n", match.str());
        }
    }
}

void display_add_service_response_err(char* str) {
    std::regex regex(ADD_SERVICE_RESP_ERR_REGEX);
    std::regex opt(OPTIONAL_MESSAGE);

    if(std::regex_match(str, regex)) {
        std::smatch match;

        if(std::regex_search(str, match, opt)) {
            printf("Envoi ERR : %s\n", match.str());
        }
    }
}

void display_list_service_response(char* str) {
    std::regex regex(LIST_SERVICE_RESPONSE_REGEX);
    std::regex id(ID);

    if(std::regex_match(str, regex)) {
        std::smatch match;

        while(std::regex_search(str, match, id)) {
            printf("%s\n", match.str());
            str = match.prefix().str();
        }
    }
}

void display_state_service_response(char* str) {
    std::regex regex(ADD_SERVICE_RESP_ERR_REGEX);
    std::regex state(STATE);

    if(std::regex_match(str, regex)) {
        std::smatch match;

        if(std::regex_search(str, match, state)) {
            printf("Envoi STATE : %s\n", match.str());
        }
    }
}