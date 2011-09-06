var state = 0;
var mes;
var file;
var numOfUsers = 0;
var roomid;
var usernameid;

function Chat (filetxt) {
	file = filetxt;
	this.init = chatInit;
    this.update = updateChat;
    this.send = sendChat;
	this.getState = getStateOfChat;
	this.trim = trimstr;
	this.getUsers = getuserlist;
}

function chatInit(room, username){
	getStateOfChat(room, username);
}

function wait(){
	updateChat();
}

$.ajaxSetup({
    cache: false // for ie
});

//gets the state of the chat
function getStateOfChat(room, username){
	
	roomid = room;
	usernameid = username;
	
	 $.ajax({
		   type: "POST",
		   url: "../ChatRoomServlet",
		   data: {  
		   			'function': 'getState',
					'file': file,
					'nickname': username,
					'rn': room
					},
		    dataType: "json",
		
		   success: function(data){
			   //state = data.state-5;
			   state = data.state;
			   updateChat(roomid, usernameid);
		   },
		});
}
		 
//Updates the chat
function updateChat(room, username){
	   
	console.log("ready ot update chat");
	   
     $.ajax({
     
        type: "GET",
        url: "../ChatRoomServlet",
        data: {  
            'state': state,
            'file' : file,
            'function' : 'updateChat',
            'nickname': username,
            'rn': room
            },
        dataType: "json",
        cache: false,
        success: function(data) {
        
            if (data.text != null) {
            	console.log(data.text);
            	var d = new Date();
            	console.log(d.toUTCString() + " " + d.getMilliseconds());
            	
                for (var i = 0; i < data.text.length; i++) {  
                $('#chat-area').append($("<p>"+ data.text[i] +"</p>"));
            }
            
            document.getElementById('chat-area').scrollTop = document.getElementById('chat-area').scrollHeight;
        
        }  
        
        instanse = false;
        state = data.state;
        console.log(state = data.state);
        setTimeout('updateChat(roomid, usernameid)', 1000);
        
        },
    });
}

//send the message
function sendChat(message, nickname, room) {       
   
     $.ajax({
		   type: "POST",
		   url: "../ChatRoomServlet",
		   data: {  
		   			'function': 'send',
					'message': message,
					'nickname': nickname,
					'file': file,
					'rn': room
					},
		   dataType: "json",
		   success: function(data){
			   //setTimeout('updateChat(roomid, usernameid)', 10000);
		   },
		});

}

function trimstr(s, limit) {
    return s.substring(0, limit);
} 

function getuserlist(room, username) {

	roomid = room;
	usernameid = username;
	
	 $.ajax({
        type: "GET",
        url: "../ChatRoomServlet",
        data: {  
        		'rn' : room,
        		'nickname': username,
        		'current' : numOfUsers,
        		'function' : 'getuserlist'
        		
        		},
        dataType: "json",
        cache: false,
        success: function(data) {
        
        	if (numOfUsers != data.numOfUsers) {
        		numOfUsers = data.numOfUsers;
        		var list = "<li class='head'>Current Chatters</li>";
        		for (var i = 0; i < data.userlist.length; i++) {  
                   list += "<li>"+ data.userlist[i] +"</li>";
                }
        		$('#userlist').html($("<ul>"+ list +"</ul>"));
        	}
        	
            //setTimeout('getuserlist(roomid, usernameid)', 1000);
           
        },
    });
	
}