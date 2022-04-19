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
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.fieldsplitter.FieldSplitterMeta;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class FieldSplitterChain extends TransformConvertChain {

    @Override
    public ResponseMeta parse(mxCell cell, TransMeta transMeta) throws JsonProcessingException {
        if (cell.isVertex() && "FieldSplitterMeta".equalsIgnoreCase(cell.getStyle())) {
            DeferredElementImpl value = (DeferredElementImpl) cell.getValue();
            FieldSplitterMeta fieldSplitterMeta = new FieldSplitterMeta();
            Map<String, Object> formAttributes = objectMapper.readValue(value.getAttribute("form"), new TypeReference<Map<String, Object>>() {
            });
            String stepName = (String) formAttributes.get("name");
            String field = (String) formAttributes.get("field");
            String delimiter = (String) formAttributes.get("delimiter");
            String enclosure = (String) formAttributes.get("enclosure");
            List<String> fieldList = new ArrayList<>(0);
            List<String> idList = new ArrayList<>(0);
            List<Boolean> removeIdList = new ArrayList<>(0);
            List<Integer> categoryList = new ArrayList<>(0);
            List<String> formatList = new ArrayList<>(0);
            List<Integer> lengthValueList = new ArrayList<>(0);
            List<Integer> accuracyList = new ArrayList<>(0);
            List<String> currencyList = new ArrayList<>(0);
            List<String> decimalList = new ArrayList<>(0);
            List<String> groupByList = new ArrayList<>(0);
            List<String> nullIfList = new ArrayList<>(0);
            List<String> emptyValueList = new ArrayList<>(0);
            List<Integer> removeBlankList = new ArrayList<>(0);
            List<Map<String, Object>> fieldMappingData = (List<Map<String, Object>>) formAttributes.get("parameters");
            for (Map<String, Object> fieldMapping : fieldMappingData) {
                fieldList.add((String) fieldMapping.get("field"));
                idList.add((String) fieldMapping.get("newId"));
                if ("Y".equals(fieldMapping.get("removeId"))) {
                    removeIdList.add(true);
                } else {
                    removeIdList.add(false);
                }
                categoryList.add(ValueMetaFactory.getIdForValueMeta((String) fieldMapping.get("category")));
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
                formatList.add((String) fieldMapping.get("formatValue"));
                currencyList.add((String) fieldMapping.get("currency"));
                decimalList.add((String) fieldMapping.get("decimal"));
                groupByList.add((String) fieldMapping.get("groupBy"));
                nullIfList.add((String) fieldMapping.get("nullIf"));
                emptyValueList.add((String) fieldMapping.get("emptyValue"));
                int removeBlank = 0;
                for (int i = 0; i < Constant.TRIM_TYPE_CODE.length; i++) {
                    if (Constant.TRIM_TYPE_CODE[i].equals(fieldMapping.get("removeBlank"))) {
                        removeBlank = i;
                        break;
                    }
                }
                removeBlankList.add(removeBlank);
            }
            fieldSplitterMeta.setSplitField(field);
            fieldSplitterMeta.setDelimiter(delimiter);
            fieldSplitterMeta.setEnclosure(enclosure);
            fieldSplitterMeta.setFieldName(fieldList.toArray(new String[0]));
            fieldSplitterMeta.setFieldID(idList.toArray(new String[0]));
            fieldSplitterMeta.setFieldRemoveID(removeIdList.stream().collect(BooleanUtils.TO_BOOLEAN_ARRAY));
            fieldSplitterMeta.setFieldType(categoryList.stream().mapToInt(Integer::intValue).toArray());
            fieldSplitterMeta.setFieldLength(lengthValueList.stream().mapToInt(Integer::valueOf).toArray());
            fieldSplitterMeta.setFieldPrecision(accuracyList.stream().mapToInt(Integer::valueOf).toArray());
            fieldSplitterMeta.setFieldFormat(formatList.toArray(new String[0]));
            fieldSplitterMeta.setFieldGroup(groupByList.toArray(new String[0]));
            fieldSplitterMeta.setFieldDecimal(decimalList.toArray(new String[0]));
            fieldSplitterMeta.setFieldCurrency(currencyList.toArray(new String[0]));
            fieldSplitterMeta.setFieldNullIf(nullIfList.toArray(new String[0]));
            fieldSplitterMeta.setFieldIfNull(emptyValueList.toArray(new String[0]));
            fieldSplitterMeta.setFieldTrimType(removeBlankList.stream().mapToInt(Integer::valueOf).toArray());
            int parallel = (int) formAttributes.get(Constant.ETL_PARALLEL);
            StepMeta stepMeta = new StepMeta(stepName, fieldSplitterMeta);
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
