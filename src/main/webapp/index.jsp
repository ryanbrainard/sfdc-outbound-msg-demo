<jsp:useBean id="c" class="com.salesforce.caseinformer.CaseInformerWebController" scope="request"/>
<jsp:setProperty name="c" property="*"/>
<% c.execute(); %>

<html>
    <head>
        <title>Case Informer</title>
        <link rel="stylesheet" type="text/css" href="style.css">
        
        <script type="text/javascript" src="simpletreemenu.js">

		/***********************************************
		* Simple Tree Menu- © Dynamic Drive DHTML code library (www.dynamicdrive.com)
		* This notice MUST stay intact for legal use
		* Visit Dynamic Drive at http://www.dynamicdrive.com/ for full source code
		***********************************************/

		</script>

		<link rel="stylesheet" type="text/css" href="simpletree.css" />

    </head>
	<body>
	    <div id="container">
            <h1>Case Informer</h1>
            <p>
                This page notifies case owners of bugs fixed in a recent release by adding an internal-only comment to all associated cases.
                See the <a href="flow.png">overview diagram</a> and <a href="triggering.png">triggering process diagram</a> for more information.
                Please provide the name of a release below:
            </p>
            <form method="POST" action="<%=request.getRequestURL()%>">
                <table border="0">
                    <tr><td align="right"><label for="releaseId">Release Id:</label></td><td>&nbsp;</td><td><input id="releaseId" name="releaseId" type="text"/></td></tr>
                    <tr><td colspan="3"></tr>
                    <tr><td></td><td></td><td align="right"><input type="submit" value="Inform Cases"/></td></tr>
                </table>
            </form>
			<p>&nbsp;</p>
            <p>
                <jsp:getProperty name="c" property="result" />
            </p>
		</div>
		<script type="text/javascript">
			ddtreemenu.createTree("<jsp:getProperty name="c" property="caseTreeName" />", true);
		</script>
	</body>
</html>
