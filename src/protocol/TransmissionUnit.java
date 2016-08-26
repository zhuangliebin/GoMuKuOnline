package protocol;

import com.alibaba.fastjson.JSONObject;

public class TransmissionUnit {

	public enum ColorEnum {
		black, white;
	}

	// 服务器与客户端的传输格式
	public int id;// 用户ID
	public String name;// 用户名字
	public int operation;// 协议类型
	public int x;// 旗子x坐标
	public int y;// 旗子y坐标
	public ColorEnum color;// 旗子颜色
	public int opponent_id;// 对手ID
	public String opponent_name;// 对手名字

	public String toJSONString() {
		JSONObject jsonObject = new JSONObject();
		switch (operation) {
		case ProtocalConstant.SEND_NAME:
			jsonObject.put(ProtocalConstant.OPERATION,operation);
			jsonObject.put(ProtocalConstant.ID,id);
			jsonObject.put(ProtocalConstant.NAME,name);// 效率考虑,不加break,顺序不可变
			break;
		case ProtocalConstant.SEND_ID:
		case ProtocalConstant.GAME_START:
		case ProtocalConstant.I_WIN:
		case ProtocalConstant.Y_LOST:
		case ProtocalConstant.Y_WIN:
		case ProtocalConstant.I_DOUBT:
		case ProtocalConstant.IS_ONLINE:
		case ProtocalConstant.IS_LIVE:
		case ProtocalConstant.BLACK:
		case ProtocalConstant.WHITE:
		case ProtocalConstant.ALREADY:
		case ProtocalConstant.YOU_FIRST:
			jsonObject.put(ProtocalConstant.OPERATION,operation);
			jsonObject.put(ProtocalConstant.ID,id);
			break;
		case ProtocalConstant.GAME_ALREADY:
			jsonObject.put(ProtocalConstant.OPERATION,operation);
			jsonObject.put(ProtocalConstant.ID,id);
			jsonObject.put(ProtocalConstant.OPPONENT_ID,opponent_id);
			break;
		case ProtocalConstant.GAME:
			jsonObject.put(ProtocalConstant.OPERATION,operation);
			jsonObject.put(ProtocalConstant.ID,id);
			jsonObject.put(ProtocalConstant.X_POS,x);
			jsonObject.put(ProtocalConstant.Y_POS,y);
			jsonObject.put(ProtocalConstant.COLOR,color.ordinal());
			jsonObject.put(ProtocalConstant.OPPONENT_ID,opponent_id);
			break;
		case ProtocalConstant.OPPONENT:
			jsonObject.put(ProtocalConstant.OPERATION,operation);
			jsonObject.put(ProtocalConstant.ID,id);
			jsonObject.put(ProtocalConstant.OPPONENT_ID,opponent_id);
			jsonObject.put(ProtocalConstant.OPPONENT_NAME,opponent_name);
		default:
			break;
		}
		return jsonObject.toJSONString();
	}

	public static TransmissionUnit parseJSON(String jsonString) {
		// 以employee为例解析，map类似
		JSONObject jsonObject = JSONObject.parseObject(jsonString);
		TransmissionUnit transmissionUnit = new TransmissionUnit();
		/*
		 * for (String string:jsonObject.keySet()) { System.out.println(string);
		 * } System.out.println(jsonObject.getIntValue(ProtocalConstant.X_POS));
		 */
		transmissionUnit.id = jsonObject.getIntValue(ProtocalConstant.ID);
		transmissionUnit.operation = jsonObject.getIntValue(ProtocalConstant.OPERATION);

		switch (transmissionUnit.operation) {
		case ProtocalConstant.SEND_NAME:
			transmissionUnit.name = jsonObject.getString(ProtocalConstant.NAME);
			break;
		case ProtocalConstant.GAME:
			transmissionUnit.x = jsonObject.getIntValue(ProtocalConstant.X_POS);
			transmissionUnit.y = jsonObject.getIntValue(ProtocalConstant.Y_POS);
			transmissionUnit.color = ColorEnum.values()[jsonObject.getIntValue(ProtocalConstant.COLOR)];
			transmissionUnit.opponent_id = jsonObject.getIntValue(ProtocalConstant.OPPONENT_ID);
			break;
		case ProtocalConstant.OPPONENT:
			transmissionUnit.opponent_id = jsonObject.getIntValue(ProtocalConstant.OPPONENT_ID);
			transmissionUnit.opponent_name = jsonObject.getString(ProtocalConstant.OPPONENT_NAME);
			break;
		case ProtocalConstant.GAME_ALREADY:
			transmissionUnit.opponent_id = jsonObject.getIntValue(ProtocalConstant.OPPONENT_ID);
			break;
		default:
			break;
		}
		return transmissionUnit;
	}

	public static void main(String[] args) {
		TransmissionUnit transmissionUnit = new TransmissionUnit();
		transmissionUnit.id = 100;
		transmissionUnit.x = 23;
		transmissionUnit.y = 43;
		transmissionUnit.color = ColorEnum.black;
		transmissionUnit.operation = ProtocalConstant.GAME;
		// System.out.println(transmissionUnit.toJSONString());
		// transmissionUnit.parseJSON(transmissionUnit.toJSONString());
	}

}
