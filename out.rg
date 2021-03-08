ADDSRV\p{Space}(?<AUGMENTED_URL>(?<ID>([A-Za-z]|[0-9]){5,10})!(?<URL>(?<PROTOCOL([A-Za-z]|[0-9]){3, 15}).//((?<USERNAME>((\p{Graph}){3, 50})>(:(?<PASSWORD>(\p{Graph}){3,50}))?@)?(?<HOST>([A-Za-z]|[0-9]|[._-]){3,50})(:(?<PORT>[1-9][0-9]{0,3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5]))?(?<PATH>/([A-Za-z]|[0-9]|[\\/._-]){0, 100}))!(?<MIN>([0-9]){1,8})!(?<MAX>([0-9]){1,8})!(?<FREQUENCY>([0-9]){1,8}))\r\n
\+OK\p{Space}(?<OPTIONAL_MESSAGE>(\p{Space}(?<MESSAGE>(\p{Print}){1,200})))\r\n
-ERR\p{Space}(?<OPTIONAL_MESSAGE>(\p{Space}(?<MESSAGE>(\p{Print}){1,200})))\r\n
LISTSRV\r\n
SRV(?<SRVLIST>(\p{Space}(?<ID>([A-Za-z]|[0-9]){5,10})){0,100})\r\n
STATESRV\p{Space}(?<ID>([A-Za-z]|[0-9]){5,10})\r\n
STATERESP\p{Space}(?<ID>([A-Za-z]|[0-9]){5,10})\p{Space}(?<URL>(?<PROTOCOL([A-Za-z]|[0-9]){3, 15}).//((?<USERNAME>((\p{Graph}){3, 50})>(:(?<PASSWORD>(\p{Graph}){3,50}))?@)?(?<HOST>([A-Za-z]|[0-9]|[._-]){3,50})(:(?<PORT>[1-9][0-9]{0,3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5]))?(?<PATH>/([A-Za-z]|[0-9]|[\\/._-]){0, 100}))\p{Space}(?<STATE>UNLAODED|OK|ALARM|NOSTATUS|DOWN)\r\n
