package com.nxin.framework.etl.designer.converter.designer.transform.lookup;

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
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.databaselookup.DatabaseLookupMeta;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class DatabaseLookupChain extends TransformConvertChain {
    @Override
    public ResponseMeta parse(mxCell cell, TransMeta transMeta) throws JsonProcessingException {
        if (cell.isVertex() && "DatabaseLookupMeta".equalsIgnoreCase(cell.getStyle())) {
            DeferredElementImpl value = (DeferredElementImpl) cell.getValue();
            Map<String, Object> formAttributes = objectMapper.readValue(value.getAttribute("form"), new TypeReference<Map<String, Object>>() {
            });
            DatabaseLookupMeta databaseLookupMeta = new DatabaseLookupMeta();
            List<String> searchDbFields = new ArrayList<>(0);
            List<String> searchStreamConditions = new ArrayList<>(0);
            List<String> searchStreamFields = new ArrayList<>(0);
            List<String> searchStreamFields2 = new ArrayList<>(0);
            List<String> returnDbFields = new ArrayList<>(0);
            List<String> returnDbFieldNames = new ArrayList<>(0);
            List<String> returnDbFieldDefaultValues = new ArrayList<>(0);
            List<Integer> returnDbFieldCategory = new ArrayList<>(0);
            String stepName = (String) formAttributes.get("name");
            String schemaName = (String) formAttributes.get("schemaName");
            String tableName = (String) formAttributes.get("tableName");
            int parallel = (int) formAttributes.get(Constant.ETL_PARALLEL);
            int databaseId = (int) formAttributes.get("datasource");
            int cacheSize = (int) formAttributes.get("cacheSize");
            boolean useCache = (boolean) formAttributes.get("useCache");
            boolean loadAll = (boolean) formAttributes.get("loadAll");
            boolean ignoreLookupFailure = (boolean) formAttributes.get("ignoreLookupFailure");
            boolean multiReturnRecord = (boolean) formAttributes.get("multiReturnRecord");
            String sortBy = (String) formAttributes.get("sortBy");
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
            databaseLookupMeta.setDatabaseMeta(databaseMeta);
            databaseLookupMeta.setTablename(tableName);
            databaseLookupMeta.setSchemaName(schemaName);
            databaseLookupMeta.setCached(useCache);
            databaseLookupMeta.setCacheSize(cacheSize);
            databaseLookupMeta.setLoadingAllDataInCache(loadAll);
            databaseLookupMeta.setEatingRowOnLookupFailure(ignoreLookupFailure);
            databaseLookupMeta.setOrderByClause(sortBy);
            databaseLookupMeta.setFailingOnMultipleResults(multiReturnRecord);
            List<Map<String, String>> searchMappingData = (List<Map<String, String>>) formAttributes.get("searchMappingData");
            for (Map<String, String> fieldMapping : searchMappingData) {
                searchDbFields.add(fieldMapping.get("target"));
                searchStreamConditions.add(fieldMapping.get("condition"));
                searchStreamFields.add(fieldMapping.get("source"));
                searchStreamFields2.add(fieldMapping.get("source2"));
            }
            List<Map<String, String>> fieldMappingData = (List<Map<String, String>>) formAttributes.get("fieldMappingData");
            for (Map<String, String> fieldMapping : fieldMappingData) {
                returnDbFields.add(fieldMapping.get("target"));
                returnDbFieldNames.add(fieldMapping.get("fieldName"));
                returnDbFieldDefaultValues.add(fieldMapping.get("defaultValue"));
                returnDbFieldCategory.add(ValueMetaFactory.getIdForValueMeta(fieldMapping.get("category")));
            }
            databaseLookupMeta.setTableKeyField(searchDbFields.toArray(new String[0]));
            databaseLookupMeta.setKeyCondition(searchStreamConditions.toArray(new String[0]));
            databaseLookupMeta.setStreamKeyField1(searchStreamFields.toArray(new String[0]));
            databaseLookupMeta.setStreamKeyField2(searchStreamFields2.toArray(new String[0]));
            databaseLookupMeta.setReturnValueDefault(returnDbFieldDefaultValues.toArray(new String[0]));
            databaseLookupMeta.setReturnValueDefaultType(returnDbFieldCategory.stream().mapToInt(Integer::valueOf).toArray());
            databaseLookupMeta.setReturnValueField(returnDbFields.toArray(new String[0]));
            databaseLookupMeta.setReturnValueNewName(returnDbFieldNames.toArray(new String[0]));


            StepMeta stepMeta = new StepMeta(stepName, databaseLookupMeta);
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
