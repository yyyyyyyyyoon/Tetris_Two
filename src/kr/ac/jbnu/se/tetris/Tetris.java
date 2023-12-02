package kr.ac.jbnu.se.tetris;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSplitPane;

public class Tetris extends JFrame {
	SidePanel sidePanel;  // 새로운 SidePanel 객체 추가

	JLabel statusbar;
	Board board;
	public Tetris() {

		board = new Board(this);
		statusbar = new JLabel(" 0");
		add(statusbar, BorderLayout.SOUTH);

		sidePanel = new SidePanel();  // SidePanel 객체 초기화

		sidePanel = board.getSidePanel(); // SidePanel 객체에 접근
		// Board 객체를 생성한 후에 splitPane를 설정
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidePanel, board);
		splitPane.setResizeWeight(0.3);  // 사이드 패널과 중앙 보드 크기 비율 조절
		splitPane.setContinuousLayout(true);  // 크기 조절이 실시간으로 반영되도록 설정
		splitPane.setDividerSize(5); // 경계선의 크기를 설정 (원하는 크기로 조절)
		add(splitPane, BorderLayout.CENTER);  // splitPane을 중앙에 배치
		board.start();

		setSize(300, 400);
		setTitle("Tetris");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public JLabel getStatusBar() {
		return statusbar;
	}

	public static void main(String[] args) {
		Music.playBackgroundMusic("source/BGM-Tetris-Kalinka_1.wav");
		Tetris game = new Tetris();
		game.setLocationRelativeTo(null);
		game.setVisible(true);
	}
}
