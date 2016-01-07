package rtsd2015.tol.pm;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Base64;

public class GameUpdate implements Serializable {
	public int tick;
	public List<EntityUpdate> updates;
	
	GameUpdate(GameUpdate other) {
		tick = other.tick;
		updates = other.updates;
	}
	
	GameUpdate(int t) {
		this(t, new ArrayList<EntityUpdate> ());
	}
	
	GameUpdate(int t, List<EntityUpdate> u) {
		tick = t;
		updates = u;
	}
	
	public static GameUpdate deserialize(String s) throws IOException, ClassNotFoundException {
		byte[] data = Base64.getDecoder().decode(s);
		ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(data));
		GameUpdate object = (GameUpdate) objectInputStream.readObject();
		objectInputStream.close();
		return object;
	}
	
	public String serialize() throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
		objectOutputStream.writeObject(this);
		objectOutputStream.close();
		
		return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
	}
}
