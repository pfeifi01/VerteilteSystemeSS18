## Usage Instructions

### Start Chord with Nodes
1. Start the Main Function. This will initialize the chord with 16 adresses and 5 nodes. The nodes will be created and then their fingertables are filled and printed.

### Work with Nodes
* Enter *add* to let a new node join the network. Enter a free node value and then enter its predecessor. The new node will request its successor from its predecessor and update the successor of its predecessor and the predecessor of its successor. Then the fngertables of the other nodes will be updated.
* Enter *remove* to let a node leave the network. The node will be removed from the chord and the other nodes will upate their fingertables.
* Enter *exit* to shut the chord down