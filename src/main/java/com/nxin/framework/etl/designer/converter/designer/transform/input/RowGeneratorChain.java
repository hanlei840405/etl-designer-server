package com.nxin.framework.etl.designer.converter.designer.transform.input;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.nxin.framework.etl.designer.converter.designer.transform.ResponseMeta;
import com.nxin.framework.etl.designer.converter.designer.transform.TransformConvertChain;
import com.nxin.framework.etl.designer.utils.BooleanUtils;
import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl;
import lombok.extern.slf4j.Slf4j;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.rowgenerator.RowGeneratorMeta;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class RowGeneratorChain extends TransformConvertChain {

    @Override
    public ResponseMeta parse(mxCell cell, TransMeta transMeta) throws JsonProcessingException {
        if (cell.isVertex() && "RowGeneratorMeta".equalsIgnoreCase(cell.getStyle())) {
            DeferredElementImpl value = (DeferredElementImpl) cell.getValue();
            RowGeneratorMeta rowGeneratorMeta = new RowGeneratorMeta();
            Map<String, Object> formAttributes = objectMapper.readValue(value.getAttribute("form"), new TypeReference<Map<String, Object>>() {
            });
            String stepName = (String) formAttributes.get("name");
            int limit;
            if (ObjectUtils.isEmpty(formAttributes.get("limit"))) {
                limit = 1;
            } else {
                limit = (int) formAttributes.get("limit");
            }
            List<String> fieldList = new ArrayList<>(0);
            List<String> categoryList = new ArrayList<>(0);
            List<String> formatList = new ArrayList<>(0);
            List<Integer> lengthValueList = new ArrayList<>(0);
            List<Integer> accuracyList = new ArrayList<>(0);
            List<String> currencyList = new ArrayList<>(0);
            List<String> decimalList = new ArrayList<>(0);
            List<String> groupByList = new ArrayList<>(0);
            List<String> valueList = new ArrayList<>(0);
            List<Boolean> emptyValueList = new ArrayList<>(0);
            List<Map<String, Object>> fieldMappingData = (List<Map<String, Object>>) formAttributes.get("parameters");
            for (Map<String, Object> fieldMapping : fieldMappingData) {
                fieldList.add((String) fieldMapping.get("field"));
                categoryList.add((String) fieldMapping.get("category"));
                formatList.add((String) fieldMapping.get("formatValue"));
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
                currencyList.add((String) fieldMapping.get("currency"));
                decimalList.add((String) fieldMapping.get("decimal"));
                groupByList.add((String) fieldMapping.get("groupBy"));
                valueList.add((String) fieldMapping.get("value"));
                if ("Y".equals(fieldMapping.get("emptyValue"))) {
                    emptyValueList.add(true);
                } else {
                    emptyValueList.add(false);
                }
            }
            rowGeneratorMeta.setCurrency(currencyList.toArray(new String[0]));
            rowGeneratorMeta.setDecimal(decimalList.toArray(new String[0]));
            rowGeneratorMeta.setEmptyString(emptyValueList.stream().collect(BooleanUtils.TO_BOOLEAN_ARRAY));
            rowGeneratorMeta.setFieldFormat(formatList.toArray(new String[0]));
            rowGeneratorMeta.setFieldLength(lengthValueList.stream().mapToInt(Integer::valueOf).toArray());
            rowGeneratorMeta.setFieldName(fieldList.toArray(new String[0]));
            rowGeneratorMeta.setFieldPrecision(accuracyList.stream().mapToInt(Integer::valueOf).toArray());
            rowGeneratorMeta.setFieldType(categoryList.toArray(new String[0]));
            rowGeneratorMeta.setGroup(groupByList.toArray(new String[0]));
            rowGeneratorMeta.setValue(valueList.toArray(new String[0]));
            rowGeneratorMeta.setRowLimit(String.valueOf(limit));
            StepMeta stepMeta = new StepMeta(stepName, rowGeneratorMeta);
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
