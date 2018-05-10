## Usage Instructions

### Start Nodes

1. Start three nodes and initialize them with one of the ports in the port array, by typing 1, 2 or 3.
3. Try to start multiple nodes at almost the same time, else they will throw an exception, because they can't connect to the other nodes

### Work with Nodes
* Nodes will automatically tell the Application to create a message and forward it to the Middleware
* The middleware will connect to another node and deliver the message after a delay
* The middleware of the other node will receive the message and makes right time and right message check
* If the message failed a check, it will be stored in a buffer. When another message is received and the check is ok, then the message will be forwarded to the Application. 
* After the ok check and the delivering to the Application, the middleware will loop through the buffer and do the checks again with all messages in the buffer. 
