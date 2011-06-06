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
package fr.paris.lutece.plugins.mylutece.modules.oauth.service;

import fr.paris.lutece.plugins.mylutece.authentication.MultiLuteceAuthentication;
import fr.paris.lutece.plugins.mylutece.modules.oauth.authentication.OAuthAuthentication;
import fr.paris.lutece.plugins.mylutece.modules.oauth.authentication.OAuthUser;
import fr.paris.lutece.plugins.mylutece.modules.oauth.business.OAuthAuthenticationHome;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.security.LuteceAuthentication;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.web.PortalJspBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * OAuthService
 */
public final class OAuthService
{
    private Map<String, OAuthAuthentication> _mapAuthentications;

    /**
     * Private constructor
     */
    private OAuthService(  )
    {
        // nothing
        _mapAuthentications = new HashMap<String, OAuthAuthentication>(  );
    }

    /**
     * Inits the authentications.
     * <strong>Plugin needs to be configured (i.e. pool set)</strong>
     */
    public void init(  )
    {
        Plugin plugin = PluginService.getPlugin( OAuthPlugin.PLUGIN_NAME );

        if ( plugin != null )
        {
            // call home to register available auth.
            try
            {
                List<OAuthAuthentication> listAuthentication = OAuthAuthenticationHome.findAll( plugin );

                for ( OAuthAuthentication authentication : listAuthentication )
                {
                    registerAuthentication( authentication );
                }
            }
            catch ( AppException e )
            {
                AppLogService.error( "Unable to find registered OAuth authentications in module-mylutece-oauth : " +
                    e.getMessage(  ), e );
            }
        }
    }

    /**
     * Gets the authentication
     * @param strAuthName the auth name
     * @return the authentication found, <code>null</code> otherwise.
     */
    public OAuthAuthentication getAuthentication( String strAuthName )
    {
        return _mapAuthentications.get( strAuthName );
    }

    /**
     * Finds the registered authentication list.
     * @return the registered authentications
     */
    public List<OAuthAuthentication> getListAuthentication(  )
    {
        return new ArrayList<OAuthAuthentication>( _mapAuthentications.values(  ) );
    }

    /**
     * Call {@link MultiLuteceAuthentication} activation.
     * @param authentication the authentication
     */
    private void registerAuthentication( OAuthAuthentication authentication )
    {
        MultiLuteceAuthentication.registerAuthentication( authentication );
        _mapAuthentications.put( authentication.getName(  ), authentication );
    }

    /**
     * Call {@link MultiLuteceAuthentication} activation.
     * @param strAuthenticationName the authentication
     */
    private void removeAuthentication( String strAuthenticationName )
    {
        MultiLuteceAuthentication.removeAuthentication( strAuthenticationName );
        _mapAuthentications.remove( strAuthenticationName );
    }

    /**
     * Creates a new authentication and registers it.
     * @param authentication the authentication to create
     * @param plugin the plugin
     */
    public void createNewAuthentication( OAuthAuthentication authentication, Plugin plugin )
    {
        OAuthAuthenticationHome.create( authentication, plugin );
        registerAuthentication( authentication );
    }

    /**
     * Updates the authentication : registers it and saves it.
     * @param authentication the authentication
     * @param plugin the plugin
     */
    public void updateAuthentication( OAuthAuthentication authentication, Plugin plugin )
    {
        OAuthAuthenticationHome.update( authentication, plugin );
        // update current authentication since name is not mutable.
        registerAuthentication( authentication );
    }

    /**
     * Removes the authentication
     * @param strAuthenticationName the authentication name.
     * @param plugin the plugin
     */
    public void removeAuthentication( String strAuthenticationName, Plugin plugin )
    {
        OAuthAuthenticationHome.remove( strAuthenticationName, plugin );
        removeAuthentication( strAuthenticationName );
    }

    /**
     * Do the actual authentication
     * @param request the request with auth_provider parameter
     * @return portal url if no error
     */
    public String doAuthentication( HttpServletRequest request )
    {
        String strAuthProvider = request.getParameter( "auth_provider" );

        if ( AppLogService.isDebugEnabled(  ) )
        {
            AppLogService.debug( "OAuth provider : " + strAuthProvider );
        }

        if ( SecurityService.getInstance(  ).isMultiAuthenticationSupported(  ) )
        {
            MultiLuteceAuthentication mainAuthentication = (MultiLuteceAuthentication) SecurityService.getInstance(  )
                                                                                                      .getAuthenticationService(  );

            LuteceAuthentication authentication = mainAuthentication.getLuteceAuthentication( strAuthProvider );

            if ( ( authentication == null ) || !( authentication instanceof OAuthAuthentication ) )
            {
                throw new AppException( "Can't use provided authentication paramater : " + strAuthProvider +
                    ". Found : " + authentication );
            }

            OAuthAuthentication oAuthAuthentication = (OAuthAuthentication) authentication;
            OAuthUser user = oAuthAuthentication.getUser( request );
            SecurityService.getInstance(  ).registerUser( request, user );
        }

        String strReturnUrl = PortalJspBean.getLoginNextUrl( request );

        if ( strReturnUrl != null )
        {
            return strReturnUrl;
        }

        return AppPathService.getBaseUrl( request ) + AppPathService.getPortalUrl(  );
    }
}
