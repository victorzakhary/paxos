import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class CommandHandler extends Thread {

	String command = "";
	
	private BufferedReader in;
	private DataOutputStream out;
	Socket clientSocket;

	public CommandHandler(String command) {
		this.command = command;
	}

	@Override
	public void run() {
		handleCommand(command);
	}
	

	// post, read, fail and unfail
	private void handleCommand(String command) {
		String lowerCaseCommand = command.toLowerCase();
		System.out.println(command);
		if (lowerCaseCommand.startsWith("post")) {
			String [] commandParts = command.split("\\(");
			commandParts[1] = commandParts[1].replace(")","").trim().replace("\"", "");
			sendToServer("post|" + commandParts[1]);
		} else if (lowerCaseCommand.startsWith("read")) {
			sendToServer("read");
		} else if (lowerCaseCommand.startsWith("fail")) {
			sendToServer("fail");
		} else if (lowerCaseCommand.startsWith("unfail")) {
			sendToServer("unfail");
		}
	}
	
	private void sendToServer (String message)
	{
		try {
	        String replyFromServer;
	        clientSocket = new Socket("localhost", 5000);
	        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
	        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	        outToServer.writeBytes(message + "\n");
	        replyFromServer = inFromServer.readLine();
	        System.out.println(replyFromServer);
	        clientSocket.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
