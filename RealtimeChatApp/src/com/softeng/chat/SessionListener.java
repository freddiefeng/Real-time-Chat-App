package com.softeng.chat;

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletContext;

/**
It is used by Chat Application for listening to session events.
* @author Sukhwinder Singh
*/
public class SessionListener implements HttpSessionAttributeListener 
{
	public void attributeAdded(HttpSessionBindingEvent hsbe)
	{
		//System.out.println("Attribute has been bound");
	}

	public void attributeReplaced(HttpSessionBindingEvent hsbe)
	{
		//System.out.println("Attribute has been replaced");
	}

	/** This is the method we are interested in. It deletes a user from this list of users when his session
		expires.
	*/
	public void attributeRemoved(HttpSessionBindingEvent hsbe)
	{
		String name = hsbe.getName();
		HttpSession session = hsbe.getSession();
		if ("nickname".equalsIgnoreCase(name))
		{
			String nickname = (String)hsbe.getValue();
			if (nickname != null)
			{
				ServletContext application = session.getServletContext();
				if (application != null)
				{
					Object o = application.getAttribute("chatroomlist");		
					if (o != null)
					{
						ChatRoomList roomList = (ChatRoomList)o;
						ChatRoom room = roomList.getRoomOfChatter(nickname);
						if (room != null)
						{
							Object chatter = room.removeChatter(nickname);
							if (chatter != null)
							{
								String n = ((Chatter)chatter).getName();
							}
							
						}
					}
				}
				else
				{
					System.out.println("ServletContext is null");
				}					
			}
		}		
	}
}