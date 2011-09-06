package com.softeng.chat;

import java.util.LinkedList;
import java.util.Queue;

import org.eclipse.jetty.continuation.Continuation;


/**
* This class represents a chatter in the chat room.
* For each Chatter object a name, age and login time is required.
* @author Sukhwinder Singh
*/
public class Chatter
{
	private String name = null;
	private String sex = null;
	private String comment = null;
	private String email = null;
	private long loginTime = -1;
	private long enteredInRoomAt = -1;
	private int age = -1;
	private Continuation continuation;
	private Queue<Message> messageQueue = new LinkedList<Message>();
	private Queue<Chatter> chatterQueue = new LinkedList<Chatter>();
	
	/**
	* Used to create a new Chatter object.
	* It acccepts a name age and time of his login.
	* @param name name of the chatter
	* @param age age of the chatter
	* @param loginTime time when user logged in. Got using Date.getTime(). Works as a timestamp.
	*/
	
	public Chatter(String name, String sex, long loginTime)
	{
		this.name = name;
		this.sex = sex;
		this.loginTime = loginTime;
	}	
	
	/**
	* returns name of the Chatter
	* @return java.lang.String
	*/
	public String getName()
	{
		return name;
	}

	/** returns sex of the Chatter
	* @return java.lang.String
	*/
	public String getSex()
	{
		return sex;
	}
	
	/** sets comments provided by the chatter.
	* @param comment Comment made by the chatter
	* @return void
	*/
	public void setComment(String comment)
	{
		this.comment = comment;
	}
	
	/** returns comments provided by the chatter
	* @return java.lang.String
	*/	
	public String getComment()
	{
		return comment;
	}
	
	/** sets age of the chatter
	* @param age Age of the chatter
	* @return void
	*/
	public void setAge(int age)
	{
		this.age=age;
	}
	
	/** returns age of the chatter
	* @return int
	*/
	public int getAge()
	{
		return age;
	}
	
	/** sets email of the chatter
	* @param email email address of the person
	* @return void
	*/
	public void setEmail(String email)
	{
		this.email = email;
	}
	
	/**
	* returns email of the user
	* @return String
	*/
	public String getEmail()
	{
		return email;
	}
	/**
	* returns time of user login in milliseconds got using Date.getTime()
	*/
	public long getLoginTime()
	{
		return loginTime;
	}

	/**
	* sets time in milliseconds when a user has entered into a particular room.
	* It is used to display only messages sent after a person has entered into a room.
	* @param long milliseconds got using Date.getTime()
	*/

	public void setEnteredInRoomAt( long enteredAt)
	{
		this.enteredInRoomAt = enteredAt;
	}
	/**
	* returns time in milliseconds when a user has entered into a particular room.
	* It is used to display only messages sent after a person has entered into a room.
	* @return long milliseconds got using Date.getTime()
	*/
	public long getEnteredInRoomAt()
	{
		return enteredInRoomAt;
	}
	
	public Continuation getContinuation()
	{
		return continuation;
	}
	
	public void setContinuation(Continuation pCon)
	{
		continuation = pCon;
	}
	
	public void setMessageQueue(Queue<Message> queue)
	{
		messageQueue = queue;
	}
	
	public Queue<Message> getMessageQueue()
	{
		return messageQueue;
	}
	
	public void setChatterQueue(Queue<Chatter> queue)
	{
		chatterQueue = queue;
	}
	
	public Queue<Chatter> getChatterQueue()
	{
		return chatterQueue;
	}
}