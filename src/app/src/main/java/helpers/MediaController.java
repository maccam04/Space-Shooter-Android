package helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Vibrator;

import com.jtronlabs.space_shooter.MainActivity;
import com.jtronlabs.space_shooter.R;

public class MediaController {
	
	/**
	 * Should be called whenever vibration is needed to ensure user's preference is respected
	 * @param ctx
	 * @param vibrationPattern
	 */
	public static void vibrate(Context ctx, long[] vibrationPattern){
		SharedPreferences gameState = ctx.getSharedPreferences(MainActivity.GAME_SETTING_PREFS, 0);
		final boolean vibrateIsOn = gameState.getBoolean(MainActivity.VIBRATE_PREF, true);
		if(vibrateIsOn){
	        Vibrator vibrator = (Vibrator)ctx.getSystemService(Context.VIBRATOR_SERVICE);
	        vibrator.vibrate(vibrationPattern, -1);
		}
	}

	public static void vibrate(Context ctx, long vibrateLength){
		SharedPreferences gameState = ctx.getSharedPreferences(MainActivity.GAME_SETTING_PREFS, 0);
		final boolean vibrateIsOn = gameState.getBoolean(MainActivity.VIBRATE_PREF, true);
		if(vibrateIsOn){
	        Vibrator vibrator = (Vibrator)ctx.getSystemService(Context.VIBRATOR_SERVICE);
	        vibrator.vibrate(vibrateLength);
		}
	}
	
	
	private static MediaPlayer soundNonLoopingMediaPlayer,//reference to MP is kept, sound can be stopped and known if playing
		soundLoopingMediaPlayer;
    public static boolean donePlayingNonLoopingSoundClip=true,
    		currentlyPlayingLoopingSound=false;

    public static void stopNonLoopingSound() {
        if (soundNonLoopingMediaPlayer != null) {
            soundNonLoopingMediaPlayer.release();
            soundNonLoopingMediaPlayer = null;
        }
    }

    public static void stopLoopingSound() {
        if (soundLoopingMediaPlayer != null) {
        	currentlyPlayingLoopingSound = false;
        	soundLoopingMediaPlayer.release();
        	soundLoopingMediaPlayer = null;
        }
    }

    public static void playSoundClip(Context c, int rid,boolean soundIsLooping) {
    	//source for non looping sounds http://stackoverflow.com/questions/18254870/play-a-sound-from-res-raw
    	
		SharedPreferences gameState = c.getSharedPreferences(MainActivity.GAME_SETTING_PREFS, 0);
		final boolean soundIsOn = gameState.getBoolean(MainActivity.SOUND_PREF, true);
		
		if(soundIsOn){
			if(soundIsLooping){
				stopLoopingSound();
				currentlyPlayingLoopingSound = true;
		        soundLoopingMediaPlayer = MediaPlayer.create(c, rid);
		        soundLoopingMediaPlayer.setLooping(true);
		        soundLoopingMediaPlayer.start();
			}else{
		    	stopNonLoopingSound();
		        donePlayingNonLoopingSoundClip=false;
		 
		        soundNonLoopingMediaPlayer = MediaPlayer.create(c, rid);
		        soundNonLoopingMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
		            @Override
		            public void onCompletion(MediaPlayer mediaPlayer) {
		            	stopNonLoopingSound();
		                donePlayingNonLoopingSoundClip=true;
		            }
		        });
		
		        soundNonLoopingMediaPlayer.start();
			}
		}
    } 
    
    private static SoundPool soundEffects;//sound pool should be used for effects that are called a lot, like shooting
    public static int SOUND_BONUS,SOUND_COINS,SOUND_EXPLOSION1,SOUND_FRIENDLY_HIT,SOUND_LASER_SHOOT,SOUND_LASER_LOOPING,
    	SOUND_ROCKET_LAUNCH;
        
    /**
     * Play a common sound effect
     * @param c		the context
     * @param soundEffect	given int from this class used to determine sound effect id
     */ 
    public static void playSoundEffect(Context c, int soundEffect){
    	setupSoundEffect(c);

    	playSound(c,false,soundEffect);
    }
    
    public static int playLoopingSoundEffect(Context c, int soundEffect){
    	setupSoundEffect(c);

    	return playSound(c,true,soundEffect);
    }
    
    public static void stopLoopingSoundEffect(Context c, int soundEffectStreamId){
    	soundEffects.stop(soundEffectStreamId);
    }
    
    private static void setupSoundEffect(Context c){
    	if(soundEffects == null){
    		soundEffects = new SoundPool(20, AudioManager.STREAM_MUSIC,0);
    		SOUND_BONUS = soundEffects.load(c, R.raw.bonus, 1);
    		SOUND_COINS = soundEffects.load(c, R.raw.coins, 1);
    		SOUND_EXPLOSION1 = soundEffects.load(c, R.raw.explosion1, 1);
    		SOUND_FRIENDLY_HIT = soundEffects.load(c, R.raw.friendly_hit, 1);
    		SOUND_LASER_SHOOT = soundEffects.load(c, R.raw.laser_shoot, 1);
    		SOUND_LASER_LOOPING = soundEffects.load(c, R.raw.laser_looping, 1);
    		SOUND_ROCKET_LAUNCH = soundEffects.load(c, R.raw.rocket_launch,1);
    	}    	
    }
    
    /**
     * 
     * @param c
     * @param isLooping
     * @param soundEffectId
     * @return StreamId of sound effect played. Required to stop a looping sound
     */
    private static int playSound(Context c,boolean isLooping, int soundEffectId){
		SharedPreferences gameState = c.getSharedPreferences(MainActivity.GAME_SETTING_PREFS, 0);
		final boolean soundIsOn = gameState.getBoolean(MainActivity.SOUND_PREF, true);
		
		int loopParameter = (isLooping) ? -1 : 0;
		
		if(soundIsOn){
			return soundEffects.play(soundEffectId,1,1, 1,loopParameter,1);			
		}else{
			return -1;
		}
    }
}
