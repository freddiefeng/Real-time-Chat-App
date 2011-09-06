package com.softeng.chat.websocket;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;

/**
 * Application Lifecycle Listener implementation class ChatServerServletContextListener
 *
 */
public class ChatServerServletContextListener implements ServletContextListener {

	private Server server = null;
	
    /**
     * Default constructor. 
     */
    public ChatServerServletContextListener() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg0) {
        // TODO Auto-generated method stub
    	try
    	{
    		this.server = new Server(8081);
    		
    		ChatWebSocketHandler chatWebSocketHandler = new ChatWebSocketHandler();
			server.setHandler(chatWebSocketHandler);
			chatWebSocketHandler.setHandler(new DefaultHandler());
    		
    		server.start();
    		//server.join();
    	}
    	catch (Throwable e)
    	{
    		e.printStackTrace();
    	}
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0) {
        // TODO Auto-generated method stub
    	if (server != null)
    	{
    		try
    		{
    			server.stop();
    		}
    		catch (Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
    }
	
}
