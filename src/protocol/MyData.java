package protocol;

import java.nio.channels.SocketChannel;

//����洢���ݸ�ʽ
public class MyData
{
	public int id=-1;//�Լ���ID
	public String name="";//�û�����
	public int opponent=-1;//���ֵ�ID
	public long waitingTime=0;//�û���ʼ���ӵ�ʱ��
	public SocketChannel sockect;//�û�������
}