package org.apache.tomee.jdbc;

import org.apache.openejb.monitoring.LocalMBeanServer;
import org.apache.openejb.resource.jdbc.pool.PoolDataSourceCreator;
import org.apache.tomcat.jdbc.pool.ConnectionPool;
import org.apache.tomcat.jdbc.pool.DataSourceFactory;
import org.apache.tomcat.jdbc.pool.PoolConfiguration;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import javax.management.ObjectName;
import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

public class TomEEDataSourceCreator extends PoolDataSourceCreator {
    @Override
    public DataSource pool(final String name, final DataSource ds, Properties properties) {
        final Properties converted = new Properties();
        updateProperties(properties, converted, null);

        final PoolConfiguration config = build(PoolProperties.class, converted);
        config.setDataSource(ds);
        final ConnectionPool pool;
        try {
            pool = new ConnectionPool(config);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        return build(TomEEDataSource.class, new TomEEDataSource(pool, name), properties);
    }

    @Override
    public DataSource pool(final String name, final String driver, final Properties properties) {
        final Properties converted = new Properties();
        converted.setProperty("name", name);
        updateProperties(properties, converted, driver);
        return new TomEEDataSource(DataSourceFactory.parsePoolProperties(converted), name);
    }

    private void updateProperties(final Properties properties, final Properties converted, final String driver) {
        // some compatibility with old dbcp style
        if (driver != null) {
            converted.setProperty("driverClassName", driver);
        }
        if (properties.containsKey("JdbcUrl")) {
            converted.setProperty("url", properties.getProperty("JdbcUrl"));
        }
        if (properties.containsKey("user")) {
            converted.setProperty("username", properties.getProperty("user"));
        }
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            final String value = entry.getValue().toString().trim();
            if (!value.isEmpty()) {
                converted.put(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public void doDestroy(final DataSource object) throws Throwable {
        org.apache.tomcat.jdbc.pool.DataSource ds = (org.apache.tomcat.jdbc.pool.DataSource) object;
        ds.close(true);
        ds.postDeregister();
    }

    public static class TomEEDataSource extends org.apache.tomcat.jdbc.pool.DataSource {
        public TomEEDataSource(final ConnectionPool pool, final String name) {
            this.pool = pool;
            try {
                preRegister(LocalMBeanServer.get(), new ObjectName("openejb", "name", name));
            } catch (Exception ignored) {
                // ignored
            }
        }

        public TomEEDataSource(final PoolConfiguration poolConfiguration, final String name) {
            super(poolConfiguration);
            try { // just to force the pool to be created
                getConnection().close();
            } catch (Throwable ignored) {
                // no-op
            }
            try {
                preRegister(LocalMBeanServer.get(), new ObjectName("openejb", "name", name));
            } catch (Exception ignored) {
                // ignored
            }
        }

        @Override
        public Connection getConnection() throws SQLException {
            final Connection connection = super.getConnection();
            return (Connection) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                    new Class<?>[] { Connection.class }, new ContantHashCodeHandler(connection, connection.hashCode()));
        }

        @Override
        public Connection getConnection(final String u, final String p) throws SQLException {
            final Connection connection = super.getConnection(u, p);
            return (Connection) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                    new Class<?>[] { Connection.class }, new ContantHashCodeHandler(connection, connection.hashCode()));
        }
    }

    private static class ContantHashCodeHandler implements InvocationHandler {
        private final Object delegate;
        private final int hashCode;

        public ContantHashCodeHandler(final Object object, int hashCode) {
            this.delegate = object;
            this.hashCode = hashCode;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("hashCode".equals(method.getName())) {
                return hashCode;
            }
            return method.invoke(delegate, args);
        }
    }
}