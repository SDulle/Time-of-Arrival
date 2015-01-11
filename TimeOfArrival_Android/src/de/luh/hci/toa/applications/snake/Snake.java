package de.luh.hci.toa.applications.snake;

import java.io.Serializable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

public class Snake extends View implements Serializable {

	private Paint emptyPaint = new Paint();
	private Paint foodPaint = new Paint();
	private Paint snakePaint = new Paint();
	private Paint infoPaint = new Paint();
	
	private int width;
	private int size;
	private int[] snake;
	private boolean[] food;
	private int head;
	private int length;

	private boolean alive;
	private int tickDelay;
	private Handler gameLoop;
	
	int ticks;

	public static enum Heading {
		North, East, South, West;
		public static Heading randomHeading() {
			switch ((int)(Math.random()*4)) {
			case 0: return North;
			case 1: return South;
			case 2: return East;
			case 3: return West;
			default: return South;
			}
		}
	}
	private Heading heading;


	public Snake(Context context) {
		super(context);

		emptyPaint.setColor(Color.WHITE);
		foodPaint.setColor(Color.GREEN);
		snakePaint.setColor(Color.RED);
		infoPaint.setColor(Color.WHITE);
		infoPaint.setTextSize(25);
		
		width = 32;
		size = width*width;
		snake = new int[size];
		food = new boolean[size];

		head = (int)(Math.random()*size);
		length = 1;
		heading = Heading.randomHeading();
		
		alive = true;
		tickDelay = 200;

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
		
		placeFood(3);
		
		
		setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				float x = event.getX();
				float y = event.getY();
				
				double a = Math.atan2(y-getHeight()/2, x-getWidth()/2);
				
				turn(getHeadingForAngle(-a));
				
				return true;
			}
		});
	}
	
	

	public void end() {
		die();
	}


	public void die() {
		alive = false;
	}

	public void turn(Heading heading) {
		if(this.heading==Heading.South && heading==Heading.North) return;
		if(this.heading==Heading.North && heading==Heading.South) return;
		if(this.heading==Heading.East && heading==Heading.West) return;
		if(this.heading==Heading.West && heading==Heading.East) return;
		
		this.heading = heading;
	}

	protected void tick() {
		for(int i=0; i<snake.length; ++i) {
			if(snake[i]>0) snake[i]--;
		}

		switch (heading) {
		case North:
			head-=width;
			if(head<0)head+=size;
			break;
		case South:
			head+=width;
			if(head>=size)head-=size;
			break;
		case East:
			head+=1;
			if(row(head) != row(head-1)) head-=width;
			break;
		case West:
			head-=1;
			if(row(head) != row(head+1)) head+=width;
			break;
		default:
			break;
		}

		if(food[head]) {
			length++;
			food[head] = false;
			placeFood(1);
		}

		if(snake[head]>0) die();

		snake[head] = length;
		
		ticks++;
	}

	private int row(int pos) {
		return pos/width;
	}

	protected void placeFood(int count) {
		for(int i=0; i<count; ++i) {
			int index = 0;
			do {
				index = (int)(Math.random()*food.length);
			} while(food[index] || snake[index] > 0);
			food[index] = true;
		}
	}
	
	protected int index(int x, int y) {
		return y*width+x;
	}
	
	public Heading getHeadingForAngle(double a) {
		if(Math.abs(a) <= Math.PI/4) return Heading.East;
		if(Math.abs(a) >= Math.PI*3/4) return Heading.West;
		if(a > 0) return Heading.North;
		return Heading.South;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		canvas.drawColor(Color.BLACK);
		
		int dx = canvas.getWidth()/width;
		int dy = canvas.getHeight()/width;
		dx = dy = Math.min(dx, dy);
		
		int i=0;
		for(int y=0; y<width; ++y) {
			for(int x=0; x<width; ++x) {
				Paint paint = emptyPaint;
				if(snake[i]>0) paint = snakePaint;
				if(food[i]) paint = foodPaint;
				canvas.drawRect(x*dx, y*dy, x*dx+dx, y*dy+dy, paint);
				
				++i;
			}
		}
		
		canvas.drawText("Ticks: "+ticks+"  Size: "+length, 20, getHeight()-50, infoPaint);
	}
}


