package rtsd2015.tol.pm.enums;

public enum MessageType {
	// General Messages
	MISC,
	PING,			// Echo request
	PONG,			// Echo response
	PAUSE,
	RESUME,
	
	// Player Messages
	JOIN,			// Request join
	QUIT,			// Disconnect
	START_GAME,		// Request game start
	READY,			// Client ready to start
	SELECT_MAP,
	RESYNC,			// Request re-sync
	COMMIT,			// Commit move for tick
	
	// Server Messages
	ACCEPT,			// Accept player join
	DECLINE,		// Decline player join
	NEW_PLAYER,		// Announce player join
	PART_PLAYER, 	// Announce player leave
	SPAWN,			// Spawn entity
	EVENT,			// Announce event
	PREPARE,		// Prepare for game start
	CHANGE_MAP,
	GAME_UPDATE,	// Tick update
	GAME_STATE,		// Sending whole game state
	GAME_END;		// Announce game end
}	
