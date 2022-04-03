package com.nxin.framework.etl.designer.converter.designer.transform.streaming;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.nxin.framework.etl.designer.converter.designer.transform.ResponseMeta;
import com.nxin.framework.etl.designer.converter.designer.transform.TransformConvertChain;
import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl;
import lombok.extern.slf4j.Slf4j;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.recordsfromstream.RecordsFromStreamMeta;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class RecordsFromStreamChain extends TransformConvertChain {

    @Override
    public ResponseMeta parse(mxCell cell, TransMeta transMeta) throws JsonProcessingException {
        if (cell.isVertex() && "RecordsFromStreamMeta".equalsIgnoreCase(cell.getStyle())) {
            DeferredElementImpl value = (DeferredElementImpl) cell.getValue();
            RecordsFromStreamMeta recordsFromStreamMeta = new RecordsFromStreamMeta();
            Map<String, Object> formAttributes = objectMapper.readValue(value.getAttribute("form"), new TypeReference<Map<String, Object>>() {
            });
            String stepName = (String) formAttributes.get("name");
            List<String> fieldList = new ArrayList<>(0);
            List<Integer> categoryList = new ArrayList<>(0);
            List<Integer> lengthList = new ArrayList<>(0);
            List<Integer> accuracyList = new ArrayList<>(0);
            List<Map<String, Object>> parameters = (List<Map<String, Object>>) formAttributes.get("parameters");
            for (Map<String, Object> parameter : parameters) {
                fieldList.add((String) parameter.get("field"));
                categoryList.add(ValueMetaFactory.getIdForValueMeta((String) parameter.get("category")));
                if (!ObjectUtils.isEmpty(parameter.get("lengthValue"))) {
                    lengthList.add((int) parameter.get("lengthValue"));
                } else {
                    lengthList.add(-1);
                }
                if (!ObjectUtils.isEmpty(parameter.get("accuracy"))) {
                    accuracyList.add((int) parameter.get("accuracy"));
                } else {
                    accuracyList.add(-1);
                }
            }
            recordsFromStreamMeta.setFieldname(fieldList.toArray(new String[0]));
            recordsFromStreamMeta.setLength(lengthList.stream().mapToInt(Integer::valueOf).toArray());
            recordsFromStreamMeta.setType(categoryList.stream().mapToInt(Integer::valueOf).toArray());
            recordsFromStreamMeta.setPrecision(accuracyList.stream().mapToInt(Integer::valueOf).toArray());
            StepMeta stepMeta = new StepMeta(stepName, recordsFromStreamMeta);
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
