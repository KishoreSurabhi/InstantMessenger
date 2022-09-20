import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class InstantMessengerClient extends JFrame{
	
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message = "";
	private String serverIP;
	private Socket connection;

	//client creation
	public InstantMessengerClient(String host){
		super("Instant Messenger Client");
		serverIP = host;
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(
				new ActionListener(){
				public void actionPerformed(ActionEvent event){
					sendMessage(event.getActionCommand());
					userText.setText("");
				}
			}
		);
		add(userText, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow));

		//window alignment
		setSize(600, 300);
		setLocation(800,100);
		setVisible(true);
	}
	
	//start the client
	public void startClient(){
		try{
			connectToServer();
			setupStreams();
			messengerChat();
		}catch(EOFException eofException){
			showMessage("\n Client ended the chat");
		}catch(IOException ioException){
			ioException.printStackTrace();
		}finally{
			closeConnection();
		}
	}
	
	//connect to server
	private void connectToServer() throws IOException{
		showMessage("Connecting to server... \n");
		connection = new Socket(InetAddress.getByName(serverIP), 6789);
		showMessage("Connection success! Connected to: " + connection.getInetAddress().getHostName());
	}

	//establish communication stream between server and client
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n The streams are now set up! \n");
	}

	//chat conversation
	private void messengerChat() throws IOException{
		ableToType(true);
		do{
			try{
				message = (String) input.readObject();
				showMessage("\n" + message);
			}catch(ClassNotFoundException classNotFoundException){
				showMessage("Unknown data received!");
			}
		}while(!message.equals("SERVER - END"));	
	}
	
	//Close connection
	private void closeConnection(){
		showMessage("\n Terminating the connection!");
		ableToType(false);
		try{
			output.close();
			input.close();
			connection.close();
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
	//send message to server
	private void sendMessage(String message){
		try{
			output.writeObject("INSTANT MESSENGER CLIENT - " + message);
			output.flush();
			showMessage("\nINSTANT MESSENGER CLIENT - " + message);
		}catch(IOException ioException){
			chatWindow.append("\n Something went wrong! Please try again.");
		}
	}
	
	//update chat window
	private void showMessage(final String message){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					chatWindow.append(message);
				}
			}
		);
	}
	
	//allows user to type
	private void ableToType(final boolean tof){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					userText.setEditable(tof);
				}
			}
		);
	}
}
