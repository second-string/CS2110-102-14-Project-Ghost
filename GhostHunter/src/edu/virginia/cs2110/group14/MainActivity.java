package edu.virginia.cs2110.group14;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {

	private final int NUMGHOSTS_EASY = 1;
	private final int NUMGHOSTS_MEDIUM = 7;
	private final int NUMGHOSTS_HARD = 10;

	private Handler frame = new Handler();
	private ImageView ghostBuster;
	private Runnable r;
	private ArrayList<Ghost> ghostList;
	private int initialNumGhosts;
	
	 Rect bottomBound= new Rect();
	 Rect topBound=new Rect();
	 Rect leftBound=new Rect();
	 Rect rightBound=new Rect();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Music
		MediaPlayer gameMusic = MediaPlayer.create(MainActivity.this,
				R.raw.gamesound);
		gameMusic.start();

		// initializes buttons
		runButtons();

		// sets up ghostbuster image
		ghostBuster = (ImageView) findViewById(R.id.ghostbuster);

		// checks the difficulty level and sets the number of ghosts accordingly
		if (getDifficultyLevel() == 0)
			initialNumGhosts = NUMGHOSTS_EASY;
		else if (getDifficultyLevel() == 1)
			initialNumGhosts = NUMGHOSTS_MEDIUM;
		else if (getDifficultyLevel() == 2)
			initialNumGhosts = NUMGHOSTS_HARD;

		// creates all of the ghosts
		ghostList = new ArrayList<Ghost>();
		for (int i = 0; i < initialNumGhosts; i++) {
			ghostList.add(makeGhost());
		}

		runOverall();

	}

	public void runOverall() {
		r = new Runnable() {
			public void run() {
				for (int i = 0; i < ghostList.size(); i++) {
					ghostList.get(i).move();
					collisionResponse(ghostList.get(i));
				}
				frame.postDelayed(r, 20);
			}
		};
		r.run();
		
	}

	public Point randomPointGenerator() {
		Point screen = getScreenSize();
		int x = (int) Math.abs(Math.random() * (screen.x - 100));
		int y = (int) Math.abs(Math.random() * (screen.y - 250));
		Point p = new Point(x, y);
		return p;
	}

	// returns the dimensions of the screen as a point
	// x value is width, y value is height
	public Point getScreenSize() {
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size;
	}

	public void collisionResponse(Ghost g) {
		Point screenDimensions = getScreenSize();
		
		if (g.getX() + g.getGhostImage().getWidth() > screenDimensions.x - 10 || g.getX() < 10)
			g.changeVelocity(-g.getXVelocity(), g.randomInitialVelocity());
		if (g.getY() > screenDimensions.y - 275 || g.getY() < 10)
			g.changeVelocity(g.randomInitialVelocity(), -g.getYVelocity());
		
		Log.d("collision ghost", "x: " + g.getX() + ", y: " + g.getY());
		
		String action = CollisionHandler.busterGhostCollisions(g, ghostBuster);
		if (action.compareTo("ghost on left") == 0
				|| action.compareTo("ghost on right") == 0) {
			// sound warning that ghost is close
			Log.d("ghost", "ghost on left or right");
		}
		if (action.compareTo("ghost on bottom=kill ghost") == 0) {
			// make ghost disappear
			//Log.d("ghost", "ghost on bottom");
		}
		if (action.compareTo("ghost on top=kill buster") == 0) {
			// make buster lose a life
			//Log.d("ghost", "ghost on  top");
		}
	}

	// call to make a new ghost object and put it on the screen at a random
	// point
	public Ghost makeGhost() {
		ImageView ghostIMG = new ImageView(this);
		ghostIMG.setImageResource(R.drawable.ghost);
		Point pt = this.randomPointGenerator();
		RelativeLayout screenLayout = (RelativeLayout) findViewById(R.id.screen);
		screenLayout.addView(ghostIMG);
		return new Ghost(ghostIMG, pt.x, pt.y);
	}

	public void runButtons() {
		// beginning of UP button implementation
		Button up = (Button) findViewById(R.id.up_button);
		up.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent e) {
				if (e.getAction() == MotionEvent.ACTION_DOWN) {
				RelativeLayout.LayoutParams mParams = (RelativeLayout.LayoutParams) ghostBuster
						.getLayoutParams();
				mParams.topMargin -= 50;
				ghostBuster.setLayoutParams(mParams);
				return true;
				}
				return false;
			}
		});

		// beginning of DOWN button implementation
		Button down = (Button) findViewById(R.id.down_button);
		down.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent e) {
				if (e.getAction() == MotionEvent.ACTION_DOWN) {
				RelativeLayout.LayoutParams mParams = (RelativeLayout.LayoutParams) ghostBuster
						.getLayoutParams();
				mParams.topMargin += 50;
				ghostBuster.setLayoutParams(mParams);
				return true;
				}
				return false;
			}
		});

		// beginning of RIGHT button implementation
		Button right = (Button) findViewById(R.id.right_button);
		right.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent e) {
				if (e.getAction() == MotionEvent.ACTION_DOWN) {
				RelativeLayout.LayoutParams mParams = (RelativeLayout.LayoutParams) ghostBuster
						.getLayoutParams();
				mParams.leftMargin += 50;
				ghostBuster.setLayoutParams(mParams);
				return true;
				}
				return false;
			}
		});

		// beginning of LEFT button implementation
		Button left = (Button) findViewById(R.id.left_button);
		left.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent e) {
				if (e.getAction() == MotionEvent.ACTION_DOWN) {
				RelativeLayout.LayoutParams mParams = (RelativeLayout.LayoutParams) ghostBuster
						.getLayoutParams();
				mParams.leftMargin -= 50;
				ghostBuster.setLayoutParams(mParams);
				return true;
				}
				return false;
			}
		});
	}

	//gets the difficulty level from the difficulty screen
	public int getDifficultyLevel() {
		Intent mainIntent = getIntent();
		return mainIntent.getIntExtra("DIFFICULTY", -1);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
