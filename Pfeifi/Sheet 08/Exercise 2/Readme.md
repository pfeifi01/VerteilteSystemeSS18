## Execution
1. Run the Version 1 Class for a) of Exercise 2
1. Run the Version 2 Class for b) of Exercise 2
* Any changes of input (differing from the ones given in the exercise), have to be made to the global variables in the code

## Version 1
> In Version 1 a number of systems is initialized with true and false values, calculated by a random function considering a given failure rate. 
Then the availability is calculated, by checking if at least one system in a simulated time slot is up and then compared with the maximum time slots

## Version 2
> The difference from Version 1 is, that the number of systems is not given. So first it will be checked, if one system is enough to achieve the failure rate. If it is enough, the number of systems is returned. 
Else the number of systems is incremented and the same procedure will be repeated, until the failure rate is achieved.
