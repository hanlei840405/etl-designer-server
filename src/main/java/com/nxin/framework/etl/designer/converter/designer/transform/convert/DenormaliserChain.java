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
import org.pentaho.di.trans.steps.denormaliser.DenormaliserMeta;
import org.pentaho.di.trans.steps.denormaliser.DenormaliserTargetField;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class DenormaliserChain extends TransformConvertChain {

    @Override
    public ResponseMeta parse(mxCell cell, TransMeta transMeta) throws JsonProcessingException {
        if (cell.isVertex() && "DenormaliserMeta".equalsIgnoreCase(cell.getStyle())) {
            DeferredElementImpl value = (DeferredElementImpl) cell.getValue();
            DenormaliserMeta denormaliserMeta = new DenormaliserMeta();
            Map<String, Object> formAttributes = objectMapper.readValue(value.getAttribute("form"), new TypeReference<Map<String, Object>>() {
            });
            String stepName = (String) formAttributes.get("name");
            String field = (String) formAttributes.get("field");
            List<String> fieldList = new ArrayList<>(0);
            List<Map<String, String>> groups = (List<Map<String, String>>) formAttributes.get("groups");
            for (Map<String, String> group : groups) {
                fieldList.add(group.get("field"));
            }
            List<DenormaliserTargetField> denormaliserTargetFields = new ArrayList<>(0);
            List<Map<String, Object>> fieldMappingData = (List<Map<String, Object>>) formAttributes.get("parameters");
            for (Map<String, Object> fieldMapping : fieldMappingData) {
                DenormaliserTargetField denormaliserTargetField = new DenormaliserTargetField();
                denormaliserTargetField.setTargetName((String) fieldMapping.get("target"));
                denormaliserTargetField.setFieldName((String) fieldMapping.get("source"));
                denormaliserTargetField.setKeyValue((String) fieldMapping.get("keyValue"));
                denormaliserTargetField.setTargetType((String) fieldMapping.get("category"));
                denormaliserTargetField.setTargetFormat((String) fieldMapping.get("formatValue"));

                if (!ObjectUtils.isEmpty(fieldMapping.get("lengthValue"))) {
                    denormaliserTargetField.setTargetLength((int) fieldMapping.get("lengthValue"));
                } else {
                    denormaliserTargetField.setTargetLength(-1);
                }
                if (!ObjectUtils.isEmpty(fieldMapping.get("accuracy"))) {
                    denormaliserTargetField.setTargetPrecision((int) fieldMapping.get("accuracy"));
                } else {
                    denormaliserTargetField.setTargetPrecision(-1);
                }
                denormaliserTargetField.setTargetCurrencySymbol((String) fieldMapping.get("currency"));
                denormaliserTargetField.setTargetGroupingSymbol((String) fieldMapping.get("groupBy"));
                denormaliserTargetField.setTargetNullString((String) fieldMapping.get("nullIf"));
                denormaliserTargetField.setTargetAggregationType((String) fieldMapping.get("agg"));
                denormaliserTargetFields.add(denormaliserTargetField);
            }
            denormaliserMeta.setKeyField(field);
            denormaliserMeta.setGroupField(fieldList.toArray(new String[0]));
            denormaliserMeta.setDenormaliserTargetField(denormaliserTargetFields.toArray(new DenormaliserTargetField[0]));
            int parallel = (int) formAttributes.get(Constant.ETL_PARALLEL);
            StepMeta stepMeta = new StepMeta(stepName, denormaliserMeta);
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
