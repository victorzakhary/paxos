public class ClientMessageHandler extends Thread {

	Replica replica;

	public ClientMessageHandler(Replica replica) {
		this.replica = replica;
	}

	public void run() {
		while(true)
		{
			ClientMessageDetails currentMessage= null;
			synchronized (replica.clientMessages) {
				currentMessage = replica.clientMessages.poll();
			}
			if(currentMessage!=null && !replica.isFailed)
			{
				handleClientMessage(currentMessage); 
			}
			else
			{
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
	public void handleClientMessage (ClientMessageDetails currentMessage)
	{
		String [] messageParts = currentMessage.message.split("|");
		
		switch (messageParts[0]){
			case "post":
				break;
			case "read":
				String logEntries = getLog();
				
				break;
			case "fail":
				break;
			case "unfail":
				break;
			default:
				break;
		
		}
	}
	public String getLog()
	{
		StringBuilder logEntries = new StringBuilder();
		for(Paxos paxos : replica.paxosEntries)
		{
			if(paxos.acceptedValue != null)
			logEntries.append(paxos.acceptedValue + "\n");
		}
		return logEntries.toString();
	}

}
