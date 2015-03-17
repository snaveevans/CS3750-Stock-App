<!-- Include Header File -->
<%@ include file="header.jsp" %>

<html>
<body>
<br></br>
<div id="increaseInvestment" style="text-align: center" align="center">
		<form:form action="/increaseInvestment" method="post" commandName="increaseInvestmentForm">
			<table width="25%" height="35%" border="0">
				<tr>
					<td colspan="2" align="center"><h2 style="color:white; font-size:16pt">Increase Stock</h2></td>
				</tr>
				<tr>
					<td style="color:white; font-size:14pt" width="25%">Symbol:</td>
					<td><form:input path="symbol" /></td>
				</tr>
				<tr>
					<td colspan="2" align="center"><input type="submit" value="Increase" /></td>
				</tr>
			</table>
		</form:form>
</div>
</body>
</html>

<!-- Include Footer File -->
<%@ include file="footer.jsp" %>