package rtsd2015.tol.pm;
import java.io.*;
import java.net.InetAddress;
import java.net.DatagramPacket;

import rtsd2015.tol.pm.enums.MessageType;

public class Message implements Serializable {
	MessageType type;
	String body;
	InetAddress address;
	int port;
	
	public Message(){
		this(MessageType.MISC, "");
	}

	public Message(MessageType t) {
		this(t, "");
	}
	
	public Message(MessageType t, String b) {
		type = t;
		body = b;
	}
	
	public Message(Message message) {
		this(message.type, message.body);
	}

	public Message(byte[] data) throws IOException, ClassNotFoundException {
		this(parseMessage(data));
	}
	
	public Message(DatagramPacket packet) throws IOException, ClassNotFoundException  {
		this(packet.getData());
		address = packet.getAddress();
		port = packet.getPort();
	}
	
	static public Message parseMessage(byte[] data) throws IOException, ClassNotFoundException {
		ByteArrayInputStream inStream = new ByteArrayInputStream(data);
		ObjectInputStream objInStream = new ObjectInputStream(inStream);
		
		Message message = (Message) objInStream.readObject();
		
		return message;
	}
	
	public byte[] getData() {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		try {
			ObjectOutputStream objOutStream = new ObjectOutputStream(outStream);
			objOutStream.writeObject(this);
		}
		catch (IOException e) {
			e.printStackTrace(System.err);
		}

		byte[] data = outStream.toByteArray();
		return data;
	}
	
	public String toString() {
		return "type: " + type.name() + " body: " + body;
	}
}
