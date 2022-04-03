package com.nxin.framework.etl.designer.converter.designer.transform.shell;

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
import org.pentaho.di.trans.steps.rowsfromresult.RowsFromResultMeta;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class RowsFromResultChain extends TransformConvertChain {

    @Override
    public ResponseMeta parse(mxCell cell, TransMeta transMeta) throws JsonProcessingException {
        if (cell.isVertex() && "RowsFromResultMeta".equalsIgnoreCase(cell.getStyle())) {
            DeferredElementImpl value = (DeferredElementImpl) cell.getValue();
            RowsFromResultMeta rowsFromResultMeta = new RowsFromResultMeta();
            Map<String, Object> formAttributes = objectMapper.readValue(value.getAttribute("form"), new TypeReference<Map<String, Object>>() {
            });
            String stepName = (String) formAttributes.get("name");
            List<String> fieldList = new ArrayList<>(0);
            List<Integer> categoryList = new ArrayList<>(0);
            List<Integer> lengthValueList = new ArrayList<>(0);
            List<Integer> accuracyList = new ArrayList<>(0);
            List<Map<String, Object>> fieldMappingData = (List<Map<String, Object>>) formAttributes.get("parameters");
            for (Map<String, Object> fieldMapping : fieldMappingData) {
                fieldList.add((String) fieldMapping.get("field"));
                if (!ObjectUtils.isEmpty(fieldMapping.get("category"))) {
                    categoryList.add(ValueMetaFactory.getIdForValueMeta((String) fieldMapping.get("category")));
                } else {
                    categoryList.add(ValueMetaFactory.getIdForValueMeta("String"));
                }
                if (!ObjectUtils.isEmpty(fieldMapping.get("lengthValue"))) {
                    lengthValueList.add((int) fieldMapping.get("lengthValue"));
                } else {
                    lengthValueList.add(-1);
                }
                if (!ObjectUtils.isEmpty(fieldMapping.get("accuracy"))) {
                    accuracyList.add((int) fieldMapping.get("accuracy"));
                } else {
                    accuracyList.add(-1);
                }
            }
            rowsFromResultMeta.setFieldname(fieldList.toArray(new String[0]));
            rowsFromResultMeta.setType(categoryList.stream().mapToInt(Integer::valueOf).toArray());
            rowsFromResultMeta.setLength(lengthValueList.stream().mapToInt(Integer::valueOf).toArray());
            rowsFromResultMeta.setPrecision(accuracyList.stream().mapToInt(Integer::valueOf).toArray());
            StepMeta stepMeta = new StepMeta(stepName, rowsFromResultMeta);
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
