package com.jtronlabs.to_the_moon.bullets;

import android.content.Context;

import com.jtronlabs.to_the_moon.R;
import com.jtronlabs.to_the_moon_interfaces.Shooter;
  

public class Bullet_LaserLong extends Bullet_Default{
	
	public BulletView getBullet(Context context,Shooter shooter,double bulletSpeedX,
	double positionOnShooterAsAPercentage){

		final int width=(int) context.getResources().getDimension(R.dimen.laser_long_width);
		final int height=(int) context.getResources().getDimension(R.dimen.laser_long_height);

		BulletView bullet = new BulletView(context,shooter,  bulletSpeedX, 
				width,height,positionOnShooterAsAPercentage);
		
		int backgroundId=R.drawable.laser1_enemy;
		if(shooter.isFriendly()){backgroundId = R.drawable.laser1_friendly;}
		
		bullet.setBackgroundResource(backgroundId);
		
		return bullet;
	}
}
