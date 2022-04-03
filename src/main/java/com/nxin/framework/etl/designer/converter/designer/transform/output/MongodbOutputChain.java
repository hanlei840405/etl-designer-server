package com.nxin.framework.etl.designer.converter.designer.transform.output;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.nxin.framework.etl.designer.converter.designer.transform.ResponseMeta;
import com.nxin.framework.etl.designer.converter.designer.transform.TransformConvertChain;
import com.nxin.framework.etl.designer.enums.Constant;
import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl;
import lombok.extern.slf4j.Slf4j;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.mongodboutput.MongoDbOutputMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class MongodbOutputChain extends TransformConvertChain {
    @Override
    public ResponseMeta parse(mxCell cell, TransMeta transMeta) throws JsonProcessingException {
        if (cell.isVertex() && "MongoDbOutputMeta".equalsIgnoreCase(cell.getStyle())) {
            DeferredElementImpl value = (DeferredElementImpl) cell.getValue();
            Map<String, Object> formAttributes = objectMapper.readValue(value.getAttribute("form"), new TypeReference<Map<String, Object>>() {
            });
            MongoDbOutputMeta mongoDbOutputMeta = new MongoDbOutputMeta();
            mongoDbOutputMeta.setAuthenticationPassword((String) formAttributes.get("password"));
            mongoDbOutputMeta.setUseKerberosAuthentication((Boolean) formAttributes.get("kerberos"));
            mongoDbOutputMeta.setAuthenticationUser((String) formAttributes.get("username"));
            mongoDbOutputMeta.setPort((String) formAttributes.get("port"));
            mongoDbOutputMeta.setHostnames((String) formAttributes.get("host"));
            mongoDbOutputMeta.setUseAllReplicaSetMembers((Boolean) formAttributes.get("replica"));
            mongoDbOutputMeta.setAuthenticationDatabaseName((String) formAttributes.get("authenticateDB"));
            mongoDbOutputMeta.setBatchInsertSize((String) formAttributes.get("batchInsertSize"));
            mongoDbOutputMeta.setModifierUpdate((Boolean) formAttributes.get("modifierUpdate"));
            mongoDbOutputMeta.setMulti((Boolean) formAttributes.get("multiUpdate"));
            mongoDbOutputMeta.setTruncate((Boolean) formAttributes.get("truncate"));
            mongoDbOutputMeta.setUpdate((Boolean) formAttributes.get("update"));
            mongoDbOutputMeta.setWriteRetries((String) formAttributes.get("writeRetryTimes"));
            mongoDbOutputMeta.setUpsert((Boolean) formAttributes.get("upSert"));
            mongoDbOutputMeta.setWriteRetryDelay((String) formAttributes.get("delay"));
            mongoDbOutputMeta.setAuthenticationMechanism((String) formAttributes.get("mechanism"));
            mongoDbOutputMeta.setCollection((String) formAttributes.get("collection"));
            mongoDbOutputMeta.setConnectTimeout((String) formAttributes.get("connectTimeout"));
            mongoDbOutputMeta.setDbName((String) formAttributes.get("database"));
            mongoDbOutputMeta.setJournal((Boolean) formAttributes.get("journaled"));
            mongoDbOutputMeta.setReadPreference((String) formAttributes.get("readPreference"));
            mongoDbOutputMeta.setSocketTimeout((String) formAttributes.get("socketTimeout"));
            mongoDbOutputMeta.setUseSSLSocketFactory((Boolean) formAttributes.get("ssl"));
            mongoDbOutputMeta.setWriteConcern((String) formAttributes.get("writeCorn"));
            mongoDbOutputMeta.setWTimeout((String) formAttributes.get("writeCornTimeout"));
            List<Map<String, String>> fieldMappings = (List<Map<String, String>>) formAttributes.get("fieldMapping");
            List<MongoDbOutputMeta.MongoField> mongoFields = new ArrayList<>(0);
            for (Map<String, String> mapping : fieldMappings) {
                MongoDbOutputMeta.MongoField mongoField = new MongoDbOutputMeta.MongoField();
                mongoField.m_incomingFieldName = mapping.get("source");
                mongoField.m_mongoDocPath = mapping.get("target");
                mongoField.m_useIncomingFieldNameAsMongoFieldName = "Y".equals(mapping.get("useSourceField"));
                mongoField.insertNull = "INSERT_NULL".equals(mapping.get("nullValue"));
                mongoField.m_JSON = "Y".equals(mapping.get("json"));
                mongoField.m_updateMatchField = "Y".equals(mapping.get("match"));
                mongoField.m_modifierUpdateOperation = mapping.get("modifierMode");
                mongoField.m_modifierOperationApplyPolicy = mapping.get("modifierPolicy");
                mongoFields.add(mongoField);
            }
            mongoDbOutputMeta.setMongoFields(mongoFields);
            List<MongoDbOutputMeta.MongoIndex> mongoIndices = new ArrayList<>(0);
            List<Map<String, String>> indices = (List<Map<String, String>>) formAttributes.get("indices");
            for (Map<String, String> index : indices) {
                MongoDbOutputMeta.MongoIndex mongoIndex = new MongoDbOutputMeta.MongoIndex();
                mongoIndex.m_pathToFields = index.get("index");
                mongoIndex.m_drop = "Drop".equals(index.get("operation"));
                mongoIndex.m_unique = "Y".equals(index.get("unique"));
                mongoIndex.m_sparse = "Y".equals(index.get("sparse"));
                mongoIndices.add(mongoIndex);
            }
            mongoDbOutputMeta.setMongoIndexes(mongoIndices);
            StepMeta stepMeta = new StepMeta((String) formAttributes.get("name"), mongoDbOutputMeta);
            int parallel = (int) formAttributes.get(Constant.ETL_PARALLEL);
            stepMeta.setCopies(parallel);
            mxGeometry geometry = cell.getGeometry();
            if (formAttributes.containsKey("distribute")) {
                boolean distribute = (boolean) formAttributes.get("distribute");
                stepMeta.setDistributes(distribute);
            }
            stepMeta.setLocation(new Double(geometry.getX()).intValue(), new Double(geometry.getY()).intValue());
            stepMeta.setDraw(true);
            return new ResponseMeta(cell.getId(), stepMeta, null);
        } else {
            return next.parse(cell, transMeta);
        }
    }

    @Override
    public void callback(TransMeta transMeta, Map<String, String> idNameMapping) {

    }
}
