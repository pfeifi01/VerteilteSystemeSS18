import java.util.Random;

public class Version2 {
	
	public static boolean isAvailable(double percentage){
		
		Random r = new Random();
		double d = r.nextDouble();
		if(d <= percentage){
			return true;
		}else{
			return false;
		}
	}
	
	
	public static boolean[][] getUptimeArray(int timesteps, int numberOfSystems, double percentage){
		
		boolean[][] arr = new boolean[numberOfSystems][timesteps];
		
		for(int i = 0; i < numberOfSystems; i++){
			for(int j = 0; j < timesteps; j++){
				arr[i][j] = isAvailable(percentage);
			}
		}
		return arr;
	}
	
	public static boolean availableAtTime(boolean[][] arr, int pos){
		
	    for(int i = 0; i < arr.length; i++){
	    	if(arr[i][pos]){
	    		return true;
	    	}
	    }
	    return false;
		
	}
	

	public static double getPercentageOfUptime(boolean[][] arr){
		
		double ups = 0;
		int total = arr[0].length;
	
		for(int i = 0; i < total; i ++){
			if(availableAtTime(arr,i)){
				ups++;
			}
		}
		
		return ups/total;
	}
	
	
	public static int getAmountOfSystems(int time, double percentage, double expectedAvailability){

		for(int i = 1;;i++){
			boolean[][] arr = getUptimeArray(time, i , percentage);
			if(getPercentageOfUptime(arr) > expectedAvailability){
				return i;
			}
		}
	}
	
	public static void main(String[] args){
		int time = 10000;
		double percentage = 0.50;
		double expectedAvailability = 0.999;
		
		System.out.println("Required amount of systems needed to get " + expectedAvailability *100 + "% Availability: " + getAmountOfSystems(time,percentage,expectedAvailability));
		
		
		
		
	}
}
