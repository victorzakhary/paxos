
public class Paxos {

	int id; // Monotonically increasing in each server, and represents log entry ID
	BallotPair acceptedBallotPair; //highest accepted ballot number for this paxos instance
	String acceptedValue; // last accepted (decided) Value for this paxos instance, once set, could not be changed
	BallotPair ballotNum;
	
	public Paxos (int id)
	{
		this.acceptedBallotPair = new BallotPair(0, 0);
		acceptedValue = "";
		ballotNum = new BallotPair(0, 0);
	}
	public Paxos (String logLine)
	{
		String logParts [] = logLine.split(" ");
		this.acceptedBallotPair = new BallotPair(Integer.parseInt(logParts[0]),Integer.parseInt(logParts[1]));
		this.acceptedValue = logParts[2];
		this.ballotNum = new BallotPair(Integer.parseInt(logParts[0]),Integer.parseInt(logParts[1]));
	}
	
}
