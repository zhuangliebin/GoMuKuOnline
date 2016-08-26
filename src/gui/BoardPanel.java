package gui;

import gomoku.Board;
import gomoku.Move;
import gomoku.Piece;
import gomoku.Player;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JPanel;

import protocol.TransmissionUnit.ColorEnum;
import net.UserClient;
import ai.GomokuAI;

public class BoardPanel extends JPanel {

	
	
	public static boolean is_Start = false;// ��Ϸ��ʼ��־
	public static boolean can_play = false;// �ܷ�����ı�־
	public static boolean is_single = true;// �Ƿ�һ��������

	public static int player = Player.BLACK;// ��Ϸ��ɫ
	public static final BoardPanel panel = new BoardPanel();

	private UserClient userClient = null;// ָ��ͻ���ָ��

	public static final int DEFAULT_DIM_X = 16;// ������������
	public static final int DEFAULT_DIM_Y = 16;// ������������

	public static final int DEFAULT_SIZE_SQUARE = 32;// ���Ӵ�С�ߴ�
	// public static final int DEFAULT_OFFSET_SQUARE = DEFAULT_SIZE_SQUARE/2;
	// ����ƫ����,ʹ���������ڽ��洦
	public static final int DEFAULT_OFFSET_SQUARE = 0;
	public static final int DEFAULT_SIZE_X = DEFAULT_SIZE_SQUARE * DEFAULT_DIM_X;
	public static final int DEFAULT_SIZE_Y = DEFAULT_SIZE_SQUARE * DEFAULT_DIM_Y;

	public static final int TITLE_BAR_THICKNESS = 30;

	PiecePainter whitePiecePainter;// ���ӻ�ͼ
	PiecePainter blackPiecePainter;// ���ӻ�ͼ
	PiecePainter backgroudPiecePainter;// ������ͼ

	Board board;

	private int drawX;
	private int drawY;
	private int victory = -1;
	private Move lastMove = null;

	
	
	
	public BoardPanel() {
		board = new Board(DEFAULT_DIM_X, DEFAULT_DIM_Y, Player.BLACK);
		whitePiecePainter = new PiecePainter("img/white.gif");
		blackPiecePainter = new PiecePainter("img/black.gif");
		backgroudPiecePainter = new PiecePainter("img/background.gif");
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == 1) {
					if (is_Start) {
						if (can_play) {
							int x = (e.getX() - DEFAULT_OFFSET_SQUARE) / DEFAULT_SIZE_SQUARE;
							int y = (e.getY() - DEFAULT_OFFSET_SQUARE) / DEFAULT_SIZE_SQUARE;
							
							parseGame(x, y);
							userClient.sendGame(x, y, ColorEnum.values()[0]);// �����Լ��µ����������
							can_play = false;
						} else {
							// �Ƿ�����,�Է���û����
							
						}
					} else {
						// ��Ϸ��û��ʼ
					}

				}
			}
		});
		this.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int x = (e.getX() - DEFAULT_OFFSET_SQUARE) / DEFAULT_SIZE_SQUARE;
				int y = (e.getY() - DEFAULT_OFFSET_SQUARE) / DEFAULT_SIZE_SQUARE;
				drawX = x;
				drawY = y;
				repaint();
			}
		});
	}

	// ���ÿͻ���ͨ��ָ��
	public void init(UserClient userClient) {
		this.userClient = userClient;
	}

	// ������Ϸ
	public void parseGame(int x, int y) {
		if (victory == -1 && board.doMove(x, y)) {
			if (GomokuAI.evaluate(board)[Player.WHITE] > 5000) {
				// White wins
				victory = Player.WHITE;
			} else {
				// Pair<Move,Integer>aiResults=GomokuAI.minimax(board,2);
				// board.doMove(aiResults.getFirst());
				// lastMove = aiResults.getFirst();
				if (GomokuAI.evaluate(board)[Player.BLACK] > 5000) {
					// Black wins
					victory = Player.BLACK;
				}
			}
			repaint();
			System.out.println("Black eval: " + GomokuAI.evaluate(board, Player.BLACK));
			System.out.println("White eval: " + GomokuAI.evaluate(board, Player.WHITE));
		}
	}

	@Override
	public void paintComponent(Graphics g) {

		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		super.paintComponent(g);

		g.drawString("(" + drawX + "," + drawY + ")", 5, 25);

		// ���Ʊ���
		for (int i = 0; i < DEFAULT_DIM_X; i++) {
			for (int j = 0; j < DEFAULT_DIM_Y; j++) {
				backgroudPiecePainter.paint(g, DEFAULT_OFFSET_SQUARE + i * DEFAULT_SIZE_SQUARE, DEFAULT_OFFSET_SQUARE + j * DEFAULT_SIZE_SQUARE);
			}
		}
		// �������Ա���
		// Draw vertical lines
		for (int i = 0; i < DEFAULT_DIM_X * DEFAULT_SIZE_SQUARE + DEFAULT_OFFSET_SQUARE; i += DEFAULT_SIZE_SQUARE) {
			g.drawLine(i, 0, i, DEFAULT_SIZE_Y);

		}
		// Draw horizontal lines
		for (int i = 0; i < DEFAULT_DIM_Y * DEFAULT_SIZE_SQUARE + DEFAULT_OFFSET_SQUARE; i += DEFAULT_SIZE_SQUARE) {
			g.drawLine(0, i, DEFAULT_SIZE_X, i);
		}

		// ���ư�ɫ����
		for (Piece piece : board.getPieces(Player.WHITE)) {
			whitePiecePainter.paint(g, DEFAULT_OFFSET_SQUARE + piece.getSquare().getX() * DEFAULT_SIZE_SQUARE, DEFAULT_OFFSET_SQUARE + piece.getSquare().getY() * DEFAULT_SIZE_SQUARE);
		}
		// ���ƺ�ɫ����
		for (Piece piece : board.getPieces(Player.BLACK)) {
			blackPiecePainter.paint(g, DEFAULT_OFFSET_SQUARE + piece.getSquare().getX() * DEFAULT_SIZE_SQUARE, DEFAULT_OFFSET_SQUARE + piece.getSquare().getY() * DEFAULT_SIZE_SQUARE);
		}

//		if (null != lastMove) {
//			player = lastMove.getPlayer();
//		}

		Color defaultColor = g.getColor();
		Stroke defaultStroke = g2d.getStroke();
		g2d.setStroke(new BasicStroke((float) 2.5));
		g.setColor(Color.RED);
		// g.drawOval(DEFAULT_OFFSET_SQUARE + lastMove.getX() *
		// DEFAULT_SIZE_SQUARE, DEFAULT_OFFSET_SQUARE + lastMove.getY() *
		// DEFAULT_SIZE_SQUARE, 30, 30);//���ƺ�Ȧ
		g.setColor(defaultColor);
		g2d.setStroke(defaultStroke);
		Font defaultFont = g.getFont();
		StringBuffer strBuffer;
		switch (victory) {

		case Player.BLACK:
			g.setFont(new Font(defaultFont.getFontName(), 1, 30));
			strBuffer=new StringBuffer();
			strBuffer.append("You (");
			strBuffer.append(userClient.getUserName());
			if(player==Player.BLACK)
			{
				strBuffer.append(") win!\nPlayer (");
				strBuffer.append(userClient.getOpponentName());
				strBuffer.append(") lost!");
			}else {
				strBuffer.append(") lost!\nPlayer (");
				strBuffer.append(userClient.getOpponentName());
				strBuffer.append(") win!");
			}
			g.drawString(strBuffer.toString(), 50, 50);
			
			g.setFont(defaultFont);
			
			break;

		case Player.WHITE:
			g.setFont(new Font(defaultFont.getFontName(), 1, 30));
			
			strBuffer=new StringBuffer();
			strBuffer.append("You (");
			strBuffer.append(userClient.getUserName());
			if(player==Player.WHITE)
			{
				strBuffer.append(") win!\nPlayer (");
				strBuffer.append(userClient.getOpponentName());
				strBuffer.append(") lost!");
			}else {
				strBuffer.append(") lost!\nPlayer (");
				strBuffer.append(userClient.getOpponentName());
				strBuffer.append(") win!");
			}
			
			g.drawString(strBuffer.toString(), 50, 50);
			g.setFont(defaultFont);
			
			break;

		default:
			break;
		}

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

	}

	public static void main(String[] args) {

		JFrame frame = new JFrame("Get 5 pieces in a row!!!");
		frame.add(panel);
		frame.setSize(DEFAULT_SIZE_X + DEFAULT_OFFSET_SQUARE, DEFAULT_SIZE_Y + TITLE_BAR_THICKNESS + DEFAULT_OFFSET_SQUARE);
		frame.setResizable(false);
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

}
