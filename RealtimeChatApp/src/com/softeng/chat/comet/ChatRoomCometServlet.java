package com.softeng.chat.comet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;

import com.softeng.chat.ChatRoom;
import com.softeng.chat.ChatRoomList;
import com.softeng.chat.Chatter;
import com.softeng.chat.Message;

/**
 * Servlet implementation class ChatRoomServlet
 */
public class ChatRoomCometServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private ChatRoom room;
	private int nMessages;
	private int nUsers;
	private int counter = 0;
	
	private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	
	public void init() throws ServletException
	{
		super.init();
		/*if (getServletConfig().getInitParameter("Testing").equalsIgnoreCase("true"))
		{
			executor.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					System.out.println("Running Server Message Sending");
					
					ChatRoom room = ((ChatRoomList)getServletContext().getAttribute("chatroomlist")).getRoom("StartUp");
					Chatter[] chatterArray = room.getChattersArray();
					
					// Store message on the server side
					Date date = new Date(System.currentTimeMillis());
					Message msg = new Message("Comet Server", "Test Message " + counter, date.getTime());
					room.addMessage(msg);
					
					for (Chatter chatter : chatterArray)
					{
						synchronized(chatter)
						{
							chatter.getMessageQueue().add(msg);
							if (chatter.getContinuation() != null)
							{
								chatter.getContinuation().resume();
								chatter.setContinuation(null);
							}
						}
					}
					
					long millis = System.currentTimeMillis();
					System.out.println(msg.getMessage() + " " + String.format("%d sec, %d millis",      
							TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MILLISECONDS.toMinutes(millis)*60,     
							TimeUnit.MILLISECONDS.toMillis(millis) - TimeUnit.MILLISECONDS.toSeconds(millis)*1000 ));
					counter++;
				}
			}, 40, 3, TimeUnit.SECONDS);
		}*/
	}
	
	public enum Function {   
		getState("getState"),   
		updateChat("updateChat"),   
		send("send"),   
		getUserList("getUserList"),
		poll("poll");
		
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
    public ChatRoomCometServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (request.getParameter("function") == null)
		{
			getServletContext().getNamedDispatcher("default").forward(request, response);
		}
		else
		{
			doPost(request, response);
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
				/*case getState:
					int numOfMessages = room.getNoOfMessages();
					
					out.print("{ \"state\" : " + numOfMessages + " }");
					break;*/
					
				case send:
					//Sanitize the message
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
					
					Chatter[] chatterArray = room.getChattersArray();
					
					// Store message on the server side
					Date date = new Date(System.currentTimeMillis());
					Message msg = new Message(nickname, message, date.getTime());
					room.addMessage(msg);
					
					for (Chatter chatter : chatterArray)
					{
						synchronized(chatter)
						{
							chatter.getMessageQueue().add(msg);
							if (chatter.getContinuation() != null)
							{
								chatter.getContinuation().resume();
								chatter.setContinuation(null);
							}
						}
					}
					break;
					
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
					
					Chatter[] chattersArray = room.getChattersArray();
					Chatter currentChatter = roomList.getRegisteredChatter(nickname);
					for (Chatter chatter : chattersArray)
					{
						synchronized(chatter)
						{
							if (chatter != currentChatter)
							{
								chatter.getChatterQueue().add(currentChatter);
							}
						}
					}
					
					out.print("{ \"numOfUsers\" : ");
					out.print(room.getNoOfChatters() + ", ");
					
					out.print("\"userlist\" : [");
					
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
					
				case poll:
					Chatter chatter = room.getChatter(nickname);
					
					synchronized(chatter)
					{
						if (chatter.getMessageQueue().size() > 0 || chatter.getChatterQueue().size() > 0)
						{
							Queue<Message> mQueue = chatter.getMessageQueue();
							Queue<Chatter> cQueue = chatter.getChatterQueue();
							
							response.setContentType("text/json;charset=utf-8");
							out.print("{ ");

							//Message newMessageObject = (mQueue.size() > 0) ? mQueue.poll() : null;
							//String newMessage = " \"" + newMessageObject.getChatterName() + " said " + newMessageObject.getMessage() + " @ " + newMessageObject.getTimeStamp() + "\" ";
							
							if (mQueue.size() > 0)
							{
								out.print("\"text\" : [");
								Message newMessageObject = mQueue.poll();
								String newMessage = " \"" + newMessageObject.getChatterName() + " said " + newMessageObject.getMessage() + " @ " + newMessageObject.getTimeStamp() + "\" ";
								out.print(newMessage);
								out.print("] ");
								if (cQueue.size() > 0)
									out.print(", ");
							}
							
							//Chatter newChatterObject = (cQueue.size() > 0) ? cQueue.poll() : null;
							//String newChatter = " \"" + newChatterObject.getName() + "\" ";
							
							if (cQueue.size() > 0)
							{
								out.print("\"userlist\" : [");
								Chatter newChatterObject = cQueue.poll();
								String newChatter = " \"" + newChatterObject.getName() + "\" ";
								out.print(newChatter);
								out.print("]");
							}
							
							out.print(" }");
						}
						else
						{
							Continuation continuation = ContinuationSupport.getContinuation(request);
							if (continuation.isInitial())
							{
								continuation.setTimeout(20000);
								continuation.suspend();
								chatter.setContinuation(continuation);
							}
							else
							{
								response.setContentType("text/json;charset=utf-8");
								out.print("{ \"method\" : \"poll\" }");
							}
						}
					}
					
				/*case updateChat:
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
					
					break;*/
					
				default:
					break;
			}
			
			//out.close();
		}
	}

}
