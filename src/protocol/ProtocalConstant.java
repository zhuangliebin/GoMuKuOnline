package protocol;

public class ProtocalConstant {
	//控制指令
	public static final int SEND_ID=0x01;//服务器分配ID给客户端
	public static final int SEND_NAME=0x02;//客户端发送用户名字给服务器
	public static final int GAME_START=0x03;//游戏开始标志
	public static final int I_WIN=0x04;//客户端发送给服务器赢的标志
	public static final int Y_LOST=0x05;//服务器发送给客户端输的标志
	public static final int Y_WIN=0x06;//服务器发送给客户端赢的标志
	public static final int I_DOUBT=0x07;//客户端发送给服务器质疑标志
	public static final int IS_ONLINE=0x08;//服务器检测客户端在线标志
	public static final int IS_LIVE=0x09;//客户端发送给服务器在线确认标志
	public static final int GAME=0x0A;//游戏指令标志
	public static final int BLACK=0x0B;//黑子角色指定
	public static final int WHITE=0x0C;//白子角色指定
	public static final int OPPONENT=0x0D;//指定对手
	public static final int ALREADY=0x0E;//客户端发送给服务器准备完毕标志
	public static final int YOU_FIRST=0x0F;//服务器发送给客户端先手的标志
	public static final int GAME_ALREADY=0x10;//客户端发送给服务器游戏准备开始标志
	//控制指令
	public static final String OPERATION="operation";//协议类型
	public static final String ID="ID";//ID字段
	public static final String NAME="name";//用户字段
	public static final String X_POS="x_pos";//棋子x坐标
	public static final String Y_POS="y_pos";//棋子y坐标
	public static final String COLOR="color";//棋子颜色
	public static final String OPPONENT_ID="opponent_id";//对手ID
	public static final String OPPONENT_NAME="opponent_name";//对手名字
	
	public static final String ACK="acK";//确认接收指令
	
}
