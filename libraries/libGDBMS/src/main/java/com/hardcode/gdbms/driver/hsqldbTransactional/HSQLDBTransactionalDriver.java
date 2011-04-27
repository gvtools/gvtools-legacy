package com.hardcode.gdbms.driver.hsqldbTransactional;

import java.sql.Connection;
import java.sql.SQLException;

import com.hardcode.gdbms.driver.hsqldb.HSQLDBDriver;
import com.hardcode.gdbms.engine.data.driver.AlphanumericDBDriver;
import com.hardcode.gdbms.engine.data.driver.DBTransactionalDriver;

/**
 * @author Fernando González Cortés
 */
public class HSQLDBTransactionalDriver extends HSQLDBDriver implements AlphanumericDBDriver, DBTransactionalDriver{

    public String getName() {
        return "GDBMS HSQLDB Transactional driver";
    }

    /**
     * @see com.hardcode.gdbms.engine.data.driver.DBTransactionalDriver#beginTrans(Connection)
     */
    public void beginTrans(Connection con) throws SQLException {
        execute(con, "SET AUTOCOMMIT FALSE");
    }

    /**
     * @see com.hardcode.gdbms.engine.data.driver.DBTransactionalDriver#commitTrans(Connection)
     */
    public void commitTrans(Connection con) throws SQLException {
        execute(con, "COMMIT;SET AUTOCOMMIT TRUE");
    }

    /**
     * @see com.hardcode.gdbms.engine.data.driver.DBTransactionalDriver#rollBackTrans(Connection)
     */
    public void rollBackTrans(Connection con) throws SQLException {
        execute(con, "ROLLBACK;SET AUTOCOMMIT TRUE");
    }
}
