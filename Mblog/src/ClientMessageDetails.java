import java.net.Socket;



public class ClientMessageDetails{

	String message;
	Socket clientSocket;
	
	public ClientMessageDetails(String message, Socket clientSocket) {
		this.clientSocket = clientSocket;
		this.message = message;
	}

}
