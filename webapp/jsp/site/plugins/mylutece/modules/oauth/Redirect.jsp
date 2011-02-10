<%@page import="fr.paris.lutece.plugins.mylutece.modules.oauth.service.OAuthPlugin"%>
<%@page import="fr.paris.lutece.portal.service.spring.SpringContextService"%>
<%@page import="fr.paris.lutece.plugins.mylutece.modules.oauth.service.OAuthService"%>
<%
OAuthService service = (OAuthService) SpringContextService.getPluginBean(OAuthPlugin.PLUGIN_NAME, "mylutece-oauth.oauthService");
response.sendRedirect( service.doAuthentication( request ) );
%>