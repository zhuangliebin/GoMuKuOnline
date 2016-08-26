package net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import protocol.MyData;
import protocol.ProtocalConstant;
import protocol.TransmissionUnit;

public class NioServer {

	public static final int SERVERPORT = 5555;
	public static final String USERNAME = "wangzhirong";
	public int userNum = 0;// �û���ʼID

	// public static final List<SocketChannel> socketChannels=new
	// ArrayList<SocketChannel>();

	public static Map<Integer, MyData> users = new HashMap<Integer, MyData>();// ���û���Ϣ��
	public static LinkedList<Integer> inUsers = new LinkedList<Integer>();// ������Ϸ���û���
	public static LinkedList<Integer> waiterUsers = new LinkedList<Integer>();// δ������Ϸ���û���

	public static final String PASSWORD = "123456";
	public static final String ISACK = "ACK";
	public static final String ISNAK = "NAK!";
	// Selector selector;//ѡ����
	// SelectionKey key;//key�� һ��key����һ��Selector ��NIOͨ���ϵ�ע��,��������;
	// //ȡ�����Key��Ϳ��Զ�Selector��ͨ���Ͻ��в���
	private ByteBuffer echoBuffer = ByteBuffer.allocate(1024);// ͨ�����ݻ�����

	public NioServer() {
	}

	public static void main(String[] args) throws IOException {
		NioServer ns = new NioServer();
		ns.BuildNioServer();
	}

	public void BuildNioServer() throws IOException {
		// ///////////////////////////////////////////////////////
		// /////�ȶԷ���˵�ServerSocket����ע��,ע�ᵽSelector ////
		// ///////////////////////////////////////////////////////
		ServerSocketChannel ssc = ServerSocketChannel.open();// �½�NIOͨ��
		ssc.configureBlocking(false);// ʹͨ��Ϊ������
		ServerSocket ss = ssc.socket();// ��������NIOͨ����socket����
		// �½�socketͨ���Ķ˿�
		ss.bind(new InetSocketAddress("127.0.0.1", SERVERPORT));
		Selector selector = Selector.open();// ��ȡһ��ѡ����
		// ��NIOͨ��ѡ�󶨵�����,��Ȼ�󶨺���������Ϊskey
		SelectionKey skey = ssc.register(selector, SelectionKey.OP_ACCEPT);
		// //////////////////////////////////////////////////////////////////
		// // ���տͻ��˵�����Socket,������SocketҲ����ע�ᵽSelector ////
		// /////////////////////////////////////////////////////////////////
		while (true) {
			int num = selector.select();// ��ȡͨ�����Ƿ���ѡ�����Ĺ����¼�
			if (num < 1) {
				continue;
			}
			Set selectedKeys = selector.selectedKeys();// ��ȡͨ���ڹ����¼��ļ���
			Iterator it = selectedKeys.iterator();
			while (it.hasNext()) {// ����ÿ���¼�
				try {
					SelectionKey key = (SelectionKey) it.next();
					// ��һ�������ӽ����¼�,������¼�
					if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
						// �������������
						ServerSocketChannel serverChanel = (ServerSocketChannel) key.channel();
						// ��serverSocketChannel�д�������ͻ��˵�����socketChannel
						SocketChannel sc = serverChanel.accept();
						sc.configureBlocking(false);

						// ���ͷ�����������û�ID
						userNum++;
						TransmissionUnit tmpData = new TransmissionUnit();
						tmpData.id = userNum;
						tmpData.operation = ProtocalConstant.SEND_ID;
						// String userID = String.valueOf(userNum);
						String order = tmpData.toJSONString();
						ByteBuffer byteBuffer = ByteBuffer.allocate(order.length());
						byteBuffer.put(order.getBytes());
						byteBuffer.flip();
						sc.write(byteBuffer);
						byteBuffer.clear();

						MyData userData = new MyData();
						userData.id = userNum;
						userData.sockect = sc;
						Calendar now = Calendar.getInstance();
						userData.waitingTime = now.getTimeInMillis();

						users.put(userNum, userData);

						// Add the new connection to the selector
						// ��������ע�ᵽѡ����
						SelectionKey newKey = sc.register(selector, SelectionKey.OP_READ);
						it.remove();
					//	System.out.println("Got connection from " + sc);
					} else
					// ���ͻ������ݵ��¼�,��ʱ�пͻ��˷����ݹ���,�ͻ����¼�
					if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
						// ��ȡ����
						SocketChannel sc = (SocketChannel) key.channel();
						int bytesEchoed = 0;
					//	System.out.println(sc.getRemoteAddress().toString());
						while ((bytesEchoed = sc.read(echoBuffer)) > 0) {
							//System.out.println("bytesEchoed:" + bytesEchoed);
						}
						echoBuffer.flip();
					//	System.out.println("limet:" + echoBuffer.limit());
						byte[] content = new byte[echoBuffer.limit()];
						echoBuffer.get(content);
						String result = new String(content);
					//	System.out.println(result);
						doPost(result, sc);
						echoBuffer.clear();
						it.remove();
					}
				} catch (Exception e) {
				}
			}
		}
	}

	public void doPost(String str, SocketChannel sc) {

		TransmissionUnit transmissionUnit = TransmissionUnit.parseJSON(str);
		switch (transmissionUnit.operation) {
		case ProtocalConstant.SEND_NAME:
			MyData userData = users.get(transmissionUnit.id);
			userData.name = transmissionUnit.name;
			System.out.println("��ȡ�û�id:" + transmissionUnit.id + " ����:" + users.get(transmissionUnit.id).name);
			if (!waiterUsers.isEmpty()) {
				// ��Ϸ��ʼ���
				int opponent_id = waiterUsers.getLast();
				waiterUsers.removeLast();
				userData.opponent = opponent_id;
				MyData opponentUser = users.get(opponent_id);
				opponentUser.opponent = userData.id;
				
				//��˫�����������Ϣ
				transmissionUnit.operation=ProtocalConstant.OPPONENT;
				transmissionUnit.id=userData.id;//����ȥ��
				transmissionUnit.opponent_id=opponent_id;
				transmissionUnit.opponent_name=opponentUser.name;
				sendOrder(transmissionUnit.toJSONString(), userData.id);
				
				transmissionUnit.id=opponent_id;
				transmissionUnit.opponent_id=userData.id;
				transmissionUnit.opponent_name=userData.name;
				sendOrder(transmissionUnit.toJSONString(), opponent_id);
				
			} else {
				waiterUsers.add(transmissionUnit.id);
			}
			
			break;
		case ProtocalConstant.ALREADY:
			transmissionUnit.operation=ProtocalConstant.GAME_START;
			//transmissionUnit.id=transmissionUnit.id;//����ȥ��
			sendOrder(transmissionUnit.toJSONString(), transmissionUnit.id);
			
			break;
		case ProtocalConstant.GAME:
//			System.out.println(transmissionUnit.toJSONString());
			sendOrder(transmissionUnit.toJSONString(), transmissionUnit.opponent_id);
			System.out.println("���(id)"+transmissionUnit.id+" ���͸����id:"+transmissionUnit.opponent_id+" ��������Ϣx:"+transmissionUnit.x+" y:"+transmissionUnit.y);
			break;
		case ProtocalConstant.GAME_ALREADY:
			transmissionUnit.operation=ProtocalConstant.YOU_FIRST;
			Random random = new Random();
			int whoFirst=random.nextInt(2);//�������ָ�������û�,(0,1)
			switch (whoFirst) {
			case 0:
				sendOrder(transmissionUnit.toJSONString(), transmissionUnit.id);
				sendOrder(transmissionUnit.toJSONString(), transmissionUnit.opponent_id);
				break;
			case 1:
				int tmpID=transmissionUnit.id;
				transmissionUnit.id=transmissionUnit.opponent_id;
				sendOrder(transmissionUnit.toJSONString(), tmpID);
				sendOrder(transmissionUnit.toJSONString(), transmissionUnit.opponent_id);
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}
	}

	private void sendOrder(String order, int id) {
		MyData userData = users.get(id);
		SocketChannel socketChannel = userData.sockect;

		ByteBuffer bb = ByteBuffer.allocate(order.length());
		bb.put(order.getBytes());
		bb.flip();

		try {
			socketChannel.write(bb);
		} catch (IOException e) {
			e.printStackTrace();
		}
		bb.clear();
	}

}
