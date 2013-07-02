package com.williamleara.utexas.ee382v12.prj3;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ArrayList;

public class ChatRegistryImplementation extends UnicastRemoteObject implements ChatRegistryInterface {

	// auto-generated UID for serialization 
	private static final long serialVersionUID = -1497162318978116340L;
	
	// instance variables to represent the registered Rooms and Clients
	HashMap<String, String> Rooms = new HashMap<String, String>();
	HashSet<String> Clients = new HashSet<String>();
	
	// constructor
	public ChatRegistryImplementation() throws RemoteException {
	}

	@Override
	public String registerRoom(String nameOfRoom, String descriptionOfRoom)
			throws RemoteException {
		if (!Rooms.containsKey(nameOfRoom)) {
			Rooms.put(nameOfRoom, descriptionOfRoom);
			log("registered room: " + nameOfRoom);
			return "INFO:  " + nameOfRoom + " has been registered";
		}
		else if (nameOfRoom.equals("foyer"))
			return "INFO:  foyer has been registered";
		else
			return "ERROR:  that room has already been registered";
	}

	@Override
	public String registerClient(String nameOfClient) throws RemoteException {
		if (!Clients.contains(nameOfClient)) {
			Clients.add(nameOfClient);
			log("registered client: "+ nameOfClient);
			return "INFO:  " + nameOfClient + " has been registered";
		}
		else
			return "ERROR:  that client has already been registered";
	}

	@Override
	public String deregisterRoom(String nameOfRoom) throws RemoteException {
		if (nameOfRoom.equalsIgnoreCase("foyer"))
			return "ERROR:  the foyer cannot be removed";
		if (Rooms.containsKey(nameOfRoom)) {
			Rooms.remove(nameOfRoom);
			log("deregistered room: " + nameOfRoom);
			return "INFO:  " + nameOfRoom + " has been deregistered";
		}
		else
			return "ERROR:  that room does not exist";
	}

	@Override
	public String deregisterClient(String nameOfClient) throws RemoteException {
		if (Clients.contains(nameOfClient)) {
			Clients.remove(nameOfClient);
			log("deregistered client: " + nameOfClient);
			return "INFO:  " + nameOfClient + " has been deregistered";
		}
		else
			return "ERROR:  that client does not exist";		
	}

	@Override
	public ArrayList<String> listRooms() throws RemoteException {
		ArrayList<String> listOfRooms = new ArrayList<String>();
		log("responded to request for List Of Rooms");
		if (Rooms.isEmpty()) {
			listOfRooms.add("INFO:  no rooms are registered");
			return listOfRooms;
		}

		for (Map.Entry<String, String> entry : Rooms.entrySet())
			listOfRooms.add(String.format("room: %-20s  description: %-20s", entry.getKey(), entry.getValue()));
		return listOfRooms;
	}

	@Override
	public ArrayList<String> listClients() throws RemoteException {
		ArrayList<String> listOfClients = new ArrayList<String>();
		log("responded to request for List Of Clients");
		if (Clients.isEmpty()) {
			listOfClients.add("INFO:  no clients are registered");
			return listOfClients;
		}
		
		for (String client : Clients)
			listOfClients.add(client);
		return listOfClients;
	}

	@Override
	public String getInfo(String name) throws RemoteException {
		log("responded to a Get Information request");
		if (Rooms.containsKey(name))
			return "INFO:  the description of room \"" + name + "\" is: " + Rooms.get(name);
		else if (Clients.contains(name))
			return "INFO:  the client \"" + name + "\" exists in the Registry";
		else
			return "ERROR:  no such entry in the Registry";
	}
	
	private void log(String toLog) {
		System.out.println("INFO:  " + toLog);		
	}
}
