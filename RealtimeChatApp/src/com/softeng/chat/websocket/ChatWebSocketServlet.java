package com.softeng.chat.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.softeng.chat.ChatRoom;
import com.softeng.chat.ChatRoomList;
import com.softeng.chat.Chatter;
import com.softeng.chat.Message;

public class ChatWebSocketServlet extends WebSocketServlet {
	private static final long serialVersionUID = -7289719281366784056L;
	public static String newLine = System.getProperty("line.separator");
	
	private final Set<ChatWebSocket> _members = new CopyOnWriteArraySet<ChatWebSocket>();
	private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	
	private int counter = 0;
	
	@Override
	public void init() throws ServletException {
		super.init();
		/*executor.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				System.out.println("Running Server Message Sending");
				for(ChatWebSocket member : _members){
					System.out.println("Trying to send to Member!");
					if(member.isOpen()){
						System.out.println("Sending!");
						try {
							member.sendMessage("Sending a Message to you Guys! "+new Date()+newLine);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}, 2, 2, TimeUnit.SECONDS);*/
		if (getServletConfig().getInitParameter("Testing").equalsIgnoreCase("true"))
		{
			executor.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					System.out.println("Running Server Message Sending");
					
					ChatRoom room = ((ChatRoomList)getServletContext().getAttribute("chatroomlist")).getRoom("StartUp");
					
					// Store message on the server side
					Date date = new Date(System.currentTimeMillis());
					Message msg = new Message("WebSocket Server", "Test Message " + counter, date.getTime());
					room.addMessage(msg);
					
					String sMessage = "Server" + " said " + "Test Message" + " @ " + msg.getTimeStamp();
					
					MessageObject outMsgObj = new MessageObject();
					outMsgObj.setMethod("AddMessage");
					outMsgObj.setRoom(room.getName());
					outMsgObj.addMessages(sMessage);
					
					String jsonString = (new Gson()).toJson(outMsgObj);
					
					for(ChatWebSocket member : _members){
						System.out.println("Trying to send AddMessage to Member!");
						if(member.isOpen()){
							System.out.println("Sending!");
							try {
								member.sendMessage(jsonString);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					
					long millis = System.currentTimeMillis();
					System.out.println(msg.getMessage() + " " + String.format("%d sec, %d millis",      
							TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MILLISECONDS.toMinutes(millis)*60,     
							TimeUnit.MILLISECONDS.toMillis(millis) - TimeUnit.MILLISECONDS.toSeconds(millis)*1000 ));
					counter++;
				}
			}, 3, 2, TimeUnit.SECONDS);
		}
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		getServletContext().getNamedDispatcher("default").forward(request,
				response);
	}

	public WebSocket doWebSocketConnect(HttpServletRequest request,
			String protocol) {
		return new ChatWebSocket();
	}

	class ChatWebSocket implements WebSocket.OnTextMessage {
		private Connection _connection;
		
		@Override
		public void onClose(int closeCode, String message) {
			_members.remove(this);
		}
		
		public void sendMessage(String data) throws IOException {
			_connection.sendMessage(data);
		}
		
		@Override
		public void onMessage(String data) {
					
			Gson gson = new Gson();
			
			MessageObject msgObj = gson.fromJson(data, MessageObject.class);
			String sMethod = msgObj.getMethod();
			
			String nickname = msgObj.getUsers().get(0);
			nickname = nickname.trim().toLowerCase();
			String roomName = msgObj.getRoom();
			ChatRoom room = ((ChatRoomList)getServletContext().getAttribute("chatroomlist")).getRoom(roomName);
			
			if (sMethod.equals("AddUser"))
			{
				AddUser(nickname, room, gson);
			}
			else if (sMethod.equals("AddMessage"))
			{
				AddMessage(nickname, room, msgObj, gson);
			}
			else if (sMethod.equals("RemoveUser"))
			{
				RemoveUser(nickname, room, gson);
			}
			
			System.out.println("Received: "+data);
		}
		
		public boolean isOpen() {
			return _connection.isOpen();
		}

		@Override
		public void onOpen(Connection connection) {
			_members.add(this);
			_connection = connection;
			try {
				connection.sendMessage("Server received Web Socket upgrade and added it to Receiver List.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		private void AddUser(String nickname, ChatRoom room, Gson gson)
		{
			Chatter[] chatterArray = room.getChattersArray();
			
			// Send this user all the user names
			MessageObject msgObj = new MessageObject();
			msgObj.setMethod("SetUp");
			msgObj.setRoom(room.getName());
			for (int i = 0; i < chatterArray.length; i++)
			{
				msgObj.addUsers(chatterArray[i].getName());
			}
			
			String jsonString = gson.toJson(msgObj);
			
			try {
				if (_connection.isOpen())
				{
					sendMessage(jsonString);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// Send all the other users this user name
			msgObj = new MessageObject();
			msgObj.setMethod("AddUser");
			msgObj.setRoom(room.getName());
			msgObj.addUsers(nickname);
			
			jsonString = gson.toJson(msgObj);
			
			for(ChatWebSocket member : _members){
				System.out.println("Trying to send to Member except for myself!");
				if(member.isOpen() && member != this){
					System.out.println("Sending!");
					try {
						member.sendMessage(jsonString);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		private void AddMessage (String nickname, ChatRoom room, MessageObject inMsgObj, Gson gson)
		{
			String sMessage = inMsgObj.getMessages().get(0);
			sMessage = sMessage.replaceAll("\\n", "");
			
			Date date = new Date(System.currentTimeMillis());
			Message sMsgObj = new Message(nickname, sMessage, date.getTime());
			room.addMessage(sMsgObj);
			
			sMessage = nickname + " said " + sMessage + " @ " + sMsgObj.getTimeStamp();
			
			MessageObject outMsgObj = new MessageObject();
			outMsgObj.setMethod("AddMessage");
			outMsgObj.setRoom(room.getName());
			outMsgObj.addMessages(sMessage);
			
			String jsonString = gson.toJson(outMsgObj);
			
			for(ChatWebSocket member : _members){
				System.out.println("Trying to send AddMessage to Member!");
				if(member.isOpen()){
					System.out.println("Sending!");
					try {
						member.sendMessage(jsonString);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		private void RemoveUser (String nickname, ChatRoom room, Gson gson)
		{
			room.removeChatter(nickname);
			
			MessageObject msgObj = new MessageObject();
			msgObj.setMethod("RemoveUser");
			msgObj.setRoom(room.getName());
			msgObj.addUsers(nickname);
			
			String jsonString = gson.toJson(msgObj);
			
			for(ChatWebSocket member : _members){
				System.out.println("Trying to send RemoveUser to Member!");
				if(member.isOpen()){
					System.out.println("Sending!");
					try {
						member.sendMessage(jsonString);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	class MessageObject {
		
		private String method;
		private String room;
		private List<String> users = new ArrayList<String>();
		private List<String> messages = new ArrayList<String>();
		
		public String getMethod()
		{
			return method;
		}
		
		public void setMethod(String pFunction)
		{
			method = pFunction;
		}
		
		public String getRoom()
		{
			return room;
		}
		
		public void setRoom(String pRoom)
		{
			room = pRoom;
		}
		
		public void addUsers(String userName)
		{
			users.add(userName);
		}
		
		public List<String> getUsers()
		{
			return users;
		}
		
		public List<String> getMessages()
		{
			return messages;
		}
		
		public void addMessages(String message)
		{
			messages.add(message);
		}
		
		public String toString()
		{
			StringBuilder strBuilder = new StringBuilder();
			
			strBuilder.append("MessageObject [ method =");
			strBuilder.append(method + ", users = ");
			for (String user : users)
			{
				strBuilder.append(user + " ");
			}
			strBuilder.append(", messages = ");
			for (String message : messages)
			{
				strBuilder.append(message + " ");
			}
			strBuilder.append("] ");
			
			return strBuilder.toString();
		}
	}
}
