var room;
var user;
var location;
var ws;

function Chat (filetext) {
	
	this.init = chatInt;
	this.send = Chat_WSSend;
	this.getUsers = function(roomname, name){ return null;};
}

function chatInt(roomname, name)
{
	room = roomname;
	user = name;
	var location = "ws://localhost:8080/RealtimeChatApp/ChatWebSocketServlet";
	
	if ('WebSocket' in window) {
        ws = new WebSocket(location);
    }
    else if ('MozWebSocket' in window) {
        ws = new MozWebSocket(location);
    }
	
	ws.onopen = function ()
	{
		var messageObject = {method : "AddUser", room : room, users : [name], messages : []};
		var jsonString = JSON.stringify(messageObject);
		ws.send(jsonString);
		//ws.send("test");
	}

	ws.onmessage = function (m)
	{
		if (m.data)
		{
			var jsonObject = JSON.parse(m.data);
			if (jsonObject.method == "SetUp")
			{
	    		var list = "<li class='head'>Current Chatters</li>";
	    		for (var i = 0; i < jsonObject.users.length; i++) {  
	               list += "<li>"+ jsonObject.users[i] +"</li>";
	            }
	    		$('#userlist').html($("<ul>"+ list +"</ul>"));
			}
			else if (jsonObject.method == "RemoveUser")
			{
				var userList = document.getElementById("userlist").getElementsByTabName("UL")[0];
				
				for (var j = 0; j < userList.children.length; j++)
				{
					if (userList.children[j].firstChild.data == jsonObject.users[0])
					{
						userList.removeChild(userList.children[j]);
						break;
					}
				}
			}
			else if (jsonObject.method == "AddMessage")
			{
				console.log(jsonObject.messages);
            	var d = new Date();
            	console.log(d.toUTCString() + " " + d.getMilliseconds());
            	
				if (jsonObject.messages.length != 0) 
				{
		            	//console.log(data.text);
		                for (var i = 0; i < jsonObject.messages.length; i++) {  
		                $('#chat-area').append($("<p>"+ jsonObject.messages[i] +"</p>"));
		            }
		            
		            document.getElementById('chat-area').scrollTop = document.getElementById('chat-area').scrollHeight;
				}
			}
			else if (jsonObject.method == "AddUser")
			{
				var userList = document.getElementById("userlist").getElementsByTagName("UL")[0];
				var newListItem = document.createElement("LI");
				
				newListItem.textContent = jsonObject.messages[0];
				userList.appendChild(newListItem);
			}
		}
	}

	ws.onclose = function ()
	{
		var messageObject = {method : "RemoveUser", room : room, users : [name], messages : []};
		ws.send(JSON.stringify(messageObject));
	}

	ws.onerror = function (e)
	{
		alert(e);
	}
}

function Chat_WSSend(text, name, roomname)
{
	var messageObject = {method : "AddMessage", room : roomname, users : [name], messages : [text]};
	ws.send(JSON.stringify(messageObject));
}



updateChat = function (roomid, usernameid)
{
	return null;
}