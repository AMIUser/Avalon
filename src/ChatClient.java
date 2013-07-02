package com.williamleara.utexas.ee382v12.prj3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringTokenizer;

public class ChatClient {
	
	// instance variable to hold connection to the chat registry
	static ChatRegistryInterface chatRegistry;
	
	// instance variable to hold the client's first name
	static String clientName;
	
	// instance variable to hold list of rooms client has joined
	static HashSet<String> subscribedRooms = new HashSet<String>();
	
	// instance variable to hold the current room
	static String currentRoom = "foyer";
	
	public static void main (String[] args) {
		
	    // make connection with the chat registry
		try {
		String ChatRegistryURL = "rmi://localhost/ChatRegistry";
		chatRegistry = (ChatRegistryInterface) Naming.lookup(ChatRegistryURL);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		// get the user's name
	    String userInput="";
    	BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("\nWelcome, what is your name?: ");
		try {
			clientName = stdIn.readLine();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		System.out.println("Hello " + clientName + ", you are in the " + currentRoom.toUpperCase());
		
		// instantiate and bind the chat client
		try {
			ChatClientImplementation chatClient = new ChatClientImplementation();
			Naming.rebind(clientName, chatClient);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		// enter the foyer
		try {
			String chatServer = "rmi://localhost/foyer";
			ChatServerInterface chatRoom = (ChatServerInterface) Naming.lookup(chatServer);
			chatRoom.joinRoom(clientName);
			subscribedRooms.add("foyer");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		// main program loop
		while (!userInput.equals("!exit")) {
        	System.out.println("\nChat Client Console:");
        	System.out.println("cmds:  [!lr]=List Rooms; [!lc]=List Clients; [!gi <name>]=Get Info; [!jr <name>]=Join Room; [!qr <name>]=Quit Room");
        	System.out.print("{" + currentRoom + "} input: ");
	    	try {
		    	while ((userInput = stdIn.readLine()) != null) {
		    		if (userInput.startsWith("!lr"))
		    			lsRooms();
		    		else if (userInput.startsWith("!lc"))
		    			lsClients();
		    		else if (userInput.startsWith("!gi"))
		    			gInfo(userInput);
		    		else if (userInput.startsWith("!jr"))
		    			joinRoom(userInput);
		    		else if (userInput.startsWith("!qr"))
		    			quitRoom(userInput);
		    		else if (userInput.equals("!exit"))
		    			break;
		    		else
		    			doTalk(userInput);
		        	System.out.println("\nChat Client Console:");
		        	System.out.println("cmds:  [!lr]=List Rooms; [!lc]=List Clients; [!gi <name>]=Get Info; [!jr <name>]=Join Room; [!qr <name>]=Quit Room");
		        	System.out.print("{" + currentRoom + "} input: ");
		        	}
	    	} catch (IOException ex) {
	    		ex.printStackTrace();
	    	}
	    }
    	exitGracefully();
	}

	private static void doTalk(String userInput) {
		try {
			String chatServer = "rmi://localhost/" + currentRoom;
			ChatServerInterface chatRoom = (ChatServerInterface) Naming.lookup(chatServer);
			chatRoom.talk(clientName, userInput);
		} catch (Exception ex) {
			ex.printStackTrace();
		}		
	}

	private static void quitRoom(String nameOfRoom) {
		StringTokenizer st = new StringTokenizer(nameOfRoom);
		if (st.countTokens() != 2) {
			System.out.println("ERROR:  usage: !qr <room_name>");
			return;
		}
		nameOfRoom = st.nextToken(); // skip over "qr"
		nameOfRoom = st.nextToken();
		try {
			String chatServer = "rmi://localhost/" + nameOfRoom.toLowerCase();
			ChatServerInterface chatRoom = (ChatServerInterface) Naming.lookup(chatServer);
			chatRoom.leaveRoom(clientName);
			subscribedRooms.remove(nameOfRoom.toLowerCase());
		} catch (Exception ex) {
			System.out.println("ERROR:  no such room as \"" + nameOfRoom + "\"");
			return;
		}
		if (nameOfRoom.equalsIgnoreCase(currentRoom))
			currentRoom = "foyer";
		System.out.println("You are now in the " + currentRoom.toUpperCase());
	}

	private static void joinRoom(String nameOfRoom) {
		if (!validateClient(clientName))
			return;
		StringTokenizer st = new StringTokenizer(nameOfRoom);
		if (st.countTokens() != 2) {
			System.out.println("ERROR:  usage: !jr <room_name>");
			return;
		}
		nameOfRoom = st.nextToken(); // skip over "jr"
		nameOfRoom = st.nextToken();
		try {
			String chatServer = "rmi://localhost/" + nameOfRoom.toLowerCase();
			ChatServerInterface chatRoom = (ChatServerInterface) Naming.lookup(chatServer);
			chatRoom.joinRoom(clientName);
			subscribedRooms.add(nameOfRoom.toLowerCase());
		} catch (Exception ex) {
			System.out.println("ERROR:  no such room as \"" + nameOfRoom + "\"");
			return;
		}
		currentRoom = nameOfRoom;
		System.out.println("You are now in " + currentRoom.toUpperCase());
	}

	private static void gInfo(String userInput) {
		if (!validateClient(clientName))
			return;
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
	
	private static boolean validateClient(String name) {
		ArrayList<String> clients;
		try {
			clients = chatRegistry.listClients();
			if (clients.contains(name))
				return true;
		} catch (RemoteException ex) {
			ex.printStackTrace();
		}
		System.out.println("ERROR:  you are not a registered client");
		return false;
	}

	private static void lsClients() {
		if (!validateClient(clientName))
			return;
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
		if (!validateClient(clientName))
			return;
		ArrayList<String> rooms;
		try {
			rooms = chatRegistry.listRooms();
			for (String room : rooms)
				System.out.println(room);
		} catch (RemoteException ex) {
			ex.printStackTrace();
		}		
	}
	

	private static void exitGracefully() {
		// unbind the client from the RMI registry
		try {
			Naming.unbind(clientName);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		// notify all the chat rooms the client has left
		for (String room : subscribedRooms) {
			try {
				String chatServer = "rmi://localhost/" + room.toLowerCase();
				ChatServerInterface chatRoom = (ChatServerInterface) Naming.lookup(chatServer);
				chatRoom.leaveRoom(clientName);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		// exit
		System.out.println("INFO:  you have now left all rooms -- *** goodbye ***");
		System.exit(0);		
	}
}

