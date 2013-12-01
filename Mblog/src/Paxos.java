import java.util.*;

public class Paxos {

	int id; // Monotonically increasing in each server, and represents log entry ID
	int replicaId;
	int numAck;
	int numNAck;
	BallotPair acceptedBallotNumPair; //highest accepted ballot number for this paxos instance
	String acceptedValue; // last accepted (decided) Value for this paxos instance, once set, could not be changed
	BallotPair proposeBallotNumPair; // for proposing and increasing on loosing
	BallotPair ownBallotNumPair; // for responding to prepare messages
	MessageCommunication Msg;
	int numTotalServers;
	String ValueToWrite;
	ArrayList<AcceptedBallotNumAndValue> ListAcceptedNumAndValue;
	ArrayList<AcceptedBallotNumAndValue> ListAcceptMsgsAndCounter;
	int acceptableFailures;
	int receivedAccepts;
	boolean decidedValue;
	boolean proposer;
	Logging logger;

	public Paxos (int id, int replicaid, int num_total_servers, String write_req_from_client, Logging replicaLogger)
	{
		this.id = id;
		this.numTotalServers = num_total_servers;
		this.numAck = 0;
		this.numNAck = 0;
		this.acceptedBallotNumPair = new BallotPair();
		this.ownBallotNumPair = new BallotPair();
		this.acceptedValue = "";
		this.proposeBallotNumPair = new BallotPair(0,replicaId);
		//this.Msg = new MessageCommunication();
		this.ListAcceptedNumAndValue = new ArrayList<AcceptedBallotNumAndValue>();
		this.ListAcceptMsgsAndCounter = new ArrayList<AcceptedBallotNumAndValue>();
		this.ValueToWrite = write_req_from_client;
		this.acceptableFailures = 1; //Ask_Victor for t
		this.receivedAccepts = 0;
		this.decidedValue = false;
		this.logger = replicaLogger; //logger of the replica instantiated in replica class - used by paxos instances.
		this.proposer = false;
		this.logger.write("Paxos Id:" + String.valueOf(this.id) + " created.");
		
	}
	
	public void iamProposer() {
		this.proposer = true;
	}
	
	/**
	public Paxos (String logLine)
	{
		String logParts [] = logLine.split(" ");
		this.acceptedBallotPair = new BallotPair(Integer.parseInt(logParts[0]),Integer.parseInt(logParts[1]));
		this.acceptedValue = logParts[2];
		this.ballotNum = new BallotPair(Integer.parseInt(logParts[0]),Integer.parseInt(logParts[1]));
	}
	*/
	
	//write prepare
	//ack to prepare
	//write accept
	
	/** sendPrepare
	 *  Called - on receiving "post" request
	 *  Does - becomes a leader and sends prepare msg to all
	 *  returns 1 on success of sendPrepare and go for accept phase
	 *  returns 0 on failure - that is another value was decided during the prepare phase 
	 */
	public int sendPrepare()
	{
		//Keep sending prepare message until you hear back from majority
		boolean keepTrying = true;
		

		while(keepTrying) {
			//Initially
			if(this.numAck == 0 && this.numNAck == 0 && !this.decidedValue) {
				try {
					//Increase own proposeBallotNumPair
					this.proposeBallotNumPair.ballotNum = this.proposeBallotNumPair.ballotNum + 1;
					this.numAck = 0;
					this.numNAck = 0;
					//send prepare msg
					Msg.sendPrepareMsg(this.id, this.proposeBallotNumPair);
					this.logger.write("PaxosID:" + String.valueOf(this.id) + " SENT PREPARE BALLOTNUMBER" + this.proposeBallotNumPair.toString());
					//Waiting for people to respond within 150 milliseconds otherwise - Increase BallotNumber and Propose again.
					Thread.sleep((long) (Math.random()*150));
				}
				catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
			else if(this.numAck > this.numTotalServers/2 && !this.decidedValue) {
				//Received Ack from majority stop trying now
				this.logger.write("RECVD ACK FROM MAJORITY FOR BALLOTNUM: " + this.proposeBallotNumPair.toString());
				keepTrying = false;
				//do something
			}
			//Received Nack from half of the total servers - propose new ballot number
			else if(this.numNAck >= this.numTotalServers/2 && !this.decidedValue) {
				this.logger.write("RECVD NACK FROM MAJORITY FOR BALLOTNUM: " + this.proposeBallotNumPair.toString());
				try {
					//Increase own proposeBallotNumPair
					this.proposeBallotNumPair.ballotNum = this.proposeBallotNumPair.ballotNum + 1;
					this.numAck = 0;
					this.numNAck = 0;
					//send prepare msg
					Msg.sendPrepareMsg(this.id, this.proposeBallotNumPair);
					this.logger.write("PaxosID:" + String.valueOf(this.id) + " SENT PREPARE BALLOTNUMBER" + this.proposeBallotNumPair.toString());
					//Waiting for people to respond within 150 milliseconds otherwise - Increase BallotNumber and Propose again. - Ask_Victor
					Thread.sleep((long) (Math.random()*150));
				}
				catch(InterruptedException e) {
					e.printStackTrace();
				}		
			}
			else {
				this.logger.write("ERROR: BALLOTNUM: " + this.proposeBallotNumPair.toString() + " ----------DID NOT RECEIVE HALF OF NACKS OR MAJORITY OF ACKS, OR DECIDED ON A VALUE SO FAIL --------");
				return 0;
			}
		}
		return 1;
	}
	
	/** onreceivePrepare
	 * Called - when a replica - receives a prepare msg and instantiates a new paxos instance if not spawned already.
	 * Does - responds back to the leader if ballot number proposed is higher than or equal to the earlier accepted ballot number
	 * 
	 */
	public synchronized void onreceivePrepare(BallotPair proposedBallotNumPair) {		
		this.logger.write("PaxosID:" + String.valueOf(this.id) + " RECEIVED PREPARE MSG WITH BALLOTNUMBER " + proposedBallotNumPair.toString());
		//send Ack
		if(proposedBallotNumPair.compareTo(this.ownBallotNumPair) >= 0) {
			this.ownBallotNumPair = proposedBallotNumPair;
			Msg.sendAckToPrepare(this.id, this.ownBallotNumPair, this.acceptedBallotNumPair, this.acceptedValue);
			this.logger.write("PaxosID:" + String.valueOf(this.id) + " SENT ACK TO PREPARE MSG WITH BALLOTNUMBER " + proposedBallotNumPair.toString());
		}
		//send Nack
		else
		{
			Msg.sendNAckToPrepare(this.id, proposedBallotNumPair);
			this.logger.write("PaxosID:" + String.valueOf(this.id) + " SENT NegativeACK TO PREPARE MSG WITH BALLOTNUMBER " + proposedBallotNumPair.toString());
		}
		
	}
	
	/** onreceiveAckToPrepare
	 * Called - when a replica - receives an "AckToPrepare" msg from another replica for ongoing paxos
	 * then replica picks up corresponding paxos and calls this method
	 * r sends for replica
	 * Does - increases the numPrepareAck and 
	 */
	public synchronized void onreceiveAckToPrepare(BallotPair r_ownBallotNumPair, BallotPair r_acceptedBallotNumPair, String r_acceptedValue) {
		//I assume that the received ack is for current ballotnum pair which I proposed most recently and increase ack for it.
		this.logger.write("PaxosID:" + String.valueOf(this.id) + " RECEIVED ACK TO PREPARE MSG WITH BALLOTNUMBER " + r_ownBallotNumPair.toString());
		if(this.proposeBallotNumPair.compareTo(r_ownBallotNumPair) == 0) {
			this.numAck = this.numAck + 1;
		    this.ListAcceptedNumAndValue.add(new AcceptedBallotNumAndValue(r_acceptedBallotNumPair, r_acceptedValue));
		    //On receiving ack from majority
		    if(this.numAck > this.numTotalServers/2) {
		    	Iterator<AcceptedBallotNumAndValue> itr = this.ListAcceptedNumAndValue.iterator();
		    	boolean novalue = true;
		    	AcceptedBallotNumAndValue highestBallotNumObj = new AcceptedBallotNumAndValue(new BallotPair(0,0),"");
		    	while(itr.hasNext()) {
		    		AcceptedBallotNumAndValue testObj = itr.next();
		    		if(testObj.r_acceptedValue != "")
		    			novalue = false;
		    		if(testObj.r_acceptedBallotNumPair.compareTo(highestBallotNumObj.r_acceptedBallotNumPair) > 0) {
		    			highestBallotNumObj = testObj;
		    		}
		    	}
		    	if(novalue) {
		    		this.logger.write("PaxosID:" + String.valueOf(this.id) + " RECVD ACK TO PREPARE FROM MAJORITY, SENDING ACCEPT MSG WITH VALUE " + this.ValueToWrite);
		    		Msg.sendAccept(this.id, this.proposeBallotNumPair,this.ValueToWrite);
		    	}
		    	else {
		    		this.logger.write("PaxosID:" + String.valueOf(this.id) + " RECVD ACK TO PREPARE FROM MAJORITY, SENDING ACCEPT MSG WITH VALUE " + highestBallotNumObj.r_acceptedValue);
		    		Msg.sendAccept(this.id, this.proposeBallotNumPair, highestBallotNumObj.r_acceptedValue);
		    	}
		    }
		}
		else {
			this.logger.write("ERROR: PAXOS ID: " + String.valueOf(this.id) + " Received ack for Ballot Number " + r_ownBallotNumPair.toString() + " While current propsosed ballot number is " + this.proposeBallotNumPair.toString());
		}
		
	}
	
	
	
	/**onreceiveNAckToPrepare
	 * 
	 */
	public synchronized void onreceiveNAckToPrepare(BallotPair proposedBallotNumPair) {
		//I assume that the received negative ack is for current ballotnum pair which I proposed most recently and increase nack for it.
		this.logger.write("PaxosID:" + String.valueOf(this.id) + " RECEIVED Negative ACK TO PREPARE MSG WITH BALLOTNUMBER " + proposedBallotNumPair.toString());
		if(this.proposeBallotNumPair.compareTo(proposedBallotNumPair) == 0) {
			this.numNAck = this.numNAck + 1;
		    }
		else {
			this.logger.write("ERROR: PAXOS ID: " + String.valueOf(this.id) + " Received nack for a different Ballot Number " + proposedBallotNumPair.toString() + " While current propsosed ballot number is " + this.proposeBallotNumPair.toString());
		}
		
	}
	
	
	
	/** onreceiveAccept
	 * 
	 */
	public synchronized void onreceiveAccept(BallotPair ballotNumPair, String value) {
		this.logger.write("PaxosID:" + this.id + "RECVD ACCEPT MSG WITH BALLOTNUMBER " + ballotNumPair.toString() + " AND VALUE " + value); 
		AcceptedBallotNumAndValue tempObj = new AcceptedBallotNumAndValue(ballotNumPair,value);
		Iterator<AcceptedBallotNumAndValue> itr = this.ListAcceptMsgsAndCounter.iterator();
		boolean increasedCount = false;
		while(itr.hasNext()) {
			AcceptedBallotNumAndValue itrObj = itr.next();
			if(itrObj.equalTo(tempObj)) {
				itrObj.numTimes = itrObj.numTimes + 1;
				increasedCount = true;
			}
		}
		if(!increasedCount) {
			this.ListAcceptMsgsAndCounter.add(tempObj);
		}
		
		itr = this.ListAcceptMsgsAndCounter.iterator();
		while(itr.hasNext()) {
			AcceptedBallotNumAndValue itrObj = itr.next();
			if(itrObj.numTimes >= this.numTotalServers - this.acceptableFailures) {
				this.logger.write("PaxosID:" + this.id + "RECVD ACCEPT MSG FROM ALL - ACCEPTABLE FAILURES WITH VALUE " + value);
				//Periodically send
				Msg.sendDecide(this.id,itrObj.r_acceptedValue); //broadcast it to all
				//decide to yourself
				this.onreceiveDecide(itrObj.r_acceptedValue);
			}
		}
		
		//ask about deciding - do we go for all new values
		if(ballotNumPair.compareTo(this.ownBallotNumPair) >= 0) {
			this.acceptedBallotNumPair = ballotNumPair;
			this.acceptedValue = value;
			Msg.sendAccept(this.id,ballotNumPair, value); //broadcast it to all
		}
	}
	
	/** onreceiveDecide
	 * 
	 */
	public synchronized void onreceiveDecide(String value) {
		this.logger.write("PaxosID:" + this.id + "RECVD DECIDE MSG WITH VALUE " + value);
		this.decidedValue = true;
		if(this.proposer) {
		if(!this.ValueToWrite.equals(value)) {
			//send fail to client
		}
		}
		//WritetoLog where logindex = this.paxosId and value = parameter
	}
	
}
