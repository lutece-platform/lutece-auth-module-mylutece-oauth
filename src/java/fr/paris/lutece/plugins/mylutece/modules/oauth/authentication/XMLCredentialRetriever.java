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

import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.util.AppLogService;

import org.apache.http.HttpResponse;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

import java.io.IOException;

import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


/**
 *
 * XMLCredentialRetriever : parses credential XML.
 * <br>
 * Set the tags map to enhance info parsing.
 * Keys of the map is {@link LuteceUser} properties, values are tag that should be looked for.
 * @see AbstractOAuthCredentialsRetriever#DEFAULT_TAGS
 * @see LuteceUser#NAME_FAMILY
 */
public class XMLCredentialRetriever extends AbstractOAuthCredentialsRetriever
{
    /**
     *
     *{@inheritDoc}
     */
    public void doRetrieveUserInfo( HttpResponse httpResponse, OAuthUser user )
    {
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(  );
            DocumentBuilder builder = factory.newDocumentBuilder(  );
            Document doc = builder.parse( httpResponse.getEntity(  ).getContent(  ) );

            for ( Entry<String, String[]> entry : getTags(  ).entrySet(  ) )
            {
                for ( String strTag : entry.getValue(  ) )
                {
                    NodeList nodeList = doc.getElementsByTagName( strTag );

                    if ( ( nodeList != null ) && ( nodeList.getLength(  ) > 0 ) )
                    {
                        String strValue = nodeList.item( 0 ).getFirstChild(  ).getNodeValue(  );

                        if ( AppLogService.isDebugEnabled(  ) )
                        {
                            AppLogService.debug( "Retrieved " + strValue + " for " + entry.getKey(  ) );
                        }

                        user.setUserInfo( entry.getKey(  ), strValue );

                        if ( entry.getKey(  ).equals( LuteceUser.NAME_FAMILY ) )
                        {
                            user.setName( strValue );
                        }

                        break; // no need to find other values
                    }
                }
            }
        }
        catch ( SAXException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }
        catch ( ParserConfigurationException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }
        catch ( IllegalStateException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }
        catch ( IOException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }
    }
}
