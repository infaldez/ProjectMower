package rtsd2015.tol.pm;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Base64;

public class GameUpdate implements Serializable {
	public int tick;
	public List<EntityUpdate> updates;
	public List<Integer> kill;
	public List<Long> scores;
	
	GameUpdate(GameUpdate other) {
		tick = other.tick;
		updates = other.updates;
		kill = other.kill;
	}
	
	
	GameUpdate(Game game) {
		tick = game.getTick(); 
		updates = new ArrayList<EntityUpdate>();
		List<Entity> updatedEntities = game.flushUpdatedEntities(); 
		for (int i = 0; i < updatedEntities.size() ; i++) {
			updates.add(new EntityUpdate(updatedEntities.get(i)));
		}
		kill = game.flushKilled();
		scores = game.getScores();
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
