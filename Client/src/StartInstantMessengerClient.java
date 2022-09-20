import javax.swing.JFrame;

public class StartInstantMessengerClient {
	public static void main(String[] args) {
		InstantMessengerClient client;
		client = new InstantMessengerClient("127.0.0.1");
		client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		client.startClient();
	}
}
