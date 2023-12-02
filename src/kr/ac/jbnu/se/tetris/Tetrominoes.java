package kr.ac.jbnu.se.tetris;

import java.awt.Color;

public enum Tetrominoes {

	NoShape, ZShape, SShape, LineShape, TShape, SquareShape, LShape, MirroredLShape,GrayShape;;



	public static int[][] getCoordinates(Tetrominoes shape) {
		switch (shape) {
			case NoShape:
				return new int[][] {{0, 0}, {0, 0}, {0, 0}, {0, 0}};
			case ZShape:
				return new int[][] {{0, 0}, {0, -1}, {-1, 0}, {-1, 1}};
			case SShape:
				return new int[][] {{0, 0}, {0, -1}, {1, 0}, {1, 1}};
			case LineShape:
				return new int[][] {{0, 0}, {0, -1}, {0, 1}, {0, 2}};
			case TShape:
				return new int[][] {{0, 0}, {0, -1}, {0, 1}, {-1, 0}};
			case SquareShape:
				return new int[][] {{0, 0}, {0, -1}, {1, 0}, {1, -1}};
			case LShape:
				return new int[][] {{0, 0}, {0, -1}, {0, 1}, {1, 1}};
			case MirroredLShape:
				return new int[][] {{0, 0}, {0, -1}, {0, 1}, {-1, 1}};
		}
		return null;
	}

	public static Color getColor(Tetrominoes shape) {
		switch (shape) {
			case NoShape:
				return Color.BLACK;
			case ZShape:
				return Color.RED;
			case SShape:
				return Color.GREEN;
			case LineShape:
				return Color.BLUE;
			case TShape:
				return Color.MAGENTA;
			case SquareShape:
				return Color.YELLOW;
			case LShape:
				return Color.ORANGE;
			case MirroredLShape:
				return Color.CYAN;
		}
		return null;


	}
}

