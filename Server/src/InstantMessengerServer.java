import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class InstantMessengerServer extends JFrame {
	
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;

	//server creation
	public InstantMessengerServer(){
		super("Instant Messenger Server");
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
		setLocation(50,100);
		setVisible(true);
	}

	//start the server
	public void startServer(){
		try{
			server = new ServerSocket(6789, 100);
			while(true){
				try{
					//Trying to connect and have conversation
					waitForConnection();
					setupStreams();
					messengerChat();
				}catch(EOFException eofException){
					showMessage("\n Server ended the connection! ");
				} finally{
					closeConnection();
				}
			}
		} catch (IOException ioException){
			ioException.printStackTrace();
		}
	}

	//waiting for client to connect
	private void waitForConnection() throws IOException{
		showMessage(" Waiting for client to connect... \n");
		connection = server.accept();
		showMessage(" Connected to client " + connection.getInetAddress().getHostName());
	}

	//establish communication stream between server and client
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		
		input = new ObjectInputStream(connection.getInputStream());
		
		showMessage("\n Streams are now setup \n");
	}
	
	//chat conversation
	private void messengerChat() throws IOException{
		String message = " You are now connected! ";
		sendMessage(message);
		userInput(true);
		do{
			try{
				message = (String) input.readObject();
				showMessage("\n" + message);
			}catch(ClassNotFoundException classNotFoundException){
				showMessage("The user has sent an unknown object!");
			}
		}while(!message.equals("CLIENT - END"));
	}

	//terminate server
	public void closeConnection(){
		showMessage("\n Closing Connections... \n");
		userInput(false);
		try{
			output.close(); //Closes the output path to the client
			input.close(); //Closes the input path to the server, from the client.
			connection.close(); //Closes the connection between you can the client
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
	//Send a message to the client
	private void sendMessage(String message){
		try{
			output.writeObject("INSTANT MESSENGER SERVER - " + message);
			output.flush();
			showMessage("\nINSTANT MESSENGER SERVER -" + message);
		}catch(IOException ioException){
			chatWindow.append("\n ERROR: CANNOT SEND MESSAGE, PLEASE RETRY");
		}
	}
	
	//update chatWindow
	private void showMessage(final String text){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					chatWindow.append(text);
				}
			}
		);
	}

	//serve should send message only when connection is established
	private void userInput(final boolean tof){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					userText.setEditable(tof);
				}
			}
		);
	}
}
