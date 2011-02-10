--
-- Table struture for mylutece_database_user
--
DROP TABLE IF EXISTS mylutece_oauth_authentication;
CREATE TABLE mylutece_oauth_authentication (
  auth_name varchar(100) NOT NULL,
  auth_service_name varchar(255) DEFAULT '' NOT NULL,
  auth_icon_url varchar(255) DEFAULT '',
  request_token_url varchar(255) DEFAULT '' NOT NULL,
  access_token_url varchar(255) DEFAULT '' NOT NULL,
  authorize_url varchar(255) DEFAULT '' NOT NULL,
  consumer_key varchar(255) DEFAULT '' NOT NULL,
  consumer_secret varchar(255) DEFAULT '' NOT NULL,
  credential_url varchar(255) DEFAULT '',
  credential_format varchar(255) DEFAULT '',
  PRIMARY KEY  (auth_name)
);
