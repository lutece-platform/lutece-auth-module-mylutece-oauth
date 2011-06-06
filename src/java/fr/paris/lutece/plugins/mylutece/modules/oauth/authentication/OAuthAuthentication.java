/*
 * Copyright (c) 2002-2011, Mairie de Paris
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
package fr.paris.lutece.plugins.mylutece.modules.oauth.authentication;

import fr.paris.lutece.plugins.mylutece.authentication.PortalAuthentication;
import fr.paris.lutece.plugins.mylutece.modules.oauth.service.OAuthPlugin;
import fr.paris.lutece.portal.service.security.LoginRedirectException;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.LocalVariables;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

import org.apache.commons.lang.StringUtils;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

import javax.security.auth.login.LoginException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;


/**
 *
 * OAuthAuthentication. (version 1.0a). <br>
 * Uses <strong>httpAccess.*</strong> properties to perform HTTP requests.
 * @see <a href="http://oauth.net/">oauth.net</a>
 */
public class OAuthAuthentication extends PortalAuthentication
{
    private static final String PROPERTY_PROXY_HOST = "httpAccess.proxyHost";
    private static final String PROPERTY_PROXY_PORT = "httpAccess.proxyPort";
    private static final String PROPERTY_PROXY_USERNAME = "httpAccess.proxyUserName";
    private static final String PROPERTY_PROXY_PASSWORD = "httpAccess.proxyPassword";
    private static final String PROPERTY_DOMAIN_NAME = "httpAccess.domainName";
    private static final String PARAMETER_OAUTH_VERIFIER = OAuth.OAUTH_VERIFIER;
    private static final String PARAMETER_OAUTH_TOKEN = OAuth.OAUTH_TOKEN;
    private static final String CONSTANT_CALLBACK_URL = "jsp/site/plugins/mylutece/modules/oauth/Redirect.jsp?auth_provider=";

    /**
     * A cookie will be created for a token request, to store the token secret, since Token Secret is not passed through callback url.
     */
    private static final String CONSTANT_COOKIE_TOKEN_SECRET = "oauth_token_secret";
    private String _strAuthServiceName;
    private String _strName;
    private String _strIconUrl;
    private String _strRequestTokenEndpointUrl;
    private String _strAccessTokenEndpointUrl;
    private String _strAuthorizeWebsiteUrl;
    private String _strConsumerKey;
    private String _strConsumerSecret;
    private String _strCredentialUrl;
    private String _strCredentialFormat;

    /**
     *
     *{@inheritDoc}
     */
    public LuteceUser getAnonymousUser(  )
    {
        return new OAuthUser( LuteceUser.ANONYMOUS_USERNAME, this );
    }

    /**
     *
     *{@inheritDoc}
     */
    public String getAuthServiceName(  )
    {
        return _strAuthServiceName;
    }

    /**
     *
     *{@inheritDoc}
     */
    public String getAuthType( HttpServletRequest request )
    {
        return HttpServletRequest.BASIC_AUTH;
    }

    /**
     *
     *{@inheritDoc}
     */
    public String getIconUrl(  )
    {
        return _strIconUrl;
    }

    /**
     *
     *{@inheritDoc}
     */
    public String getName(  )
    {
        return _strName;
    }

    /**
     *
     *{@inheritDoc}
     */
    public String getPluginName(  )
    {
        return OAuthPlugin.PLUGIN_NAME;
    }

    /**
     *
     *{@inheritDoc}
     */
    public boolean isUserInRole( LuteceUser user, HttpServletRequest request, String strRole )
    {
        return false;
    }

    /**
     * Builds a new {@link HttpClient}
     * @return new HttpClient
     */
    private HttpClient getHttpClient(  )
    {
        DefaultHttpClient client = new DefaultHttpClient(  );

        String strUserName = AppPropertiesService.getProperty( PROPERTY_PROXY_USERNAME );
        String strPassword = AppPropertiesService.getProperty( PROPERTY_PROXY_PASSWORD );
        String strDomainName = AppPropertiesService.getProperty( PROPERTY_DOMAIN_NAME );

        if ( StringUtils.isNotBlank( strUserName ) && StringUtils.isNotBlank( strPassword ) )
        {
            // at least Userpasswordcredz
            Credentials creds;

            if ( StringUtils.isBlank( strDomainName ) )
            {
                creds = new UsernamePasswordCredentials( strUserName, strPassword );
            }
            else
            {
                creds = new NTCredentials( strUserName, strPassword, "", strDomainName );
            }

            CredentialsProvider credsProvider = new BasicCredentialsProvider(  );
            credsProvider.setCredentials( AuthScope.ANY, creds );
            client.setCredentialsProvider( credsProvider );

            HttpHost proxy = new HttpHost( AppPropertiesService.getProperty( PROPERTY_PROXY_HOST ),
                    AppPropertiesService.getPropertyInt( PROPERTY_PROXY_PORT, 8080 ) );
            client.getParams(  ).setParameter( ConnRoutePNames.DEFAULT_PROXY, proxy );
        }

        return client;
    }

    /**
     * Gets the request token url. Builds the provider. CallBack url is {@link #CONSTANT_CALLBACK_URL}
     * @param request the request
     * @param consumer the consumer
     * @return the redirect url
     * @throws OAuthMessageSignerException if occurs
     * @throws OAuthNotAuthorizedException if occurs
     * @throws OAuthExpectationFailedException if occurs
     * @throws OAuthCommunicationException if occurs
     */
    private String getRedirectUrl( HttpServletRequest request, OAuthConsumer consumer )
        throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException,
            OAuthCommunicationException
    {
        return getProvider(  )
                   .retrieveRequestToken( consumer,
            AppPathService.getBaseUrl( request ) + CONSTANT_CALLBACK_URL + this.getName(  ) );
    }

    /**
     * Builds a provider
     * @return the provider
     */
    private OAuthProvider getProvider(  )
    {
        CommonsHttpOAuthProvider provider = new CommonsHttpOAuthProvider( getRequestTokenEndpointUrl(  ),
                getAccessTokenEndpointUrl(  ), getAuthorizeWebsiteUrl(  ) );

        provider.setHttpClient( getHttpClient(  ) );

        return provider;
    }

    /**
     * Buids a consumer
     * @return the {@link OAuthConsumer}
     */
    private OAuthConsumer getConsumer(  )
    {
        return new CommonsHttpOAuthConsumer( getConsumerKey(  ), getConsumerSecret(  ) );
    }

    /**
     *
     *{@inheritDoc}
     */
    @Override
    public boolean isDelegatedAuthentication(  )
    {
        return true;
    }

    /**
     *
     *{@inheritDoc}
     */
    public LuteceUser login( String strUserName, String strUserPassword, HttpServletRequest request )
        throws LoginException, LoginRedirectException
    {
        OAuthConsumer consumer = getConsumer(  );

        try
        {
            // FIXME : version specific
            String url = getRedirectUrl( request, consumer );

            if ( AppLogService.isDebugEnabled(  ) )
            {
                AppLogService.debug( "Url to visit : " + url );
                AppLogService.debug( "Token secret : " + consumer.getTokenSecret(  ) );
            }

            // create cookie to store token secret
            createCookie( CONSTANT_COOKIE_TOKEN_SECRET, consumer.getTokenSecret(  ) );

            throw new LoginRedirectException( url );
        }
        catch ( OAuthMessageSignerException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }
        catch ( OAuthExpectationFailedException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }
        catch ( OAuthCommunicationException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }
        catch ( OAuthNotAuthorizedException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }

        return null;
    }

    /**
     * Gets the user
     * @param request the reuqest
     * @return the user found.
     */
    public OAuthUser getUser( HttpServletRequest request )
    {
        String strToken = request.getParameter( PARAMETER_OAUTH_TOKEN );
        String strVerifier = request.getParameter( PARAMETER_OAUTH_VERIFIER );

        if ( StringUtils.isNotBlank( strToken ) && StringUtils.isNotBlank( strVerifier ) )
        {
            // FIXME : version specific
            Cookie cookie = findCookie( request, CONSTANT_COOKIE_TOKEN_SECRET );

            OAuthConsumer consumer = getConsumer(  );
            consumer.setTokenWithSecret( strToken, cookie.getValue(  ) );

            try
            {
                OAuthProvider provider = getProvider(  );
                provider.setOAuth10a( true );
                provider.retrieveAccessToken( consumer, strVerifier );

                // FIXME :  username.
                OAuthUser user = new OAuthUser( this.getName(  ), this );
                user.setToken( strToken );
                user.setVerifier( strVerifier );
                user.setTokenSecret( cookie.getValue(  ) );

                // get user infos
                if ( StringUtils.isNotBlank( getCredentialUrl(  ) ) )
                {
                    HttpGet getMethod = new HttpGet( getCredentialUrl(  ) );

                    consumer.sign( getMethod );

                    HttpResponse httpResponse = getHttpClient(  ).execute( getMethod );

                    int nStatusCode = httpResponse.getStatusLine(  ).getStatusCode(  );

                    if ( nStatusCode == HttpStatus.SC_OK )
                    {
                        OAuthCredentialsRetrieverUtils.doRetrieveUserInfo( httpResponse, user, getCredentialFormat(  ) );
                    }
                    else
                    {
                        AppLogService.error( httpResponse.getStatusLine(  ) );
                        throw new AppException( "Unable to find user infos : " + httpResponse.getStatusLine(  ) );
                    }
                }

                return user;
            }
            catch ( OAuthMessageSignerException e )
            {
                AppLogService.error( e.getMessage(  ), e );
            }
            catch ( OAuthNotAuthorizedException e )
            {
                AppLogService.error( e.getMessage(  ), e );
            }
            catch ( OAuthExpectationFailedException e )
            {
                AppLogService.error( e.getMessage(  ), e );
            }
            catch ( OAuthCommunicationException e )
            {
                AppLogService.error( e.getMessage(  ), e );
            }
            catch ( ClientProtocolException e )
            {
                AppLogService.error( e.getMessage(  ), e );
            }
            catch ( IOException e )
            {
                AppLogService.error( e.getMessage(  ), e );
            }
            catch ( IllegalStateException e )
            {
                AppLogService.error( e.getMessage(  ), e );
            }
        }

        return null;
    }

    /**
     * Finds a cookie by its name
     * @param request the request
     * @param strKey the cookie name
     * @return the cookie found, <code>null</code> otherwise.
     */
    private Cookie findCookie( HttpServletRequest request, String strKey )
    {
        if ( StringUtils.isBlank( strKey ) )
        {
            return null;
        }

        for ( Cookie cookie : request.getCookies(  ) )
        {
            if ( strKey.equals( cookie.getName(  ) ) )
            {
                return cookie;
            }
        }

        return null;
    }

    /**
     * Creates a cookie
     * @param strKey the cookie name
     * @param strValue the value
     */
    private void createCookie( String strKey, String strValue )
    {
        Cookie cookie = new Cookie( strKey, strValue );
        LocalVariables.getResponse(  ).addCookie( cookie );
    }

    /**
     *
     *{@inheritDoc}
     */
    public void logout( LuteceUser user )
    {
        // nothing
    }

    // SETTERS
    /**
     * Sets the auth service name
     * @param strAuthServiceName the service name
     */
    public void setAuthServiceName( String strAuthServiceName )
    {
        _strAuthServiceName = strAuthServiceName;
    }

    /**
     * Set the name
     * @param strName the service key
     */
    public void setName( String strName )
    {
        _strName = strName;
    }

    /**
     * Sets the icon url
     * @param strIconUrl icon url
     */
    public void setIconUrl( String strIconUrl )
    {
        _strIconUrl = strIconUrl;
    }

    /**
     * The request token url
     * @return the request token url
     */
    public String getRequestTokenEndpointUrl(  )
    {
        return _strRequestTokenEndpointUrl;
    }

    /**
     * The request token url
     * @param strRequestTokenEndpointUrl the request token url
     */
    public void setRequestTokenEndpointUrl( String strRequestTokenEndpointUrl )
    {
        _strRequestTokenEndpointUrl = strRequestTokenEndpointUrl;
    }

    /**
     * The access token url
     * @return the access token url
     */
    public String getAccessTokenEndpointUrl(  )
    {
        return _strAccessTokenEndpointUrl;
    }

    /**
     * Access token url
     * @param strAccessTokenEndpointUrl the access token url
     */
    public void setAccessTokenEndpointUrl( String strAccessTokenEndpointUrl )
    {
        _strAccessTokenEndpointUrl = strAccessTokenEndpointUrl;
    }

    /**
     * Gets The authorize/authenticate url
     * @return The authorize/authenticate url
     */
    public String getAuthorizeWebsiteUrl(  )
    {
        return _strAuthorizeWebsiteUrl;
    }

    /**
     * The authorize/authenticate url
     * @param strAuthorizeWebsiteUrl the authorize url
     */
    public void setAuthorizeWebsiteUrl( String strAuthorizeWebsiteUrl )
    {
        _strAuthorizeWebsiteUrl = strAuthorizeWebsiteUrl;
    }

    /**
     * Returns the consumer key
     * @return the consumer key
     */
    public String getConsumerKey(  )
    {
        return _strConsumerKey;
    }

    /**
     * Sets the consumer key
     * @param strConsumerKey the consumer key
     */
    public void setConsumerKey( String strConsumerKey )
    {
        _strConsumerKey = strConsumerKey;
    }

    /**
     * Returns the consumer secret
     * @return consumer secret
     */
    public String getConsumerSecret(  )
    {
        return _strConsumerSecret;
    }

    /**
     * Consumer secret
     * @param strConsumerSecret consumer secret
     */
    public void setConsumerSecret( String strConsumerSecret )
    {
        _strConsumerSecret = strConsumerSecret;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString(  )
    {
        return super.toString(  ) + "{" + this.getConsumerKey(  ) + "," + this.getConsumerSecret(  ) + "," +
        this.getAuthorizeWebsiteUrl(  ) + "," + this.getRequestTokenEndpointUrl(  ) + "," +
        this.getAccessTokenEndpointUrl(  ) + "}";
    }

    /**
     *  credential url
     *  @return  credential url
     */
    public String getCredentialUrl(  )
    {
        return _strCredentialUrl;
    }

    /**
     * Set the credential url
     * @param strCredentialUrl credential url
     */
    public void setCredentialUrl( String strCredentialUrl )
    {
        _strCredentialUrl = strCredentialUrl;
    }

    /**
     * Gets the credentials format
     * @return format
     */
    public String getCredentialFormat(  )
    {
        return _strCredentialFormat;
    }

    /**
     * Credentials format
     * @param strCredentialFormat strCredentialFormat
     */
    public void setCredentialFormat( String strCredentialFormat )
    {
        this._strCredentialFormat = strCredentialFormat;
    }
}
