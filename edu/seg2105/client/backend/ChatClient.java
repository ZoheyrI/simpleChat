// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;

import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  String username;
  

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String username, String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.username = username;
    openConnection();
    try
	  {
		  sendToServer("#login "+username);
	  }
	  catch(IOException e)
	  {
		  clientUI.display
		  ("Could not send message to server.  Terminating client.");
		  quit();
	}

  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
    
    
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message) throws IOException
  {
	  
	  if(message.charAt(0)=='#') {
		  consoleCommand(message);
	  }else {
		  try
		  {
			  sendToServer(message);
		  }
		  catch(IOException e)
		  {
			  clientUI.display
			  ("Could not send message to server.  Terminating client.");
			  quit();
    	}
	  }
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
  
  //---------------------------------My codes------------------------------------
  //Name: Ahmad Zoheyr Imrit
  //Student #:300453175
  //Due: November 5th 2025
  //
  
  //----------------------------------Ex 1------------------------------------
  
  
  /**
	 * Displays that the connection was closed and terminates client
	 */
	protected void connectionClosed() {
		clientUI.display("The connection has been closed. Terminating client");
	}

	/**
	 * Displays that a server connection error occurred and terminates client
	 * 
	 * @param exception
	 *            the exception raised.
	 */
	protected void connectionException(Exception exception) {
		clientUI.display("Error!! Cannot connect to the Server.");
		quit();
	}
	
	private void consoleCommand(String command) throws IOException{
		
		if(command.equals("#quit")) {
			quit();
		}else if(command.equals("#logoff")) {
			try
			   {
			     closeConnection();
			   }
			    catch(IOException e) {}
			clientUI.display("Connection to server terminated");
		}else if(command.length()> 8 && command.substring(0,8).equals("#sethost")) {
			if(!isConnected()) {
				int i =8;
				while(command.charAt(i)==' ' || command.charAt(i)=='<') {
					i++;
				}
				int start=i;
				while(i< command.length() && command.charAt(i)!=' ' && command.charAt(i)!='>') {
					i++;
				}
				int stop=i;
				setHost(command.substring(start, stop));
			}else {
				clientUI.display("Error!! Currently connected to a server. \nNeed to disconnect before changing Host");
			}
		}else if(command.length()> 8 && command.substring(0,8).equals("#setport")) {
			if(!isConnected()) {
				int i =8;
				while(command.charAt(i)==' ' || command.charAt(i)=='<') {
					i++;
				}
				int start=i;
				while(i< command.length() && command.charAt(i)!=' ' && command.charAt(i)!='>') {
					i++;
				}
				int stop=i;
				int newport = getPort();
				try {
					newport = Integer.parseInt(command.substring(start, stop));
				}catch(NumberFormatException e) {
					clientUI.display("Error!!Port has to be an integer");
				}
				setPort(newport);
			}else {
				clientUI.display("Error!! Currently connected to a server. \nNeed to disconnect before changing port");
			}
		}else if(command.equals("#gethost")) {
			String msg = "Current Host:\t"+getHost();
			clientUI.display(msg);
		}else if(command.equals("#getport")) {
			String msg = "Current Port:\t"+getPort();
			clientUI.display(msg);
		}else if(command.length()>= 6 && command.substring(0,6).equals("#login")) {
			if(!isConnected()) {
				openConnection();
				if(command.length()> 6) {
					int i =6;
					while(command.charAt(i)==' ' || command.charAt(i)=='<') {
						i++;
					}
					int start=i;
					while(i< command.length() && command.charAt(i)!=' ' && command.charAt(i)!='>') {
						i++;
					}
					int stop=i;
					if(stop-start>1) {
						username = command.substring(start, stop);
					}
				}
				sendToServer(command.substring(0,6)+" "+username);
			}else {
				clientUI.display("Error!! Currently connected to a server. \nNeed to be disconnected to login");
			}
			
			
		}else {
			clientUI.display("Error!! Ivalid command. \nCommads: #quit, #logoff, #sethost, #setport, #login, #gethost, #getport");
		}
	}
}
//End of ChatClient class
