package com.softeng.chat;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;

/**
* This class is used to manipulate and store ChatRoom objects.
* It provides methods to store and retrieve ChatRoom objects
* in this <code>ChatRoomList</code>.
*@author Sukhwinder Singh(ssruprai@hotmail.com)
*/
public class ChatRoomList
{
	/**
	* Stores all the ChatRoom objects
	*/
	private Map roomList;
	/**
	*/
	public ChatRoomList()
	{
		roomList = new HashMap();
	}
	/**
	* adds new chat room object to a list of Rooms.
	* @param room ChatRoom object
	* @return void
	*/
	public synchronized void addRoom(ChatRoom room)
	{
		roomList.put(room.getName(), room);
	}
	
	/**
	* Used to remove a ChatRoom object from the
	* list of ChatRooms.
	* @param name is a String object is the name of the
	* room to be removed from the list of rooms.
	* @return void
	*/
	public synchronized void removeRoom(String name)
	{
		roomList.remove(name);
	}
	
	/** Returns a ChatRoom object
	* @param name is the name of the ChatRoom object to be returned.
	* @return ChatRoom object.
	*/
	public ChatRoom getRoom(String name)
	{
		return (ChatRoom) roomList.get(name);
	}
	/** Finds the room of chatter having this name.
	* @param name is the name of the Chatter object.
	* @return ChatRoom object.
	*/
	public ChatRoom getRoomOfChatter(String name)
	{
		ChatRoom[] rooms = this.getRoomListArray();
		for (int i = 0; i < rooms.length; i++)
		{
			boolean chatterexists = rooms[i].chatterExists(name.toLowerCase());
			if (chatterexists)
			{
				return rooms[i];
			}
		}
		return null;
	}
	/** Returns a Set containing all the ChatRoom objects
	* @return Set
	*/
	
	public Set getRoomList()
	{
		return roomList.entrySet();
	}
	
	/** returns an array containing all ChatRoom objects
	* @return sukhwinder.chat.ChatRoom[]
	*/
	public ChatRoom[] getRoomListArray()
	{
		ChatRoom[] roomListArray = new ChatRoom[roomList.size()];
		Set roomlist = getRoomList();
		Iterator roomlistit = roomlist.iterator();
		int i = 0;
		while(roomlistit.hasNext())
		{
			Map.Entry me = (Map.Entry)roomlistit.next();
			String key = (String) me.getKey();
			roomListArray[i] = (ChatRoom)me.getValue();
			i++;
		}
		return roomListArray;
	}
	
	/**
	* searches each ChatRoom for existance of a chatter.
	* @param nickname Name of the chatter to find.
	* @return boolean
	*/
	public boolean chatterExists(String nickname)
	{
		boolean chatterexists = false;
		ChatRoom[] rooms = this.getRoomListArray();
		for (int i = 0; i < rooms.length; i++)
		{
			chatterexists = rooms[i].chatterExists(nickname);
			if (chatterexists)
			{
				break;
			}
		}
		return chatterexists;
	}
	
	public Chatter getRegisteredChatter(String nickName)
	{
		ChatRoom[] rooms = this.getRoomListArray();
		for (int i = 0; i < rooms.length; i++)
		{
			Chatter chatter = rooms[i].getChatter(nickName);
			if (chatter != null)
				return chatter;
		}
		return null;
	}
}