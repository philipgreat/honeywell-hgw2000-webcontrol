<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<style>
.message {
	font-size: 20px;
}

.paramlist {
	font-size: 20px;
	text-align: left;
	overflow-x: hidden;
}
</style>

<script type="text/javascript">
	$(function() {
		
		
		$(".parameterItem").click(function() {			
		
			var currentTargetId= $("#parameters").attr("targetInputId");
			console.log(currentTargetId)	;
			$("#"+currentTargetId).val($(this).html());
			
			$("#parameters").toggle();
			
		});

		$(".delete").click(function() {			
			
			//alert("want to delete something?");
			var targetElement=$(this).attr("targetelement");
			var itemId=$(this).attr("itemId");
			var url="removeParameter/"+itemId+"/";
			fillResult(url,targetElement);

			var parameterType=$(this).attr("parameterType");
			var parameterName=$(this).attr("parameterName");
		    
		    	fillResult("suggestParameter/"+parameterType+"/"+parameterName+"/","#parameters");
	

		});
		

		//

	});
	
	$(document).ready(function() {

		
		
	
	});
	
	
</script>

Parameter from Previous Input <div id="log"> </div>
	<hr />

	<div class="paramlist">
		<c:forEach var="item" items="${result}">
		
			<c:if test="${not empty item.value}">
			<a href="#" class="delete" itemId="${item.persistantId}" targetelement="#log" parameterType="${item.type}" parameterName="${item.name}">[x]</a>


<a href="#" class="parameterItem" title="used ${item.usedCount} times">${item.value}</a><br/>
			</c:if>

		</c:forEach>
	</div>


