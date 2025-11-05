package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.IOException;

import edu.seg2105.client.common.ChatIF;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  ChatIF serverUI; 
  
  boolean serverOpen;
  boolean serverNotStop;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port, ChatIF serverUI) 
  {
    super(port);
    this.serverUI=serverUI;
    serverOpen=true;
    serverNotStop=true;
    try 
  {
    listen(); //Start listening for connections
  } 
  catch (Exception ex) 
  {
    System.out.println("ERROR - Could not listen for clients!");
  }
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
    System.out.println("Message received: " + msg + " from " + client);
    String msg2= (String) msg;
    String username = (String) client.getInfo("Username");
    if(msg2.length()>6 && msg2.substring(0,6).equals("#login")) {
    	if(username==null) {
    		client.setInfo("Username", msg2.substring(7));
    	}else {
    		try {
    			client.sendToClient("Error! Already logged in. Terminating connection");
    			client.close();
    		}catch(IOException e) {
    			System.out.println("Cannot message and close client");
    		}
    	}
    }else {
    	this.sendToAllClients(username+" > "+msg);
    }
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }
  
  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
//   */
//  public static void main(String[] args) 
//  {
//    int port = 0; //Port to listen on
//
//    try
//    {
//      port = Integer.parseInt(args[0]); //Get port from command line
//    }
//    catch(Throwable t)
//    {
//      port = DEFAULT_PORT; //Set port to 5555
//    }
//	
//    EchoServer sv = new EchoServer(port);
//    
//    try 
//    {
//      sv.listen(); //Start listening for connections
//    } 
//    catch (Exception ex) 
//    {
//      System.out.println("ERROR - Could not listen for clients!");
//    }
//  }
  
  //---------------------------------My codes------------------------------------
  //Name: Ahmad Zoheyr Imrit
  //Student #:300453175
  //Due: November 5th 2025
  //
  
  //----------------------------------Ex 1------------------------------------
  
  /*
   * Sends a message to all clients when a client connects to the server
   * @param client is the client that connected
   */
  protected void clientConnected(ConnectionToClient client) {
	  String username= (String) client.getInfo("Username");
	  if (username==null) {
		  username = client.toString();
	  }
	  String msg = username+" Connected";
	  System.out.println(msg);
	  this.sendToAllClients(msg);
  }

  /*
   * Sends a message to all clients when a client disconnects to the server
   * remove the client from the list of connected clients
   * @param client is the client that disconnected
   */
  synchronized protected void clientException(ConnectionToClient client, Throwable exception){
	  String msg = client.getInfo("Username")+" Disconnected";
	  System.out.println(msg);
	  this.sendToAllClients(msg);
	  super.clientDisconnected(client);
  }
  //-------------------------------------------Ex 2----------------------------
  
  protected void serverClosed()  {serverOpen=false;serverNotStop=false;}
  
  public void handleMessageFromServerUI(String message) throws IOException{
	  if(message.charAt(0)=='#') {
		  consoleCommand(message);
	  }else {
			  message = "SERVER MSG> "+message;
			  this.sendToAllClients(message);
	  }
  }
  
  private void consoleCommand(String command) throws IOException{
		if(command.equals("#quit")) {
			close();
			System.exit(0);
		}else if(command.equals("#stop")) {
			stopListening();
			serverUI.display("Stopped Lsitening for Clients");
			serverNotStop=false;
		}else if(command.equals("#close")) {
			close();
			serverUI.display("all connections close");
		}else if(command.length()>8 && command.substring(0,8).equals("#setport")) {
			if(!serverOpen) {
				int i =8;
				while(command.charAt(i)==' ' || command.charAt(i)=='<') {
					i++;
				}
				int start=i;
				while(i< command.length() &&  command.charAt(i)!=' ' && command.charAt(i)!='>') {
					i++;
				}
				int stop=i;
				int newport = getPort();
				try {
					newport = Integer.parseInt(command.substring(start, stop));
				}catch(NumberFormatException e) {
					serverUI.display("Error!!Port has to be an integer");
				}
				setPort(newport);
			}else {
				serverUI.display("Error!! Server is currently open. \nNeed to close the  server before changing port");
			}
		}else if(command.equals("#start")) {
			if(!serverNotStop) {
				listen();
				serverOpen=true;
				serverNotStop=true;
			}else {
				serverUI.display("Error!! Server is currently open. \nNeed to close the  server to start listening");
			}
		}else if(command.equals("#getport")) {
			String msg = "Current Port:\t"+getPort();
			serverUI.display(msg);
		}else {
			serverUI.display("Error!! Ivalid command. \nCommads: #quit, #stop, #close, #setport, #start, #getport");
		}
	}
}
//End of EchoServer class
