import java.io.DataOutputStream;
import java.io.IOException;

public class ClientMessageHandler extends Thread {

	Replica replica;

	public ClientMessageHandler(Replica replica) {
		this.replica = replica;
	}

	public void run() {
		while (true) {
			ClientMessageDetails currentMessage = null;
			synchronized (replica.clientMessages) {
				currentMessage = replica.clientMessages.poll();
			}
			if (currentMessage != null) {
				System.out.println(currentMessage.message);
				
				if (!replica.isFailed
						|| (currentMessage.message.startsWith("unfail") 
								&& replica.isFailed)) {
					handleClientMessage(currentMessage);
				}
			} else {
				try {
					Thread.sleep(150);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}

	// Message parts are "fail" or "unfail" or "read" or "post|Message Body"
	public void handleClientMessage(ClientMessageDetails currentMessage) {
		String[] messageParts = new String[1];
		if(currentMessage.message.contains("|"))
		{
			messageParts = currentMessage.message.split("|");
		}
		else
		{
			messageParts[0] = currentMessage.message;
		}

		switch (messageParts[0]) {
		case "post":
			
			break;
		case "read":
			String logEntries = getLog();
			replyToClient(currentMessage, logEntries);
			break;
		case "fail":
			replica.isFailed = true;
			replyToClient(currentMessage, "Server is down");
			break;
		case "unfail":
			replica.isFailed = false;
			replyToClient(currentMessage, "Server is up back");
			break;
		default:
			break;

		}
	}

	public String getLog() {
		StringBuilder logEntries = new StringBuilder();
		for (Paxos paxos : replica.paxosEntries) {
			if (paxos.acceptedValue != null)
				logEntries.append(paxos.acceptedValue + "\n");
		}
		return logEntries.toString();
	}

	public void replyToClient(ClientMessageDetails clientMessage,
			String replyMessage) {
		try {
			DataOutputStream outToClient = new DataOutputStream(
					clientMessage.clientSocket.getOutputStream());

			outToClient.writeBytes(replyMessage + "\n");
			System.out.println("client reply = " + replyMessage);
			clientMessage.clientSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
