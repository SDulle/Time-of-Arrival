package de.luh.hci.toa.applications.hockey;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PathEffect;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class Hockey extends View {

	public static final double FIELD_HEIGHT = 1;
	public static final double FIELD_WIDTH = 2;
	public static final double FIELD_PLAYERAREA = 1.5;

	public static final double DISC_STUCK_THRESHOLD = 0.01;
	public static final double DISC_BOUNCE_SLOWDOWN = 0.8;
	public static final double DISC_TICK_SLOWDOWN = 0.999;
	public static final double DISC_SIZE = 0.35;

	private double _discX;
	private double _discY;
	private double _discVX;
	private double _discVY;

	private double lastTapX;
	private double lastTapY;
	
	private int currentPlayer = 1;
	
	private boolean alive;
	private int tickDelay;
	private Handler gameLoop;

	private Paint discPaint;
	private Paint playerTapPaint;
	
	public Hockey(Context context) {
		super(context);
		
		reset(1);
		
		alive = true;
		tickDelay = 50;
		
		gameLoop = new Handler();
		gameLoop.post(new Runnable() {

			@Override
			public void run() {

				if(alive) {
					tick();
					invalidate();
					gameLoop.postDelayed(this, tickDelay);
				}
			}
		});
		
		setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if((event.getAction() == MotionEvent.ACTION_DOWN)) {

					float x = event.getX();
					float y = event.getY();

					double a = Math.atan2(y-getHeight()/2, x-getWidth()/2);

					input(-a);
					System.out.println("yoo");
					return true;
				} else {
					return false;
				}
			}
		});
		
		discPaint = new Paint();
		discPaint.setStyle(Style.FILL_AND_STROKE);
		discPaint.setColor(Color.RED);
		
		playerTapPaint = new Paint();
		playerTapPaint.setColor(Color.WHITE);
	}
	
	public void end() {
		alive = false;
	}
	
	public void tick() {

		move();
		if(getVelocity()>0) setVelocity(getVelocity()*DISC_TICK_SLOWDOWN);

		checkBounce();
		checkStuck();
		checkPlayer1Score();
		checkPlayer2Score();
	}

	public void checkBounce() {
		if(Math.abs(_discY)+DISC_SIZE > FIELD_HEIGHT) {

			if(_discY > 0) {
				_discY -= 2*(_discY + DISC_SIZE - FIELD_HEIGHT);
			} else {
				_discY += 2*(-_discY + DISC_SIZE - FIELD_HEIGHT);
			}

			_discVY *= -1;
			setVelocity(getVelocity()*DISC_BOUNCE_SLOWDOWN);
		}
	}

	public void checkStuck() {
		if(Math.abs(_discX) < FIELD_PLAYERAREA && getVelocity() <= DISC_STUCK_THRESHOLD) {
			reset(currentPlayer == 1 ? 2 : 1);
		}
	}

	public void move() {
		_discX += _discVX;
		_discY += _discVY;
	}

	public void checkPlayer1Score() {
		if(_discX > FIELD_WIDTH+DISC_SIZE) {
			//TODO: add score to player 1
			reset(2);
		}
	}

	public void checkPlayer2Score() {
		if(_discX < -FIELD_WIDTH-DISC_SIZE) {
			//TODO: add score to player 2
			reset(1);
		}
	}
	
	public void hit(double position, int player) {
		double x = player == 1 ? -FIELD_WIDTH : FIELD_WIDTH;
		double y = position;
		lastTapX = x;
		lastTapY = y;
		
		double dist = getDiscDistance(x, y);
		System.out.println(dist);
		if(dist > 2*DISC_SIZE) return;
		
		_discVX = _discX-x;
		_discVY = _discY-y;
		
		setVelocity(Math.min(0.4, 0.05*1/dist));
		
		currentPlayer = player;

		
		postInvalidate();
	}
	
	public double getDiscDistance(double x, double y) {
		double dx = _discX-x;
		double dy = _discY-y;
		
		return Math.sqrt(dx*dx+dy*dy);
	}
	
	
	
	public void input(double theta) {
		int player = (Math.abs(theta) > Math.PI/2) ? 1 : 2;
		double position = -2*Math.sin(theta);
		
		hit(position, player);
	}

	public double getVelocity() {
		return Math.sqrt(_discVX*_discVX + _discVY*_discVY);
	}
	
	public void setVelocity(double newV) {
		double curV = getVelocity();
		
		_discVX *= newV/curV;
		_discVY *= newV/curV;
	}

	public void reset(int player) {
		_discY = 0;
		_discX = (FIELD_WIDTH+FIELD_PLAYERAREA)/2;
		_discVX = 0;
		_discVY = 0;

		if(player == 1) {
			_discX *= -1;
		}
		
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		int w = getWidth();
		int h = getHeight();
		
		canvas.translate(w/2, h/2);
		canvas.scale((float)((w/2)/FIELD_WIDTH), (float)((h/2)/FIELD_HEIGHT));
		
		//System.out.println(_discX+" "+_discY);
		//canvas.drawCircle((float)_discX, (float)_discY, 0.2f, discPaint);
		canvas.drawCircle((float)_discX, (float)_discY, (float)DISC_SIZE, discPaint);
		canvas.drawCircle((float)lastTapX, (float)lastTapY, (float)DISC_SIZE, playerTapPaint);
		//canvas.drawCircle(50, 50, 20, discPaint);
	}
}
