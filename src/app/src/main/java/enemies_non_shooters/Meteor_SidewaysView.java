package enemies_non_shooters;

import android.widget.RelativeLayout;

import com.jtronlabs.space_shooter.R;

public class Meteor_SidewaysView extends Gravity_MeteorView{
	
	public final static float
			DEFAULT_SPEED_X = DEFAULT_SPEED_Y / 4;
	
	public Meteor_SidewaysView(RelativeLayout layout,int level) {
		super(layout,level);

		initMeteorSideways(level);
	}

	private void initMeteorSideways(int level){
		float speedX = gravitySpeedMultiplier(level,DEFAULT_SPEED_X);
		if(Math.random()<0.5){speedX *= -1;}
		this.setSpeedX(speedX);
	}

	@Override
	public void unRemoveMeteorView(RelativeLayout layout,int level){
		super.unRemoveMeteorView(layout,level);
		initMeteorSideways(level);
	}
}
