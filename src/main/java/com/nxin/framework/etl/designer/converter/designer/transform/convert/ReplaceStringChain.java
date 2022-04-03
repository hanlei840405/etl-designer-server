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
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.replacestring.ReplaceStringMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class ReplaceStringChain extends TransformConvertChain {

    @Override
    public ResponseMeta parse(mxCell cell, TransMeta transMeta) throws JsonProcessingException {
        if (cell.isVertex() && "ReplaceStringMeta".equalsIgnoreCase(cell.getStyle())) {
            DeferredElementImpl value = (DeferredElementImpl) cell.getValue();
            ReplaceStringMeta replaceStringMeta = new ReplaceStringMeta();
            Map<String, Object> formAttributes = objectMapper.readValue(value.getAttribute("form"), new TypeReference<Map<String, Object>>() {
            });
            String stepName = (String) formAttributes.get("name");
            List<String> sourceList = new ArrayList<>(0);
            List<String> targetList = new ArrayList<>(0);
            List<Boolean> useRegList = new ArrayList<>(0);
            List<String> searchList = new ArrayList<>(0);
            List<String> replaceList = new ArrayList<>(0);
            List<Boolean> emptyValueList = new ArrayList<>(0);
            List<String> replaceWithFieldList = new ArrayList<>(0);
            List<Boolean> matchList = new ArrayList<>(0);
            List<Boolean> ignoreList = new ArrayList<>(0);
            List<Boolean> unicodeList = new ArrayList<>(0);
            List<Map<String, Object>> fieldMappingData = (List<Map<String, Object>>) formAttributes.get("parameters");
            for (Map<String, Object> fieldMapping : fieldMappingData) {
                sourceList.add((String) fieldMapping.get("source"));
                targetList.add((String) fieldMapping.get("target"));
                if ("Y".equals(fieldMapping.get("useReg"))) {
                    useRegList.add(true);
                } else {
                    useRegList.add(false);
                }
                searchList.add((String) fieldMapping.get("search"));
                replaceList.add((String) fieldMapping.get("replace"));
                if ("Y".equals(fieldMapping.get("emptyValue"))) {
                    emptyValueList.add(true);
                } else {
                    emptyValueList.add(false);
                }
                replaceWithFieldList.add((String) fieldMapping.get("replaceWithField"));
                if ("Y".equals(fieldMapping.get("match"))) {
                    matchList.add(true);
                } else {
                    matchList.add(false);
                }
                if ("Y".equals(fieldMapping.get("ignore"))) {
                    ignoreList.add(true);
                } else {
                    ignoreList.add(false);
                }
                if ("Y".equals(fieldMapping.get("unicode"))) {
                    unicodeList.add(true);
                } else {
                    unicodeList.add(false);
                }
            }
            replaceStringMeta.setFieldInStream(sourceList.toArray(new String[0]));
            replaceStringMeta.setFieldOutStream(targetList.toArray(new String[0]));
            replaceStringMeta.setUseRegEx(useRegList.stream().collect(BooleanUtils.TO_BOOLEAN_ARRAY));
            replaceStringMeta.setReplaceString(searchList.toArray(new String[0]));
            replaceStringMeta.setReplaceByString(replaceList.toArray(new String[0]));
            replaceStringMeta.setEmptyString(emptyValueList.stream().collect(BooleanUtils.TO_BOOLEAN_ARRAY));
            replaceStringMeta.setFieldReplaceByString(replaceWithFieldList.toArray(new String[0]));
            replaceStringMeta.setWholeWord(matchList.stream().collect(BooleanUtils.TO_BOOLEAN_ARRAY));
            replaceStringMeta.setCaseSensitive(ignoreList.stream().collect(BooleanUtils.TO_BOOLEAN_ARRAY));
            replaceStringMeta.setIsUnicode(unicodeList.stream().collect(BooleanUtils.TO_BOOLEAN_ARRAY));
            int parallel = (int) formAttributes.get(Constant.ETL_PARALLEL);
            StepMeta stepMeta = new StepMeta(stepName, replaceStringMeta);
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
