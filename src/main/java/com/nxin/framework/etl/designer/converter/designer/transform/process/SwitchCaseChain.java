package com.nxin.framework.etl.designer.converter.designer.transform.process;

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
import org.pentaho.di.trans.steps.switchcase.SwitchCaseMeta;
import org.pentaho.di.trans.steps.switchcase.SwitchCaseTarget;
import org.pentaho.di.trans.steps.tableinput.TableInputMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SwitchCaseChain extends TransformConvertChain {
    private static final Map<String, Object> callbackMap = new ConcurrentHashMap<>(0);

    @Override
    public ResponseMeta parse(mxCell cell, TransMeta transMeta) throws JsonProcessingException {
        if (cell.isVertex() && "SwitchCaseMeta".equalsIgnoreCase(cell.getStyle())) {
            DeferredElementImpl value = (DeferredElementImpl) cell.getValue();
            Map<String, Object> formAttributes = objectMapper.readValue(value.getAttribute("form"), new TypeReference<Map<String, Object>>() {
            });
            List<SwitchCaseTarget> switchCaseTargets = new ArrayList<>(0);
            String stepName = (String) formAttributes.get("name");
            String switchField = (String) formAttributes.get("field");
            boolean useStringIn = (boolean) formAttributes.get("useStringIn");
            String category = (String) formAttributes.get("category");
            String mask = (String) formAttributes.get("mask");
            String decimalSymbol = (String) formAttributes.get("decimalSymbol");
            String groupBy = (String) formAttributes.get("groupBy");
            String defaultNextStep = (String) formAttributes.get("nextStep");
            int parallel = (int) formAttributes.get(Constant.ETL_PARALLEL);
            List<Map<String, String>> cases = (List<Map<String, String>>) formAttributes.get("cases");
            for (Map<String, String> item : cases) {
                SwitchCaseTarget switchCaseTarget = new SwitchCaseTarget();
                switchCaseTarget.caseTargetStepname = item.get("step");
                switchCaseTarget.caseValue = item.get("value");
                switchCaseTargets.add(switchCaseTarget);
            }
            SwitchCaseMeta switchCaseMeta = new SwitchCaseMeta();
            switchCaseMeta.setFieldname(switchField);
            switchCaseMeta.setContains(useStringIn);
            switchCaseMeta.setCaseValueType(ValueMetaFactory.getIdForValueMeta(category));
            switchCaseMeta.setCaseValueFormat(mask);
            switchCaseMeta.setCaseValueDecimal(decimalSymbol);
            switchCaseMeta.setCaseValueGroup(groupBy);
            // 先使用ID存储步骤，在回调方法中再重置步骤名称
            switchCaseMeta.setDefaultTargetStepname(defaultNextStep);
            switchCaseMeta.setCaseTargets(switchCaseTargets);
            callbackMap.put(stepName, switchCaseMeta);
            StepMeta stepMeta = new StepMeta(stepName, switchCaseMeta);
            TransformConvertFactory.getTransformConvertChains().add(this);
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
            SwitchCaseMeta switchCaseMeta = (SwitchCaseMeta) entry.getValue();
            switchCaseMeta.setDefaultTargetStepname(idNameMapping.get(switchCaseMeta.getDefaultTargetStepname()));
            switchCaseMeta.getCaseTargets().forEach(target -> {
                target.caseTargetStepname = idNameMapping.get(target.caseTargetStepname);
            });
            switchCaseMeta.searchInfoAndTargetSteps(transMeta.getSteps());
            callbackMap.remove(entry.getKey());
        }
    }
}
