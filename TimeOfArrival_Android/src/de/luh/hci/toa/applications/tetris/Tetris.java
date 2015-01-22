package de.luh.hci.toa.applications.tetris;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class Tetris extends View {

	int width = 10;
	int heigth = 20;

	int blocks[][] = new int[heigth][width];

	int cursX;
	int cursY;

	private boolean alive = true;

	private Handler loop = new Handler();

	private int[][] tetromino;
	int tetrominoIndex;
	int tetrominoSize;

	long lastTouch = 0;

	Paint black = new Paint();
	Paint white = new Paint();
	
	public Tetris(Context context) {
		super(context);

		black.setColor(Color.BLACK);
		white.setColor(Color.WHITE);
		
		loop.post(new Runnable() {

			@Override
			public void run() {

				if(alive) {
					tick();
					invalidate();
					loop.postDelayed(this, 400);
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
					return true;
				} else {
					return false;
				}
			}
		});

		spawn();
		
	}
	
	public void end() {
		alive = false;
	}

	public void spawn() {
		cursX = (int)(Math.random()*7);
		cursY = 0;
		tetromino = Tetromino.random();
		tetrominoIndex = 0;

		if(tetromino[0].length == 16) tetrominoSize = 4;
		else if(tetromino[0].length == 9) tetrominoSize = 3;
		else tetrominoSize = 2;

		if(isStuck()) die();
	}

	public void input(double a) {
		if(alive) {
			
			if(a < 0) {
				if(-a<Math.PI/3) moveRight();
				else if(-a>2*Math.PI/3) moveLeft();
				//else drop();
			} else {
				if(a<Math.PI/3) turnRight();
				else if(a>2*Math.PI/3) turnLeft();
				//else pause();
			}

			
			postInvalidate();
		}
	}

	public void die() {
		alive = false;

		System.out.println("game over");
	}

	public void moveLeft() {
		cursX--;
		if(isStuck()) moveRight();
	}

	public void moveRight() {
		cursX++;
		if(isStuck()) moveLeft();
	}

	public void turnLeft() {
		tetrominoIndex--;
		if(tetrominoIndex<0) tetrominoIndex+=4;
		
		if(isStuck()) turnRight();
	}

	public void turnRight() {
		tetrominoIndex++;
		if(tetrominoIndex>3) tetrominoIndex-=4;

		if(isStuck()) turnLeft();
	}

	private boolean isStuck() {
		for(int x=0; x<tetrominoSize; ++x) {
			for(int y=0; y<tetrominoSize; ++y) {
				int t = tetromino[tetrominoIndex][y*tetrominoSize+x];
				if(t==0) continue;

				if(cursY+y >= heigth || cursX+x >= width || cursY+y < 0 || cursX+x < 0) return true;

				if(blocks[cursY+y][cursX+x]>0) return true;
			}
		}
		return false;
	}

	private void pasteBlocks() {
		for(int x=0; x<tetrominoSize; ++x) {
			for(int y=0; y<tetrominoSize; ++y) {
				int t = tetromino[tetrominoIndex][y*tetrominoSize+x];
				if(t>0)
					blocks[cursY+y][cursX+x] = t;
			}
		}
	}

	public void deleteRow(int row) {
		for(int i=row-1; i>=0; --i) {
			System.arraycopy(blocks[i], 0, blocks[i+1], 0, width);
		}
	}

	private void check() {
		for(int i=0; i<heigth; ++i) {
			if(rowFull(i)) {
				deleteRow(i);
				//TODO add score
				System.out.println("row "+i+" full");
			}
		}
	}
	
	private boolean rowFull(int row) {
		for(int i=0; i<width; ++i) {
			if(blocks[row][i] == 0) return false;
		}
		
		return true;
	}

	public void tick() {
		cursY++;

		if(isStuck()) {
			cursY--;
			pasteBlocks();
			
			check();
			spawn();
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		canvas.drawColor(Color.BLACK);
		
		for(int y=0; y<heigth; ++y) {
			for(int x=0; x<width; ++x) {
				int t = blocks[y][x];
				drawBlock(canvas, t, x, y);
			}
		}
		
		for(int y=0; y<tetrominoSize; ++y) {
			for(int x=0; x<tetrominoSize; ++x) {
				int t = tetromino[tetrominoIndex][y*tetrominoSize+x];

				if(t>0) {
					drawBlock(canvas, t, cursX+x, cursY+y);
				}
			}
		}
		
		
	}

	private void drawBlock(Canvas canvas, int block, int x, int y) {
		float size = getHeight()/heigth;
		
		if(block == 0) {
			canvas.drawRect(x*size, y*size, x*size+size, y*size+size, Tetromino.NO_COLOR);
			return;
		}
		
		Paint p = new Paint();
		p.setColor(Color.BLACK);
		
		canvas.drawRect(x*size, y*size, x*size+size, y*size+size, p);
		
		canvas.drawRect(x*size+1, y*size+1, x*size+size-1, y*size+size-1, Tetromino.getColor(block));
	}


}
