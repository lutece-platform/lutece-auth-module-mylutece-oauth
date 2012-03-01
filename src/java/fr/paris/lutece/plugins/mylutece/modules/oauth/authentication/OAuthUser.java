/*
 * Copyright (c) 2002-2012, Mairie de Paris
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

import fr.paris.lutece.portal.service.security.LuteceAuthentication;
import fr.paris.lutece.portal.service.security.LuteceUser;


/**
 * OAuthUser.
 * <br>
 * Don't forget to set tokens and verifier.
 */
public class OAuthUser extends LuteceUser
{
    private String _strToken;
    private String _strVerifier;
    private String _strTokenSecret;

    /**
     * Builds a new OAuthUser.
     * Don't forget to set tokens and verifier.
     * @param strUserName the user name
     * @param authenticationService the authentication service
     */
    public OAuthUser( String strUserName, LuteceAuthentication authenticationService )
    {
        super( strUserName, authenticationService );
    }

    /**
     * Gets the token
     * @return the token
     */
    public String getToken(  )
    {
        return _strToken;
    }

    /**
     * Sets the token
     * @param token the token
     */
    public void setToken( String token )
    {
        this._strToken = token;
    }

    /**
     * Gets the verifier
     * @return the verifier
     */
    public String getVerifier(  )
    {
        return _strVerifier;
    }

    /**
     * Set the verifier
     * @param strVerifier verifier
     */
    public void setVerifier( String strVerifier )
    {
        this._strVerifier = strVerifier;
    }

    /**
     * Sets the tokensecret
     * @param strTokenSecret the token secret
     */
    public void setTokenSecret( String strTokenSecret )
    {
        this._strTokenSecret = strTokenSecret;
    }

    /**
     * Gets the token secret
     * @return the token secret
     */
    public String getTokenSecret(  )
    {
        return _strTokenSecret;
    }
}
