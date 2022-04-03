package com.nxin.framework.etl.designer.controller.designer;

import com.nxin.framework.etl.designer.entity.basic.User;
import com.nxin.framework.etl.designer.entity.designer.Datasource;
import com.nxin.framework.etl.designer.enums.Constant;
import com.nxin.framework.etl.designer.enums.DatasourceType;
import com.nxin.framework.etl.designer.service.basic.UserService;
import com.nxin.framework.etl.designer.service.designer.DatasourceService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.database.DatabaseMetaInformation;
import org.pentaho.di.core.database.SqlScriptStatement;
import org.pentaho.di.core.encryption.Encr;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.logging.LoggingObjectType;
import org.pentaho.di.core.logging.SimpleLoggingObject;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.security.Principal;
import java.util.*;

@PreAuthorize("hasAuthority('ROOT') or hasAuthority('DATASOURCE') or hasAuthority('DESIGNER') or hasAuthority('REPORT') or hasAuthority('LAYOUT')")
@Slf4j
@RestController
@RequestMapping
public class PreviewDataController {
    @Autowired
    private UserService userService;
    @Autowired
    private DatasourceService datasourceService;
    public static final int MAX_BINARY_STRING_PREVIEW_SIZE = 1000000;

    @PostMapping("/preview")
    public ResponseEntity<Map<String, Object>> preview(@RequestBody Payload payload, Principal principal) {
        User loginUser = userService.one(principal.getName());
        Datasource datasource = datasourceService.one(payload.datasourceId, loginUser.getTenant().getId());
        if (datasource != null && datasource.getProject() != null && datasource.getProject().getUsers().contains(loginUser)) {
            Map<String, Object> result = new HashMap<>(0);
            List<String> headers = new ArrayList<>(0);
            List<Map<String, String>> records = new ArrayList<>(0);
            DatabaseMeta databaseMeta = new DatabaseMeta(datasource.getName(), DatasourceType.getValue(datasource.getCategory()), "JDBC", datasource.getHost(), datasource.getSchemaName(), datasource.getPort().toString(), datasource.getUsername(), Constant.PASSWORD_ENCRYPTED_PREFIX + Encr.encryptPassword(datasource.getPassword()));
            List<SqlScriptStatement> statements = databaseMeta.getDatabaseInterface().getSqlScriptStatements(payload.sql + Const.CR);
            Database db = new Database(new SimpleLoggingObject("Spoon", LoggingObjectType.SPOON, null), databaseMeta);
            try {
                db.connect();
                db.setQueryLimit(10);
                for (SqlScriptStatement sql : statements) {
                    List<Object[]> rows = db.getRows(sql.getStatement(), -1);
                    RowMetaInterface rowMeta = db.getReturnRowMeta();
                    for (int i = 0; i < rowMeta.size(); i++) {
                        ValueMetaInterface v = rowMeta.getValueMeta(i);
                        headers.add(v.getName());
                    }
                    for (Object[] row : rows) {
                        Map<String, String> record = new HashMap<>(0);
                        for (int c = 0; c < rowMeta.size(); c++) {
                            ValueMetaInterface v = rowMeta.getValueMeta(c);
                            try {
                                String show = v.getString(row[c]);
                                if (v.isBinary() && show != null && show.length() > MAX_BINARY_STRING_PREVIEW_SIZE) {
                                    show = show.substring(0, MAX_BINARY_STRING_PREVIEW_SIZE);
                                }
                                record.put(v.getName(), show);
                            } catch (KettleValueException e) {
                                log.error(e.toString());
                            }
                        }
                        records.add(record);
                    }
                }
            } catch (KettleDatabaseException e) {
                return ResponseEntity.status(Constant.EXCEPTION_SQL_GRAMMAR).build();
            }
            result.put("headers", headers);
            result.put("records", records);
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
    }

    @PostMapping("/structure")
    public ResponseEntity<List<Map<String, Object>>> structure(@RequestBody Payload payload, Principal principal) {
        User loginUser = userService.one(principal.getName());
        Datasource datasource = datasourceService.one(payload.datasourceId, loginUser.getTenant().getId());
        if (datasource != null && datasource.getProject() != null && datasource.getProject().getUsers().contains(loginUser)) {
            List<Map<String, Object>> response = new ArrayList<>(0);
            DatabaseMeta databaseMeta = new DatabaseMeta(datasource.getName(), DatasourceType.getValue(datasource.getCategory()), null, datasource.getHost(), datasource.getSchemaName(), datasource.getPort().toString(), datasource.getUsername(), Constant.PASSWORD_ENCRYPTED_PREFIX + Encr.encryptPassword(datasource.getPassword()));
            Database db = new Database(new SimpleLoggingObject("Spoon", LoggingObjectType.SPOON, null), databaseMeta);
            try {
                db.connect();
                if ("db".equals(payload.category)) {
                    String key;
                    if (Arrays.asList(db.getCatalogs()).contains(datasource.getSchemaName())) {
                        key = datasource.getSchemaName();
                    } else if (Arrays.asList(db.getSchemas()).contains(datasource.getSchemaName())) {
                        key = datasource.getSchemaName();
                    } else {
                        key = null;
                    }
                    DatabaseMetaInformation databaseMetaInformation = new DatabaseMetaInformation(databaseMeta);
                    databaseMetaInformation.setDbInfo(db.getDatabaseMeta());
                    databaseMetaInformation.getData(new SimpleLoggingObject("Spoon", LoggingObjectType.SPOON, null), null);
                    Map<String, Collection<String>> tableMap = databaseMetaInformation.getTableMap();
                    if (tableMap != null) {
                        List<String> tableKeys = new ArrayList<>(0);
                        if (key != null) {
                            tableKeys.add(key);
                        } else {
                            tableKeys.addAll(tableMap.keySet());
                        }
                        Collections.sort(tableKeys);
                        for (String schema : tableKeys) {
                            if (tableMap.containsKey(schema)) {
                                List<String> tables = new ArrayList<>(tableMap.get(schema));
                                Collections.sort(tables);
                                for (String table : tables) {
                                    Map<String, Object> result = new HashMap<>(0);
                                    result.put("name", table);
                                    result.put("type", "TABLE");
                                    response.add(result);
                                }
                            }
                        }
                    }
                    Map<String, Collection<String>> viewMap = databaseMetaInformation.getViewMap();
                    if (viewMap != null) {
                        List<String> viewKeys = new ArrayList<>(0);
                        if (key != null) {
                            viewKeys.add(key);
                        } else {
                            viewKeys.addAll(viewMap.keySet());
                        }
                        Collections.sort(viewKeys);
                        for (String schema : viewKeys) {
                            if (viewMap.containsKey(schema)) {
                                List<String> views = new ArrayList<>(viewMap.get(schema));
                                Collections.sort(views);
                                for (String view : views) {
                                    Map<String, Object> result = new HashMap<>(0);
                                    result.put("name", view);
                                    result.put("type", "VIEW");
                                    response.add(result);
                                }
                            }
                        }
                    }
                } else {
                    db.getFirstRows(payload.name, 1);
                    RowMetaInterface rowMeta = db.getReturnRowMeta();
                    for (int i = 0; i < rowMeta.size(); i++) {
                        Map<String, Object> meta = new HashMap<>(2);
                        ValueMetaInterface v = rowMeta.getValueMeta(i);
                        meta.put("name", v.getName());
                        meta.put("info", v.toStringMeta());
                        response.add(meta);
                    }
                }
            } catch (KettleDatabaseException e) {
                log.error(e.getMessage());
            } finally {
                db.close();
            }
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
    }

    @Data
    public static class Payload implements Serializable {
        private Long datasourceId;
        private String name;
        private String category;
        private String sql;
        private List<String> filenames;
        private String sheetName;
    }
}
