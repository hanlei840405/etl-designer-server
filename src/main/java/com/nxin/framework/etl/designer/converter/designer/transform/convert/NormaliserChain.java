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
import org.pentaho.di.trans.steps.normaliser.NormaliserMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class NormaliserChain extends TransformConvertChain {

    @Override
    public ResponseMeta parse(mxCell cell, TransMeta transMeta) throws JsonProcessingException {
        if (cell.isVertex() && "NormaliserMeta".equalsIgnoreCase(cell.getStyle())) {
            DeferredElementImpl value = (DeferredElementImpl) cell.getValue();
            NormaliserMeta normaliserMeta = new NormaliserMeta();
            Map<String, Object> formAttributes = objectMapper.readValue(value.getAttribute("form"), new TypeReference<Map<String, Object>>() {
            });
            String stepName = (String) formAttributes.get("name");
            String field = (String) formAttributes.get("field");
            List<Map<String, String>> fieldMappingData = (List<Map<String, String>>) formAttributes.get("parameters");
            List<NormaliserMeta.NormaliserField> normaliserFields = new ArrayList<>(0);
            for (Map<String, String> fieldMapping : fieldMappingData) {
                NormaliserMeta.NormaliserField normaliserField = new NormaliserMeta.NormaliserField();
                normaliserField.setName(fieldMapping.get("field"));
                normaliserField.setValue(fieldMapping.get("keyField"));
                normaliserField.setNorm(fieldMapping.get("valueField"));
                normaliserFields.add(normaliserField);
            }
            normaliserMeta.setTypeField(field);
            normaliserMeta.setNormaliserFields(normaliserFields.toArray(new NormaliserMeta.NormaliserField[0]));
            int parallel = (int) formAttributes.get(Constant.ETL_PARALLEL);
            StepMeta stepMeta = new StepMeta(stepName, normaliserMeta);
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
