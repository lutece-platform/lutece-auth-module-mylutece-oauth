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
package fr.paris.lutece.plugins.mylutece.modules.oauth.authentication;

import fr.paris.lutece.portal.service.spring.SpringContextService;

import org.apache.http.HttpResponse;


/**
 * This class provides method to try to retrieve user infos.
 */
public final class OAuthCredentialsRetrieverUtils
{
    /**
     * Private contructor
     */
    private OAuthCredentialsRetrieverUtils(  )
    {
        // nothing
    }

    /**
     * Finds the user info from the response.
     * <br>
     * Declare your parser in mylutece-oauth_context.xml like this :
     * <pre>
     * &lt;bean id="mylutece-oauth.credRetriever-XML" class="fr.paris.lutece.plugins.mylutece.modules.oauth.authentication.XMLCredentialRetriever"&gt;
     *         &lt;property name="format" value="xml" /&gt;
     * &lt;/bean&gt;
     * </pre>
     * @param httpResponse http response
     * @param user the user to fill
     * @param strFormat the format
     * @see IOAuthCredentialsRetriever
     */
    public static void doRetrieveUserInfo( HttpResponse httpResponse, OAuthUser user, String strFormat )
    {
        for ( IOAuthCredentialsRetriever retriever : SpringContextService.getBeansOfType( 
                IOAuthCredentialsRetriever.class ) )
        {
            if ( strFormat.equals( retriever.getFormat(  ) ) )
            {
                retriever.doRetrieveUserInfo( httpResponse, user );
            }
        }
    }
}
