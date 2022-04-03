package com.nxin.framework.etl.designer.converter.designer.transform.convert;

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
import org.pentaho.di.trans.steps.valuemapper.ValueMapperMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class ValueMapperChain extends TransformConvertChain {

    @Override
    public ResponseMeta parse(mxCell cell, TransMeta transMeta) throws JsonProcessingException {
        if (cell.isVertex() && "ValueMapperMeta".equalsIgnoreCase(cell.getStyle())) {
            DeferredElementImpl value = (DeferredElementImpl) cell.getValue();
            ValueMapperMeta valueMapperMeta = new ValueMapperMeta();
            Map<String, Object> formAttributes = objectMapper.readValue(value.getAttribute("form"), new TypeReference<Map<String, Object>>() {
            });
            String stepName = (String) formAttributes.get("name");
            String field = (String) formAttributes.get("field");
            String target = (String) formAttributes.get("target");
            String defaultValue = (String) formAttributes.get("defaultValue");
            List<String> sourceList = new ArrayList<>(0);
            List<String> targetList = new ArrayList<>(0);
            List<Map<String, Object>> fieldMappingData = (List<Map<String, Object>>) formAttributes.get("parameters");
            for (Map<String, Object> fieldMapping : fieldMappingData) {
                sourceList.add((String) fieldMapping.get("source"));
                targetList.add((String) fieldMapping.get("target"));
            }
            valueMapperMeta.setFieldToUse(field);
            valueMapperMeta.setTargetField(target);
            valueMapperMeta.setNonMatchDefault(defaultValue);
            valueMapperMeta.setSourceValue(sourceList.toArray(new String[0]));
            valueMapperMeta.setTargetValue(targetList.toArray(new String[0]));
            int parallel = (int) formAttributes.get(Constant.ETL_PARALLEL);
            StepMeta stepMeta = new StepMeta(stepName, valueMapperMeta);
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
