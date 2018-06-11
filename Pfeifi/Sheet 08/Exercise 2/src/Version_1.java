import java.util.Random;

/** Author: Martin Pfeifhofer © SS 2018 **/

public class Version_1 {

    public static double upTimePercentageOfSystem = 0.975;
    public static int runTime = 10000;
    public static int numberOfSystems = 2;

    public static void main(String[] args) {
        boolean systems[][] = new boolean[runTime][numberOfSystems];
        initSystems(systems);
        calculateGlobalAvailability(systems);
    }

    public static void initSystems(boolean systems[][]){

        for(int i = 0; i < systems.length; i ++){
            for(int j = 0; j < systems[i].length; j++){
                systems[i][j] = calculateAvailabilityState();
            }
        }
    }

    public static void calculateGlobalAvailability(boolean systems[][]){

        double availabilityCounter = 0;
        for (int i = 0; i < runTime; i++){
            if(atLeastOneSystemAvailable(systems[i]))
                availabilityCounter++;
        }
        System.out.println("** Version 1 **");
        System.out.println("\nOf a time of {" + runTime + " seconds}, we have at least {" + (int) availabilityCounter + " seconds} avalability given\n");
        System.out.println("Expected Availability: 99,9%" + "\nSimulated Availability: " + (availabilityCounter/runTime) * 100 + "%");
    }

    public static boolean atLeastOneSystemAvailable(boolean systems[]){
        for (boolean availabilityState: systems) {
            if(availabilityState)
                return true;
        }
        return false;
    }


    public static boolean calculateAvailabilityState(){
        Random rnd = new Random();
        if(rnd.nextDouble() < upTimePercentageOfSystem)
            return true;
        else
            return false;
    }

}