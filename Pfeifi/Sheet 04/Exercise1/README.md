## Usage Instructions

### Start Nodes

1. Initialise a number of nodes by giving them a port (ip is commented out, because its always localhost) 
2. Choose either to give the node the port of another node or just press enter if you don't want to
3. Start node by pressing enter again, but try to start multiple nodes at almost the same time, else they will throw the given node out of their table, because they can't connect to it

### Work with Nodes
* Nodes will automatically send requests every 3 seconds

# Answer to Network Seggregation
* My approach to avoid this problem is to hold a table of all nodes, which a node ever connected to and if the actual table of 3 nodes is empty, the node will try to establish connection to a random node of the other table.