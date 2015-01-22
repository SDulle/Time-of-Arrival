package de.luh.hci.toa.applications.snake;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Op;
import android.graphics.PointF;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class SuperSnake extends View {
	
	public static float SNAKE_MOVE_DIST = 50;
	public static float SNAKE_HEAD_SIZE = 25;
	
	List<PointF> snake = new ArrayList<PointF>();
	Path snakePath = new Path();
	
	PointF food;

	float snakeHeadingX;
	float snakeHeadingY;
	
	Paint snakePaint;
	Paint foodPaint;
	Paint textPaint;
	Paint linePaint;

	private boolean alive;
	private int tickDelay;
	private Handler gameLoop;
	
	public SuperSnake(Context context) {
		super(context);
		
		alive = true;
		tickDelay = 100;
		
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
				
				float x = event.getX();
				float y = event.getY();
				
				double a = Math.atan2(y-getHead().y, x-getHead().x);
				
				input(a);
				
				return true;
			}
		});
		
		snake.add(new PointF(20, 20));
		food = new PointF(100, 100);
		
		snakePaint = new Paint();
		snakePaint.setStrokeWidth(SNAKE_MOVE_DIST/2);
		snakePaint.setStrokeJoin(Join.ROUND);
		snakePaint.setStrokeCap(Cap.ROUND);
		snakePaint.setColor(Color.RED);
		snakePaint.setStyle(Style.STROKE);
		snakePaint.setAntiAlias(true);
		
		foodPaint = new Paint();
		foodPaint.setColor(Color.GREEN);
		
		textPaint = new Paint();
		textPaint.setColor(Color.WHITE);
		textPaint.setTextSize(20);
		
		linePaint = new Paint();
		linePaint.setColor(Color.BLUE);
		linePaint.setStrokeWidth(15);
	}
	
	
	
	public void tick() {
		checkFood();
		moveBody();
		moveHead();
		checkBounce();
		//checkEat();
	}
	
	public void checkBounce() {
		PointF head = getHead();
		
		if(head.y > getHeight()) {
			head.y = getHeight();
			snakeHeadingY *= -1;
		}
		
		if(head.y < 0) {
			head.y = 0;
			snakeHeadingY *= -1;
		}
		
		if(head.x > getWidth()) {
			head.x = getWidth();
			snakeHeadingX *= -1;
		}
		
		if(head.x < 0) {
			head.x = 0;
			snakeHeadingX *= -1;
		}
	}
	
	public void checkEat() {
		constructPath();
		
		if(checkIntersect()) end();
		
	}
	
	private boolean checkIntersect() {
		for(int i=1; i<snake.size(); ++i) {
			System.out.println(distance(getHead(), snake.get(i)));
			if(distance(getHead(), snake.get(i)) < SNAKE_HEAD_SIZE) return true;
		}
		return false;
	}
//	
//	private boolean checkIntersect() {
//		for(int i=0; i<snake.size()-1; ++i) {
//			if(checkIntersect(snake.get(i), snake.get(i+1))) return true;
//		}
//		
//		return false;
//	}
//	
//	private boolean checkIntersect(PointF p1, PointF p2) {
//		for(int i=0; i<snake.size()-1; ++i) {
//			
//			if(checkIntersect(p1, p2, snake.get(i), snake.get(i+1))) return true;
//		}
//		
//		return false;
//	}
//	
//	private boolean checkIntersect(PointF p1, PointF p2, PointF q1, PointF q2) {
//		
//		float xMin_p = Math.min(p1.x, p2.x);
//		float xMax_p = Math.max(p1.x, p2.x);
//		float yMin_p = Math.min(p1.y, p2.y);
//		float yMax_p = Math.max(p1.y, p2.y);
//		
//		float xMin_q = Math.min(q1.x, q2.x);
//		float xMax_q = Math.max(q1.x, q2.x);
//		float yMin_q = Math.min(q1.y, q2.y);
//		float yMax_q = Math.max(q1.y, q2.y);
//		
//		if(xMax_q < xMin_p) return false;
//		if(yMax_q < yMin_p) return false;
//		if(xMin_q > xMax_p) return false;
//		if(yMin_q > yMax_p) return false;
//		
//		return (test2(p1, p2, q1) * test2(p1, p2, q2)) <= 0 &&
//			   (test2(q1, q2, p1) * test2(q1, q2, p2)) <= 0;
//	}
//	
//	private float test2(PointF o, PointF p, PointF q) {
//		return (p.x-o.x)*(q.y-o.y) - (p.y-o.y)*(q.x-o.x);
//	}
	
	public static double distance(PointF p1, PointF p2) {
		float dx = p1.x - p2.x;
		float dy = p1.y - p2.y;
		return Math.sqrt(dx*dx+dy*dy);
	}
	
	public void end() {
		alive = false;
	}
	
	public PointF getHead() {
		return snake.get(0);
	}
	
	public void input(double theta) {
		snakeHeadingX = (float)(SNAKE_MOVE_DIST * Math.cos(theta));
		snakeHeadingY = (float)(SNAKE_MOVE_DIST * Math.sin(theta));
	}
	
	public void placeFood() {
		food = new PointF((float)(Math.random()*getWidth()), (float)(Math.random()*getHeight())); 
	}
	
	public void checkFood() {
		PointF head = snake.get(0);

		double dist = distance(head, food);
		
		if(dist < SNAKE_MOVE_DIST) {
			addTail();
			placeFood();
		}
	}
	
	public void moveHead() {
		snake.get(0).offset(snakeHeadingX, snakeHeadingY);
	}
	
	public void addTail() {
		PointF tail = new PointF();
		tail.set(snake.get(snake.size()-1));
		
		snake.add(tail);
	}
	
	public void moveBody() {
		for(int i=snake.size()-1; i>0; --i) {
			PointF cur = snake.get(i);
			PointF next = snake.get(i-1);
			
			cur.set(next);
		}
	}
	
	public Path constructPath() {
		snakePath.rewind();
		snakePath.moveTo(snake.get(0).x, snake.get(0).y);
		for(PointF p: snake) {
			snakePath.lineTo(p.x, p.y);
		}
		
		return snakePath;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		
		constructPath();
		
		canvas.drawCircle(food.x, food.y, 10, foodPaint);
		
		canvas.drawPath(snakePath, snakePaint);
		
		canvas.drawCircle(getHead().x, getHead().y, SNAKE_HEAD_SIZE, snakePaint);
		
		canvas.drawLine(
				getHead().x, 
				getHead().y, 
				getHead().x+snakeHeadingX,
				getHead().y+snakeHeadingY, 
				linePaint);
		
		canvas.drawText("SCORE: "+snake.size(), 20, 20, textPaint);
	}
}
