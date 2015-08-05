package parents;

import helpers.KillableRunnable;
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
	
	
	public Projectile_GravityView(RelativeLayout layout,float movingSpeedY,float movingSpeedX,int projectileDamage,
			int projectileHealth,int width,int height,int imageId){
		super(layout, movingSpeedY, movingSpeedX,projectileDamage,projectileHealth, width, height, imageId);

		hasReachedGravityThreshold=false;
		gravityThreshold=NO_THRESHOLD;

		reassignMoveRunnable( new KillableRunnable(){
	    	@Override
	        public void doWork() {
	    		move();
	    		
	    		float y=Projectile_GravityView.this.getY();
	    		hasReachedGravityThreshold = (y+getHeight()) > gravityThreshold;

        		//if View is at lowest threshold stop reposting runnable
        		if(!hasReachedGravityThreshold){
        			postDelayed(this, HOW_OFTEN_TO_MOVE/2);
        		}else{
        			reachedGravityPosition();
        		}
	    	}
	    });
	}
    /**
     * Once the View has achieved it's threshold, allow further logic to be called
     */
    protected abstract void reachedGravityPosition();
    
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
}
