import java.io.DataOutputStream;
import java.io.IOException;


public class ServerMessageHandler extends Thread{
	Replica replica;

	public ServerMessageHandler(Replica replica) {
		this.replica = replica;
	}

	public void run() {
		while (true) {
			String  serverMessage = null;
			synchronized (replica.serverMessages) {
				serverMessage = replica.serverMessages.poll();
			}
			if (serverMessage != null) {
				System.out.println(serverMessage);
				
				if (!replica.isFailed) {
					handleClientMessage(serverMessage);
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

	public void handleClientMessage(String currentMessage) {
		String[] messageParts = currentMessage.split("|");
		int paxosId = Integer.parseInt(messageParts[1]);
		
		switch (Integer.parseInt(messageParts[0])) {
		case 1:
			//receivePrepareMsg
			this.replica.logger.write("Received Prepare Msg for Paxos Instance: " + String.valueOf(paxosId) );
			//Already present in Log
			if(paxosId <= this.replica.logEntries.size()) {
				Paxos tempPaxos = this.replica.paxosEntries.get(paxosId);
				sendDecide(paxosId,this.replica.logEntries.get(paxosId - 1));
			}
			//Check for ongoing paxos Instance for this log position
			else {
			Paxos currentPaxos = this.replica.paxosEntries.get(paxosId);
			if(currentPaxos == null) {
				this.replica.paxosEntries.put(paxosId, new Paxos(paxosId, this.replica.replicaId,));
				currentPaxos = this.replica.get(paxosId);
			}
			currentPaxos.onreceivePrepare(proposedBallotNumPair);
			//create a new one otherwise
							
			}
			
			break;
		case 2:
			break;
		case 3:
			break;
		case 4:
			break;
		case 5:
			break;
		default:
			break;

		}
	}
/*
	public String getLog() {
		StringBuilder logEntries = new StringBuilder();
		for (Paxos paxos : replica.paxosEntries) {
			if (paxos.acceptedValue != null)
				logEntries.append(paxos.acceptedValue + "\n");
		}
		return logEntries.toString();
	}
*/	

}
