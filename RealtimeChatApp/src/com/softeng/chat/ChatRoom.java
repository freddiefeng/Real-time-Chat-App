package com.softeng.chat;

import java.util.Map;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Set;

/** This class represents a chat room in the Chat System
*@author Sukhwinder Singh(ssruprai@hotmail.com)
*/
public class ChatRoom
{
	/*
	* used to stroe name of the room
	*/
	
	private String name = null;
	/*
	* used to store description of the room
	*/
	private String description = null;
	
	/*
	* Map to store Chatter objects
	*/
	private Map chatters = new HashMap();
	/*
	* Linked list to store Message object
	*/
	private List messages = new LinkedList();
	
	/*
	* Used to set the maximum no of messages
	*/
	private int messages_size = 25;
	/**
	* This constructor takes a name and description
	* to create a new ChatRoom
	* @param name Name of the Room
	* @param descr Description of the Room
	*/
	public ChatRoom(String name, String descr)
	{
		this.name= name;
		this.description = descr;
	}
	
	/**
	* Returns name of the room
	* @return java.lang.String
	*/
	public String getName()
	{
		return name;
	}
	
	/**
	* Returns description of the room
	* @return java.lang.String
	*/
	public String getDescription()
	{
		return description;
	}
	
	/**
	* adds a Chatter object to list of Chatters
	* @param chatter Chatter object
	* @return void
	*/
	public synchronized void addChatter(Chatter chatter)
	{
		chatters.put(chatter.getName(), chatter);
	}
	/**
	* removes a Chatter object from list of Chatters
	* @param chatterName name of the chatter.
	* @return void
	*/
	public synchronized Object removeChatter(String chatterName)
	{
		return chatters.remove(chatterName);
	}
	
	/**
	* returns a Chatter object from chatters list.
	* @param chatterName name of the chatter
	* @return sukhwinder.chat.Chatter
	*/
	public Chatter getChatter(String chatterName)
	{
		return (Chatter)chatters.get(chatterName);
	}
	
	/**
	* checks whether a chatter exists or not
	* @param chatterName name of the chatter to check
	* @return boolean
	*/
	
	public boolean chatterExists(String chatterName)
	{
		return chatters.containsKey(chatterName);
	}
	
	/**
	* returns total number of chatters in this room
	* @return int
	*/
	public int getNoOfChatters()
	{
		return chatters.size();
	}
	
	/**
	* returns a Set containing all the Chatters in the room
	* @return java.util.Set
	*/
	public Set getChatters()
	{
		return chatters.entrySet();
	}
	
	/** returns an array containing all Chatter objects
	* @return sukhwinder.chat.Chatter[]
	*/
	public Chatter[] getChattersArray()
	{
		Chatter[] chattersArray = new Chatter[chatters.size()];
		Set chatters = getChatters();
		Iterator chattersit = chatters.iterator();
		int i = 0;
		while(chattersit.hasNext())
		{
			Map.Entry me = (Map.Entry)chattersit.next();
			String key = (String) me.getKey();
			chattersArray[i] = (Chatter)me.getValue();
			i++;
		}
		return chattersArray;
	}
	
	/** adds the message to the messages list
	* @param msg A Message Object
	* @return void
	*/
	public synchronized void addMessage(Message msg)
	{
		if(messages.size()==messages_size)
		{
			((LinkedList)messages).removeFirst();
		}
		messages.add(msg);
	}
	
	/**
	* returns a ListIterator object containing all the messages
	* @return java.util.ListIterator
	*/	
	public ListIterator getMessages()
	{
		return messages.listIterator();
	}

	/**
	* returns an array of messages sent after given time
	* @param afterTimeStamp Time in milliseconds.
	* @return array
	*/	
	public Message[] getMessages(long afterTimeStamp)
	{
		ListIterator li = messages.listIterator();
		List temp = new ArrayList();
		Message m;
		while (li.hasNext())
		{
			m = (Message)li.next();
			if (m.getTimeStamp() >= afterTimeStamp)
			{
				temp.add(m);
			}
		}
		Object o[] = temp.toArray();
		Message[] arr = new Message[o.length];
		for (int i = 0; i < arr.length; i++)
		{
			arr[i] = (Message)o[i];
		}
		return arr;
	}
	
	public Message[] getMessages(int afterNum)
	{
		int numOfMessages = messages.size();
		if (numOfMessages > 0 && afterNum < numOfMessages)
		{
			Message[] arr = new Message[numOfMessages - afterNum];
			
			for (int i = afterNum; i < numOfMessages; i++)
			{
				arr[i - afterNum] = (Message)messages.get(i);
			}
			
			return arr;
		}
		else
		{
			return null;
		}
	}

	/**
	* returns total number of messages in the messages List
	* @return int
	*/
	public int getNoOfMessages()
	{
		return messages.size();
	}
	
	/**
	* sets maxmium number of messages to this number.
	* @param size the maximum no of messages to hold at a time.
	* @return void
	*/
	public void setMaximumNoOfMessages(int size)
	{
		messages_size = size;
	}
	
	/**
	* returns maxmium number of messages set.
	* @return int
	*/
	public int getMaxiumNoOfMessages()
	{
		return messages_size;
	}
}