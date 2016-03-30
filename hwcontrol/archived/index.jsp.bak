<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GBK">
<title>Insert title here</title>
<script src="/scripts/jquery-1.9.1.js"></script>
<style>
.menu {
	width: 100%;
	height: 80px;
	border: 1px solid red;
	font-size: 20px;
	text-align: center;
}

.content {
	width: 100%;
	height: 800px;
	text-align: center;
	border: 1px solid red;
	padding-top: 30px;
}
</style>

<script>
	$(function() {
		var cache = {};

		$(".action").click(
				function() {
					//alert( );
					$("#content").text($(this).attr("href"));
					//$("#content").text(event.target+"/"+$(this).attr("href"));
					var reqURI = "/naf/invoke/"
							+ $(this).attr("href").substring(1) + "/";
					$("#content").text(reqURI);

					var request = $.ajax({
						url : reqURI,
						type : "GET",
						data : {
							id : "id"
						},
						dataType : "html"
					});

					request.done(function(msg) {
						$("#content").html(msg);
					});

					request.fail(function(jqXHR, textStatus) {
						alert("Request failed: " + textStatus);
					});

				});

	});

	$(document).ready(function() {
		//alert($(location).attr('href'));
		var currentURL=$(location).attr('href');
		var index=currentURL.indexOf("#");
		if(index<0){
			return;
		}
		
		reqURI = "/naf/invoke/"	+ currentURL.substring(index+1) + "/";
		
		var request = $.ajax({
			url : reqURI,
			type : "GET",
			data : {
				id : "id"
			},
			dataType : "html"
		});

		request.done(function(msg) {
			$("#content").html(msg);
		});

		request.fail(function(jqXHR, textStatus) {
			alert("Request failed: " + textStatus);
		});
		
			
			
		
	});
</script>
</head>

<body>

	<div class="menu" width="100%">
		<a href="#getNomalResult" class="action">Normal Result</a> | <a
			href="#getExceptionResult" class="action">ExceptionResult</a> | <a
			href="#getCustomExceptionResult" class="action">CustomExceptionResult</a>|
		<a href="#getFormResult" class="action">FormResult</a> 


	</div>

	<div class="content" id="content">Home Info</div>
</body>
</html>
