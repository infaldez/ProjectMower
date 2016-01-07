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

	static public GameUpdate fromEntities(int t, List<Entity> u) {
		GameUpdate gameUpdate = new GameUpdate(t);
		for (Entity e : u) {
			gameUpdate.updates.add(new EntityUpdate(e));
		}
		return gameUpdate;
	}
	
	public static GameUpdate deserialize(String s) throws IOException, ClassNotFoundException {
		byte[] data = Base64.getDecoder().decode(s);
		ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(data));
		GameUpdate object = (GameUpdate) objectInputStream.readObject();
		objectInputStream.close();
		return object;
	}
	
	public String serialize() {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(this);
			objectOutputStream.close();
		}
		catch (IOException e) {
			e.printStackTrace(System.err);
		}
		
		return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
	}
}
