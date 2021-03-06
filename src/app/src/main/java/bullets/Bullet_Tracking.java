package bullets;

import interfaces.Shooter;
import parents.MovingView;
import android.widget.RelativeLayout;

public class Bullet_Tracking extends Bullet_Interface{

	public Bullet_Tracking(MovingView viewToTrack,
			Shooter shooterWithTrackingBullets,
			int bulletWidth, int bulletHeight, int bulletBackgroundId){
		super(bulletWidth,bulletHeight,bulletBackgroundId);

	}
	
	public BulletView makeBullet(int posOnShooterAsAPercentage, RelativeLayout layout, Shooter shooter, float bulletSpeedY, int bulletDamage){
		Bullet_TrackingView b = new Bullet_TrackingView(posOnShooterAsAPercentage,layout,shooter,
				bulletSpeedY, bulletDamage,width,height,backgroundId);
		return b;
	}
	
}
