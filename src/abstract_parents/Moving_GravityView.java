package abstract_parents;

import support.ConditionalHandler;
import interfaces.Gravity;
import android.content.Context;
import android.util.AttributeSet;

/**
 * A ProjectileView with a constant downwards force. This force is removed when the instance reaches its lowest threshold. 
 * The downward force may be different from the upward speed.
 * 
 * @author JAMES LOWREY
 *
 */
public abstract class Moving_GravityView extends MovingView implements Gravity{
	
	private int gravityThreshold;
	private boolean atThreshold;
	
	public Moving_GravityView(Context context,double movingSpeedY,double movingSpeedX){
		super(context, movingSpeedY, movingSpeedX);

		atThreshold=false;
		gravityThreshold=Gravity.NO_THRESHOLD;
		this.post(gravityRunnable);
	}
	
	public Moving_GravityView(Context context,AttributeSet at,double movingSpeedY,double movingSpeedX) {
		super(context,at,movingSpeedY,movingSpeedX);

		atThreshold=false;
		gravityThreshold=Gravity.NO_THRESHOLD;
		this.post(gravityRunnable);
	}

    //GRAVITY RUNNABLE
    Runnable gravityRunnable = new Runnable(){
    	@Override
        public void run() {
        		atThreshold=moveDirection(Moving_ProjectileView.DOWN);
        		
        		//if View is at lowest threshold stop reposting runnable
        		if(!atThreshold){
        			ConditionalHandler.postIfAlive(this, HOW_OFTEN_TO_MOVE/2,Moving_GravityView.this);
        		}
    	}
    };
    
    public boolean moveDirection(int direction){
    	boolean offScreen =  super.moveDirection(direction);
    	boolean atThreshold=false;
    	if(direction==DOWN){
    		float y=this.getY();
    		atThreshold = gravityThreshold!=NO_THRESHOLD && (y+getHeight())>gravityThreshold;
    	}
    	return offScreen || atThreshold;
    }
    
    
    
	public void stopGravity(){
		this.removeCallbacks(gravityRunnable);		
	}
	public void startGravity(){
		this.postDelayed(gravityRunnable,HOW_OFTEN_TO_MOVE);		
	}
	
	@Override
	public void restartThreads(){
		if( ! atThreshold){
			startGravity();
		}
	}

	@Override
	public void setThreshold(int newLowestPositionThreshold) {
		this.gravityThreshold = newLowestPositionThreshold;		
	}

	@Override
	public int getThreshold() {
		return gravityThreshold;
	}
}