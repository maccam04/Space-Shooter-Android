package levels;

import java.util.ArrayList;
import java.util.Arrays;


public abstract class SpawnableWave {

	private static ArrayList<SpawnableWave> allWaves;
	
	private long time;
	private int spawningWeight;
	
	public SpawnableWave(long howLongUntilCanSpawnAgain, int spawningProbabilityWeight){
		time = howLongUntilCanSpawnAgain;
		spawningWeight= spawningProbabilityWeight;
	} 
	
	public abstract void spawn();
	
	/**
	 * MUST BE AT LEAST ONE ENTRY IN THIS ARRAY. Otherwise getRandomWave will throw an error
	 * @param allSpawnableWaves
	 */
	public static void initializeSpawnableWaves(SpawnableWave[] allSpawnableWaves){
		allWaves = new ArrayList<SpawnableWave>(Arrays.asList(allSpawnableWaves));
	}
	
	/**
	 * Utilize inverse transform sampling to find random SpawnableWave with respect to all SpawnableWave weighting
	 * Resources : 
	 * 		http://stackoverflow.com/a/9330493
	 * 		http://stackoverflow.com/a/17250703
	 * @return SpawnableWave object found using weighted probabilities
	 */
	public static SpawnableWave getRandomWaveUsingWeightedProbabilities(){
		//sum up probability weights of all SpawnableWave objects
		int totalSum = 0;
		for(SpawnableWave wave : allWaves){
			totalSum += wave.spawningProbabilityWeight();
		}
		
		//use inverse transform sampling to randomly choose SpawnableWave from list with respect to weights
		int rand = (int) (Math.random() * totalSum) + 1;//generate rand between 1 and sum of all
		int cumulativeSum = 0;
		int index = 0;
		
		while(cumulativeSum < rand && index < allWaves.size() ){
			cumulativeSum += allWaves.get(index).spawningProbabilityWeight();
			index++;
		}
		index--;//compensate for the last index++
		index = (index<0) ? index + 1 : index;//compensate for the last index++
		
		return allWaves.get(index);
	}
	
	//GETTER methods
	public long howLongUntilCanSpawnAgain(){
		return time;
	}
	public int spawningProbabilityWeight(){
		return spawningWeight;
	}
}
