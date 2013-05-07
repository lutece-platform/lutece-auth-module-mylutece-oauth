/*
 * Copyright (c) 2002-2013, Mairie de Paris
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

import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.util.AppLogService;

import net.sf.json.JSONObject;

import org.apache.http.HttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.Map.Entry;


/**
 * Parses JSON to retrieve credentials.<br>
 * Uses {@link AbstractOAuthCredentialsRetriever#getTags()}
 * @see AbstractOAuthCredentialsRetriever
 */
public class JSONCredentialRetriever extends AbstractOAuthCredentialsRetriever
{
    /**
     *
     *{@inheritDoc}
     */
    public void doRetrieveUserInfo( HttpResponse httpResponse, OAuthUser user )
    {
        JSONObject json = JSONObject.fromObject( readResponse( httpResponse ) );

        for ( Entry<String, String[]> entry : getTags(  ).entrySet(  ) )
        {
            String strKey = entry.getKey(  );

            for ( String strJSONKey : entry.getValue(  ) )
            {
                if ( json.containsKey( strJSONKey ) )
                {
                    String strValue = json.getString( strJSONKey );

                    if ( AppLogService.isDebugEnabled(  ) )
                    {
                        AppLogService.debug( "Retrieved " + strValue + " for " + entry.getKey(  ) );
                    }

                    user.setUserInfo( strKey, strValue );

                    if ( LuteceUser.NAME_GIVEN.equals( strKey ) )
                    {
                        user.setName( strValue );
                    }

                    break;
                }
            }
        }
    }

    /**
     * Reads the responses and returns it as String
     * @param httpResponse the response to read
     * @return the String.
     */
    private String readResponse( HttpResponse httpResponse )
    {
    	BufferedReader buffer = null;
        try
        {
            StringBuilder sbResponse = new StringBuilder(  );
            InputStreamReader streamReader = new InputStreamReader( httpResponse.getEntity(  ).getContent(  ) );

            buffer = new BufferedReader( streamReader );

            String line = buffer.readLine(  );

            while ( line != null )
            {
                sbResponse.append( line );
                line = buffer.readLine(  );
            }

            return sbResponse.toString(  );
        }
        catch ( IllegalStateException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }
        catch ( IOException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }
        finally
        {
        	if ( buffer != null )
        	{
        		try
				{
					buffer.close();
				}
				catch ( IOException e )
				{
					AppLogService.error( e.getMessage(), e );
				}
        	}
        }

        return null;
    }
}
