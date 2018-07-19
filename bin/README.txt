Server:
to start the server do "java ChatServer <port>" if no port is specified it used the default 7777.
to stop it type "stop" in the console, might take a second for receives to time out.
to switch to des type "des" in the console, this sets a static variable in the Crypto class that changes the algorithm.

Client:
to start the client you can type "java ChatClient", you can start as many as you like. Make sure the server is running before you try and connect.
choose a name
for the ip field type "<ip>:<port>" then press submit to connect
you can press enter to send messages or click send
to send images there is a send image button, they are all resized to fit nicely in the window
type "/des" to change to des


Note all clients and the server must change to des.
When hotswapping to des make sure to change all the clients and server before sending any messages or pictures.
To use des by default change line 16 in the Crypto class to "Crypto.algorithm = Algorithm.DES;"
