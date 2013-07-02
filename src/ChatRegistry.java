package com.williamleara.utexas.ee382v12.prj3;

import java.rmi.Naming;

public class ChatRegistry {
	
	public static void main (String[] args) {
		// instantiate and bind the chat registry
		try {
			ChatRegistryImplementation chatRegistry = new ChatRegistryImplementation();
			Naming.rebind("ChatRegistry", chatRegistry);
			System.out.println("INFO:  the Registry is running");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
