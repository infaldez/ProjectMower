package rtsd2015.tol.pm;
import java.io.*;
import rtsd2015.tol.pm.enums.MessageType;

public class Message implements Serializable {
	MessageType type;
	String body;
	
	public Message(){
		this(MessageType.MISC, "");
	}
	
	public Message(MessageType t, String b) {
		type = t;
		body = b;
	}
	
	public Message(Message message) {
		this(message.type, message.body);
	}

	public Message(byte[] data) throws Exception {
		this(parseMessage(data));
	}
	
	static public Message parseMessage(byte[] data)
			throws Exception {
		ByteArrayInputStream inStream = new ByteArrayInputStream(data);
		ObjectInputStream objInStream = new ObjectInputStream(inStream);
		
		Message message = (Message) objInStream.readObject();
		
		return message;
	}
	
	public byte[] getData() throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		ObjectOutputStream objOutStream = new ObjectOutputStream(outStream);

		objOutStream.writeObject(this);
		byte[] data = outStream.toByteArray();
		return data;
	}
	
	public String toString() {
		return "type: " + type.name() + " body: " + body;
	}
}
