package com.williamleara.utexas.ee382v12.prj3;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatServerInterface extends Remote {
	public String joinRoom(String nameOfClient) throws RemoteException;
	public void talk(String nameOfClient, String text) throws RemoteException;
	public String leaveRoom(String nameOfClient) throws RemoteException;
	public void deleteRoom() throws RemoteException;
}
