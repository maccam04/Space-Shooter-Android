package com.jtronlabs.to_the_moon.views;
  
import android.content.Context;
import android.util.AttributeSet;

import com.jtronlabs.to_the_moon.guns.Gun;
import com.jtronlabs.to_the_moon.guns.Gun_Special;
import com.jtronlabs.to_the_moon.guns.Gun_Upgradeable;

public class Gravity_ShootingView extends Projectile_GravityView{

	//myGun needs to be set in a specific View's class
	public Gun myGun;
	
	public int removeView(boolean showExplosion){
		this.cleanUpThreads();
		return super.removeView(showExplosion);
	}
	
	public Gravity_ShootingView(Context context,boolean shootingUpwards,int scoreValue,double projectileSpeedUp,
			double projectileSpeedDown,double projectileSpeedX, 
			double projectileDamage,double projectileHealth,double probSpawnBeneficialObjectOnDeath) {
		super(context,scoreValue,projectileSpeedUp,projectileSpeedDown,projectileSpeedX,
				projectileDamage,projectileHealth,probSpawnBeneficialObjectOnDeath);
	}
	
	public Gravity_ShootingView(Context context,AttributeSet at,boolean shootingUpwards,int scoreValue,double projectileSpeedUp,
			double projectileSpeedDown,double projectileSpeedX, 
			double projectileDamage,double projectileHealth,double probSpawnBeneficialObjectOnDeath) {
		super(context,at,scoreValue,projectileSpeedUp,projectileSpeedDown,projectileSpeedX,
				projectileDamage,projectileHealth,probSpawnBeneficialObjectOnDeath);	
	}
	
	public void cleanUpThreads(){
		myGun.stopShooting();
		super.cleanUpThreads();
	}
	
	/**
	 * 
	 * @param upgrade True to upgrade gun, False to downgrade
	 */
	public void upgradeOrDowngradeGun(boolean upgrade){
		//create new upgra
		Gun_Upgradeable nextGun;
		if(upgrade){
			nextGun = this.myGun.getMostRecentUpgradeableGun().upgradeGun();
		}else{
			nextGun = this.myGun.getMostRecentUpgradeableGun().downgradeGun();			
		}
		
		if(this.myGun instanceof Gun_Special){
			this.myGun.setPreviousUpgradeableGun(nextGun);//set it up so once special disappears, new gun will take over
		}else if( ! this.myGun.getClass().equals(nextGun.getClass())){
			this.myGun=nextGun;
		}
	}
	
	public void giveSpecialGun(Gun_Special newGun, int ammo){
		this.myGun.transferGunProperties(newGun);
		this.myGun=newGun;
		newGun.setAmmo(ammo);
	}
	
	public void restartThreads(){
		super.restartThreads();
		myGun.startShooting();
	}
}