package com.softeng.chat.websocket;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketHandler;

public class ChatWebSocketHandler extends WebSocketHandler {

	private final Set<ChatWebSocket> webSockets = new CopyOnWriteArraySet<ChatWebSocket>();

	public WebSocket doWebSocketConnect(HttpServletRequest request,
			String protocol) {
		return new ChatWebSocket();
	}

	private class ChatWebSocket implements WebSocket.OnTextMessage {

		private Connection connection;

		public void onOpen(Connection connection) {
			// Client (Browser) WebSockets has opened a connection.
			// 1) Store the opened connection
			this.connection = connection;
			// 2) Add ChatWebSocket in the global list of ChatWebSocket
			// instances
			// instance.
			webSockets.add(this);
		}

		public void onMessage(String data) {
			// Loop for each instance of ChatWebSocket to send message server to
			// each client WebSockets.
			try {
				for (ChatWebSocket webSocket : webSockets) {
					// send a message to the current client WebSocket.
					webSocket.connection.sendMessage(data);
				}
			} catch (IOException x) {
				// Error was detected, close the ChatWebSocket client side
				this.connection.disconnect();
			}

		}

		public void onClose(int closeCode, String message) {
			// Remove ChatWebSocket in the global list of ChatWebSocket
			// instance.
			webSockets.remove(this);
		}
	}
}