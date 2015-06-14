package bonuses;
 
import support.MediaController;
import interfaces.MovingViewInterface;
import interfaces.Shooter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.jtronlabs.to_the_moon.GameActivity;
import com.jtronlabs.to_the_moon.R;

public class Bonus_HealView extends BonusView implements MovingViewInterface{
	
	public Bonus_HealView(Context context,float positionX,float positionY) {
		super(context,positionX,positionY);	
 
		//set image size
		Drawable dr = this.getResources().getDrawable(R.drawable.heal);
		Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
		int newLength = this.getResources().getDimensionPixelOffset(R.dimen.bonus_img_len);
		Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, newLength, newLength, true));
		
		this.setImageDrawable(d);
	}
	
	public void applyBenefit(Shooter theBenefitter){
		MediaController.playSoundEffect(getContext(), MediaController.SOUND_BONUS);
		
		final int amtToHeal = theBenefitter.getMaxHealth()/6;
		
		if(theBenefitter.getHealth()+amtToHeal<theBenefitter.getMaxHealth()){
			theBenefitter.heal(amtToHeal);
		}else{
			theBenefitter.heal(theBenefitter.getMaxHealth()-theBenefitter.getHealth());
		}
			
	}
}
