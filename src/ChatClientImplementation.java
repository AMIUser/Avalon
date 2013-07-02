package com.williamleara.utexas.ee382v12.prj3;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ChatClientImplementation extends UnicastRemoteObject implements ChatClientInterface {

	// auto-generated UID for serialization
	private static final long serialVersionUID = 6014851478898137663L;
	
	// constructor
	public ChatClientImplementation() throws RemoteException {
	}

	@Override
	public void chatCallback(String room, String text) throws RemoteException {
		System.out.println("\n" + "{" + room + "} " + text);
		
		// account for the provider deregistering rooms
		if (text.contains("FATAL:")) {
			ChatClient.subscribedRooms.remove(room);
			if (room.equalsIgnoreCase(ChatClient.currentRoom))
				ChatClient.currentRoom = "foyer";
		}
		System.out.print("{" + ChatClient.currentRoom + "} input: ");
	}
}
