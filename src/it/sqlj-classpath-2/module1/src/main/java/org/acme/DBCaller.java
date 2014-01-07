package org.acme;

abstract public class DBCaller
{
    public static class SqljContext
        extends sqlj.runtime.ref.ConnectionContextImpl
        implements sqlj.runtime.ConnectionContext
    {
        private static java.util.Map m_typeMap = null;

        private static final sqlj.runtime.ref.ProfileGroup profiles = new sqlj.runtime.ref.ProfileGroup();

        private static SqljContext defaultContext = null;

        public SqljContext( java.sql.Connection conn )
            throws java.sql.SQLException
        {
            super( profiles, conn );
        }

        public SqljContext( java.lang.String url, java.lang.String user, java.lang.String password, boolean autoCommit )
            throws java.sql.SQLException
        {
            super( profiles, url, user, password, autoCommit );
        }

        public SqljContext( java.lang.String url, java.util.Properties info, boolean autoCommit )
            throws java.sql.SQLException
        {
            super( profiles, url, info, autoCommit );
        }

        public SqljContext( java.lang.String url, boolean autoCommit )
            throws java.sql.SQLException
        {
            super( profiles, url, autoCommit );
        }

        public SqljContext( sqlj.runtime.ConnectionContext other )
            throws java.sql.SQLException
        {
            super( profiles, other );
        }

        public static SqljContext getDefaultContext()
        {
            if ( defaultContext == null )
            {
                java.sql.Connection conn = sqlj.runtime.RuntimeContext.getRuntime().getDefaultConnection();
                if ( conn != null )
                {
                    try
                    {
                        defaultContext = new SqljContext( conn );
                    }
                    catch ( java.sql.SQLException e )
                    {
                    }
                }
            }
            return defaultContext;
        }

        public static void setDefaultContext( SqljContext ctx )
        {
            defaultContext = ctx;
        }

        public static java.lang.Object getProfileKey( sqlj.runtime.profile.Loader loader, java.lang.String profileName )
            throws java.sql.SQLException
        {
            return profiles.getProfileKey( loader, profileName );
        }

        public static sqlj.runtime.profile.Profile getProfile( java.lang.Object profileKey )
        {
            return profiles.getProfile( profileKey );
        }

        public java.util.Map getTypeMap()
        {
            return m_typeMap;
        }
    }
}