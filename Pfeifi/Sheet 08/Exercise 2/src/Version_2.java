import java.util.Random;

/** Author: Martin Pfeifhofer © SS 2018 **/

public class Version_2 {

    public static double upTimePercentageOFSystem = 0.975;
    public static double availability = 0.999;
    public static int runTime = 10000;

    public static void main(String[] args) {

        int systemsNecessary = getAmountOfSystemsToReachAvailability();
        System.out.println("** Version 2 **");
        System.out.println("\nTo reach an availability of: " + availability*100 + "% we need " + systemsNecessary + " systems");
    }

    public static int getAmountOfSystemsToReachAvailability(){
        for(int i = 0; i < i+1; i++){
            for(int j = 0; j < runTime; j++){
                boolean[][] systems = new boolean[runTime][j];
                initSystems(systems);
                if(calculateGlobalAvailability(systems) > availability)
                    return j;
            }
        }
        return 0;
    }

    public static void initSystems(boolean systems[][]){

        for(int i = 0; i < systems.length; i++){
            for(int j = 0; j < systems[i].length; j++){
                systems[i][j] = calculateAvailabilityState();
            }
        }
    }

    public static double calculateGlobalAvailability(boolean systems[][]){

        double availabilityCounter = 0;
        for (int i = 0; i < runTime; i++){
            if(atLeastOneSystemAvailable(systems[i]))
                availabilityCounter++;
        }
        return availabilityCounter/runTime;
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
        if(rnd.nextDouble() < upTimePercentageOFSystem)
            return true;
        else
            return false;
    }

}