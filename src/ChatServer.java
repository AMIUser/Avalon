package com.williamleara.utexas.ee382v12.prj3;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.Naming;
import java.util.HashSet;

public class ChatServer extends UnicastRemoteObject implements ChatServerInterface {

	// auto-generated UID for serialization  
	private static final long serialVersionUID = 5406338694792757570L;
	
	// instance variable to represent all the clients in the room
	HashSet<String> roomParticipants = new HashSet<String>();
	
	// instance variable to hold the name of the chat room
	String nameOfRoom = new String();
	
	// constructor
	protected ChatServer(String serverName) throws RemoteException {
		super();
		
		// bind the chat server
		try {
			Naming.rebind(serverName, this);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		// provide value for nameOfRoom
		nameOfRoom = serverName;
	}

	@Override
	public String joinRoom(String nameOfClient) throws RemoteException {
		roomParticipants.add(nameOfClient);
		return "INFO:  " + nameOfClient + " has been added to " + nameOfRoom;
	}
	
	@Override
	public String joinRoom(String nameOfClient) throws RemoteException {
		roomParticipants.add(nameOfClient);
		return "INFO:  " + nameOfClient + " has been added to " + nameOfRoom;
	}

	@Override
	public void talk(String nameOfClient, String text) throws RemoteException {
		for (String name : roomParticipants) {
			// make connection with each room participants' callback object
			try {
			String clientCallback = "rmi://localhost/" + name;
			ChatClientInterface chatClient = (ChatClientInterface) Naming.lookup(clientCallback);
			if (!name.equalsIgnoreCase(nameOfClient))
				chatClient.chatCallback(nameOfRoom, "<" + nameOfClient + "> " + text);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public String leaveRoom(String nameOfClient) throws RemoteException {
		if (!roomParticipants.contains(nameOfClient))
			return "ERROR:  " + nameOfClient + " is not a member of room: " + nameOfRoom;
		else {
			roomParticipants.remove(nameOfClient);
			return "INFO:  " + nameOfClient + " has been removed from " + nameOfRoom;
		}
	}

	public void deleteRoom() throws RemoteException {
		for (String name : roomParticipants) {
			// notify participants the room has been deleted
			try {
			String clientCallback = "rmi://localhost/" + name;
			ChatClientInterface chatClient = (ChatClientInterface) Naming.lookup(clientCallback);
			chatClient.chatCallback(nameOfRoom, "FATAL:  the provider has elected to terminate this room");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		// unbind the chat server
		try {
			Naming.unbind(nameOfRoom);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		// clear room of all participants
		roomParticipants.clear();
		
	}
	

}
