package kr.ac.jbnu.se.tetris;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener {
	private boolean removeBottomLines = false;
	private boolean removeAllLines = false;
	private boolean isScoreIncreased = false;
	private boolean isSlowDownItemUsed = false;
	private int slowDownItemUsage = 1;
	private int removeBottomLinesUsage = 1;
	private int removeAllLinesUsage = 1;
	private int isScoreIncreasedUsage = 1;
	private boolean isGameOver = false;
	final int BoardWidth = 10;
	final int BoardHeight = 22;
	private int accumulatedScore = 0;  // 누적 점수를 저장하는 변수
	private Tetrominoes savedShape = Tetrominoes.NoShape;
	private int slowDownDuration = 3000;
	private String displayMessage = "";
	private int messageDisplayDuration = 1000; // 효과를 표시할 시간 (밀리초)
	private long messageDisplayStartTime; // 효과 표시가 시작된 시간


	private long startTime;     // 게임이 시작될 때의 타임스탬프
	private JLabel timeLabel;   // 경과 시간을 표시하는 JLabel

	private SidePanel sidePanel; // Board 클래스 내부에서만 접근 가능한 SidePanel 객체
	Timer timer;
	boolean isFallingFinished = false;
	boolean isStarted = false;
	boolean isPaused = false;
	int numLinesRemoved = 0;
	int curX = 0;
	int curY = 0;
	JLabel statusbar;
	Shape curPiece;
	Tetrominoes[] board;

	private Shape nextPiece; // 다음 블록을 저장할 변수 추가

	public void saveShape() {
		if (savedShape == Tetrominoes.NoShape) {
			savedShape = curPiece.getShape();
			newPiece(); // 새로운 블록 생성
		}
	}

	public void retrieveSavedShape() {
		if (savedShape != Tetrominoes.NoShape) {
			// 현재 블록의 좌표를 설정하고 저장된 블록을 가져옴
			curX = BoardWidth / 2 + 1;
			curY = BoardHeight - 1 + curPiece.minY();
			curPiece.setShape(savedShape);
			savedShape = Tetrominoes.NoShape;
			repaint(); // 화면을 다시 그려줌
		}
	}

	public static Color colors[] = { new Color(0, 0, 0), new Color(204, 102, 102), new Color(102, 204, 102),
			new Color(102, 102, 204), new Color(204, 204, 102), new Color(204, 102, 204), new Color(102, 204, 204),
			new Color(218, 170, 0) };

	public Board(Tetris parent) {
		setFocusable(true);
		curPiece = new Shape();
		timer = new Timer(400, this);
		timer.start();

		this.sidePanel = sidePanel; // 전달받은 SidePanel 객체를 저장
		sidePanel = new SidePanel(); // SidePanel 객체 초기화
		nextPiece = new Shape();
		nextPiece.setRandomShape();
		sidePanel.setNextShape(nextPiece.getShape()); // SidePanel에 첫 번째 다음 블록 설정

		statusbar = parent.getStatusBar();
		board = new Tetrominoes[BoardWidth * BoardHeight];
		addKeyListener(new TAdapter());
		clearBoard();

		timeLabel = new JLabel("Time: 0", JLabel.RIGHT);
		timeLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
		timeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		add(timeLabel);

	}

	public SidePanel getSidePanel() {
		return sidePanel;
	}

	public void actionPerformed(ActionEvent e) {
		if (isFallingFinished) {
			isFallingFinished = false;
			newPiece();
		} else {
			oneLineDown();
		}
	}

	int squareWidth() {
		return (int) getSize().getWidth() / BoardWidth;
	}

	int squareHeight() {
		return (int) getSize().getHeight() / BoardHeight;
	}

	Tetrominoes shapeAt(int x, int y) {
		if (x < 0 || x >= BoardWidth || y < 0 || y >= BoardHeight) {
			return Tetrominoes.NoShape;
		}
		return board[(y * BoardWidth) + x];
	}

	public void start() {
		if (isPaused)
			return;

		isStarted = true;
		isFallingFinished = false;
		numLinesRemoved = 0;
		clearBoard();

		startTime = System.currentTimeMillis();

		newPiece();
		timer.start();

	}

	private void pause() {
		if (!isStarted)
			return;

		isPaused = !isPaused;
		if (isPaused) {
			timer.stop();
			statusbar.setText("paused");
		} else {
			timer.start();
			statusbar.setText(String.valueOf(numLinesRemoved));
		}
		repaint();
	}



	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Dimension size = getSize();
		int boardTop = (int) size.getHeight() - BoardHeight * squareHeight();

		// 게임 창 배경 색, 격자무늬
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.LIGHT_GRAY);

		drawGridLines(g);

		// 게임 오버 시 현재 쌓여있는 블록을 회색으로 변경
		if (isGameOver) {
			for (int i = 0; i < BoardHeight; ++i) {
				for (int j = 0; j < BoardWidth; ++j) {
					Tetrominoes shape = shapeAt(j, BoardHeight - i - 1);
					if (shape != Tetrominoes.NoShape) {
						drawSquare(g, 0 + j * squareWidth(), boardTop + i * squareHeight(), Tetrominoes.GrayShape);
					}
				}
			}
		}

		// 줄 삭제시 +포인트 문구 표시
		if (!displayMessage.isEmpty()) {
			g.setColor(Color.PINK);
			g.setFont(new Font("SansSerif", Font.BOLD, 20));

			// 문구 일정시간 표시 후 삭제
			if (System.currentTimeMillis() - messageDisplayStartTime < messageDisplayDuration) {
				g.drawString(displayMessage, getWidth() / 2 - 20, getHeight() / 2);
			} else {
				displayMessage = "";
			}
		}
	}

	private void drawGridLines(Graphics g) {
		for (int i = 0; i <= BoardHeight; i++) {
			int y = i * squareHeight();
			g.drawLine(0, y, BoardWidth * squareWidth(), y);
		}

		for (int i = 0; i < BoardWidth; i++) {
			int x = i * squareWidth();
			g.drawLine(x, 0, x, (BoardHeight + 1) * squareHeight());
		}
	}


	public void paint(Graphics g) {
		super.paint(g);
		Dimension size = getSize();
		int boardTop = (int) size.getHeight() - BoardHeight * squareHeight();

		for (int i = 0; i < BoardHeight; ++i) {
			for (int j = 0; j < BoardWidth; ++j) {
				if (j >= 0 && j < BoardWidth && (BoardHeight - i - 1) >= 0 && (BoardHeight - i - 1) < BoardHeight) {
					Tetrominoes shape = shapeAt(j, BoardHeight - i - 1);
					if (shape != Tetrominoes.NoShape) {
						drawSquare(g, 0 + j * squareWidth(), boardTop + i * squareHeight(), shape);
					}
				}
			}
		}


		if (isStarted && curPiece.getShape() != Tetrominoes.NoShape) {
			// 현재 Tetris 조각을 그림
			for (int i = 0; i < 4; ++i) {
				int x = curX + curPiece.x(i);
				int y = curY - curPiece.y(i);
				drawSquare(g, 0 + x * squareWidth(), boardTop + (BoardHeight - y - 1) * squareHeight(), curPiece.getShape());
			}

			// 현재 Tetris 조각의 위치를 저장
			Shape savedPiece = curPiece;
			int savedX = curX;
			int savedY = curY;

			// 착지 위치를 계산
			int landingY = curY;
			while (tryMove(curPiece, curX, landingY - 1)) {
				landingY--;
			}

			// 착지 위치를 미리보기로 그림
			for (int i = 0; i < 4; ++i) {
				int x = curX + curPiece.x(i);
				int y = landingY - curPiece.y(i);
				if (y >= 0 && shapeAt(x, y) == Tetrominoes.NoShape) {
					Color currentBlockColor = colors[curPiece.getShape().ordinal()];
					int transparency = 110;  // 투명도를 0에서 255까지의 값으로 조절
					Color transparentColor = new Color(
							currentBlockColor.getRed(),
							currentBlockColor.getGreen(),
							currentBlockColor.getBlue(),
							transparency
					);
					g.setColor(transparentColor);
					g.fillRect(0 + x * squareWidth(), boardTop + (BoardHeight - y - 1) * squareHeight(), squareWidth(), squareHeight());
				}
			}



			// 현재 Tetris 조각의 위치를 복원
			curPiece = savedPiece;
			curX = savedX;
			curY = savedY;


			// 게임 화면을 그림
			for (int i = 0; i < BoardHeight; ++i) {
				for (int j = 0; j < BoardWidth; ++j) {
					Tetrominoes shape = shapeAt(j, BoardHeight - i - 1);
					if (shape != Tetrominoes.NoShape)
						drawSquare(g, 0 + j * squareWidth(), boardTop + i * squareHeight(), shape);
				}
			}


			// 경과 시간 업데이트 및 표시
			if (isStarted && !isGameOver) {
				long currentTime = System.currentTimeMillis();
				long elapsedTime = (currentTime - startTime) / 1000; // 경과 시간(초) 계산
				timeLabel.setText("Time: " + elapsedTime + "s");
			}
		}
	}



	private void dropDown() {
		int newY = curY;
		while (newY > 0) {
			if (!tryMove(curPiece, curX, newY - 1))
				break;
			--newY;
		}
		pieceDropped();
	}

	private void oneLineDown() {
		if (!tryMove(curPiece, curX, curY - 1))
			pieceDropped();
	}

	private void clearBoard() {
		for (int i = 0; i < BoardHeight; i++) {
			for (int j = 0; j < BoardWidth; j++) {
				board[i * BoardWidth + j] = Tetrominoes.NoShape;
			}
		}
	}

	private void pieceDropped() {
		for (int i = 0; i < 4; ++i) {
			int x = curX + curPiece.x(i);
			int y = curY - curPiece.y(i);
			board[(y * BoardWidth) + x] = curPiece.getShape();
		}

		removeFullLines();

		if (!isFallingFinished)
			newPiece();
	}



	private boolean tryMove(Shape newPiece, int newX, int newY) {
		for (int i = 0; i < 4; ++i) {
			int x = newX + newPiece.x(i);
			int y = newY - newPiece.y(i);
			if (x < 0 || x >= BoardWidth || y < 0 || y >= BoardHeight)
				return false;
			if (shapeAt(x, y) != Tetrominoes.NoShape)
				return false;
		}

		curPiece = newPiece;
		curX = newX;
		curY = newY;
		repaint();
		return true;
	}

	private void removeFullLines() {
		int numFullLines = 0;

		for (int i = 0; i < BoardHeight; ++i) {
			boolean lineIsFull = true;

			for (int j = 0; j < BoardWidth; ++j) {
				if (j >= 0 && j < BoardWidth && i >= 0 && i < BoardHeight) {
					if (shapeAt(j, i) == Tetrominoes.NoShape) {
						lineIsFull = false;
						break;
					}
				}
			}

			if (lineIsFull) {
				++numFullLines;
				// 라인을 지우기 전에 아래에서부터 순서대로 검사하므로
				// 현재 줄을 지우고 나서 그 위의 줄을 아래로 내려줍니다.
				for (int k = i; k < BoardHeight - 1; ++k) {
					for (int j = 0; j < BoardWidth; ++j)
						board[(k * BoardWidth) + j] = shapeAt(j, k + 1);
				}
			}
		}

		if (numFullLines > 0) {
			int points = numFullLines * 100;
			numLinesRemoved += numFullLines;
			accumulatedScore += points;

			//효과를 표시할 문자열을 설정하고 표시 시작 시간 설정
			displayMessage = String.format("+%d", points);
			messageDisplayStartTime = System.currentTimeMillis();

			statusbar.setText(String.valueOf(numLinesRemoved));
			sidePanel.updateScore(accumulatedScore);
			isFallingFinished = true;
			curPiece.setShape(Tetrominoes.NoShape);
			repaint();


			// 누적 점수가 300 이상일 때 맨 아래에서부터 한 칸 비어있는 회색 줄을 만듭니다.
			if (accumulatedScore >= 300) {
				// 맨 아래 줄에서 한 칸을 랜덤으로 선택하여 비우고 나머지를 회색 블록으로 채웁니다.
				int emptyColumn = (int) (Math.random() * BoardWidth);
				for (int j = 0; j < BoardWidth; ++j) {
					if (j != emptyColumn) {
						board[j] = Tetrominoes.GrayShape;
					}
				}

				// 누적 점수가 300 이상이면 타이머 속도를 증가시킴
				if (accumulatedScore >= 300) {
					accumulatedScore -= 300;
					int currentDelay = timer.getDelay();
					if (currentDelay > 100) {
						timer.setDelay(currentDelay - 100);
					}
				}
			}
		}
	}

	//하단 2줄 삭제 아이템
	private void removeBottomLines(int numLines) {
		for (int i = 0; i < numLines; i++) {
			for (int j = 0; j < BoardWidth; j++) {
				board[i * BoardWidth + j] = Tetrominoes.NoShape;
			}
		}

		if (removeBottomLinesUsage > 0) {
			statusbar.setText(String.valueOf(numLinesRemoved));
			isFallingFinished = true;
			curPiece.setShape(Tetrominoes.NoShape);
			repaint();
		}

		for (int i = numLines; i < BoardHeight; i++) {
			for (int j = 0; j < BoardWidth; j++) {
				board[(i - numLines) * BoardWidth + j] = board[i * BoardWidth + j];
			}
		}

		for (int i = BoardHeight - numLines; i < BoardHeight; i++) {
			for (int j = 0; j < BoardWidth; j++) {
				board[i * BoardWidth + j] = Tetrominoes.NoShape;
			}
		}
		repaint();
	}

	//전체 블록 삭제 아이템
	private void removeAllLines() {
		for (int i = 0; i < BoardHeight; i++) {
			for (int j = 0; j < BoardWidth; j++) {
				board[i * BoardWidth + j] = Tetrominoes.NoShape;
			}
		}

		if (removeAllLinesUsage > 0) {
			statusbar.setText(String.valueOf(numLinesRemoved));
			isFallingFinished = true;
			curPiece.setShape(Tetrominoes.NoShape);
			repaint();
		}
	}

	private void newPiece() {
		curPiece.setRandomShape();
		curX = BoardWidth / 2 + 1;
		curY = BoardHeight - 1 + curPiece.minY();

		// 게임 오버 조건 확인
		if (!tryMove(curPiece, curX, curY)) {
			curPiece.setShape(Tetrominoes.NoShape);
			timer.stop();
			isGameOver = true;
			isStarted = false;
			statusbar.setText("game over");
			repaint();  // 게임 오버 시 보드를 다시 그려서 회색으로 변경
		}


		// 현재 블록을 SidePanel에 설정하고, 다음 블록을 생성하여 SidePanel에 설정
		sidePanel.setCurShape(curPiece.getShape());
		curPiece = nextPiece;
		nextPiece = new Shape();
		nextPiece.setRandomShape();
		sidePanel.setNextShape(nextPiece.getShape());}



	private void drawSquare(Graphics g, int x, int y, Tetrominoes shape) {
		Color colors[] = {new Color(0, 0, 0), new Color(204, 102, 102), new Color(102, 204, 102),
				new Color(102, 102, 204), new Color(204, 204, 102), new Color(204, 102, 204), new Color(102, 204, 204),
				new Color(218, 170, 0),new Color(50, 50, 50), Color.GRAY };

		if (isGameOver) {
			g.setColor(Color.GRAY);  // 게임 오버 시 블록을 회색으로 변경
			g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);
		} else {
			Color color = colors[shape.ordinal()];
			g.setColor(color);
			g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

			g.setColor(color.brighter());
			g.drawLine(x, y + squareHeight() - 1, x, y);
			g.drawLine(x, y, x + squareWidth() - 1, y);

			g.setColor(color.darker());
			g.drawLine(x + 1, y + squareHeight() - 1, x + squareWidth() - 1, y + squareHeight() - 1);
			g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1, x + squareWidth() - 1, y + 1);
		}
	}







	private void restartGame () {
		isGameOver = false;
		clearBoard();
		numLinesRemoved = 0;
		newPiece();
		isStarted = true;
		isFallingFinished = false;
		startTime = System.currentTimeMillis();
		timer.start();
		statusbar.setText("0");
		removeBottomLines = false;
		removeBottomLinesUsage = 1;
		removeAllLines = false;
		removeAllLinesUsage = 1;
		isScoreIncreasedUsage = 1;
		repaint();
	}


	class TAdapter extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			int keycode = e.getKeyCode();

			// 이전에 저장한 블록을 꺼내오기
			if (keycode == 'c' || keycode == 'C') {
				saveShape();
			}
			// 저장된 블록을 현재 블록으로 교체
			else if (keycode == 'e' || keycode == 'E') {
				retrieveSavedShape();
			}

			if (keycode == 'w' || keycode == 'W') {
				// 아이템 사용 횟수가 0보다 크면 실행
				if (removeAllLinesUsage > 0) {
					removeAllLines(); // 전체 블록 삭제 아이템
					// 아이템 사용 후 사용 횟수 감소
					removeAllLinesUsage--;
				}
			}

			if (keycode == 'q' || keycode == 'Q') {
				if (removeBottomLinesUsage > 0) {
					removeBottomLines(2); // 하단 n줄 삭제
					removeBottomLinesUsage--;
				}
			}

			if (keycode == 's' || keycode == 'S') {
				if (isScoreIncreasedUsage > 0) {
					// 's' 키가 눌리면 스코어 10점 추가
					numLinesRemoved += 10;
					// 스코어를 업데이트하여 화면에 표시
					statusbar.setText(String.valueOf(numLinesRemoved));
					isScoreIncreasedUsage--;
				}
			}


			if (keycode == 'p' || keycode == 'P') {
				pause();
				return;
			}

			if (!isStarted) {
				if (keycode == 'r' || keycode == 'R') {
					restartGame();
				}
				return;
			}

			if (isPaused)
				return;

			if (!isStarted || curPiece.getShape() == Tetrominoes.NoShape) {
				return;
			}

			switch (keycode) {


				case KeyEvent.VK_LEFT:
					tryMove(curPiece, curX - 1, curY);
					break;
				case KeyEvent.VK_RIGHT:
					tryMove(curPiece, curX + 1, curY);
					break;
				case KeyEvent.VK_DOWN:
					tryMove(curPiece.rotateRight(), curX, curY);
					break;
				case KeyEvent.VK_UP:
					tryMove(curPiece.rotateLeft(), curX, curY);
					break;
				case KeyEvent.VK_SPACE:
					dropDown();
					break;
				case 'd':
					oneLineDown();
					break;
				case 'D':
					oneLineDown();
					break;

			}
		}
	}
}

