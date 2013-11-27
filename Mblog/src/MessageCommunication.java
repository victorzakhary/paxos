import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class MessageCommunication {

	
	public void sendPrepareMsg(int paxosId, BallotPair proposedBallotNumPair) {
		// BroadCast this message to everybody.
		// only broadcast a string with a delimtetr
	}

	public void sendAckToPrepare(int paxosId, BallotPair ownBallotNumPair,
			BallotPair acceptedBallotNumPair, String acceptedValue) {

	}

	public void sendAccept(int paxosId, BallotPair proposeBallotNumPair,
			String ValueToWrite) {

	}

	public void sendDecide(int paxosId, String acceptedValue) {
		// keep sending it periodically Ask_Victor would it block the call
	}

	public void broadCast(int replicaId, ArrayList<ReplicaCommInfo> receivers,
			String message) {
		for (ReplicaCommInfo receiver : receivers) {
			Socket clientSocket;
			try {
				if (receiver.replicaId != replicaId) {
					clientSocket = new Socket(receiver.replicaIP,
							receiver.socketId);
					DataOutputStream outToServer = new DataOutputStream(
							clientSocket.getOutputStream());
					outToServer.writeBytes(message + "\n");
					clientSocket.close();
				}

			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
