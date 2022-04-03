package com.nxin.framework.etl.designer.converter.designer.transform.shell;

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
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.scriptvalues_mod.ScriptValuesMetaMod;
import org.pentaho.di.trans.steps.scriptvalues_mod.ScriptValuesScript;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class JavaScriptChain extends TransformConvertChain {

    @Override
    public ResponseMeta parse(mxCell cell, TransMeta transMeta) throws JsonProcessingException {
        if (cell.isVertex() && "JavaScriptMeta".equalsIgnoreCase(cell.getStyle())) {
            DeferredElementImpl value = (DeferredElementImpl) cell.getValue();
            Map<String, Object> formAttributes = objectMapper.readValue(value.getAttribute("form"), new TypeReference<Map<String, Object>>() {
            });
            List<String> parameterFields = new ArrayList<>(0);
            List<String> parameterFieldNames = new ArrayList<>(0);
            List<Integer> parameterFieldLengthValues = new ArrayList<>(0);
            List<Integer> parameterFieldPrecisions = new ArrayList<>(0);
            List<Boolean> parameterFieldRenames = new ArrayList<>(0);
            List<Integer> parameterFieldCategory = new ArrayList<>(0);
            ScriptValuesMetaMod scriptMeta = new ScriptValuesMetaMod();
            String stepName = (String) formAttributes.get("name");
            int parallel = (int) formAttributes.get(Constant.ETL_PARALLEL);
            String script = (String) formAttributes.get("script");
            boolean compatible = (boolean) formAttributes.get("compatible");
            int level;
            if (ObjectUtils.isEmpty(formAttributes.get("level"))) {
                level = 0;
            } else {
                level = (int) formAttributes.get("level");
            }
            scriptMeta.setJSScripts(new ScriptValuesScript[]{new ScriptValuesScript(ScriptValuesScript.TRANSFORM_SCRIPT, "SCRIPT", script)});
            scriptMeta.setCompatible(compatible);
            scriptMeta.setOptimizationLevel(String.valueOf(level));
            List<Map<String, Object>> fieldMappingData = (List<Map<String, Object>>) formAttributes.get("parameters");
            for (Map<String, Object> fieldMapping : fieldMappingData) {
                parameterFields.add((String) fieldMapping.get("field"));
                parameterFieldNames.add((String) fieldMapping.get("name"));
                if (!ObjectUtils.isEmpty(fieldMapping.get("lengthValue"))) {
                    parameterFieldLengthValues.add((int) fieldMapping.get("lengthValue"));
                } else {
                    parameterFieldLengthValues.add(-1);
                }
                if (!ObjectUtils.isEmpty(fieldMapping.get("accuracy"))) {
                    parameterFieldPrecisions.add((int) fieldMapping.get("accuracy"));
                } else {
                    parameterFieldPrecisions.add(-1);
                }
                if ("Y".equals(fieldMapping.get("rename"))) {
                    parameterFieldRenames.add(true);
                } else {
                    parameterFieldRenames.add(false);
                }
                parameterFieldCategory.add(ValueMetaFactory.getIdForValueMeta((String) fieldMapping.get("category")));
            }
            scriptMeta.setFieldname(parameterFields.toArray(new String[0]));
            scriptMeta.setLength(parameterFieldLengthValues.stream().mapToInt(Integer::valueOf).toArray());
            scriptMeta.setRename(parameterFieldNames.toArray(new String[0]));
            scriptMeta.setPrecision(parameterFieldPrecisions.stream().mapToInt(Integer::valueOf).toArray());
            scriptMeta.setReplace(parameterFieldRenames.stream().collect(BooleanUtils.TO_BOOLEAN_ARRAY));
            scriptMeta.setType(parameterFieldCategory.stream().mapToInt(Integer::valueOf).toArray());
            StepMeta stepMeta = new StepMeta(stepName, scriptMeta);
            if (formAttributes.containsKey("distribute")) {
                boolean distribute = (boolean) formAttributes.get("distribute");
                stepMeta.setDistributes(distribute);
            }
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
