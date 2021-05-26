* Requires Postman for REST persistence backend -> DB access in application.properties

> Start Chat client:
|| telnet localhost 8090


> Login is required to send chats.
> Login as a new client:
|| login newUser

> Login as existing client:
|| login {receiver_display_name}#{numeric_id}
Example:
|| login oldUser#1

> Send private message
|| {receiver_display_name}#{numeric_id}::{message}
Example:
|| gil#1::this is a message for user gil # 1

> Disconnect
|| logout {receiver_display_name}#{numeric_id}
Example:
|| logout gil#1

Channel controller endpoints:

> Send message
POST {server_url}/api/channels/message/public
BODY: {
    "message": "Message"
}

> Send message to specific person
POST {server_url}/api/channels/message/public
BODY: {
    "client": {
        "id": 1, //long
        "displayName": "displayName"
    },
    "message": "Message"
}

> List active clients
GET {server_url}/api/channels/clients

Client controller endpoints:
> Logout client:
POST {server_url}/api/clients/logout
BODY: {
              "id": 1, //long
              "displayName": "displayName"
      }

Chat controller endpoints:
>List persistent chats:
GET {server_url}/api/chats
