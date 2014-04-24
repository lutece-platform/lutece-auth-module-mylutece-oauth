/*
 * Copyright (c) 2002-2014, Mairie de Paris
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
package fr.paris.lutece.plugins.mylutece.modules.oauth.business;

import java.util.List;

import fr.paris.lutece.plugins.mylutece.modules.oauth.authentication.OAuthAuthentication;
import fr.paris.lutece.portal.service.plugin.Plugin;


/**
 *
 * IOAuthAuthenticationDAO.
 */
public interface IOAuthAuthenticationDAO
{
    /**
     * Stores the authentication
     * @param authentication the authentication
     * @param plugin the plugin
     */
    void store( OAuthAuthentication authentication, Plugin plugin );

    /**
     * Removes the authentication
     * @param strIdAuthentication the id
     * @param plugin the plugin
     */
    void delete( String strIdAuthentication, Plugin plugin );

    /**
     * Inserts the authentication
     * @param authentication the authentication
     * @param plugin the plugin
     */
    void insert( OAuthAuthentication authentication, Plugin plugin );

    /**
     * Finds the authentication list
     * @param plugin the plugin
     * @return authentication lisrt
     */
    List<OAuthAuthentication> selectListAuthentication( Plugin plugin );

    /**
     * Finds the authentication
     * @param strIdAuthentication the id
     * @param plugin the plugin
     * @return the authentication
     */
    OAuthAuthentication load( String strIdAuthentication, Plugin plugin );
}
