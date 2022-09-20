import javax.swing.JFrame;
public class StartInstantMessengerServer {
	public static void main(String[] args) {
		InstantMessengerServer server = new InstantMessengerServer();
		server.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		server.startServer();
	}
}
