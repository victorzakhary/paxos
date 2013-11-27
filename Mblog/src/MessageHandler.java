import java.net.Socket;



public class MessageHandler{

	Replica replica;
	String message;
	Socket clientSocket;
	
	public MessageHandler(Replica replica, String message, Socket clientSocket) {
		this.replica = replica;
		this.clientSocket = clientSocket;
		this.message = message;
		}

}
