<%@ page errorPage="../../../../ErrorPage.jsp" %>
<jsp:include page="../../../../AdminHeader.jsp" />

<jsp:useBean id="oauth" scope="session" class="fr.paris.lutece.plugins.mylutece.modules.oauth.web.OAuthJspBean" />

<% oauth.init( request, oauth.RIGHT_MANAGE_OAUTH ); %>
<%= oauth.getModifyOAuth( request ) %>

<%@ include file="../../../../AdminFooter.jsp" %>