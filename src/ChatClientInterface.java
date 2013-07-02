package com.williamleara.utexas.ee382v12.prj3;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatClientInterface extends Remote {
	public void chatCallback(String room, String text) throws RemoteException;
}
