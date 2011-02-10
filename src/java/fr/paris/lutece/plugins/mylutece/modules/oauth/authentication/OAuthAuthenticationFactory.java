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
package fr.paris.lutece.plugins.mylutece.modules.oauth.authentication;

import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;

import org.apache.commons.lang.ClassUtils;

import java.util.HashMap;
import java.util.Map;


/**
 *
 * OAuthAuthenticationFactory.
 */
public final class OAuthAuthenticationFactory
{
    /**
     * Default map. Set to unique value <"10a", OAuthAuthentication10a.class.getName( )>
     */
    private static final Map<String, String> DEFAULT_MAP_CLASSES = new HashMap<String, String>(  );

    static
    {
        DEFAULT_MAP_CLASSES.put( "10a", OAuthAuthentication.class.getName(  ) );
    }

    private Map<String, String> _mapClasses = DEFAULT_MAP_CLASSES;

    /**
     *
     * @param strOAuthProtocolVersion the protocol version
     * @return the new OAuthAuthentication instance
     */
    public OAuthAuthentication newAuthentication( String strOAuthProtocolVersion )
    {
        String strClassName = _mapClasses.get( strOAuthProtocolVersion );

        if ( strClassName != null )
        {
            Class<OAuthAuthentication> clazz;

            try
            {
                clazz = ClassUtils.getClass( strClassName );

                return clazz.newInstance(  );
            }
            catch ( ClassNotFoundException e )
            {
                AppLogService.error( e.getMessage(  ), e );
            }
            catch ( InstantiationException e )
            {
                AppLogService.error( e.getMessage(  ), e );
            }
            catch ( IllegalAccessException e )
            {
                AppLogService.error( e.getMessage(  ), e );
            }
        }

        throw new AppException( "Unsupported protocol version for " + strOAuthProtocolVersion );
    }

    /**
     * "Getter method" for {@link #_mapClasses}
     * @return value of {@link #_mapClasses}
     */
    public Map<String, String> getMapClasses(  )
    {
        return _mapClasses;
    }

    /**
     * "Setter method" for {@link #_mapClasses}
     * @param mapClasses new value of {@link #_mapClasses}
     */
    public void setMapClasses( Map<String, String> mapClasses )
    {
        this._mapClasses = mapClasses;
    }
}
