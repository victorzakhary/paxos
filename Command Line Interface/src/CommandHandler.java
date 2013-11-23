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
			post(commandParts[1]);
		} else if (lowerCaseCommand.startsWith("read")) {
			read();
		} else if (lowerCaseCommand.startsWith("fail")) {
			fail();
		} else if (lowerCaseCommand.startsWith("unfail")) {
			unfail();
		}
	}
	private boolean post (String tweet)
	{
		System.out.println(tweet);
		Socket clientSocket;
		try {
	        String modifiedSentence;
	        clientSocket = new Socket("localhost", 9999);
	        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
	        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	        outToServer.writeBytes(tweet+"\n");
	        modifiedSentence = inFromServer.readLine();
	        System.out.println(modifiedSentence);
	        clientSocket.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			return false;
		}
	}
	private String read ()
	{
		return "";
	}
	private void fail ()
	{
		
	}
	private void unfail ()
	{
		
	}
	
	

}
