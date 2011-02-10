/*
 * Copyright (c) 2002-2010, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.mylutece.modules.oauth.web;

import fr.paris.lutece.plugins.mylutece.modules.oauth.authentication.OAuthAuthentication;
import fr.paris.lutece.plugins.mylutece.modules.oauth.authentication.OAuthAuthenticationFactory;
import fr.paris.lutece.plugins.mylutece.modules.oauth.service.OAuthPlugin;
import fr.paris.lutece.plugins.mylutece.modules.oauth.service.OAuthService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.constants.Parameters;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.sort.AttributeComparator;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * OAuthJspBean : provides crud operation for {@link OAuthAuthentication}
 */
public class OAuthJspBean extends PluginAdminPageJspBean
{
    public static final String RIGHT_MANAGE_OAUTH = "OAUTH_MANAGEMENT";
    
    private static final String PARAMETER_OAUTH_ID = "oauth_id";
    private static final String PARAMETER_CANCEL = "cancel";
    private static final String PARAMETER_AUTH_NAME = "auth_name";
    private static final String PARAMETER_AUTH_SERVICE_NAME = "auth_service_name";
    private static final String PARAMETER_AUTH_ICON_URL = "auth_icon_url";
    private static final String PARAMETER_REQUEST_TOKEN_URL = "request_token_url";
    private static final String PARAMETER_ACCESS_TOKEN_URL = "access_token_url";
    private static final String PARAMETER_AUTHORIZE_URL = "authorize_url";
    private static final String PARAMETER_CONSUMER_KEY = "consumer_key";
    private static final String PARAMETER_CONSUMER_SECRET = "consumer_secret";
    private static final String PARAMETER_CREDENTIAL_URL = "credential_url";
    private static final String PARAMETER_CREDENTIAL_FORMAT = "credential_format";
    private static final String JSP_DO_REMOVE_OAUTH = "jsp/admin/plugins/mylutece/modules/oauth/DoRemoveOAuth.jsp";
    private static final String PROPERTY_PAGE_TITLE_MANAGE_OAUTH = "module.mylutece.oauth.manage_oauth.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_OAUTH = "module.mylutece.oauth.create_oauth.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_OAUTH = "module.mylutece.oauth.modify_oauth.pageTitle";
    private static final String MESSAGE_CONFIRM_REMOVE_OAUTH = "module.mylutece.oauth.message.confirmRemoveOAuth";
    private static final String TEMPLATE_MANAGE_OAUTH = "admin/plugins/mylutece/modules/oauth/manage_oauth.html";
    private static final String TEMPLATE_CREATE_OAUTH = "admin/plugins/mylutece/modules/oauth/create_oauth.html";
    private static final String TEMPLATE_MODIFY_OAUTH = "admin/plugins/mylutece/modules/oauth/modify_oauth.html";

    /**
     * FIXME : remove
     */
    private static final String CONSTANT_PROTOCOL_VERSION = "10a";

    // Properties
    private static final String PROPERTY_OAUTH_PER_PAGE = "mylutece-oauth.itemsPerPage";

    // Marks
    private static final String MARK_LIST_OAUTH = "oauth_list";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_OAUTH = "oauth";

    // Variables
    private int _nItemsPerPage;
    private int _nDefaultItemsPerPage;
    private String _strCurrentPageIndex;
    private OAuthAuthenticationFactory _factory;
    private OAuthService _service;

    /**
     * Constructor
     */
    public OAuthJspBean(  )
    {
        _factory = (OAuthAuthenticationFactory) SpringContextService.getPluginBean( OAuthPlugin.PLUGIN_NAME,
                "mylutece-oauth.authenticationFactory" );
        _service = (OAuthService) SpringContextService.getPluginBean( OAuthPlugin.PLUGIN_NAME,
                "mylutece-oauth.oauthService" );
    }

    /**
     * 
     *{@inheritDoc}
     */
    @Override
    public Plugin getPlugin(  )
    {
        return PluginService.getPlugin( OAuthPlugin.PLUGIN_NAME );
    }

    /**
     * Gets the list
     * @param request the request
     * @return html code
     */
    public String getManageOAuth( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_MANAGE_OAUTH );

        List<OAuthAuthentication> listAuthentication = _service.getListAuthentication(  );

        _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_OAUTH_PER_PAGE, 10 );
        _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage,
                _nDefaultItemsPerPage );

        // SORT
        String strSortedAttributeName = request.getParameter( Parameters.SORTED_ATTRIBUTE_NAME );
        String strAscSort = null;

        if ( strSortedAttributeName != null )
        {
            strAscSort = request.getParameter( Parameters.SORTED_ASC );

            boolean bIsAscSort = Boolean.parseBoolean( strAscSort );

            Collections.sort( listAuthentication, new AttributeComparator( strSortedAttributeName, bIsAscSort ) );
        }

        String strURL = getHomeUrl( request );
        UrlItem url = new UrlItem( strURL );

        if ( strSortedAttributeName != null )
        {
            url.addParameter( Parameters.SORTED_ATTRIBUTE_NAME, strSortedAttributeName );
        }

        if ( strAscSort != null )
        {
            url.addParameter( Parameters.SORTED_ASC, strAscSort );
        }

        LocalizedPaginator<OAuthAuthentication> paginator = new LocalizedPaginator<OAuthAuthentication>( (List<OAuthAuthentication>) listAuthentication,
                _nItemsPerPage, url.getUrl(  ), Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex, getLocale(  ) );

        Map<String, Object> model = new HashMap<String, Object>(  );

        model.put( MARK_LIST_OAUTH, paginator.getPageItems(  ) );
        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_NB_ITEMS_PER_PAGE, "" + _nItemsPerPage );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_OAUTH, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Get create form
     * @param request the request
     * @return html code
     */
    public String getCreateOAuth( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_CREATE_OAUTH );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_OAUTH, getLocale(  ) );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Gets modify form
     * @param request the reuqest
     * @return html code
     */
    public String getModifyOAuth( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_MODIFY_OAUTH );

        String strAuthName = request.getParameter( PARAMETER_OAUTH_ID );

        OAuthAuthentication authentication = _service.getAuthentication( strAuthName );

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_OAUTH, authentication );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_OAUTH, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Modifies the auth
     * @param request the request
     * @return url
     */
    public String doModifyOAuth( HttpServletRequest request )
    {
        if ( request.getParameter( PARAMETER_CANCEL ) != null )
        {
            return getHomeUrl( request );
        }

        String strAuthName = request.getParameter( PARAMETER_AUTH_NAME );

        OAuthAuthentication authentication = _service.getAuthentication( strAuthName );

        if ( authentication != null )
        {
            String strErrorUrl = getOAuthData( authentication, request );

            if ( strErrorUrl != null )
            {
                return strErrorUrl;
            }

            _service.updateAuthentication( authentication, getPlugin(  ) );
        }

        return getHomeUrl( request );
    }

    /**
     * Creates an authentication
     * @param request the request
     * @return url
     */
    public String doCreateOAuth( HttpServletRequest request )
    {
        if ( request.getParameter( PARAMETER_CANCEL ) != null )
        {
            return getHomeUrl( request );
        }

        // for future usage (support multiple version)
        OAuthAuthentication authentication = _factory.newAuthentication( CONSTANT_PROTOCOL_VERSION );
        String strError = getOAuthData( authentication, request );

        if ( strError != null )
        {
            return strError;
        }

        _service.createNewAuthentication( authentication, getPlugin(  ) );

        return getHomeUrl( request );
    }

    /**
     * Gets the confirm message url
     * @param request the request
     * @return url
     */
    public String getConfirmRemoveOAuth( HttpServletRequest request )
    {
        Map<String, String> requestParameters = new HashMap<String, String>(  );
        requestParameters.put( PARAMETER_OAUTH_ID, request.getParameter( PARAMETER_OAUTH_ID ) );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_OAUTH, JSP_DO_REMOVE_OAUTH,
            AdminMessage.TYPE_CONFIRMATION, requestParameters );
    }

    /**
     * Removes the authentication
     * @param request the request
     * @return url
     */
    public String doRemoveOAuth( HttpServletRequest request )
    {
        String strOAuthId = request.getParameter( PARAMETER_OAUTH_ID );
        _service.removeAuthentication( strOAuthId, getPlugin(  ) );

        return getHomeUrl( request );
    }

    /**
     * Gets data from request
     * @param auth the auth to fill
     * @param request the request
     * @return message url if any error, <code>null</code> otherwise.
     */
    private String getOAuthData( OAuthAuthentication auth, HttpServletRequest request )
    {
        String strName = request.getParameter( PARAMETER_AUTH_NAME );
        String strServiceName = request.getParameter( PARAMETER_AUTH_SERVICE_NAME );
        String strIconUrl = request.getParameter( PARAMETER_AUTH_ICON_URL );
        String strRequestTokenUrl = request.getParameter( PARAMETER_REQUEST_TOKEN_URL );
        String strAccessTokenUrl = request.getParameter( PARAMETER_ACCESS_TOKEN_URL );
        String strAuthorizeUrl = request.getParameter( PARAMETER_AUTHORIZE_URL );
        String strConsumerKey = request.getParameter( PARAMETER_CONSUMER_KEY );
        String strConsumerSecret = request.getParameter( PARAMETER_CONSUMER_SECRET );
        String strCredentialUrl = request.getParameter( PARAMETER_CREDENTIAL_URL );
        String strCredentialFormat = request.getParameter( PARAMETER_CREDENTIAL_FORMAT );

        if ( isOneBlank( strName, strServiceName, strIconUrl, strRequestTokenUrl, strAccessTokenUrl, strAuthorizeUrl,
                    strConsumerKey, strConsumerSecret, strCredentialUrl, strCredentialFormat ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        auth.setName( strName );
        auth.setAuthServiceName( strServiceName );
        auth.setIconUrl( strIconUrl );
        auth.setRequestTokenEndpointUrl( strRequestTokenUrl );
        auth.setAccessTokenEndpointUrl( strAccessTokenUrl );
        auth.setAuthorizeWebsiteUrl( strAuthorizeUrl );
        auth.setConsumerKey( strConsumerKey );
        auth.setConsumerSecret( strConsumerSecret );
        auth.setCredentialUrl( strCredentialUrl );
        auth.setCredentialFormat( strCredentialFormat );

        return null;
    }

    /**
     * Finds if at least one value is blank
     * @param values the values to test
     * @return <code>true</code> if at least one value is blank, <code>null</code> otherwise.
     */
    private boolean isOneBlank( String... values )
    {
        for ( String strValue : values )
        {
            if ( StringUtils.isBlank( strValue ) )
            {
                return true;
            }
        }

        return false;
    }
}
