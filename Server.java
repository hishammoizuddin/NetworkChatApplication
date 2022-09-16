import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.scene.control.ListView;
/*
 * Clicker: A: I really get it    B: No idea what you are talking about
 * C: kind of following
 */

public class Server{

	int count = 1;	
	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	TheServer server;
	private Consumer<Serializable> callback;
	
	
	Server(Consumer<Serializable> call){
	
		callback = call;
		server = new TheServer();
		server.start();
	}
	
	
	public class TheServer extends Thread{
		
		public void run() {
		
			try(ServerSocket mysocket = new ServerSocket(5555);){
		    System.out.println("Server is waiting for a client!");
		  
			
		    while(true) {
		
				ClientThread c = new ClientThread(mysocket.accept(), count);
				callback.accept("client has connected to server: " + "client #" + count);
				clients.add(c);
				callback.accept("In the server: ");
				for(ClientThread cli : clients) {
					callback.accept("  client #"+cli.count);
				}
				c.start();
				
				count++;
				
			    }
			}//end of try
				catch(Exception e) {
					callback.accept("Server socket did not launch");
				}
			}//end of while
		}
	

		class ClientThread extends Thread{
			
		
			Socket connection;
			int count;
			ObjectInputStream in;
			ObjectOutputStream out;
			
			ClientThread(Socket s, int count){
				this.connection = s;
				this.count = count;	
			}
			
			public void updateClients(String message) {
				for(int i = 0; i < clients.size(); i++) {
					ClientThread t = clients.get(i);
					try {
					 t.out.writeObject(message);
					}
					catch(Exception e) {}
				}
			}
			
			public void updateSpecClients(String message,int clientNum) {
				synchronized(clients) {
					for(int i = 0; i < clients.size(); i++) {
						ClientThread t = clients.get(i);
						if (t.count == clientNum) {
						try {
						 t.out.writeObject(message);
						}
						catch(Exception e) {}
						}
					}
				}
			}
			
			public void run(){
					
				try {
					in = new ObjectInputStream(connection.getInputStream());
					out = new ObjectOutputStream(connection.getOutputStream());
					connection.setTcpNoDelay(true);	
				}
				catch(Exception e) {
					System.out.println("Streams not open");
				}
				
				updateClients("new client on server: client #"+count);
				updateClients("In the server: ");
				for(ClientThread cli: clients) {
					updateClients("  client #"+cli.count);
				}
				 while(true) {
					    try {
					    	newInfo data = (newInfo) in.readObject();
					    	callback.accept("client: " + count + " sent: " + data.message);
					    	if (data.sentNums.size() != 0) {
						    	for(Integer m : data.sentNums) {
						    		updateSpecClients("client #"+count+" said: "+data.message, m);
						    	}
						    	data.sentNums.clear();
					    	} else {updateClients("client #"+count+" said: "+data.message);}
					    	}
					    catch(Exception e) {
					    	callback.accept("OOOOPPs...Something wrong with the socket from client: " + count + "....closing down!");
					    	updateClients("Client #"+count+" has left the server!");
					    	clients.remove(this);
					    	callback.accept("In the server: ");
							for(ClientThread cli : clients) {
								callback.accept("  client #"+cli.count);
							}
					    	updateClients("In the server: ");
					    	for(ClientThread cli: clients) {
								updateClients("  client #"+cli.count);
							}
					    	break;
					    }
					}
				}//end of run
			
			
		}//end of client thread
}


	
	

	

