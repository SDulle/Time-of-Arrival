package de.luh.hci.toa.applications.tetris;

import android.graphics.Color;
import android.graphics.Paint;

public abstract class Tetromino {
	public static final int[][] I = new int[][] {
		new int[] {
				0, 1, 0, 0,
				0, 1, 0, 0,
				0, 1, 0, 0,
				0, 1, 0, 0
		},
		new int[] {
				0, 0, 0, 0,
				1, 1, 1, 1,
				0, 0, 0, 0,
				0, 0, 0, 0
		},
		new int[] {
				0, 0, 1, 0,
				0, 0, 1, 0,
				0, 0, 1, 0,
				0, 0, 1, 0
		},
		new int[] {
				0, 0, 0, 0,
				0, 0, 0, 0,
				1, 1, 1, 1,
				0, 0, 0, 0
		}
	};
	
	public static final int[][] J = new int[][] {
		new int[] {
				0, 2, 0,
				0, 2, 0,
				2, 2, 0
		},
		new int[] {
				2, 0, 0,
				2, 2, 2,
				0, 0, 0
		},
		new int[] {
				0, 2, 2,
				0, 2, 0,
				0, 2, 0
		},
		new int[] {
				0, 0, 0,
				2, 2, 2,
				0, 0, 2
		}
	};
	
	public static final int[][] L = new int[][] {
		new int[] {
				0, 3, 0,
				0, 3, 0,
				0, 3, 3
		},
		new int[] {
				0, 0, 0,
				3, 3, 3,
				3, 0, 0
		},
		new int[] {
				3, 3, 0,
				0, 3, 0,
				0, 3, 0
		},
		new int[] {
				0, 0, 3,
				3, 3, 3,
				0, 0, 0
		}
	};
	
	public static final int[][] O = new int[][] {
		new int[] {
				4, 4,
				4, 4
		},
		new int[] {
				4, 4,
				4, 4
		},
		new int[] {
				4, 4,
				4, 4
		},
		new int[] {
				4, 4,
				4, 4
		}
	};
	
	public static final int[][] S = new int[][] {
		new int[] {
				0, 0, 0,
				0, 5, 5,
				5, 5, 0
		},
		new int[] {
				5, 0, 0,
				5, 5, 0,
				0, 5, 0
		},
		new int[] {
				0, 5, 5,
				5, 5, 0,
				0, 0, 0
		},
		new int[] {
				0, 5, 0,
				0, 5, 5,
				0, 0, 5
		}
	};
	
	public static final int[][] T = new int[][] {
		new int[] {
				0, 0, 0,
				6, 6, 6,
				0, 6, 0
		},
		new int[] {
				0, 6, 0,
				6, 6, 0,
				0, 6, 0
		},
		new int[] {
				0, 6, 0,
				6, 6, 6,
				0, 0, 0
		},
		new int[] {
				0, 6, 0,
				0, 6, 6,
				0, 6, 0
		}
	};
	
	public static final int[][] Z = new int[][] {
		new int[] {
				0, 0, 0,
				7, 7, 0,
				0, 7, 7
		},
		new int[] {
				0, 7, 0,
				7, 7, 0,
				7, 0, 0
		},
		new int[] {
				7, 7, 0,
				0, 7, 7,
				0, 0, 0
		},
		new int[] {
				0, 0, 7,
				0, 7, 7,
				0, 7, 0
		}
	};
	
	public static final Paint UNKNOWN_COLOR = new Paint();
	public static final Paint NO_COLOR = new Paint();
	public static final Paint I_COLOR = new Paint();
	public static final Paint J_COLOR = new Paint();
	public static final Paint L_COLOR = new Paint();
	public static final Paint O_COLOR = new Paint();
	public static final Paint S_COLOR = new Paint();
	public static final Paint T_COLOR = new Paint();
	public static final Paint Z_COLOR = new Paint();
	
	static{
		UNKNOWN_COLOR.setColor(Color.parseColor("black"));
		NO_COLOR.setColor(Color.parseColor("white"));
		I_COLOR.setColor(Color.parseColor("cyan"));
		J_COLOR.setColor(Color.parseColor("blue"));
		L_COLOR.setColor(Color.parseColor("#FF7F00"));
		O_COLOR.setColor(Color.parseColor("yellow"));
		S_COLOR.setColor(Color.parseColor("#BFFF00"));
		T_COLOR.setColor(Color.parseColor("#8B008B"));
		Z_COLOR.setColor(Color.parseColor("red"));
	}
	
	public static Paint getColor(int tetrominoType) {
		switch (tetrominoType) {
		case 0: return NO_COLOR;
		case 1: return I_COLOR;
		case 2: return J_COLOR;
		case 3: return L_COLOR;
		case 4: return O_COLOR;
		case 5: return S_COLOR;
		case 6: return T_COLOR;
		case 7: return Z_COLOR;
		default: return UNKNOWN_COLOR;
		}
	}
	
	public static int[][] random() {
		switch ((int)(Math.random()*7)) {
		case 0: return I;
		case 1: return J;
		case 2: return L;
		case 3: return O;
		case 4: return S;
		case 5: return T;
		case 6: return Z;
		default: return random();
		}
	}
}