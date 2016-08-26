package protocol;

import java.nio.channels.SocketChannel;

//服务存储数据格式
public class MyData
{
	public int id=-1;//自己的ID
	public String name="";//用户名字
	public int opponent=-1;//对手的ID
	public long waitingTime=0;//用户开始连接的时间
	public SocketChannel sockect;//用户的连接
}