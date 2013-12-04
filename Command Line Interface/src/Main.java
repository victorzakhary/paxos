import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		for(String arg : args)
		{
			System.out.println(arg);
		}
		int serverPortNumber = Integer.parseInt(args[0]);
		String serverAddress = args[1];
		int clientPortNumber = Integer.parseInt(args[2]);
		String clientAddress = args[3];
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));

			String input = "";
			ServerMessagesReceiver receiver = new ServerMessagesReceiver(clientPortNumber);
			receiver.start();
			while ((input = br.readLine()) != null) {
				CommandHandler handler = new CommandHandler(input,serverPortNumber,serverAddress, clientPortNumber,  clientAddress);
				handler.start();
			}
		} catch (IOException io) {
			io.printStackTrace();
		}
	}

}
