package com.nxin.framework.etl.designer.converter.designer.transform.convert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.nxin.framework.etl.designer.converter.designer.transform.ResponseMeta;
import com.nxin.framework.etl.designer.converter.designer.transform.TransformConvertChain;
import com.nxin.framework.etl.designer.enums.Constant;
import com.nxin.framework.etl.designer.utils.BooleanUtils;
import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl;
import lombok.extern.slf4j.Slf4j;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.uniquerows.UniqueRowsMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class UniqueRowsChain extends TransformConvertChain {

    @Override
    public ResponseMeta parse(mxCell cell, TransMeta transMeta) throws JsonProcessingException {
        if (cell.isVertex() && "UniqueRowsMeta".equalsIgnoreCase(cell.getStyle())) {
            DeferredElementImpl value = (DeferredElementImpl) cell.getValue();
            UniqueRowsMeta uniqueRowsMeta = new UniqueRowsMeta();
            Map<String, Object> formAttributes = objectMapper.readValue(value.getAttribute("form"), new TypeReference<Map<String, Object>>() {
            });
            String stepName = (String) formAttributes.get("name");
            Boolean useCounter = (Boolean) formAttributes.get("useCounter");
            String counterName = (String) formAttributes.get("counterName");
            Boolean useRedirect = (Boolean) formAttributes.get("useRedirect");
            String error = (String) formAttributes.get("error");
            List<String> fieldList = new ArrayList<>(0);
            List<Boolean> ignoreList = new ArrayList<>(0);
            List<Map<String, Object>> fieldMappingData = (List<Map<String, Object>>) formAttributes.get("parameters");
            for (Map<String, Object> fieldMapping : fieldMappingData) {
                fieldList.add((String) fieldMapping.get("field"));
                if ("Y".equals(fieldMapping.get("ignore"))) {
                    ignoreList.add(true);
                } else {
                    ignoreList.add(false);
                }
            }
            uniqueRowsMeta.setCountRows(useCounter);
            uniqueRowsMeta.setCountField(counterName);
            uniqueRowsMeta.setRejectDuplicateRow(useRedirect);
            uniqueRowsMeta.setErrorDescription(error);
            uniqueRowsMeta.setCompareFields(fieldList.toArray(new String[0]));
            uniqueRowsMeta.setCaseInsensitive(ignoreList.stream().collect(BooleanUtils.TO_BOOLEAN_ARRAY));
            int parallel = (int) formAttributes.get(Constant.ETL_PARALLEL);
            StepMeta stepMeta = new StepMeta(stepName, uniqueRowsMeta);
            stepMeta.setCopies(parallel);
            mxGeometry geometry = cell.getGeometry();
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
