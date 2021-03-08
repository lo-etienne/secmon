#define SP                  "[[:space:]]"
#define CRLF                "\\r\\n"
#define LETTER              "[A-Za-z]"
#define DIGIT               "[0-9]"
#define LETTER_DIGIT        LETTER "|" DIGIT
#define CHARACTER           "[[:print:]]"
#define CHARACTER_PASS      "[[:graph:]]"

#define ID                  "(" LETTER_DIGIT "){5,10}"
#define PROTOCOL            "(" LETTER_DIGIT "){3,15}"
#define USERNAME            "(" CHARACTER_PASS "){3,50}"
#define PASSWORD            "(" CHARACTER_PASS "){3,50}"
#define PORT                "[1-9][0-9]{0,3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5]"
#define HOST                "(" LETTER_DIGIT "|[._-]){3,50}"
#define PATH                "/(" LETTER_DIGIT "|[\\\\/._-]){0,100}"
#define MIN_REGEX           "(" DIGIT "){1,8}"
#define MAX_REGEX           "(" DIGIT "){1,8}"
#define FREQUENCY           "(" DIGIT "){1,8}"
#define STATE               "UNLAODED|OK|ALARM|NOSTATUS|DOWN"
#define MESSAGE             "(" CHARACTER "){1,200}"
#define URL                 PROTOCOL "://(" USERNAME "(:" PASSWORD ")?@)?" HOST "(:" PORT ")?" PATH
#define AUGMENTED_URL       ID "!" URL "!" MIN_REGEX "!" MAX_REGEX "!" FREQUENCY
#define SRVLIST             "(" SP ID "){0,100}"
#define OPTIONAL_MESSAGE    "(" SP MESSAGE ")"

#define _ADD_SERVICE_REQ(augmented_url)         "ADDSRV" SP augmented_url CRLF
#define _ADD_SERVICE_RESP_OK(optional_message)  "\\+OK" optional_message CRLF
#define _ADD_SERVICE_RESP_ERR(optional_message) "-ERR" optional_message CRLF
#define _LIST_SERVICE_REQUEST                   "LISTSRV" CRLF
#define _LIST_SERVICE_RESPONSE(list)            "SRV" list CRLF
#define _STATE_SERVICE_REQUEST(id)              "STATESRV" SP id CRLF
#define _STATE_SERVICE_RESPONSE(id, url, state) "STATERESP" SP id SP url SP state CRLF

#define ADD_SERVICE_REQ_REGEX           _ADD_SERVICE_REQ(AUGMENTED_URL)
#define ADD_SERVICE_RESP_OK_REGEX       _ADD_SERVICE_RESP_OK(OPTIONAL_MESSAGE)
#define ADD_SERVICE_RESP_ERR_REGEX      _ADD_SERVICE_RESP_ERR(OPTIONAL_MESSAGE)
#define LIST_SERVICE_REQUEST_REGEX      _LIST_SERVICE_REQUEST
#define LIST_SERVICE_RESPONSE_REGEX     _LIST_SERVICE_RESPONSE(SRVLIST)
#define STATE_SERVICE_REQUEST_REGEX     _STATE_SERVICE_REQUEST(ID)
#define STATE_SERVICE_RESPONSE_REGEX    _STATE_SERVICE_RESPONSE(ID, URL, STATE)
