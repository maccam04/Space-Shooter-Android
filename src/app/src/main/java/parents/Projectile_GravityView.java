package parents;

import android.widget.RelativeLayout;
/**
 * A ProjectileView with a constant downwards force. This force is removed when the instance reaches its lowest threshold. 
 * The downward force may be different from the upward speed.
 * 
 * @author JAMES LOWREY
 *
 */
public abstract class Projectile_GravityView extends Moving_ProjectileView {

	public static int NO_THRESHOLD=Integer.MAX_VALUE;
	
	private int gravityThreshold;
	private boolean hasReachedGravityThreshold;
	
	
	public Projectile_GravityView(float xInitialPosition,float yInitialPosition,RelativeLayout layout,float movingSpeedY,float movingSpeedX,int projectileDamage,
			int projectileHealth,int width,int height,int imageId){
		super(xInitialPosition,yInitialPosition,layout, movingSpeedY, movingSpeedX,projectileDamage,projectileHealth, width, height, imageId);

		initProjGravView();
	}
	private void initProjGravView(){
		hasReachedGravityThreshold=false;
		gravityThreshold=NO_THRESHOLD;
	}
	public void unRemoveProjGravView(float xInitialPosition,float yInitialPosition,RelativeLayout layout,float movingSpeedY,float movingSpeedX,int projectileDamage,
									 int projectileHealth,int width,int height,int imageId){
		super.unRemoveProjectile(xInitialPosition,yInitialPosition,layout,movingSpeedY,movingSpeedX,projectileDamage,
		projectileHealth,width,height,imageId);

		initProjGravView();
	}
	public void setGravityThreshold (int newLowestPositionThreshold){
		gravityThreshold=newLowestPositionThreshold;
	}
	//Interface Methods
	@Override
	public void restartThreads(){
		super.restartThreads();
	}

	public boolean hasReachedGravityThreshold() {
		return hasReachedGravityThreshold;
	}

	@Override
	public void movePhysicalPosition(long deltaTime) {
		float y=Projectile_GravityView.this.getY();
		//can only reach gravity threshold 1 time. After that, a new movement behavior should take over
		hasReachedGravityThreshold = hasReachedGravityThreshold || ( y+getHeight() ) > gravityThreshold; 
		
		super.movePhysicalPosition(deltaTime);
	}
}
