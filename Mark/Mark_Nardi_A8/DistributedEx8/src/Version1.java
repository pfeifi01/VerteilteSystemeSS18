import java.util.Arrays;
import java.util.Random;

public class Version1 {
	
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
	
	
	public static void main(String[] args){
		int time = 3000;
		int systems = 2;
		double percentage = 0.9;
		
		boolean[][] arr = getUptimeArray(time,systems,percentage);
		
		for(int i = 0; i < systems; i++){
			System.out.println(Arrays.toString(arr[i]));
		}
	
		System.out.println("Availability with " + systems + " systems: " + getPercentageOfUptime(arr));
		
		
		
		
	}
}
