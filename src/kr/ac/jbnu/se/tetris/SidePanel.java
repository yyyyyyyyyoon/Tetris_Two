package kr.ac.jbnu.se.tetris;

import javax.swing.JPanel;
import java.awt.Graphics; // Graphics를 import 추가
import java.awt.Color;   // Color를 import 추가

public class SidePanel extends JPanel {
    private static final int SQUARE_WIDTH = 20;
    private static final int SQUARE_HEIGHT = 20;
    private static final int TEXT_X = 10;
    private static final int TEXT_Y = 180;
    private static final int INITIAL_X = 30;
    private static final int INITIAL_Y = 220;
    int score = 0;

    // 점수를 업데이트하는 메서드
    public void updateScore(int points) {
        score += points;
        repaint();
    }

    private Tetrominoes nextShape;

    private Tetrominoes curShape; // curShape 변수 추가

    private Tetrominoes currentShape;

    public void setCurrentShape(Tetrominoes currentShape) {
        this.currentShape = currentShape;
        repaint();
    }

    public void setCurShape(Tetrominoes curShape) {
        this.curShape = curShape;
        repaint();
    }

    public void setNextShape(Tetrominoes nextShape) {
        this.nextShape = nextShape;
        repaint(); // SidePanel을 다시 그리도록 요청
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 다음 블록 표시
        g.setColor(Color.BLACK);
        g.drawString("Next Shape:", TEXT_X, TEXT_Y);

        if (nextShape != null) {
            int[][] coords = Tetrominoes.getCoordinates(nextShape);

            // 다음 블록을 그리기
            for (int i = 0; i < coords.length; i++) {
                int x = coords[i][0] * SQUARE_WIDTH + INITIAL_X;
                int y = coords[i][1] * SQUARE_HEIGHT + INITIAL_Y;

                g.setColor(Board.colors[nextShape.ordinal()]);
                g.fillRect(x, y, SQUARE_WIDTH, SQUARE_HEIGHT);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, SQUARE_WIDTH, SQUARE_HEIGHT);
            }
        }
    }
}
