package com.nxin.framework.etl.designer.converter.designer.transform.shell;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.nxin.framework.etl.designer.converter.designer.transform.ResponseMeta;
import com.nxin.framework.etl.designer.converter.designer.transform.TransformConvertChain;
import com.nxin.framework.etl.designer.entity.designer.Datasource;
import com.nxin.framework.etl.designer.enums.Constant;
import com.nxin.framework.etl.designer.enums.DatasourceType;
import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl;
import lombok.extern.slf4j.Slf4j;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.encryption.Encr;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.sql.ExecSQLMeta;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class ExecSqlChain extends TransformConvertChain {
    @Override
    public ResponseMeta parse(mxCell cell, TransMeta transMeta) throws JsonProcessingException {
        if (cell.isVertex() && "ExecSQLMeta".equalsIgnoreCase(cell.getStyle())) {
            DeferredElementImpl value = (DeferredElementImpl) cell.getValue();
            Map<String, Object> formAttributes = objectMapper.readValue(value.getAttribute("form"), new TypeReference<Map<String, Object>>() {
            });
            ExecSQLMeta execSQLMeta = new ExecSQLMeta();
            List<String> sqlParameters = new ArrayList<>(0);
            String stepName = (String) formAttributes.get("name");
            String sql = (String) formAttributes.get("sql");
            boolean executeEachInputRow = (boolean) formAttributes.get("executeEachInputRow");
            boolean executeInOneStatement = (boolean) formAttributes.get("executeInOneStatement");
            boolean variableReplace = (boolean) formAttributes.get("variableReplace");
            boolean bindParameters = (boolean) formAttributes.get("bindParameters");
            boolean quoteString = (boolean) formAttributes.get("quoteString");

            int parallel = (int) formAttributes.get(Constant.ETL_PARALLEL);
            int databaseId = (int) formAttributes.get("datasource");
            Datasource datasource = datasourceService.one((long) databaseId);
            DatabaseMeta databaseMeta = new DatabaseMeta(datasource.getName(), DatasourceType.getValue(datasource.getCategory()), "JDBC", datasource.getHost(), datasource.getSchemaName(), datasource.getPort().toString(), datasource.getUsername(), Constant.PASSWORD_ENCRYPTED_PREFIX + Encr.encryptPassword(datasource.getPassword()));
            databaseMeta.setStreamingResults(datasource.getUseCursor());
            String parameters = datasource.getParameter();
            if (StringUtils.hasLength(parameters)) {
                List<Map<String, Object>> params = objectMapper.readValue(parameters, new TypeReference<List<Map<String, Object>>>() {
                });
                Properties properties = new Properties();
                for (Map<String, Object> param : params) {
                    for (Map.Entry<String, Object> entry : param.entrySet()) {
                        if (StringUtils.hasLength(entry.getKey()) && !ObjectUtils.isEmpty(entry.getValue())) {
                            databaseMeta.addExtraOption(DatasourceType.getValue(datasource.getCategory()), entry.getKey(), (String) entry.getValue());
                            properties.put(entry.getKey(), entry.getValue());
                        }
                    }
                }
                databaseMeta.setConnectionPoolingProperties(properties);
            }
            if (datasource.getUsePool()) {
                databaseMeta.setUsingConnectionPool(true);
                if (datasource.getPoolInitialSize() != null) {
                    databaseMeta.setInitialPoolSize(datasource.getPoolInitialSize());
                }
                if (datasource.getPoolMaxSize() != null) {
                    databaseMeta.setMaximumPoolSize(datasource.getPoolMaxSize());
                }
                if (datasource.getPoolInitial() != null) {
                    databaseMeta.getConnectionPoolingProperties().put("initialSize", datasource.getPoolInitial());
                }
                if (datasource.getPoolMaxActive() != null) {
                    databaseMeta.getConnectionPoolingProperties().put("maxActive", datasource.getPoolMaxActive());
                }
                if (datasource.getPoolMaxIdle() != null) {
                    databaseMeta.getConnectionPoolingProperties().put("maxIdle", datasource.getPoolMaxIdle());
                }
                if (datasource.getPoolMinIdle() != null) {
                    databaseMeta.getConnectionPoolingProperties().put("minIdle", datasource.getPoolMinIdle());
                }
                if (datasource.getPoolMaxWait() != null) {
                    databaseMeta.getConnectionPoolingProperties().put("maxWait", datasource.getPoolMaxWait());
                }
            }
            execSQLMeta.setSql(sql);
            execSQLMeta.setDatabaseMeta(databaseMeta);
            execSQLMeta.setParams(bindParameters);
            execSQLMeta.setExecutedEachInputRow(executeEachInputRow);
            execSQLMeta.setQuoteString(quoteString);
            execSQLMeta.setSingleStatement(executeInOneStatement);
            execSQLMeta.setVariableReplacementActive(variableReplace);
            if (executeEachInputRow) {
                List<Map<String, String>> searchMappingData = (List<Map<String, String>>) formAttributes.get("parameters");
                for (Map<String, String> fieldMapping : searchMappingData) {
                    sqlParameters.add(fieldMapping.get("parameter"));
                }
            }
            execSQLMeta.setArguments(sqlParameters.toArray(new String[0]));

            StepMeta stepMeta = new StepMeta(stepName, execSQLMeta);
            mxGeometry geometry = cell.getGeometry();
            if (formAttributes.containsKey("distribute")) {
                boolean distribute = (boolean) formAttributes.get("distribute");
                stepMeta.setDistributes(distribute);
            }
            stepMeta.setLocation(new Double(geometry.getX()).intValue(), new Double(geometry.getY()).intValue());
            stepMeta.setDraw(true);
            stepMeta.setCopies(parallel);
            return new ResponseMeta(cell.getId(), stepMeta, databaseMeta);
        } else {
            return next.parse(cell, transMeta);
        }
    }

    @Override
    public void callback(TransMeta transMeta, Map<String, String> idNameMapping) {

    }
}
