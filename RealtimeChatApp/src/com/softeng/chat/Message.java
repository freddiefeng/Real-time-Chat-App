package com.softeng.chat;

/**
Represents a Message sent by a user.
*/
public class Message
{
	/**
	* String used to store the name of a chatter
	*@author Sukhwinder Singh(ssruprai@hotmail.com)
	*/
	private String chatterName = null;
	/**
	* String containing message
	*/
	private String message = null;

	/**
	* long containing the time when message was delivered
	*/
	private long timeStamp;
	/**
	* This constructor accepts a name of the chatterand the message.
	* @param name Name of the chatter
	* @param message message of the chatter
	* @param timeStamp time of the message
	*/
	public Message(String name, String message, long timeStamp)
	{
		this.chatterName = name;
		this.message= message;
		this.timeStamp = timeStamp;
	}
	
	/**
	* Returns name of the Chatter
	* @return String
	*/
	public String getChatterName()
	{
		return chatterName;
	}
	
	/**
	* Returns message of the chatter
	* @return String
	*/
	public String getMessage()
	{
		return message;
	}
	/**
	* Returns time of the message
	* @return long
	*/
	public long getTimeStamp()
	{
		return timeStamp;
	}
}