package protocol;

public class ProtocalConstant {
	//����ָ��
	public static final int SEND_ID=0x01;//����������ID���ͻ���
	public static final int SEND_NAME=0x02;//�ͻ��˷����û����ָ�������
	public static final int GAME_START=0x03;//��Ϸ��ʼ��־
	public static final int I_WIN=0x04;//�ͻ��˷��͸�������Ӯ�ı�־
	public static final int Y_LOST=0x05;//���������͸��ͻ�����ı�־
	public static final int Y_WIN=0x06;//���������͸��ͻ���Ӯ�ı�־
	public static final int I_DOUBT=0x07;//�ͻ��˷��͸����������ɱ�־
	public static final int IS_ONLINE=0x08;//���������ͻ������߱�־
	public static final int IS_LIVE=0x09;//�ͻ��˷��͸�����������ȷ�ϱ�־
	public static final int GAME=0x0A;//��Ϸָ���־
	public static final int BLACK=0x0B;//���ӽ�ɫָ��
	public static final int WHITE=0x0C;//���ӽ�ɫָ��
	public static final int OPPONENT=0x0D;//ָ������
	public static final int ALREADY=0x0E;//�ͻ��˷��͸�������׼����ϱ�־
	public static final int YOU_FIRST=0x0F;//���������͸��ͻ������ֵı�־
	public static final int GAME_ALREADY=0x10;//�ͻ��˷��͸���������Ϸ׼����ʼ��־
	//����ָ��
	public static final String OPERATION="operation";//Э������
	public static final String ID="ID";//ID�ֶ�
	public static final String NAME="name";//�û��ֶ�
	public static final String X_POS="x_pos";//����x����
	public static final String Y_POS="y_pos";//����y����
	public static final String COLOR="color";//������ɫ
	public static final String OPPONENT_ID="opponent_id";//����ID
	public static final String OPPONENT_NAME="opponent_name";//��������
	
	public static final String ACK="acK";//ȷ�Ͻ���ָ��
	
}
