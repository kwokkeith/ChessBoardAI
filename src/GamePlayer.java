import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GamePlayer extends NanoHTTPD {
	
	private Agent agent;
	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	public GamePlayer(int port, Agent agent) throws IOException {
		super(port);
		this.agent = agent;
	}

	/**
	 * this method is called when a new match begins
	 */
	protected void commandStart(String msg) {
		// msg="(START <MATCH ID> <ROLE> <GAME DESCRIPTION> <STARTCLOCK> <PLAYCLOCK>)
		// e.g. msg="(START tictactoe1 white ((role white) (role black) ...) 1800 120)" means:
		//       - the current match is called "match0815"
		//       - your role is "white",
		//       - after at most 1800 seconds, you have to return from the commandStart method
		//       - for each move you have 120 seconds
		Matcher m=Pattern.compile("\\s*\\(\\s*start\\s+([^\\s]+)\\s+([^\\s]+)(.*)\\s([0-9]+)\\s+([0-9]+)\\s*\\)\\s*\\z", Pattern.DOTALL).matcher(msg);
		try {
			String role;
			int playclock, width, height;
			if (m.lookingAt()) {
				role = m.group(2);
				String gameDescription = m.group(3);
				playclock = Integer.parseInt(m.group(5));
				m=Pattern.compile("\\(\\s*width\\s+([0-9]+)\\s*\\)").matcher(gameDescription);
				if(m.find()){
					width = Integer.parseInt(m.group(1));
				} else {
					throw new Exception("Board width not found in game rules!");
				}
				m=Pattern.compile("\\(\\s*height\\s+([0-9]+)\\s*\\)").matcher(gameDescription);
				if(m.find()){
					height = Integer.parseInt(m.group(1));
				} else {
					throw new Exception("Board height not found in game rules!");
				}
				System.out.println("role: " + role + ", board: " + width + "x" + height + ", playclock: " + playclock);
			} else {
				throw new Exception("unrecognized message format:" + msg);
			}
			agent.init(role, width, height, playclock);
		} catch(Exception e) {
			System.err.println("Can not parse start message: " + e);
			System.exit(-1);
		}
	}

	/**
	 * this method is called once for each move
	 * @return the move of this player
	 */
	protected String commandPlay(String msg){
		// msg="(PLAY <MATCHID> <LASTMOVES>)"
		int[] coord = null;
		try{
			Matcher m=Pattern.compile("\\(\\s*move\\s+([0-9]+)\\s+([0-9]+)\\s+([0-9]+)\\s+([0-9]+)\\s*\\)", Pattern.DOTALL).matcher(msg);
			if (m.find()) {
				coord = new int[4];
				for (int i = 0; i<4; i++) {
					coord[i] = Integer.parseInt(m.group(i+1)); 
				}
			}
		}catch(Exception ex){
			System.err.println("Pattern to detect moves does not match!");
			ex.printStackTrace();
		}
		return agent.nextAction(coord);
	}

	/**
	 * this method is called if the match is over
	 */
	protected void commandStop(String msg){
		// msg="(STOP <MATCH ID> <JOINT MOVE>)
		System.out.println("game over");
		agent.cleanup();
	}

	/**
	 * this method is called if the match is aborted
	 */
	protected void commandAbort(String msg){
		// msg="(STOP <MATCH ID> <JOINT MOVE>)
		System.out.println("match aborted");
		agent.cleanup();
	}

	public Response serve( String uri, String method, Properties header, Properties parms, String data )
	{
		try{
			String response_string=null;
			if(data!=null){
				data=data.toLowerCase();
				System.out.print(dateFormat.format(Calendar.getInstance().getTime()));
				System.out.println(" Command: " + data);
				String command=getCommand(data);
				if(command==null){
					throw(new IllegalArgumentException("Unknown message format"));
				}else if(command.equals("start")){
					response_string="ready";
					commandStart(data);
				}else if(command.equals("play")){
					response_string=commandPlay(data);
/*				}else if(command.equals("replay")){
					response_string=commandReplay(data);*/
				}else if(command.equals("stop")){
					response_string="done";
					commandStop(data);
				}else if(command.equals("abort")){
					response_string="done";
					commandAbort(data);
				}else{
					throw(new IllegalArgumentException("Unknown command:"+command));
				}
			}else{
				throw(new IllegalArgumentException("Message is empty!"));
			}
			System.out.print(dateFormat.format(Calendar.getInstance().getTime()));
			System.out.println(" Response: "+response_string);
			if(response_string!=null && response_string.equals("")) response_string=null;
			return new Response( HTTP_OK, "text/acl", response_string );
		}catch(IllegalArgumentException ex){
			System.err.println(ex);
			ex.printStackTrace();
			return new Response( HTTP_BADREQUEST, "text/acl", "NIL" );
		}
	}

	private String getCommand(String msg){
		String cmd=null;
		try{
			Matcher m=Pattern.compile("\\s*\\(\\s*([^\\s]*)\\s").matcher(msg);
			if(m.lookingAt()){
				cmd=m.group(1);
			}
		}catch(Exception ex){
			System.err.println("Pattern to extract command did not match!");
			ex.printStackTrace();
		}
		return cmd;
	}

	public void waitForExit(){
		try {
			server_thread.join(); // wait for server thread to exit
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
