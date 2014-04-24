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

import java.util.HashMap;
import java.util.Map;


/**
 *
 * AbstractOAuthCredentialsRetriever.
 * <br>
 * Set the tags map to enhance info parsing.
 * Keys of the map is {@link LuteceUser} properties, values are tag that should be looked for.
 * @see #DEFAULT_TAGS
 * @see LuteceUser#NAME_FAMILY
 */
public abstract class AbstractOAuthCredentialsRetriever implements IOAuthCredentialsRetriever
{
    /**
     * Map containing default tags : "last_name", "last-name", "name" for {@link LuteceUser#NAME_FAMILY},
     * "first-name", "first_name" for {@link LuteceUser#NAME_GIVEN}, "email" form {@link LuteceUser#HOME_INFO_ONLINE_EMAIL}
     */
    private static final Map<String, String[]> DEFAULT_TAGS = new HashMap<String, String[]>(  );

    static
    {
        DEFAULT_TAGS.put( LuteceUser.NAME_FAMILY, new String[] { "last-name", "last_name", "name", } );
        DEFAULT_TAGS.put( LuteceUser.NAME_GIVEN, new String[] { "first-name", "first_name", } );
        DEFAULT_TAGS.put( LuteceUser.HOME_INFO_ONLINE_EMAIL, new String[] { "email", } );
        DEFAULT_TAGS.put( LuteceUser.HOME_INFO_ONLINE_URI, new String[] { "site", } );
    }

    private String _strFormat;

    /**
     * Tags we are looking for. Defaulted to {@link #DEFAULT_TAGS}
     */
    private Map<String, String[]> _tags = DEFAULT_TAGS;

    /**
     * Sets the supported format
     * @param strFormat the supported format
     */
    public void setFormat( String strFormat )
    {
        _strFormat = strFormat;
    }

    /**
     *
     *{@inheritDoc}
     */
    public String getFormat(  )
    {
        return _strFormat;
    }

    /**
     * "Getter method" for {@link #_tags}.
     * <br>Keys of the map is {@link LuteceUser} properties, values are tag that should be looked for.
     * @return value of {@link #_tags}
     */
    public Map<String, String[]> getTags(  )
    {
        return _tags;
    }

    /**
     * "Setter method" for {@link #_tags}.
     * <br>Keys of the map is {@link LuteceUser} properties, values are tag that should be looked for.
     * @param tags new value of {@link #_tags}
     */
    public void setTags( Map<String, String[]> tags )
    {
        this._tags = tags;
    }
}
