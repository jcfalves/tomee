package org.apache.tomee.jdbc;

import org.apache.openejb.jee.EjbJar;
import org.apache.openejb.jee.SingletonBean;
import org.apache.openejb.junit.ApplicationComposer;
import org.apache.openejb.junit.Configuration;
import org.apache.openejb.junit.Module;
import org.apache.openejb.resource.jdbc.managed.local.ManagedConnection;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(ApplicationComposer.class)
public class TomcatPoolTest {
    private static final String URL = "jdbc:hsqldb:mem:managed;hsqldb.tx=MVCC"; // mvcc otherwise multiple transaction tests will fail
    private static final String USER = "sa";
    private static final String PASSWORD = "";
    private static final String TABLE = "PUBLIC.MANAGED_DATASOURCE_TEST";

    @EJB
    private Persister persistManager;

    @BeforeClass
    public static void createTable() throws SQLException, ClassNotFoundException {
        Class.forName("org.hsqldb.jdbcDriver");

        final Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        final Statement statement = connection.createStatement();
        statement.execute("CREATE TABLE " + TABLE + "(ID INTEGER)");
        statement.close();
        connection.commit();
        connection.close();
    }

    @Configuration
    public Properties config() {
        final Properties p = new Properties();
        p.put("openejb.jdbc.datasource-creator", TomEEDataSourceCreator.class.getName());

        p.put("managed", "new://Resource?type=DataSource");
        p.put("managed.JdbcDriver", "org.hsqldb.jdbcDriver");
        p.put("managed.JdbcUrl", URL);
        p.put("managed.UserName", USER);
        p.put("managed.Password", PASSWORD);
        p.put("managed.JtaManaged", "true");
        return p;
    }

    @Module
    public EjbJar app() throws Exception {
        return new EjbJar()
                .enterpriseBean(new SingletonBean(Persister.class).localBean())
                .enterpriseBean(new SingletonBean(OtherPersister.class).localBean());
    }

    @LocalBean
    @Singleton
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public static class OtherPersister {
        @Resource(name = "managed")
        private DataSource ds;

        @Resource
        private EJBContext context;

        public void save() throws SQLException {
            TomcatPoolTest.save(ds, 10);
        }

        public void saveAndRollback() throws SQLException {
            TomcatPoolTest.save(ds, 11);
            context.setRollbackOnly();
        }
    }

    @LocalBean
    @Singleton
    public static class Persister {
        @Resource(name = "managed")
        private DataSource ds;

        @Resource
        private EJBContext context;

        @EJB
        private OtherPersister other;

        public void save() throws SQLException {
            TomcatPoolTest.save(ds, 1);
        }

        public void saveAndRollback() throws SQLException {
            TomcatPoolTest.save(ds, 2);
            context.setRollbackOnly();
        }

        public void saveTwice() throws SQLException {
            TomcatPoolTest.save(ds, 3);
            TomcatPoolTest.save(ds, 4);
        }

        public void rollbackMultipleSave() throws SQLException {
            TomcatPoolTest.save(ds, 5);
            TomcatPoolTest.save(ds, 6);
            context.setRollbackOnly();
        }

        public void saveInThisTxAndAnotherOne() throws SQLException {
            TomcatPoolTest.save(ds, 7);
            other.save();
        }

        public void saveInThisTxAndRollbackInAnotherOne() throws SQLException {
            TomcatPoolTest.save(ds, 8);
            other.saveAndRollback();
        }
    }

    @Test
    public void commit() throws SQLException {
        persistManager.save();
        assertTrue(exists(1));
    }

    @Test
    public void rollback() throws SQLException {
        persistManager.saveAndRollback();
        assertFalse(exists(2));
    }

    @Test
    public void commit2() throws SQLException {
        persistManager.saveTwice();
        assertTrue(exists(3));
        assertTrue(exists(4));
    }

    @Test
    public void rollback2() throws SQLException {
        persistManager.rollbackMultipleSave();
        assertFalse(exists(5));
        assertFalse(exists(6));
    }

    @Test
    public void saveDifferentTx() throws SQLException {
        persistManager.saveInThisTxAndAnotherOne();
        assertTrue(exists(7));
        assertTrue(exists(10));
    }

    @Test
    public void saveRollbackDifferentTx() throws SQLException {
        persistManager.saveInThisTxAndRollbackInAnotherOne();
        assertTrue(exists(8));
        assertFalse(exists(12));
    }

    @After
    public void checkTxMapIsEmpty() throws Exception { // avoid memory leak
        final Field map = ManagedConnection.class.getDeclaredField("CONNECTION_BY_TX");
        map.setAccessible(true);
        final Map<?, ?> instance = (Map<?, ?>) map.get(null);
        assertEquals(0, instance.size());
    }

    private static boolean exists(int id) throws SQLException {
        final Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        final Statement statement = connection.createStatement();
        final ResultSet result = statement.executeQuery("SELECT count(*) AS NB FROM " + TABLE + " WHERE ID = " + id);
        try {
            assertTrue(result.next());
            return result.getInt(1) == 1;
        } finally {
            statement.close();
            connection.close();
        }
    }

    private static void save(final DataSource ds, int id) throws SQLException {
        execute(ds, "INSERT INTO " + TABLE + "(ID) VALUES(" + id + ")");
    }

    private static void execute(final DataSource ds, final String sql) throws SQLException {
        final Connection connection = ds.getConnection();
        final Statement statement = connection.createStatement();
        statement.executeUpdate(sql);
        statement.close();
        connection.close();
    }
}
