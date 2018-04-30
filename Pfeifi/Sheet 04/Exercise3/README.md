## Usage Instructions

### Start Network Handler + Nodes
1. Start the network handler. After a node is initialised it will register with ip,port and name at the network handler
2. Initialize a number of nodes by giving them a port (ip is commented out, because its always localhost) and a name
3. Choose either to give the node the name of another node or just press enter if you don't want to
4. Start node by pressing enter again, but try to start multiple nodes at almost the same time, else they will throw the given node out of their table, because they can't connect to it

### Work with Nodes
* Nodes will automatically register themselves at the Network Handler
* Nodes will automatically send requests every 3 seconds (first to the network handler with a name and they receive the ip and port, then to the requested node)