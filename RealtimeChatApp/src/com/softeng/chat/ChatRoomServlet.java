package com.softeng.chat;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ChatRoomServlet
 */
public class ChatRoomServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private ChatRoom room;
	private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	private int counter = 0;
	
	public enum Function {   
		getState("getState"),   
		updateChat("updateChat"),   
		send("send"),   
		getUserList("getUserList");
		
		private String text;    
		
		Function(String text) {     
			this.text = text;   
		}    
		
		public String getText() {     
			return this.text;   
		}
		
		public static Function fromString(String text) 
		{     
			if (text != null) 
			{       
				for (Function b : Function.values()) 
				{         
					if (text.equalsIgnoreCase(b.text)) {           
						return b;       
					}     
				}
			}
			return null;
		} 
	}

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ChatRoomServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void init() throws ServletException
    {
    	super.init();
		if (getServletConfig().getInitParameter("Testing").equalsIgnoreCase("true"))
		{
			executor.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					System.out.println("Running Server Message Sending");
					
					ChatRoom room = ((ChatRoomList)getServletContext().getAttribute("chatroomlist")).getRoom("StartUp");
					//Chatter[] chatterArray = room.getChattersArray();
					
					Date now = new Date();
					
					// Store message on the server side
					Message msg = new Message("Ajax Server", "Test Message " + counter, now.getTime());
					room.addMessage(msg);
					
					long millis = System.currentTimeMillis();
					System.out.println(msg.getMessage() + " " + String.format("%d sec, %d millis",      
							TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MILLISECONDS.toMinutes(millis)*60,     
							TimeUnit.MILLISECONDS.toMillis(millis) - TimeUnit.MILLISECONDS.toSeconds(millis)*1000 ));
					counter++;
				}
			}, 3, 2, TimeUnit.SECONDS);
		}
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		
		String nickname = request.getParameter("nickname");
		nickname = nickname.trim().toLowerCase();
		String roomName = request.getParameter("rn");
		ChatRoom room = ((ChatRoomList)getServletContext().getAttribute("chatroomlist")).getRoom(roomName);
		
		if (nickname != null && room != null)
		{
			PrintWriter out = response.getWriter();
			String functionName = request.getParameter("function");
			switch (Function.fromString(functionName))
			{
				case getUserList:
					ChatRoomList roomList = (ChatRoomList)getServletContext().getAttribute("chatroomlist");
					if (!roomList.chatterExists(nickname))
					{
						response.sendRedirect(request.getContextPath() + "/index.html");
					}
					else if (!room.chatterExists(nickname))
					{
						room.addChatter(roomList.getRegisteredChatter(nickname));
					}
					
					out.print("{ \"numOfUsers\" : ");
					out.print(room.getNoOfChatters() + ", ");
					
					out.print("\"userlist\" : [");
					
					Chatter[] chattersArray = room.getChattersArray();
					int i = 0;
					for (; i < chattersArray.length - 1; i++)
					{
						Chatter chatter = chattersArray[i];
						out.print(" \"" + chatter.getName() + "\" ");
						out.print(",");
					}
					out.print(" \"" + chattersArray[i].getName() + "\" ");
					
					out.print("] }");
					break;
				case updateChat:
					int numOfClientMessages = Integer.parseInt(request.getParameter("state"));
					
					Message[] msgs = room.getMessages(numOfClientMessages);
					out.print("{ \"text\" : [");
					if (msgs != null)
					{
						int j = 0;
						for (; j < msgs.length - 1; j++)
						{
							out.print(" \"" + msgs[j].getChatterName() + " said " + msgs[j].getMessage() + " @ " + msgs[j].getTimeStamp() + "\" ");
							out.print(", ");
						}
						System.out.println("index: " + j);
						out.print(" \"" + msgs[j].getChatterName() + " said " + msgs[j].getMessage() + " @ " + msgs[j].getTimeStamp() + "\" ");
					}
					out.print("],");
					out.print(" \"state\" : " + room.getNoOfMessages() + "}");
					
					break;
				default:
					break;
			}
			out.close();
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		
		String nickname = request.getParameter("nickname");
		nickname = nickname.trim().toLowerCase();
		String roomName = request.getParameter("rn");
		ChatRoom room = ((ChatRoomList)getServletContext().getAttribute("chatroomlist")).getRoom(roomName);
		
		if (nickname != null && room != null)
		{
			PrintWriter out = response.getWriter();
			String functionName = request.getParameter("function");
			
			switch (Function.fromString(functionName))
			{
				case getState:
					int numOfMessages = room.getNoOfMessages();
					out.print("{ \"state\" : " + numOfMessages + " }");
					break;
				case send:
					String message = request.getParameter("message");
					
					Pattern[] patterns = new Pattern[5];
					patterns[0] = Pattern.compile(":\\)");
					patterns[1] = Pattern.compile(":D");
					patterns[2] = Pattern.compile(":p");
					patterns[3] = Pattern.compile(":P");
					patterns[4] = Pattern.compile(":\\(");
					
					String[] replacements = {"<img src='smiles/smile.gif'/>", "<img src='smiles/bigsmile.png'/>", "<img src='smiles/tongue.png'/>", "<img src='smiles/tongue.png'/>", "<img src='smiles/sad.png'/>"};
					String reg_exURL = "(http|https|ftp|ftps)\\:\\/\\/[a-zA-Z0-9\\-\\.]+.[a-zA-Z]{2,3}(\\/\\S*)?";
					
					String blankexp = "\\n";
					message = message.replaceAll(blankexp, "");
					
					if (!Pattern.compile(blankexp).matcher(message).matches())
					{
						Pattern.compile(blankexp).matcher(message).replaceAll("");
						for (int i = 0; i < patterns.length; i++)
						{
							patterns[i].matcher(message).replaceAll(replacements[i]);
						}
					}
					
					Date date = new Date(System.currentTimeMillis());
					Message msg = new Message(nickname, message, date.getTime());
					room.addMessage(msg);
					break;
				default:
					break;
			}
			
			out.close();
		}
	}

}
