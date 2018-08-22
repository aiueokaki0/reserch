package RSLBench;

import RSLBench.Comm.Message;
import rescuecore2.worldmodel.EntityID;

public class MessageString implements Message {

    private EntityID sender;
    private String msg;
	 
	public MessageString(EntityID sender, String msg) {
		super();
		this.sender = sender;
		this.msg = msg;
	}

	public EntityID getSender() {
		return sender;
	}

	public void setSender(EntityID sender) {
		this.sender = sender;
	}
	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Override
	public int getBytes() {
        return Message.BYTES_ENTITY_ID + (int)msg.length() * Message.BYTES_UTILITY_VALUE;
    }

}
