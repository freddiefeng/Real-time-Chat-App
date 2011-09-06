var spinner_count = 0;

function startTest()
{
	var data = { x : 50, y : 50, size : 20, degrees : 30 };
	var degrees = data.degrees;
	var limit = 200;
	
	buildMultipleSpinner(200);
	
	function buildMultipleSpinner(n)
	{
		for (var i = 0; i < n ; i ++)
		{
			setTimeout(function()
			{
				buildSpinner(data);
				i++;
				document.getElementById("spinner_count").firstChild.data = i;
			}, i*100);
		}
		
		// so I can kill it later
		window.canvasTimer = setInterval(draw, 1000/degrees); 
		/*window.setInterval(function()
				{
					var text = $('#sendie').val();
				    var maxLength = $('#sendie').attr("maxlength");  
				    var length = text.length; 
				     
				    if (length <= maxLength + 1) {  
				        chat.send(text, name, roomname);	
				        $('#sendie').val("");
				    } else {
				        $('#sendie').val(text.substring(0, maxLength));
				    }
				}, 1000);*/
	}
	
	
	function buildSpinner(data) {
	  
	  var canvas = document.createElement('canvas');
	  canvas.height = 200;
	  canvas.width = 300;
	  document.querySelector('article').appendChild(canvas);
	  canvas.i = 0;
	  //var ctx = canvas.getContext("2d");
	  
	  canvas.degreesList = [];
	  var j = 0;
	
	  for (j = 0; j < degrees; j++) {
		canvas.degreesList.push(j);
	  }
	
	  
	} 
	
	function reset(canvas) {
	canvas.getContext("2d").clearRect(0,0,100,100); // clear canvas
	
	var left = canvas.degreesList.slice(0, 1);
	var right = canvas.degreesList.slice(1, canvas.degreesList.length);
	canvas.degreesList = right.concat(left);
	}
	  
	function draw() 
	{
		var canvasCollection = document.getElementsByTagName('canvas');
		var textArea = document.getElementById("sendie");
		
		for (var i = 0; i < canvasCollection.length; i++)
		{
			var canvas = canvasCollection[i];
			var ctx = canvas.getContext("2d");
			var c, s, e;
	
			var d = 0;
	
			if (canvas.i == 0) {
			  reset(canvas);
			  
				/*var text = $('#sendie').val();
			    var maxLength = $('#sendie').attr("maxlength");  
			    var length = text.length; 
			     
			    if (length <= maxLength + 1) {  
			        chat.send(text, name, roomname);	
			        $('#sendie').val("");
			    } else {
			        $('#sendie').val(text.substring(0, maxLength));
			    }*/
			  
			}
	
			ctx.save();
	
			d = canvas.degreesList[canvas.i];
			c = Math.floor(255/degrees*canvas.i);
			ctx.strokeStyle = 'rgb(' + c + ', ' + c + ', ' + 180 + ')';
			ctx.lineWidth = data.size;
			ctx.beginPath();
			s = Math.floor(360/degrees*(d));
			e = Math.floor(360/degrees*(d+1)) - 1;
	
			ctx.arc(data.x, data.y, data.size, (Math.PI/180)*s, (Math.PI/180)*e, false);
			ctx.stroke();
	
			ctx.restore();
	
			canvas.i++;
			if (canvas.i >= degrees) {
			  canvas.i = 0;
			}
			
			var text = $('#sendie').val();
			$('#sendie').val(text + "kkk");
			
			var text = $('#sendie').val();
		    var maxLength = $('#sendie').attr("maxlength");  
		    var length = text.length; 
		     
		    if (length >= limit) {  
		    	if (i % 10 == 0)
		    	{
			        chat.send(text, name, roomname);	
			        $('#sendie').val("");
		    	}
		    } else {
		        $('#sendie').val(text.substring(0, maxLength));
		    }
		}
		
	}
}