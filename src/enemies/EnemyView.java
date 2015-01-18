package enemies;

import levels.LevelSystem;
import parents.Projectile_GravityView;
import android.content.Context;
import android.util.Log;
import bonuses.BonusView;

public class EnemyView extends Projectile_GravityView{
	
	//TODO Enemies that dodge (move away) from nearby friendly's bullets ? 
	public static int numSpawn=0,numRemoved=0;
	private int score;
	private double probSpawnBeneficialObject;
	
	public EnemyView(Context context,int scoreForKilling,double projectileSpeedY,
			double projectileSpeedX, 
			double projectileDamage,double projectileHealth,double probSpawnBeneficialObjectUponDeath,int width,int height,int imageId) {
		super( context, projectileSpeedY, projectileSpeedX, 
				 projectileDamage, projectileHealth, width, height, imageId);
		
		this.setY( - this.getHeight() /2 );//start all enemies 3/4 way offscreen
		
		numSpawn++;
		score=scoreForKilling;
		probSpawnBeneficialObject= probSpawnBeneficialObjectUponDeath;
		LevelSystem.enemies.add(this);
	}
	
	/**
	 * To be called whenever an Enemyview implements removeGameObject()
	 * NEW BEHAVIOR  :: 
	 * Two cases when an enemy will be removed, fallen off side of screen or hase been killed
	 * If has been killed, check to spawn a random BonusView and increment level score by this.score value
	 * If has fallen off screen, increment level score by this.score/3. I did this to help at lower levels and to give a benefit for dodging
	 */
	@Override
	public void removeGameObject(){
		if(this.getHealth()<=0){//died
			LevelSystem.incrementScore(this.getScoreForKilling());
			
			if(Math.random()<probSpawnBeneficialObject){//check for random bonus
				final float xAvg = (2 * this.getX()+this.getWidth())/2;
				final float yAvg = (2 * this.getY()+this.getHeight())/2;
				Log.d("lowrey","x="+xAvg);
				BonusView.displayRandomBonusView(this.getContext(),xAvg,yAvg);
			}
		}
		else {//fallen offscreen
			LevelSystem.incrementScore(this.getScoreForKilling()/3);
		}
		boolean passed = LevelSystem.enemies.remove(this);
		if(!passed){Log.d("lowrey","this should very rarely be printed.");}

		numRemoved++;
		this.deaultCleanupOnRemoval();//needs to be the last thing called for handler to remove all callbacks
	}
	
	public void setProbSpawnBeneficialObjectOnDeath(double prob){
		probSpawnBeneficialObject=prob;
	}
	public double getProbSpawnBeneficialObjectOnDeath(){
		return probSpawnBeneficialObject;
	}
	public int getScoreForKilling(){
		return score;
	}
	public void setScoreValue(int newScore){
		score=newScore;
	}
}
