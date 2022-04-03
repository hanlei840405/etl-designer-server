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
import org.pentaho.di.trans.steps.elasticsearchbulk.ElasticSearchBulkMeta;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ElasticSearchBulkChain extends TransformConvertChain {
    @Override
    public ResponseMeta parse(mxCell cell, TransMeta transMeta) throws JsonProcessingException {
        if (cell.isVertex() && "ElasticSearchBulkMeta".equalsIgnoreCase(cell.getStyle())) {
            DeferredElementImpl value = (DeferredElementImpl) cell.getValue();
            Map<String, Object> formAttributes = objectMapper.readValue(value.getAttribute("form"), new TypeReference<Map<String, Object>>() {
            });
            ElasticSearchBulkMeta elasticSearchBulkMeta = new ElasticSearchBulkMeta();
            String stepName = (String) formAttributes.get("name");
            String index = (String) formAttributes.get("index");
            String type = (String) formAttributes.get("type");
            String idField = (String) formAttributes.get("idField");
            Integer batchSize = (Integer) formAttributes.get("batchSize");
            String time = (String) formAttributes.get("time");
            String timeUnit = (String) formAttributes.get("timeUnit");
            boolean stopOnError = (boolean) formAttributes.get("stopOnError");
            boolean overwriteIfExist = (boolean) formAttributes.get("overwriteIfExist");
            boolean outputRows = (boolean) formAttributes.get("outputRows");
            boolean fromJson = (boolean) formAttributes.get("fromJson");
            String outputId = (String) formAttributes.get("outputId");
            String jsonField = (String) formAttributes.get("jsonField");
            elasticSearchBulkMeta.setIndex(index);
            elasticSearchBulkMeta.setType(type);
            elasticSearchBulkMeta.setIdInField(idField);
            elasticSearchBulkMeta.setBatchSize(batchSize.toString());
            elasticSearchBulkMeta.setTimeOut(time);
            if (StringUtils.hasLength(timeUnit)) {
                elasticSearchBulkMeta.setTimeoutUnit(TimeUnit.valueOf(timeUnit));
            }
            elasticSearchBulkMeta.setStopOnError(stopOnError);
            elasticSearchBulkMeta.setOverWriteIfSameId(overwriteIfExist);
            elasticSearchBulkMeta.setUseOutput(outputRows);
            elasticSearchBulkMeta.setJsonInsert(fromJson);
            elasticSearchBulkMeta.setIdOutField(outputId);
            elasticSearchBulkMeta.setJsonField(jsonField);
            List<Map<String, String>> servers = (List<Map<String, String>>) formAttributes.get("servers");
            for (Map<String, String> server : servers) {
                elasticSearchBulkMeta.addServer(server.get("address"), Integer.parseInt(server.get("port")));
            }
            List<Map<String, String>> mappings = (List<Map<String, String>>) formAttributes.get("mappings");
            for (Map<String, String> mapping : mappings) {
                elasticSearchBulkMeta.addField(mapping.get("field"), mapping.get("target"));
            }
            List<Map<String, String>> settings = (List<Map<String, String>>) formAttributes.get("settings");
            for (Map<String, String> setting : settings) {
                elasticSearchBulkMeta.addSetting(setting.get("setting"), setting.get("value"));
            }
            elasticSearchBulkMeta.getXML();
            StepMeta stepMeta = new StepMeta(stepName, elasticSearchBulkMeta);
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
