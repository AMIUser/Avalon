package com.williamleara.utexas.ee382v12.prj3;

import java.io.*;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.*;

public class ChatProvider {
	
	// instance variable to hold connection to the chat registry
	static ChatRegistryInterface chatRegistry;
	
	// instance variable to hold the provider's chat servers
	static HashSet<ChatServer> chatServers = new HashSet<ChatServer>();
	
	public static void main (String[] args) {
		
	    // make connection with the chat registry
		try {
		String ChatRegistryURL = "rmi://localhost/ChatRegistry";
		chatRegistry = (ChatRegistryInterface) Naming.lookup(ChatRegistryURL);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		// register and instantiate the foyer
		ArrayList<String> rooms;
		try {
			rooms = chatRegistry.listRooms();
			if (!rooms.contains("foyer")) {
				chatRegistry.registerRoom("foyer", "Free area where unregistered users can chat");
				chatServers.add(new ChatServer("foyer"));
			}
		} catch (RemoteException ex) {
			ex.printStackTrace();
		}
		
		// main program loop
	    String userInput="";
    	BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
    	while (!userInput.equals("!exit")) {
        	System.out.println("\nChat Provider Console:");
        	System.out.println("cmds:[!rr <name> <desc>]=Register Room; [!rc <name>]=Register Client; [!dr <name>]=Deregister Room");
        	System.out.println("     [!dc <name>]=Deregister Client; [!lr]=List Rooms; [!lc]=List Clients; [!gi <name>]=Get Info");
        	System.out.print("input: ");
	    	try {
		    	while ((userInput = stdIn.readLine()) != null) {
		    		if (userInput.startsWith("!rr"))
		    			regRoom(userInput);
		    		else if (userInput.startsWith("!rc"))
		    			regClient(userInput);
		    		else if (userInput.startsWith("!dr"))
		    			deregRoom(userInput);
		    		else if (userInput.startsWith("!dc"))
		    			deregClient(userInput);
		    		else if (userInput.startsWith("!lr"))
		    			lsRooms();
		    		else if (userInput.startsWith("!lc"))
		    			lsClients();
		    		else if (userInput.startsWith("!gi"))
		    			gInfo(userInput);
		    		else if (userInput.equals("!exit"))
		    			break;
		    		else
		    			System.out.println("ERROR:  unrecognized command");
		        	System.out.println("\nChat Provider Console:");
		        	System.out.println("cmds:[!rr <name> <desc>]=Register Room; [!rc <name>]=Register Client; [!dr <name>]=Deregister Room");
		        	System.out.println("     [!dc <name>]=Deregister Client; [!lr]=List Rooms; [!lc]=List Clients; [!gi <name>]=Get Info");
		        	System.out.print("input: ");		    	}
	    	} catch (IOException ex) {
	    		ex.printStackTrace();
	    	}
	    }
    	System.exit(0);
	}

	private static void gInfo(String userInput) {
		StringTokenizer st = new StringTokenizer(userInput);
		if (st.countTokens() != 2) {
			System.out.println("ERROR:  usage: !gi <room_name | client_name>");
			return;
		}
		String name = st.nextToken(); // skip over "gi"
		name = st.nextToken();
		String response;
		try {
			response = chatRegistry.getInfo(name);
			System.out.println(response);
		} catch (RemoteException ex) {
			ex.printStackTrace();
		}		
	}

	private static void lsClients() {
		ArrayList<String> clients;
		try {
			clients = chatRegistry.listClients();
			for (String client : clients)
				System.out.println(client);
		} catch (RemoteException ex) {
			ex.printStackTrace();
		}		
	}

	private static void lsRooms() {
		ArrayList<String> rooms;
		try {
			rooms = chatRegistry.listRooms();
			for (String room : rooms)
				System.out.println(room);
		} catch (RemoteException ex) {
			ex.printStackTrace();
		}		
	}

	private static void deregClient(String userInput) {
		StringTokenizer st = new StringTokenizer(userInput);
		if (st.countTokens() != 2) {
			System.out.println("ERROR:  usage: !dc <client_name>");
			return;
		}
		String client = st.nextToken(); // skip over "dc"
		client = st.nextToken();
		String response;
		try {
			response = chatRegistry.deregisterClient(client);
			System.out.println(response);
		} catch (RemoteException ex) {
			ex.printStackTrace();
		}		
	}

	private static void deregRoom(String userInput) {
		StringTokenizer st = new StringTokenizer(userInput);
		if (st.countTokens() != 2) {
			System.out.println("ERROR:  usage: !dr <room_name>");
			return;
		}
		String room = st.nextToken(); // skip over "dr"
		room = st.nextToken();
		
		// remove the room from the registry
		String response;
		try {
			response = chatRegistry.deregisterRoom(room);
			System.out.println(response);
		} catch (RemoteException ex) {
			ex.printStackTrace();
		}
		
		// call the room's delete method in order to clean everything up
		try {
		String chatRoomURL = "rmi://localhost/" + room;
		ChatServerInterface chatRoom = (ChatServerInterface) Naming.lookup(chatRoomURL);
		chatRoom.deleteRoom();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void regClient(String userInput) {
		StringTokenizer st = new StringTokenizer(userInput);
		if (st.countTokens() != 2) {
			System.out.println("ERROR:  usage: !rc <client_name>");
			return;
		}
		String client = st.nextToken(); // skip over "rc"
		client = st.nextToken();
		String response;
		try {
			response = chatRegistry.registerClient(client);
			System.out.println(response);
		} catch (RemoteException ex) {
			ex.printStackTrace();
		}		
	}

	private static void regRoom(String userInput) {
		StringTokenizer st = new StringTokenizer(userInput);
		if (st.countTokens() < 2) {
			System.out.println("ERROR:  usage: !rr <room_name> <description>");
			return;
		}
		String room = st.nextToken(); // skip over "rr"
		room = st.nextToken();
		String description = "";
		while (st.hasMoreTokens())
			description = description + " " + st.nextToken();
		description = description.trim();
		String response;
		
		// register the room in the registry
		try {
			response = chatRegistry.registerRoom(room, description);
			System.out.println(response);
		} catch (RemoteException ex) {
			ex.printStackTrace();
		}
		
		// instantiate the new chat room
		try {
			chatServers.add(new ChatServer(room));
		} catch (RemoteException ex) {
			ex.printStackTrace();
		}
	}
}
