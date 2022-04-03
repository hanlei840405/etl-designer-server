package com.nxin.framework.etl.designer.converter.designer.transform.shell;

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
import org.pentaho.di.trans.steps.setvariable.SetVariableMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class SetVariableChain extends TransformConvertChain {

    @Override
    public ResponseMeta parse(mxCell cell, TransMeta transMeta) throws JsonProcessingException {
        if (cell.isVertex() && "SetVariableMeta".equalsIgnoreCase(cell.getStyle())) {
            DeferredElementImpl value = (DeferredElementImpl) cell.getValue();
            SetVariableMeta setVariableMeta = new SetVariableMeta();
            Map<String, Object> formAttributes = objectMapper.readValue(value.getAttribute("form"), new TypeReference<Map<String, Object>>() {
            });
            List<String> fieldList = new ArrayList<>(0);
            List<String> variableList = new ArrayList<>(0);
            List<Integer> scopeList = new ArrayList<>(0);
            List<String> defaultValueList = new ArrayList<>(0);
            List<Map<String, String>> fieldMappingData = (List<Map<String, String>>) formAttributes.get("parameters");
            for (Map<String, String> fieldMapping : fieldMappingData) {
                fieldList.add(fieldMapping.get("field"));
                variableList.add(fieldMapping.get("variable"));
                scopeList.add(SetVariableMeta.getVariableType(fieldMapping.get("scope")));
                defaultValueList.add(fieldMapping.get("defaultValue"));
            }
            setVariableMeta.setFieldName(fieldList.toArray(new String[0]));
            setVariableMeta.setVariableName(variableList.toArray(new String[0]));
            setVariableMeta.setVariableType(scopeList.stream().mapToInt(Integer::valueOf).toArray());
            setVariableMeta.setDefaultValue(defaultValueList.toArray(new String[0]));
            setVariableMeta.setUsingFormatting(true);
            String stepName = (String) formAttributes.get("name");
            int parallel = (int) formAttributes.get(Constant.ETL_PARALLEL);
            StepMeta stepMeta = new StepMeta(stepName, setVariableMeta);
            mxGeometry geometry = cell.getGeometry();
            stepMeta.setLocation(new Double(geometry.getX()).intValue(), new Double(geometry.getY()).intValue());
            stepMeta.setDraw(true);
            stepMeta.setCopies(parallel);
            return new ResponseMeta(cell.getId(), stepMeta, null);
        } else {
            return next.parse(cell, transMeta);
        }
    }

    @Override
    public void callback(TransMeta transMeta, Map<String, String> idNameMapping) {

    }
}
