package enemies_non_shooters;

import parents.Moving_ProjectileView;
import support.ConditionalHandler;
import android.content.Context;

import com.jtronlabs.to_the_moon.MainActivity;
import com.jtronlabs.to_the_moon.R;

import enemies.EnemyView;
import friendlies.ProtagonistView;

public class TrackingView extends EnemyView{

	public static final float DEFAULT_SPEED_Y=(float) 4.5,
			DEFAULT_SPEED_X=(float) 2.5,
			DEFAULT_SPAWN_BENEFICIAL_OBJECT_ON_DEATH=(float) .02,
			DEFAULT_BULLET_FREQ=5000;
	
	public static final int DEFAULT_COLLISION_DAMAGE=ProtagonistView.DEFAULT_HEALTH/10,
			DEFAULT_SCORE=50,
			DEFAULT_HEALTH=ProtagonistView.DEFAULT_BULLET_DAMAGE*3,
			DEFAULT_BACKGROUND=R.drawable.ship_enemy_tracker;
	
	private Moving_ProjectileView viewToTrack;
	
	private Runnable track = new Runnable(){
		@Override
		public void run() {
			final int trackPoint =(int) ( viewToTrack.getX()*2 + viewToTrack.getWidth() )/2;
			final int myPos = (int)( TrackingView.this.getX()*2 + TrackingView.this.getWidth() )/2;
			final int diff = trackPoint - myPos;
			float speedX=Math.abs( TrackingView.this.getSpeedX() );
			if(diff!=0){
				speedX  *= diff/Math.abs(diff);
			}
			TrackingView.this.setSpeedX(speedX);
			TrackingView.this.moveDirection(SIDEWAYS);
			
			ConditionalHandler.postIfAlive(this,HOW_OFTEN_TO_MOVE,TrackingView.this);
		}
	};
	public TrackingView(Context context,Moving_ProjectileView trackMe) {
		super(context, DEFAULT_SCORE, 
				DEFAULT_SPEED_Y, DEFAULT_SPEED_X,
				DEFAULT_COLLISION_DAMAGE,
				DEFAULT_HEALTH, 
				DEFAULT_SPAWN_BENEFICIAL_OBJECT_ON_DEATH, 
				(int)context.getResources().getDimension(R.dimen.ship_tracker_width),
				(int)context.getResources().getDimension(R.dimen.ship_tracker_height), 
				DEFAULT_BACKGROUND);
		
		viewToTrack=trackMe;
		this.setX((float) (MainActivity.getWidthPixels()*Math.random()));
		ConditionalHandler.postIfAlive(track, this);
	}

	public TrackingView(Context context,Moving_ProjectileView trackMe, int scoreForKilling,
			float projectileSpeedY, float projectileSpeedX,
			int projectileDamage, int projectileHealth,
			float probSpawnBeneficialObject, int width, int height, int imageId) {
		super(context, 
				scoreForKilling, 
				projectileSpeedY, projectileSpeedX,
				projectileDamage,
				projectileHealth, 
				probSpawnBeneficialObject, 
				width,height, 
				imageId);
		
		viewToTrack=trackMe;
		this.setX((float) (MainActivity.getWidthPixels()*Math.random()));
		ConditionalHandler.postIfAlive(track, this);
	}

	@Override 
	public void restartThreads(){
		ConditionalHandler.postIfAlive(track,HOW_OFTEN_TO_MOVE, this);
		super.restartThreads();
	}
}