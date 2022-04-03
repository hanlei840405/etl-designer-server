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
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.getvariable.GetVariableMeta;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class GetVariableChain extends TransformConvertChain {

    @Override
    public ResponseMeta parse(mxCell cell, TransMeta transMeta) throws JsonProcessingException {
        if (cell.isVertex() && "GetVariableMeta".equalsIgnoreCase(cell.getStyle())) {
            DeferredElementImpl value = (DeferredElementImpl) cell.getValue();
            GetVariableMeta getVariableMeta = new GetVariableMeta();
            Map<String, Object> formAttributes = objectMapper.readValue(value.getAttribute("form"), new TypeReference<Map<String, Object>>() {
            });
            List<GetVariableMeta.FieldDefinition> fieldDefinitions = new ArrayList<>(0);
            List<Map<String, Object>> fieldMappingData = (List<Map<String, Object>>) formAttributes.get("parameters");
            for (Map<String, Object> fieldMapping : fieldMappingData) {
                GetVariableMeta.FieldDefinition fieldDefinition = new GetVariableMeta.FieldDefinition();
                fieldDefinition.setFieldName((String) fieldMapping.get("field"));
                fieldDefinition.setVariableString((String) fieldMapping.get("variable"));
                fieldDefinition.setCurrency((String) fieldMapping.get("currency"));
                fieldDefinition.setFieldFormat((String) fieldMapping.get("formatValue"));
                fieldDefinition.setDecimal((String) fieldMapping.get("decimal"));
                fieldDefinition.setGroup((String) fieldMapping.get("groupBy"));
                fieldDefinition.setTrimType(ValueMetaString.getTrimTypeByCode((String) fieldMapping.get("emptyValue")));
                if (!ObjectUtils.isEmpty(fieldMapping.get("lengthValue"))) {
                    fieldDefinition.setFieldLength((int) fieldMapping.get("lengthValue"));
                } else {
                    fieldDefinition.setFieldLength(-1);
                }
                if (!ObjectUtils.isEmpty(fieldMapping.get("accuracy"))) {
                    fieldDefinition.setFieldPrecision((int) fieldMapping.get("accuracy"));
                } else {
                    fieldDefinition.setFieldPrecision(-1);
                }
                fieldDefinition.setFieldType(ValueMetaFactory.getIdForValueMeta((String) fieldMapping.get("category")));
                fieldDefinitions.add(fieldDefinition);
            }
            getVariableMeta.setFieldDefinitions(fieldDefinitions.toArray(new GetVariableMeta.FieldDefinition[0]));
            String stepName = (String) formAttributes.get("name");
            StepMeta stepMeta = new StepMeta(stepName, getVariableMeta);
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
