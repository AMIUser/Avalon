package com.williamleara.utexas.ee382v12.prj3;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ChatRegistryInterface extends Remote {
	public String registerRoom(String nameOfRoom, String descriptionOfRoom) throws RemoteException;
	public String registerClient(String nameOfClient) throws RemoteException;
	public String deregisterRoom(String nameOfRoom) throws RemoteException;
	public String deregisterClient(String nameOfClient) throws RemoteException;
	public ArrayList<String> listRooms() throws RemoteException;
	public ArrayList<String> listClients() throws RemoteException;
	public String getInfo(String name) throws RemoteException;
}
