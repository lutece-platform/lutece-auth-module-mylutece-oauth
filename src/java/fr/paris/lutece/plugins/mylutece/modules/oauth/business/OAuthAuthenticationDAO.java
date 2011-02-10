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
package fr.paris.lutece.plugins.mylutece.modules.oauth.business;

import fr.paris.lutece.plugins.mylutece.modules.oauth.authentication.OAuthAuthentication;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * OAuthAuthenticationDAO.
 */
public class OAuthAuthenticationDAO implements IOAuthAuthenticationDAO
{
    private static final String SQL_QUERY_DELETE = "DELETE FROM mylutece_oauth_authentication WHERE auth_name = ? ";
    private static final String SQL_QUERY_INSERT = "INSERT INTO mylutece_oauth_authentication " +
        "(auth_name, auth_service_name, auth_icon_url, request_token_url, access_token_url, authorize_url, consumer_key, consumer_secret, credential_url, credential_format ) " +
        " VALUES(?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE mylutece_oauth_authentication SET " +
        "auth_service_name = ?, auth_icon_url = ?, request_token_url = ?, access_token_url = ?, authorize_url = ?, consumer_key = ?, consumer_secret = ?, credential_url = ?, credential_format = ?" +
        "WHERE auth_name = ?";
    private static final String SQL_QUERY_SELECT = "SELECT auth_name, auth_service_name, auth_icon_url, request_token_url, " +
        "access_token_url, authorize_url, consumer_key, consumer_secret, credential_url, credential_format FROM mylutece_oauth_authentication ";
    private static final String SQL_QUERY_SELECT_ALL = SQL_QUERY_SELECT;
    private static final String SQL_QUERY_SELECT_BY_PRIMARY_KEY = SQL_QUERY_SELECT + " WHERE auth_name = ?";

    /**
     *
     *{@inheritDoc}
     */
    public void delete( String strIdAuthentication, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );

        daoUtil.setString( 1, strIdAuthentication );

        daoUtil.executeUpdate(  );

        daoUtil.free(  );
    }

    /**
     *
     *{@inheritDoc}
     */
    public void insert( OAuthAuthentication authentication, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        daoUtil.setString( 1, authentication.getName(  ) );
        setInsertOrUpdateValues( 2, authentication, daoUtil );

        daoUtil.executeUpdate(  );

        daoUtil.free(  );
    }

    /**
     *
     *{@inheritDoc}
     */
    public OAuthAuthentication load( String strIdAuthentication, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_PRIMARY_KEY, plugin );

        daoUtil.setString( 1, strIdAuthentication );

        daoUtil.executeQuery(  );

        OAuthAuthentication authAuthentication = null;

        if ( daoUtil.next(  ) )
        {
            authAuthentication = new OAuthAuthentication(  );
            load( authAuthentication, daoUtil );
        }

        daoUtil.free(  );

        return authAuthentication;
    }

    /**
     *
     *{@inheritDoc}
     */
    public List<OAuthAuthentication> selectListAuthentication( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ALL, plugin );

        daoUtil.executeQuery(  );

        List<OAuthAuthentication> listAuthentication = new ArrayList<OAuthAuthentication>(  );

        while ( daoUtil.next(  ) )
        {
            OAuthAuthentication authentication = new OAuthAuthentication(  );
            load( authentication, daoUtil );
            listAuthentication.add( authentication );
        }

        daoUtil.free(  );

        return listAuthentication;
    }

    /**
     *
     *{@inheritDoc}
     */
    public void store( OAuthAuthentication authentication, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        int nIndex = setInsertOrUpdateValues( 1, authentication, daoUtil );
        daoUtil.setString( nIndex, authentication.getName(  ) );

        daoUtil.executeUpdate(  );

        daoUtil.free(  );
    }

    /**
     * Sets values
     * @param nStartIndex start index
     * @param authAuthentication the authentication
     * @param daoUtil daoUtil
     * @return the end index
     */
    private int setInsertOrUpdateValues( int nStartIndex, OAuthAuthentication authAuthentication, DAOUtil daoUtil )
    {
        int nIndex = nStartIndex;

        // auth_service_name, auth_icon_url, request_token_url, access_token_url, authorize_url, consumer_key, consumer_secret
        daoUtil.setString( nIndex++, authAuthentication.getAuthServiceName(  ) );
        daoUtil.setString( nIndex++, authAuthentication.getIconUrl(  ) );
        daoUtil.setString( nIndex++, authAuthentication.getRequestTokenEndpointUrl(  ) );
        daoUtil.setString( nIndex++, authAuthentication.getAccessTokenEndpointUrl(  ) );
        daoUtil.setString( nIndex++, authAuthentication.getAuthorizeWebsiteUrl(  ) );
        daoUtil.setString( nIndex++, authAuthentication.getConsumerKey(  ) );
        daoUtil.setString( nIndex++, authAuthentication.getConsumerSecret(  ) );
        daoUtil.setString( nIndex++, authAuthentication.getCredentialUrl(  ) );
        daoUtil.setString( nIndex++, authAuthentication.getCredentialFormat(  ) );

        return nIndex;
    }

    /**
     * Filds the authentication with daoUtil data.
     * @param authAuthentication the authentication to fill
     * @param daoUtil the daoUtil
     */
    private void load( OAuthAuthentication authAuthentication, DAOUtil daoUtil )
    {
        int nIndex = 1;
        // auth_name, auth_service_name, auth_icon_url, request_token_url, access_token_url, authorize_url, consumer_key, consumer_secret, credential_url, credential_format
        authAuthentication.setName( daoUtil.getString( nIndex++ ) );
        authAuthentication.setAuthServiceName( daoUtil.getString( nIndex++ ) );
        authAuthentication.setIconUrl( daoUtil.getString( nIndex++ ) );
        authAuthentication.setRequestTokenEndpointUrl( daoUtil.getString( nIndex++ ) );
        authAuthentication.setAccessTokenEndpointUrl( daoUtil.getString( nIndex++ ) );
        authAuthentication.setAuthorizeWebsiteUrl( daoUtil.getString( nIndex++ ) );
        authAuthentication.setConsumerKey( daoUtil.getString( nIndex++ ) );
        authAuthentication.setConsumerSecret( daoUtil.getString( nIndex++ ) );
        authAuthentication.setCredentialUrl( daoUtil.getString( nIndex++ ) );
        authAuthentication.setCredentialFormat( daoUtil.getString( nIndex++ ) );
    }
}
