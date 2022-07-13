package com.nxin.framework.etl.designer.converter.designer.transform.shell;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.nxin.framework.etl.designer.converter.designer.transform.ResponseMeta;
import com.nxin.framework.etl.designer.converter.designer.transform.TransformConvertChain;
import com.nxin.framework.etl.designer.converter.designer.transform.TransformConvertFactory;
import com.nxin.framework.etl.designer.enums.Constant;
import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl;
import lombok.extern.slf4j.Slf4j;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.userdefinedjavaclass.InfoStepDefinition;
import org.pentaho.di.trans.steps.userdefinedjavaclass.TargetStepDefinition;
import org.pentaho.di.trans.steps.userdefinedjavaclass.UserDefinedJavaClassDef;
import org.pentaho.di.trans.steps.userdefinedjavaclass.UserDefinedJavaClassMeta;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class UserDefinedJavaClassChain extends TransformConvertChain {
    private static final Map<String, Object> callbackMap = new ConcurrentHashMap<>(0);

    @Override
    public ResponseMeta parse(mxCell cell, TransMeta transMeta) throws JsonProcessingException {
        if (cell.isVertex() && "UserDefinedJavaClassMeta".equalsIgnoreCase(cell.getStyle())) {
            DeferredElementImpl value = (DeferredElementImpl) cell.getValue();
            Map<String, Object> formAttributes = objectMapper.readValue(value.getAttribute("form"), new TypeReference<Map<String, Object>>() {
            });
            UserDefinedJavaClassMeta userDefinedJavaClassMeta = new UserDefinedJavaClassMeta();
            String stepName = (String) formAttributes.get("name");
            int parallel = (int) formAttributes.get(Constant.ETL_PARALLEL);
            String script = (String) formAttributes.get("script");
            UserDefinedJavaClassDef userDefinedJavaClassDef = new UserDefinedJavaClassDef(UserDefinedJavaClassDef.ClassType.TRANSFORM_CLASS, "Processor", script);
            List<UserDefinedJavaClassDef> userDefinedJavaClassDefs = Arrays.asList(userDefinedJavaClassDef);
            userDefinedJavaClassMeta.replaceDefinitions(userDefinedJavaClassDefs);
            List<Map<String, Object>> outputFields = (List<Map<String, Object>>) formAttributes.get("outputFields");
            List<UserDefinedJavaClassMeta.FieldInfo> fieldInfos = new ArrayList<>(0);
            for (Map<String, Object> fieldMapping : outputFields) {
                String field = (String) fieldMapping.get("field");
                String category = (String) fieldMapping.get("category");
                int lengthValue = (int) fieldMapping.get("lengthValue");
                int accuracy = (int) fieldMapping.get("accuracy");
                fieldInfos.add(new UserDefinedJavaClassMeta.FieldInfo(field, ValueMetaFactory.getIdForValueMeta(category), lengthValue, accuracy));
            }
            userDefinedJavaClassMeta.replaceFields(fieldInfos);
            userDefinedJavaClassMeta.getInfoStepDefinitions().clear();
            List<Map<String, String>> infoSteps = (List<Map<String, String>>) formAttributes.get("infoSteps");
            List<Map<String, String>> targetSteps = (List<Map<String, String>>) formAttributes.get("targetSteps");
            if (!infoSteps.isEmpty() || !targetSteps.isEmpty()) {
                Map<String, Object> userDefinedJavaClassMetaMap = new HashMap<>(0);
                userDefinedJavaClassMetaMap.put("stepMetaInterface", userDefinedJavaClassDef);
                Map<String, String> previousStepMap = new HashMap<>(0);
                userDefinedJavaClassMetaMap.put("previousStep", previousStepMap);
                for (Map<String, String> infoStep : infoSteps) {
                    InfoStepDefinition stepDefinition = new InfoStepDefinition();
                    previousStepMap.put(infoStep.get("tag"), infoStep.get("step"));
                    stepDefinition.tag = infoStep.get("tag");
                    stepDefinition.description = infoStep.get("description");
                    userDefinedJavaClassMeta.getInfoStepDefinitions().add(stepDefinition);
                }
                userDefinedJavaClassMeta.getTargetStepDefinitions().clear();
                Map<String, String> nextStepMap = new HashMap<>(0);
                userDefinedJavaClassMetaMap.put("nextStep", nextStepMap);
                for (Map<String, String> targetStep : targetSteps) {
                    TargetStepDefinition stepDefinition = new TargetStepDefinition();
                    nextStepMap.put(targetStep.get("tag"), targetStep.get("step"));
                    stepDefinition.tag = targetStep.get("tag");
                    stepDefinition.description = targetStep.get("description");
                    userDefinedJavaClassMeta.getTargetStepDefinitions().add(stepDefinition);
                }
            }
            TransformConvertFactory.getTransformConvertChains().add(this);
            StepMeta stepMeta = new StepMeta(stepName, userDefinedJavaClassMeta);
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
        for (Map.Entry<String, Object> entry : callbackMap.entrySet()) {
            Map<String, Object> tableInputMetaMap = (Map<String, Object>) entry.getValue();
            UserDefinedJavaClassMeta userDefinedJavaClassMeta = (UserDefinedJavaClassMeta) tableInputMetaMap.get("stepMetaInterface");
            Map<String, String> previousStep = (Map<String, String>) tableInputMetaMap.get("previousStep");
            Map<String, String> nextStep = (Map<String, String>) tableInputMetaMap.get("nextStep");
            for (InfoStepDefinition infoStepDefinition : userDefinedJavaClassMeta.getInfoStepDefinitions()) {
                infoStepDefinition.stepMeta = transMeta.findStep(idNameMapping.get(previousStep.get(infoStepDefinition.tag)));
            }
            for (TargetStepDefinition targetStepDefinition : userDefinedJavaClassMeta.getTargetStepDefinitions()) {
                targetStepDefinition.stepMeta = transMeta.findStep(idNameMapping.get(nextStep.get(targetStepDefinition.tag)));
            }
            callbackMap.remove(entry.getKey());
        }
    }
}
