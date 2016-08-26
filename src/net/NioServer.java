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
	public int userNum = 0;// 用户初始ID

	// public static final List<SocketChannel> socketChannels=new
	// ArrayList<SocketChannel>();

	public static Map<Integer, MyData> users = new HashMap<Integer, MyData>();// 总用户信息表
	public static LinkedList<Integer> inUsers = new LinkedList<Integer>();// 进入游戏的用户表
	public static LinkedList<Integer> waiterUsers = new LinkedList<Integer>();// 未进入游戏的用户表

	public static final String PASSWORD = "123456";
	public static final String ISACK = "ACK";
	public static final String ISNAK = "NAK!";
	// Selector selector;//选择器
	// SelectionKey key;//key。 一个key代表一个Selector 在NIO通道上的注册,类似主键;
	// //取得这个Key后就可以对Selector在通道上进行操作
	private ByteBuffer echoBuffer = ByteBuffer.allocate(1024);// 通道数据缓冲区

	public NioServer() {
	}

	public static void main(String[] args) throws IOException {
		NioServer ns = new NioServer();
		ns.BuildNioServer();
	}

	public void BuildNioServer() throws IOException {
		// ///////////////////////////////////////////////////////
		// /////先对服务端的ServerSocket进行注册,注册到Selector ////
		// ///////////////////////////////////////////////////////
		ServerSocketChannel ssc = ServerSocketChannel.open();// 新建NIO通道
		ssc.configureBlocking(false);// 使通道为非阻塞
		ServerSocket ss = ssc.socket();// 创建基于NIO通道的socket连接
		// 新建socket通道的端口
		ss.bind(new InetSocketAddress("127.0.0.1", SERVERPORT));
		Selector selector = Selector.open();// 获取一个选择器
		// 将NIO通道选绑定到择器,当然绑定后分配的主键为skey
		SelectionKey skey = ssc.register(selector, SelectionKey.OP_ACCEPT);
		// //////////////////////////////////////////////////////////////////
		// // 接收客户端的连接Socket,并将此Socket也接连注册到Selector ////
		// /////////////////////////////////////////////////////////////////
		while (true) {
			int num = selector.select();// 获取通道内是否有选择器的关心事件
			if (num < 1) {
				continue;
			}
			Set selectedKeys = selector.selectedKeys();// 获取通道内关心事件的集合
			Iterator it = selectedKeys.iterator();
			while (it.hasNext()) {// 遍历每个事件
				try {
					SelectionKey key = (SelectionKey) it.next();
					// 有一个新联接接入事件,服务端事件
					if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
						// 接收这个新连接
						ServerSocketChannel serverChanel = (ServerSocketChannel) key.channel();
						// 从serverSocketChannel中创建出与客户端的连接socketChannel
						SocketChannel sc = serverChanel.accept();
						sc.configureBlocking(false);

						// 发送服务器分配的用户ID
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
						// 把新连接注册到选择器
						SelectionKey newKey = sc.register(selector, SelectionKey.OP_READ);
						it.remove();
					//	System.out.println("Got connection from " + sc);
					} else
					// 读客户端数据的事件,此时有客户端发数据过来,客户端事件
					if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
						// 读取数据
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
			System.out.println("获取用户id:" + transmissionUnit.id + " 名字:" + users.get(transmissionUnit.id).name);
			if (!waiterUsers.isEmpty()) {
				// 游戏开始配对
				int opponent_id = waiterUsers.getLast();
				waiterUsers.removeLast();
				userData.opponent = opponent_id;
				MyData opponentUser = users.get(opponent_id);
				opponentUser.opponent = userData.id;
				
				//向双方发送配对信息
				transmissionUnit.operation=ProtocalConstant.OPPONENT;
				transmissionUnit.id=userData.id;//可以去掉
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
			//transmissionUnit.id=transmissionUnit.id;//可以去掉
			sendOrder(transmissionUnit.toJSONString(), transmissionUnit.id);
			
			break;
		case ProtocalConstant.GAME:
//			System.out.println(transmissionUnit.toJSONString());
			sendOrder(transmissionUnit.toJSONString(), transmissionUnit.opponent_id);
			System.out.println("玩家(id)"+transmissionUnit.id+" 发送给玩家id:"+transmissionUnit.opponent_id+" 的棋子信息x:"+transmissionUnit.x+" y:"+transmissionUnit.y);
			break;
		case ProtocalConstant.GAME_ALREADY:
			transmissionUnit.operation=ProtocalConstant.YOU_FIRST;
			Random random = new Random();
			int whoFirst=random.nextInt(2);//随机生成指定先手用户,(0,1)
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
