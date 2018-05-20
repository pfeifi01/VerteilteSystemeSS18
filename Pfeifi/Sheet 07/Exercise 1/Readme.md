## Working with DataCenters
**Execute the Main Class. This will do the job!**

## Explanation Step by Step
* 4 Data Centers are created with their name (A,B,C,D), the given number of clients and random amount of requests (0-15)
* The latencies, which are calculated by the Matriculationnumber (1416200) are stored into an 2D-Array 
* In this Array  the first parameter is the index of the Data Center that checks for a connection and the second Parameter is the index of the other Data Center (A=0,B=1,C=2,D=3 e.g. A-->C [0][2])
* These latencies are then minimized, if there exist a shorter path - considering the latencies - by not using the direct link. This is done with the Distance Vector Algorithm.
* Then the Data Centers are initialized, with these connections and latencies
* Then each Data Center that has unhandled requests (more requests than clients) will look at the optimal Data Center connection - by comparing the latency of all Data Centers - and if the optimal Data Center can handle further requests it will add a Replica to that Data Center.
* If the most optimal cannot handle any requests, the next one with higher latency will be chosen, and so on. If none of the others can handle a request, then the Data Center adds a replica to itself.
* Then the different cost-calculations will be executed and their results are printed.