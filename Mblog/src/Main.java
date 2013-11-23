import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;


public class Main {

	public static void main(String[] args)  throws IOException{
		/* ServerSocket listener = new ServerSocket(9999);
	        try {
	            while (true) {
	            	 Socket connectionSocket = listener.accept();
	                 BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
	                 DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
	                 String clientSentence = inFromClient.readLine();
	                 String capitalizedSentence = clientSentence.toUpperCase() + '\n';
	                 System.out.println(capitalizedSentence);
	                 outToClient.writeBytes(capitalizedSentence);
	            }
	        }
	        finally {
	            listener.close();
	        }
		 */
		Replica replica = new Replica(1, "","configuration/config.txt" );
	}

}
