<%@ page errorPage="../../../../ErrorPage.jsp" %>

<jsp:useBean id="oauth" scope="session" class="fr.paris.lutece.plugins.mylutece.modules.oauth.web.OAuthJspBean" />

<%
	oauth.init( request, oauth.RIGHT_MANAGE_OAUTH );
   	response.sendRedirect( oauth.doCreateOAuth( request ) ); 
%>
