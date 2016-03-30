   function  fillResult(reqURI, container)
    {
    	
    	var request = $.ajax({
			url : "/hwcontrol/"+reqURI,
			type : "GET",
			dataType : "html"
		});

		request.done(function(msg) {
			$(container).html(msg);
		});

		request.fail(function(jqXHR, textStatus) {
			//alert("Request failed: " + textStatus);
			$(container).html("從服務器取得數據失敗，請檢查網絡連接")
		});    	
    }


