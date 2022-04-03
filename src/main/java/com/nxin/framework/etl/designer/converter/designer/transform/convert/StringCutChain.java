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
import org.pentaho.di.trans.steps.stringcut.StringCutMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class StringCutChain extends TransformConvertChain {

    @Override
    public ResponseMeta parse(mxCell cell, TransMeta transMeta) throws JsonProcessingException {
        if (cell.isVertex() && "StringCutMeta".equalsIgnoreCase(cell.getStyle())) {
            DeferredElementImpl value = (DeferredElementImpl) cell.getValue();
            StringCutMeta stringCutMeta = new StringCutMeta();
            Map<String, Object> formAttributes = objectMapper.readValue(value.getAttribute("form"), new TypeReference<Map<String, Object>>() {
            });
            String stepName = (String) formAttributes.get("name");
            List<String> sourceList = new ArrayList<>(0);
            List<String> targetList = new ArrayList<>(0);
            List<String> fromList = new ArrayList<>(0);
            List<String> endList = new ArrayList<>(0);
            List<Map<String, String>> fieldMappingData = (List<Map<String, String>>) formAttributes.get("parameters");
            for (Map<String, String> fieldMapping : fieldMappingData) {
                sourceList.add(fieldMapping.get("source"));
                targetList.add(fieldMapping.get("target"));
                fromList.add(fieldMapping.get("from"));
                endList.add(fieldMapping.get("end"));
            }
            stringCutMeta.setFieldInStream(sourceList.toArray(new String[0]));
            stringCutMeta.setFieldOutStream(targetList.toArray(new String[0]));
            stringCutMeta.setCutFrom(fromList.toArray(new String[0]));
            stringCutMeta.setCutTo(endList.toArray(new String[0]));
            int parallel = (int) formAttributes.get(Constant.ETL_PARALLEL);
            StepMeta stepMeta = new StepMeta(stepName, stringCutMeta);
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
