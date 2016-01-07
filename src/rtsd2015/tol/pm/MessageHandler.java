package rtsd2015.tol.pm;

import java.util.EnumMap;
import java.util.function.Consumer;

import rtsd2015.tol.pm.enums.MessageType;

		
public class MessageHandler {
	private EnumMap<MessageType, Consumer<Message>> handlers;
	
	public Consumer<Message> unexpectedMessage;

	MessageHandler() {
		handlers = new EnumMap<MessageType, Consumer<Message>>(MessageType.class);
		unexpectedMessage = (Message m) -> {};
	}
	
	public void addHandler(MessageType type, Consumer<Message> handler) {
		handlers.put(type, handler);
	}
	
	public void removeHandler(MessageType type) {
		handlers.remove(type);
	}
	
	public void handle(Message message) {
		if (handlers.containsKey(message.type)) {
			handlers.get(message.type).accept(message);
		}
		else {
			unexpectedMessage.accept(message);
		}
	}

}
