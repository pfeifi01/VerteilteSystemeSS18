## Usage Instructions

### Start Nodes

1. Initialise a number of nodes by giving them a port (ip is commented out, because its always localhost) 
2. Give the node the port of another node
3. Start node by pressing enter again, but try to start multiple nodes at almost the same time, else they will throw the given node out of their table, because they can't connect to it
4. You can now enter a name at a name of your preference and press enter. The node will now propagate this name update to a random node in its table.
5. After the random node received the name update, it will update its table if it has that node saved in the table and with a 50% chance it will continue propagation to a random node of its table.

### Work with Nodes
* Nodes will automatically send requests every 3 seconds
* Enter a name to start the name update propagation