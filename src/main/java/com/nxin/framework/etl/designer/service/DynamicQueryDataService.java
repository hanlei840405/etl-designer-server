package com.nxin.framework.etl.designer.service;

import com.nxin.framework.etl.designer.entity.designer.Datasource;
import com.nxin.framework.etl.designer.enums.Constant;
import com.nxin.framework.etl.designer.enums.DatasourceType;
import com.nxin.framework.etl.designer.service.designer.DatasourceService;
import lombok.SneakyThrows;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.database.SqlScriptStatement;
import org.pentaho.di.core.encryption.Encr;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.logging.LoggingObjectType;
import org.pentaho.di.core.logging.SimpleLoggingObject;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DynamicQueryDataService {
    @Autowired
    private DatasourceService datasourceService;

    public Map<String, Object> query(String name, String category, String host, String schemaName, String port, String username, String password, String sql) throws KettleDatabaseException, KettleValueException {
        Map<String, Object> result = new HashMap<>(0);
        List<String> headers = new ArrayList<>(0);
        List<Map<String, String>> records = new ArrayList<>(0);
        DatabaseMeta databaseMeta = new DatabaseMeta(name, DatasourceType.getValue(category), "JDBC", host, schemaName, port, username, Constant.PASSWORD_ENCRYPTED_PREFIX + Encr.encryptPassword(password));
        List<SqlScriptStatement> statements = databaseMeta.getDatabaseInterface().getSqlScriptStatements(sql + Const.CR);
        Database db = new Database(new SimpleLoggingObject("Spoon", LoggingObjectType.SPOON, null), databaseMeta);
        db.connect();
        for (SqlScriptStatement sss : statements) {
            List<Object[]> rows = db.getRows(sss.getStatement(), -1);
            RowMetaInterface rowMeta = db.getReturnRowMeta();
            for (int i = 0; i < rowMeta.size(); i++) {
                ValueMetaInterface v = rowMeta.getValueMeta(i);
                headers.add(v.getName());
            }
            for (Object[] row : rows) {
                Map<String, String> record = new HashMap<>(0);
                for (int c = 0; c < rowMeta.size(); c++) {
                    ValueMetaInterface v = rowMeta.getValueMeta(c);
                    record.put(v.getName(), v.getString(row[c]));
                }
                records.add(record);
            }
        }
        result.put("headers", headers);
        result.put("records", records);
        return result;
    }

    @SneakyThrows
    public boolean exist(String name, String category, String host, String schemaName, String port, String username, String password, String tableName) {
        DatabaseMeta databaseMeta = new DatabaseMeta(name, DatasourceType.getValue(category), "JDBC", host, schemaName, port, username, Constant.PASSWORD_ENCRYPTED_PREFIX + Encr.encryptPassword(password));
        Database db = new Database(new SimpleLoggingObject("Spoon", LoggingObjectType.SPOON, null), databaseMeta);
        db.connect();
        return db.checkTableExists(schemaName, tableName);
    }

    @SneakyThrows
    public boolean execute(String name, String category, String host, String schemaName, String port, String username, String password, String sql) {
        DatabaseMeta databaseMeta = new DatabaseMeta(name, DatasourceType.getValue(category), "JDBC", host, schemaName, port, username, Constant.PASSWORD_ENCRYPTED_PREFIX + Encr.encryptPassword(password));
        Database db = new Database(new SimpleLoggingObject("Spoon", LoggingObjectType.SPOON, null), databaseMeta);
        db.connect();
        return db.execStatement(sql).isSafeStop();
    }
}
