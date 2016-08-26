package net;

import gomoku.Player;
import gui.BoardPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.jar.Attributes.Name;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import protocol.ProtocalConstant;
import protocol.TransmissionUnit;
import protocol.TransmissionUnit.ColorEnum;

public class UserClient implements ActionListener {

	private int userID = -1;// �û�ID
	private String name;// �û�����
	private String pswd;
	private int opponent_ID = -1;// ����ID
	private String opponent_name;// ��������

	JFrame jf;
	JPanel jp;
	JLabel label_name;
	JLabel label_pswd;
	JTextField userName;
	JButton jb;
	JPasswordField paswrd;
	JLabel hintStr;

	public UserClient() {
		jf = new JFrame("XXX ��½ϵͳ");
		jp = new JPanel();
		jf.setContentPane(jp);
		jf.setPreferredSize(new Dimension(350, 220));
		jp.setPreferredSize(new Dimension(350, 220));
		jp.setBackground(Color.gray);
		label_name = new JLabel();
		label_name.setPreferredSize(new Dimension(150, 30));
		label_name.setText("�������ʻ�(���ֻ�Ӣ��):");
		userName = new JTextField();
		userName.setPreferredSize(new Dimension(150, 30));
		jp.add(label_name);
		jp.add(userName);
		// label_pswd = new JLabel();
		// label_pswd.setPreferredSize(new Dimension(150, 30));
		// label_pswd.setText("����������:");
		// jp.add(label_pswd);
		// paswrd = new JPasswordField();
		// paswrd.setPreferredSize(new Dimension(150, 30));
		// jp.add(paswrd);
		jb = new JButton("OK");
		jb.setPreferredSize(new Dimension(150, 30));
		jb.setText("ȷ  ��");
		jb.addActionListener(this);
		jp.add(jb);
		hintStr = new JLabel();
		hintStr.setPreferredSize(new Dimension(210, 40));
		hintStr.setText("");
		hintStr.setForeground(Color.RED);
		jp.add(hintStr);
		jf.pack();
		jf.setVisible(true);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	// ���ȷ�ϰ���
	public void actionPerformed(ActionEvent e) {
		name = userName.getText().trim();
		// pswd = new String(paswrd.getPassword());
		// if (pswd == null) {
		// pswd = "";
		// } else {
		// pswd = pswd.trim();
		// }
		if (name != null && name.length() > 0) {
			hintStr.setText("�������ӷ����������Ժ�...");
			System.out.println("�����������ͨ��,���ڷ��Ϳͻ�������");
			TransmissionUnit transmissionUnit = new TransmissionUnit();
			transmissionUnit.id = userID;
			transmissionUnit.name = name;
			transmissionUnit.operation = ProtocalConstant.SEND_NAME;
			send(transmissionUnit.toJSONString());
		}
	}

	OutputStream os;
	Socket s;
	InputStream is;

	public void connect() {
		try {
			s = new Socket("127.0.0.1", 5555);
			// д
			os = s.getOutputStream();
			is = s.getInputStream();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// �����Լ��µ����ӵ�λ��
	public void sendGame(int x, int y, ColorEnum colorEnum) {
		TransmissionUnit transmissionUnit = new TransmissionUnit();
		transmissionUnit.id = userID;
		transmissionUnit.x = x;
		transmissionUnit.y = y;
		transmissionUnit.color = colorEnum;
		transmissionUnit.operation = ProtocalConstant.GAME;
		transmissionUnit.opponent_id = opponent_ID;
		send(transmissionUnit.toJSONString());
	}

	// ���ͱ���
	private void send(String order) {
		try {
			os.write(order.getBytes());
			os.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void start() {
		// ���������߳�
		new Thread(new Runnable() {
			public void run() {
				try {
					while (true) {
						int len = is.available();
						byte[] bytes = new byte[len];
						// System.out.println("len:" + len);
						if (is.read(bytes) > 0) {
							String result = new String(bytes);
							parseOrder(result);// ����Э��

							// TODO ����ͨ�����ؽ������
							// if (result.equals("ACK")) {
							// hintStr.setText("��֤�ɹ�����ӭ����!");
							// } else {
							// // paswrd.setText(null);
							// hintStr.setText("�û����������������������");
							// }
						}
					}

				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					// try {
					// os.close();
					// is.close();
					// s.close();
					// } catch (IOException e) {
					// e.printStackTrace();
					// }
				}
			}
		}).start();
	}

	private void parseOrder(String order) {
		System.out.println("���ձ���:" + order);
		TransmissionUnit transmissionUnit = TransmissionUnit.parseJSON(order);
		switch (transmissionUnit.operation) {
		case ProtocalConstant.SEND_NAME:
			break;
		case ProtocalConstant.SEND_ID:
			userID = transmissionUnit.id;
			System.out.println("�ͻ��˳ɹ�������ID:" + userID);
			break;
		case ProtocalConstant.GAME_START:
			gameStart();
			transmissionUnit.operation = ProtocalConstant.GAME_ALREADY;
			transmissionUnit.opponent_id = opponent_ID;
			send(transmissionUnit.toJSONString());
			break;
		case ProtocalConstant.GAME:
			System.out.println("�������� x:" + transmissionUnit.x + " y:" + transmissionUnit.y);
			BoardPanel.panel.parseGame(transmissionUnit.x, transmissionUnit.y);
			BoardPanel.can_play = true;
			break;
		case ProtocalConstant.OPPONENT:
			opponent_ID = transmissionUnit.opponent_id;
			opponent_name = transmissionUnit.opponent_name;
			System.out.println("�յ���ԵĶ���ID:" + opponent_ID + " �û���:" + opponent_name);
			transmissionUnit.operation = ProtocalConstant.ALREADY;// �ͻ��˷��͸�������׼�����
			send(transmissionUnit.toJSONString());
			break;
		case ProtocalConstant.YOU_FIRST:
			System.out.println("�����������������");
			if (!BoardPanel.is_Start) {
				BoardPanel.is_Start = true;
				if(transmissionUnit.id==userID)
				{
					BoardPanel.can_play = true;
					BoardPanel.player=Player.BLACK;//���ַ�Ϊ�����壬�˴����ȥ
				}else{
					BoardPanel.player=Player.WHITE;//���ַ�Ϊ������
				}
			}
			break;
		case ProtocalConstant.I_WIN:
		case ProtocalConstant.Y_LOST:
		case ProtocalConstant.Y_WIN:
		case ProtocalConstant.I_DOUBT:
		case ProtocalConstant.IS_ONLINE:
		case ProtocalConstant.IS_LIVE:
		case ProtocalConstant.BLACK:
		case ProtocalConstant.WHITE:
		default:
			break;
		}
	}

	// ��Ϸ��ʼ
	private void gameStart() {
		jf.setVisible(false);
		// ��Ϸ��ʼ
		JFrame frame = new JFrame("��������Ϸ");
		frame.add(BoardPanel.panel);
		frame.setSize(BoardPanel.DEFAULT_SIZE_X + BoardPanel.DEFAULT_OFFSET_SQUARE, BoardPanel.DEFAULT_SIZE_Y + BoardPanel.TITLE_BAR_THICKNESS + BoardPanel.DEFAULT_OFFSET_SQUARE);
		frame.setResizable(false);
		frame.setVisible(true);

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		BoardPanel.panel.init(this);// ����ָ��
	}

	//��ȡ�������
	public String getUserName()
	{
		return name;
	}
	
	//��ȡ�����������
	public String getOpponentName()
	{
		return opponent_name;
	}
	
	public static void main(String[] args) {
		UserClient userClient = new UserClient();
		userClient.connect();
		userClient.start();
	}

}
