package com.jtronlabs.to_the_moon;

import friendlies.ProtagonistView;
import interfaces.GameActivityInterface;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import levels.LevelSystem;
import support.KillableRunnable;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends Activity implements OnTouchListener, GameActivityInterface{

	private static int offscreenBottom;
	
	public static final String GAME_STATE_PREFS = "GameStatePrefs",
			STATE_HEALTH="health",
			STATE_RESOURCES="resources",
			STATE_GUN_CONFIG="gunConfig",
			STATE_BULLET_FREQ_LEVEL="bulletFreqLevel",
			STATE_BULLET_DAMAGE_LEVEL="bulletDamageLevel",
			STATE_DEFENCE_LEVEL="defenceLevel",
			STATE_RESOURCE_MULTIPLIER_LEVEL="resourceMultLevel",
			STATE_FRIEND_LEVEL="friendLevel",
			STATE_LEVEL="level";
//			STATE_WAVE="wave";
	
//	private Button btnMoveLeft,btnMoveRight,btnMoveUp,btnMoveDown;
	private ImageButton btnMove;
	private Button btnShoot;
	private ImageButton	btnIncBulletDmg,btnIncBulletVerticalSpeed,
	btnIncBulletFreq,btnIncScoreWeight,btnNewGun,btnHeal,btnPurchaseFriend,btnNextLevel;
	private TextView resourceCount,healthCount,levelCount;
	private ProgressBar healthBar;
	public ProtagonistView protagonist;
	public ImageView rocketExhaust;
	private RelativeLayout gameLayout,storeLayout;

	//MODEL
	private LevelSystem levelCreator;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		//set up Gameplay Views and listeners and layouts
//		btnMoveLeft= (Button)findViewById(R.id.btn_move_left); 
//		btnMoveRight= (Button)findViewById(R.id.btn_move_right);
//		btnMoveUp = (Button)findViewById(R.id.btn_move_up);
//		btnMoveDown = (Button)findViewById(R.id.btn_move_down);
	    btnMove = (ImageButton)findViewById(R.id.btn_move);
		btnShoot = (Button)findViewById(R.id.btn_shoot);
//		btnMoveUp.setOnTouchListener(this);
//		btnMoveDown.setOnTouchListener(this);
//		btnMoveLeft.setOnTouchListener(this);
//		btnMoveRight.setOnTouchListener(this); 
		btnMove.setOnTouchListener(this);
		btnShoot.setOnTouchListener(this);
		gameLayout=(RelativeLayout)findViewById(R.id.gameplay_layout);
		healthBar=(ProgressBar)findViewById(R.id.health_bar);
		healthBar.setMax((int) ProtagonistView.DEFAULT_HEALTH);
		healthBar.setProgress(healthBar.getMax());
		rocketExhaust = (ImageView)findViewById(R.id.rocket_exhaust);
		
		//set up Store View and listeners
		resourceCount = (TextView)findViewById(R.id.resource_count);
		healthCount = (TextView)findViewById(R.id.health_count);
		levelCount = (TextView)findViewById(R.id.level_count);
		storeLayout = (RelativeLayout)findViewById(R.id.store_layout);
		btnIncBulletDmg= (ImageButton)findViewById(R.id.btn_inc_bullet_dmg); 
		btnIncBulletVerticalSpeed= (ImageButton)findViewById(R.id.btn_inc_defence);  
		btnIncBulletFreq= (ImageButton)findViewById(R.id.btn_inc_bullet_freq); 
		btnIncScoreWeight= (ImageButton)findViewById(R.id.btn_inc_score_weight); 
		btnNewGun= (ImageButton)findViewById(R.id.btn_new_gun); 
		btnHeal= (ImageButton)findViewById(R.id.btn_heal); 
		btnPurchaseFriend= (ImageButton)findViewById(R.id.btn_purchase_friend); 
		btnNextLevel= (ImageButton)findViewById(R.id.start_next_level); 
		
		//Set up misc views and listeners
		ImageButton accept = (ImageButton)findViewById(R.id.gameOverAccept);
		accept.setOnTouchListener(this);
		ImageButton share = (ImageButton)findViewById(R.id.gameOverShare);
		share.setOnTouchListener(this);
		
		btnIncBulletDmg.setOnTouchListener(this); 
		btnIncBulletVerticalSpeed.setOnTouchListener(this); 
		btnIncBulletFreq.setOnTouchListener(this); 
		btnIncScoreWeight.setOnTouchListener(this); 
		btnHeal.setOnTouchListener(this); 
		btnPurchaseFriend.setOnTouchListener(this); 
		btnNextLevel.setOnTouchListener(this); 
		btnNewGun.setOnTouchListener(this); 
		
		//set up control panel
		RelativeLayout controlPanel = (RelativeLayout)findViewById(R.id.control_panel);
		offscreenBottom = (int) MainActivity.getHeightPixels() - controlPanel.getLayoutParams().height ;
		
		//set up the game
		levelCreator = new LevelSystem(this);
		
		//onResume() is called after onCreate, so needed setup is done there
	}
	
	public static int getBottomScreen(){
		return offscreenBottom;
	}
	/**
	 * Pause game. Eventually, state will need to be saved to database, as after on pause any
	 * variables are liable for Android garbage collection!
	 */
	@Override
    public void onPause() {
        super.onPause();
        
        KillableRunnable.killAll();
		levelCreator.pauseLevel();
		
		//protagonist attributes saved when he is removeGameObject() in pauseLevel
		//store upgrades are saved straight to persistent storage when bought (so does not need to be saved here).
		//level attributes are saved in the levelSystem
		//gameover() resets variables in method
		
		Log.d("lowrey","num enemies on pause = "+levelCreator.enemies.size());
    }
	
	@Override
	public void onResume(){
		super.onResume(); 
		
		//I have NO IDEA why, but enemies can exist onResume(). These enemies are gone onPause(), and all debugging has failed thus 
		//far. Simple workaround is to remove these misplaced enemies
		for(int i=levelCreator.enemies.size()-1;i>=0;i--){
			levelCreator.enemies.get(i).removeGameObject();
		}

				
		levelCreator.loadScoreAndWaveAndLevel();//need to reload variables first thing
		recreateFriendlies();			
		
		if(levelCreator.getLevel()==0){
			levelCreator.resumeLevel();
		}else{
			openStore();
		}
		
	}
	
	public void lostGame(){
		gameOver();
		
		healthBar.setProgress(0);
		
		TextView title = (TextView)findViewById(R.id.gameOverTitle);
		title.setText("GAME OVER");
	}
	
	public void beatGame(){
		gameOver();

		TextView title = (TextView)findViewById(R.id.gameOverTitle);
		title.setText("WINNER");
	}
	
	private void gameOver(){
		KillableRunnable.killAll();
		levelCreator.pauseLevel();
		resetSavedVariables();
		
		RelativeLayout gameOverLayout = (RelativeLayout)findViewById(R.id.gameOverWindow);
		gameOverLayout.setVisibility(View.VISIBLE);
		
		gameLayout.setVisibility(View.GONE);
		storeLayout.setVisibility(View.GONE);
	}
	
	/**
	 * All persistent variables set to default values
	 */
	private void resetSavedVariables(){
		SharedPreferences gameState = getSharedPreferences(GAME_STATE_PREFS, 0);
		SharedPreferences.Editor editor = gameState.edit();
		
		editor.putInt(STATE_HEALTH,ProtagonistView.DEFAULT_HEALTH);	//protagonist properties
		
		editor.putInt(STATE_RESOURCES, 0);//store upgrades	
		editor.putInt(STATE_DEFENCE_LEVEL, 0);
		editor.putInt(STATE_GUN_CONFIG, -1);
		editor.putInt(STATE_BULLET_DAMAGE_LEVEL, 0);
		editor.putInt(STATE_BULLET_FREQ_LEVEL, 0);
		editor.putInt(STATE_RESOURCE_MULTIPLIER_LEVEL, 0);
		editor.putInt(STATE_FRIEND_LEVEL, 0);
		
		editor.putInt(STATE_LEVEL, 0);		//reset level properties
		
		editor.commit();
	}
	
	public void openStore(){
		gameLayout.setVisibility(View.GONE);
		storeLayout.setVisibility(View.VISIBLE);
		resourceCount.setText(""+NumberFormat.getNumberInstance(Locale.US).format(levelCreator.getResourceCount()));
		levelCount.setText("Days In Space : "+ levelCreator.getLevel() );
		final int health =  (int) ((protagonist.getHealth()+0.0) /protagonist.getMaxHealth() * 100);
		healthCount.setText("Hull : "+ health +"%");
	}
	
	private void closeStoreAndResumeLevel(){
		storeLayout.setVisibility(View.GONE);
		gameLayout.setVisibility(View.VISIBLE);

		levelCreator.resumeLevel();
	}
	
	private void recreateFriendlies(){
		SharedPreferences gameState = getSharedPreferences(GAME_STATE_PREFS, 0);
		
		//create protagonist View & restore his state
		protagonist = new ProtagonistView(GameActivity.this,GameActivity.this);
		int protagonistPosition = (int) (offscreenBottom - protagonist.getLayoutParams().height * 1.5);// * 1.5 is for some botttom margin
		protagonist.setY( protagonistPosition );
		protagonist.setHealth(gameState.getInt(STATE_HEALTH, ProtagonistView.DEFAULT_HEALTH));
		
	}

	private boolean canBeginShooting=true,beginShootingRunnablePosted=false;
	/**
	 * 
	 * @param left
	 * @param top
	 * @param right 
	 * @param bottom
	 * @param xTouch
	 * @param yTouch
	 * @return 1=top left, 2=top mid, 3=top right, 4=mid left, 5=mid mid, 6=mid right, 7=bottom left, 8=bottom mid, 9=bottom right
	 */
	private float[] percentXYAwayFromMidPoint(float left, float top,float right,float bottom,float xTouch,float yTouch){
		final float midX = (right-left)/2;
		final float midY = (bottom-top)/2;
		float xPosInPercent = (xTouch-midX)/(right-left);
		float yPosInPercent = (yTouch-midY)/(bottom-top);
		xPosInPercent = (Math.abs(xPosInPercent)>1) ? xPosInPercent/(Math.abs(xPosInPercent)) : xPosInPercent ;//maximum value is +/- 1
		yPosInPercent = (Math.abs(yPosInPercent)>1) ? yPosInPercent/(Math.abs(yPosInPercent)) : yPosInPercent ;
		final float[] percents = {xPosInPercent,yPosInPercent};
//		Log.d("lowrey","x= "+xPosInPercent+" y= "+yPosInPercent);
		return percents;
	} 
	
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch(event.getAction()){
		case MotionEvent.ACTION_MOVE:
			switch(v.getId()){
				case R.id.btn_move:
					final float[] percentXY = percentXYAwayFromMidPoint(v.getX(), v.getY(), v.getX()+v.getWidth(), v.getY()+v.getHeight(),event.getX(),event.getY());
					protagonist.beginMoving(percentXY[0],percentXY[1]);
					break;
			}
			break;
		case MotionEvent.ACTION_DOWN:
			switch(v.getId()){
			case R.id.btn_move:
				final float[] percentXY_2 = percentXYAwayFromMidPoint(v.getX(), v.getY(), v.getX()+v.getWidth(), v.getY()+v.getHeight(),event.getX(),event.getY());
				protagonist.beginMoving(percentXY_2[0],percentXY_2[1]);
				break;
//				case R.id.btn_move_left:
//					protagonist.beginMoving(Moving_ProjectileView.LEFT);
//					break;  
//				case R.id.btn_move_right:
//					protagonist.beginMoving(Moving_ProjectileView.RIGHT);
//					break;
//				case R.id.btn_move_up:
//					protagonist.beginMoving(Moving_ProjectileView.UP);
//					break;
//				case R.id.btn_move_down:
//					protagonist.beginMoving(Moving_ProjectileView.DOWN);
//					break;
				case R.id.btn_shoot:
						
					if(canBeginShooting){
						protagonist.startShooting();
						//force a delay after finishing shooting before user can shoot again. This is to try and fix an infinite shooting bug
					}	
					canBeginShooting=false;
					break;
			}
			break;
		case MotionEvent.ACTION_UP:
//			v.performClick();//why is this needed?
			switch(v.getId()){
			case R.id.btn_move:
				protagonist.stopMoving();
				break;
//				case R.id.btn_move_left:
//						protagonist.stopMoving();
//					break; 
//				case R.id.btn_move_right:
//						protagonist.stopMoving();
//					break;
//				case R.id.btn_move_up:
//					protagonist.stopMoving();
//					break; 
//				case R.id.btn_move_down:
//					protagonist.stopMoving();
//					break;
				case R.id.btn_shoot:					
					protagonist.stopShooting();	
					
					if( ! beginShootingRunnablePosted){
						protagonist.postDelayed(new KillableRunnable(){
							@Override 
							public void doWork() {	
								canBeginShooting=true;beginShootingRunnablePosted=false;	
							}	
						},(long)ProtagonistView.DEFAULT_BULLET_FREQ);
						beginShootingRunnablePosted=true;
					}
					
					break;
				case R.id.btn_heal:
					confirmUpgradeDialog(ProtagonistView.UPGRADE_HEAL);
					break;
				case R.id.btn_inc_bullet_dmg:
					confirmUpgradeDialog(ProtagonistView.UPGRADE_BULLET_DAMAGE);					
					break;
				case R.id.btn_inc_bullet_freq:
					confirmUpgradeDialog(ProtagonistView.UPGRADE_BULLET_FREQ);
					break;
				case R.id.btn_inc_defence:
					confirmUpgradeDialog(ProtagonistView.UPGRADE_DEFENCE);					
					break;
				case R.id.btn_inc_score_weight:
					confirmUpgradeDialog(ProtagonistView.UPGRADE_SCORE_MULTIPLIER);
					break;
				case R.id.btn_new_gun:
					confirmUpgradeDialog(ProtagonistView.UPGRADE_GUN);					
					break;
				case R.id.btn_purchase_friend:
					confirmUpgradeDialog(ProtagonistView.UPGRADE_FRIEND);
					break;
				case R.id.start_next_level: 
					if(protagonist.getGunLevel() < 0){
						Toast.makeText(getApplicationContext(),"It's not safe! Repair ship blasters first", Toast.LENGTH_LONG).show();
					}else{
							new AlertDialog.Builder(this)
					    .setTitle("Continue")
					    .setMessage("Venture out into the void")
					    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
					        public void onClick(DialogInterface dialog, int which) { dialog.cancel(); }
					     })
					    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					        public void onClick(DialogInterface dialog, int which) {
					        	dialog.cancel();
					        	closeStoreAndResumeLevel();
					        }
					     })
//					    .setIcon(android.R.drawable.ic_dialog_alert)
					     .show();
					}
					break;
				case R.id.gameOverAccept:
					finish();
					break;
				case R.id.gameOverShare:
					final String[] sharingTargets = {"face","twit","whats","chat","sms","mms","plus","email","gmail"};
					
					List<Intent> targetedShareIntents = new ArrayList<Intent>();
					for(int i=0;i<sharingTargets.length;i++){
						Intent thisShareIntent = getShareIntent(this,sharingTargets[i]);
						if(thisShareIntent != null){targetedShareIntents.add(thisShareIntent);}
					}
					Intent chooser = Intent.createChooser(targetedShareIntents.remove(0), "Share via");
					chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
					this.startActivity(chooser);
			
					break;
			}
			break;
		}
		return false;//do not consume event, allow buttons to receive the touch as well
	}
	private Intent getShareIntent(Context ctx,String type){
		boolean found = false;
		Intent share = new Intent(android.content.Intent.ACTION_SEND);
		share.setType("text/plain");
		// gets the list of intents that can be loaded.
		List<ResolveInfo> resInfo = ctx.getPackageManager().queryIntentActivities(share, 0);
//		System.out.println("resinfo: " + resInfo);
		if (!resInfo.isEmpty()){
			for (ResolveInfo info : resInfo) {
				if (info.activityInfo.packageName.toLowerCase().contains(type) ||
						info.activityInfo.name.toLowerCase().contains(type) ) {
					if(!type.equals("mms")){
						share.putExtra(Intent.EXTRA_SUBJECT, this.getResources().getString(R.string.app_name) );
					}
					share.putExtra(Intent.EXTRA_TEXT, "I beat " + this.getResources().getString(R.string.app_name)+ "!!");
					found = true;
					share.setPackage(info.activityInfo.packageName);
					break;
				}
			}
			if (!found)
				return null;
			return share;
			}
		return null;
		}

	private void confirmUpgradeDialog(final int whichUpgrade){
		String msg="";
		int cost=0;
		boolean maxLevelItem=false;
		
		try{
			switch(whichUpgrade){
			case ProtagonistView.UPGRADE_BULLET_DAMAGE:
				cost = 	(int) (this.getResources().getInteger(R.integer.inc_bullet_damage_base_cost) 
						* Math.pow((protagonist.getBulletDamageLevel()+1),2)) ;
				msg=this.getResources().getString(R.string.upgrade_bullet_damage);
				break;
			case ProtagonistView.UPGRADE_DEFENCE:
				cost = (int) (this.getResources().getInteger(R.integer.inc_defence_base_cost) 
						* Math.pow((protagonist.getDefenceLevel()+1),2)) ;
				msg=this.getResources().getString(R.string.upgrade_defence);
				break;
			case ProtagonistView.UPGRADE_BULLET_FREQ:
				cost = (int) (this.getResources().getInteger(R.integer.inc_bullet_frequency_base_cost) 
						* Math.pow((protagonist.getBulletBulletFreqLevel()+1),2)) ;
				msg=this.getResources().getString(R.string.upgrade_bullet_frequency);
				break;
			case ProtagonistView.UPGRADE_GUN:
				cost = this.getResources().getIntArray(R.array.gun_upgrade_costs)[protagonist.getGunLevel()+1] ;
				msg = this.getResources().getStringArray(R.array.gun_descriptions)[protagonist.getGunLevel()+1];
				break;
			case ProtagonistView.UPGRADE_FRIEND:
				cost = this.getResources().getInteger(R.integer.friend_base_cost) ;
				msg=this.getResources().getString(R.string.upgrade_buy_friend);
				break;
			case ProtagonistView.UPGRADE_SCORE_MULTIPLIER:
				cost = this.getResources().getInteger(R.integer.score_multiplier_base_cost) ;
				msg=this.getResources().getString(R.string.upgrade_score_multiplier_create);
				break;
			case ProtagonistView.UPGRADE_HEAL:
				if(protagonist.getHealth()==protagonist.getMaxHealth()){
					maxLevelItem=true;
					msg="Ship fully healed";
				}else{
					cost = 	this.getResources().getInteger(R.integer.heal_base_cost) * (this.levelCreator.getLevel()) ;
					msg=this.getResources().getString(R.string.upgrade_heal);					
				}
				break;
			}
			if(!maxLevelItem){
				msg+="\n\n"+NumberFormat.getNumberInstance(Locale.US).format(cost);//add cost formatted with commas
			}
		}catch(IndexOutOfBoundsException e){//upgrade is past set bounds of the arrays.xml value
			msg="Maximum upgrade attained";
			maxLevelItem=true;
		}
		final int costCopy=cost;
		final boolean maxLevelItemCopy = maxLevelItem;
		
		new AlertDialog.Builder(this)
	    .setTitle("Upgrade")
	    .setMessage(msg)
	    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { dialog.cancel(); }
	     })
	    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	        	if(!maxLevelItemCopy){
	        		if(costCopy<=levelCreator.getResourceCount()){
		        		protagonist.applyUpgrade(whichUpgrade);
		        		
		        		//update Views in the store
		        		levelCreator.setResources(levelCreator.getResourceCount()-costCopy);
		        		levelCreator.saveResourceCount();
		    			resourceCount.setText(""+NumberFormat.getNumberInstance(Locale.US).format( levelCreator.getResourceCount()));
		    			final int health =  (int) ((protagonist.getHealth()+0.0) /protagonist.getMaxHealth() * 100);//in case health was purchased
		    			healthCount.setText("Hull : "+ health +"%");	 
		    			Toast.makeText(getApplicationContext(),"Purchased!", Toast.LENGTH_SHORT).show();       			
	        		}else{
	        			Toast.makeText(getApplicationContext(),"Not enough resources", Toast.LENGTH_SHORT).show();
	        		}
	        		dialog.cancel(); 
        		}else{
        			dialog.cancel();
        		}
	        }
	     })
//	    .setIcon(android.R.drawable.ic_dialog_alert)
	     .show();
	}
	
	
	@Override
	public ProtagonistView getProtagonist() {
		return this.protagonist;
	}
	@Override
	public void setHealthBar(int max, int progress){
		healthBar.setMax(max);
		healthBar.setProgress(progress);
	}
	@Override
	public void changeGameBackground(int newBackgroundId) {
		this.gameLayout.setBackgroundResource(newBackgroundId);
	}
	@Override
	public ImageView getExhaust(){
		return this.rocketExhaust;
	}

	@Override
	public void setScore(int score) {
		levelCreator.setResources(score);		
	}
	@Override
	public void incrementScore(int score){
		//load resource multiplier 
		SharedPreferences gameState = this.getSharedPreferences(GameActivity.GAME_STATE_PREFS, 0);
		int resourceMultiplier = (int) ( gameState.getInt(GameActivity.STATE_RESOURCE_MULTIPLIER_LEVEL, 0) + 1 );
		
		levelCreator.setResources(levelCreator.getResourceCount()+score * resourceMultiplier);		
	}

	@Override
	public void removeView(ImageView view) {
		gameLayout.removeView(view);
	}

	@Override
	public void addToForeground(ImageView view) {
		gameLayout.addView(view,gameLayout.getChildCount()-2);
	}

	@Override
	public void addToBackground(ImageView view) {
		//addToForeground is called in every instantiation of every MovingView. Thus addToBackground is non default,
		//and thus the view needs to be removed from its parent before it can be re-added
		gameLayout.removeView(view);
		gameLayout.addView(view,0);
	}
}