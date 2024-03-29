var stompClient = null;
var notificationCount = 0;

$(document).ready(function() {
    console.log("Index page is ready");
});

var stompClient;
var sessionId;
var gameId;
var email = 'cat_lover@yahoo.com';
var url = "http://lyra.et-inf.fho-emden.de:19036";

function changeToLocal() {
	url = "http://localhost:8080";
}

function subscribe() {
	stompClient.subscribe("/game/" + email + "/init", (payload) => {
		console.log("SUBSCRIBE!")
		var message = JSON.parse(payload.body);
		gameId = message.id;
		console.log(message);
		stompClient.subscribe(
			"/game/" + gameId + "/paddleMovement",
			(paddleMovement) =>{
				console.log(paddleMovement);
			});
		stompClient.subscribe(
			"/game/" + gameId + "/ballMovement",
			(ballMovement) =>{
				console.log(ballMovement);
			});
		stompClient.subscribe(
			"/game/" + gameId + "/score",
			(score) =>{
				console.log(score);
			});
	});
}

var DIRECTION_UP = -1;
var DIRECTION_HALT = 0;
var DIRECTION_DOWN = 1;

function sendPaddleDirection(direction) {
	stompClient.send("/pong/game/" + gameId + "/paddle", {}, direction);
}

function sendTest() {
	stompClient.send("/pong/test");
} 

function sendTestToUser() {
	stompClient.send("/pong/test/" + sessionId);
} 

function connectUser1() {
	connectUser();
}

function connectUser2() {
	email = 'cat_lover2@yahoo.com';
	connectUser();
}

function closeConnection() {
	stompClient.disconnect();
}

function connectUser() {
	var token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjYXRfbG92ZXJAeWFob28uY29tIiwiaWF0IjoxNzAxMzM5OTk5LCJleHAiOjE3MDE0MjYzOTl9.HNn8WXMn7j2IOC6Qmki1VQlSTqtZFFD8aYWVBFJLSWc";
	var tokenWithBearer = "Bearer " + token;
	var headers = {
		email: email,
		password: 'garfield',
		// not working
		// access_token: token,
		// additional header
		// 'Authorization': tokenWithBearer
	  };
	var socket = new SockJS(url + "/ws?email=" + email + "&password=garfield");
	stompClient = Stomp.over(socket);
	stompClient.connect(headers, function (frame) {
		var urlSplit = socket._transport.url.split('/');
		sessionId = urlSplit[urlSplit.length - 2];
		console.log("sessionId: " + sessionId)
		console.log(frame);
		subscribe();
		stompClient.send("/pong/game");
	});
}

function createUsers() {
	const registerUrl = url + '/api/v1/auth/register';
	const data1 = {
		"email": "cat_lover@yahoo.com",
		"playername": "PongMaster666",
		"password": "garfield",
		"role": "PLAYER"
	};

	const data2 = {
		"email": "cat_lover2@yahoo.com",
		"playername": "PongMaster666",
		"password": "garfield",
		"role": "PLAYER"
	};

	fetch(
		registerUrl,
		{
			headers: { "Content-Type": "application/json" },
			body: JSON.stringify(data1),
			method: "POST"
		}
	)
	.then(data => data.json())
	.then((json) => {
		console.log(JSON.stringify(json));
	});

	fetch(
		registerUrl,
		{
			headers: { "Content-Type": "application/json" },
			body: JSON.stringify(data2),
			method: "POST"
		}
	)
	.then(data => data.json())
	.then((json) => {
		console.log(JSON.stringify(json));
	});

}

function uploadTextImage() {
	const uploadUrl = url + '/api/v1/users/image';
	const data = {
		"file": getTestImageBase64()
	};

	
}

