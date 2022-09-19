package com.nxin.framework.etl.designer.converter.designer.transform.lookup;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.nxin.framework.etl.designer.converter.designer.transform.ResponseMeta;
import com.nxin.framework.etl.designer.converter.designer.transform.TransformConvertChain;
import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl;
import lombok.extern.slf4j.Slf4j;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.multimerge.MultiMergeJoinMeta;

import java.util.List;
import java.util.Map;

@Slf4j
public class MultiMergeJoinChain extends TransformConvertChain {

    @Override
    public ResponseMeta parse(mxCell cell, TransMeta transMeta) throws JsonProcessingException {
        if (cell.isVertex() && "MultiMergeJoinMeta".equalsIgnoreCase(cell.getStyle())) {
            DeferredElementImpl value = (DeferredElementImpl) cell.getValue();
            Map<String, Object> formAttributes = objectMapper.readValue(value.getAttribute("form"), new TypeReference<Map<String, Object>>() {
            });
            MultiMergeJoinMeta multiMergeJoinMeta = new MultiMergeJoinMeta();
            String stepName = (String) formAttributes.get("name");
            String joinType = (String) formAttributes.get("joinType");
            Map<String, Object> inputStep = (Map<String, Object>) formAttributes.get("inputStep");
            Map<String, Object> inputStepLabel = (Map<String, Object>) formAttributes.get("inputStepLabel");
            Map<String, Object> joinKey = (Map<String, Object>) formAttributes.get("joinKey");
            String[] steps = new String[inputStepLabel.size()];
            String[] keys = new String[joinKey.size()];
            int i = 0;
            for (Map.Entry<String, Object> entry : inputStep.entrySet()) {
                steps[i] = (String) inputStepLabel.get(entry.getKey());
                if (joinKey.get(entry.getKey()) instanceof String) {
                    keys[i] = (String) joinKey.get(entry.getKey());
                } else {
                    List<String> multiKeys = (List<String>) joinKey.get(entry.getKey());
                    keys[i] = String.join(",", multiKeys);
                }
                i++;
            }
            multiMergeJoinMeta.setJoinType(joinType);
            multiMergeJoinMeta.setInputSteps(steps);
            multiMergeJoinMeta.setKeyFields(keys);
            StepMeta stepMeta = new StepMeta(stepName, multiMergeJoinMeta);
            if (formAttributes.containsKey("distribute")) {
                boolean distribute = (boolean) formAttributes.get("distribute");
                stepMeta.setDistributes(distribute);
            }
            mxGeometry geometry = cell.getGeometry();
            stepMeta.setLocation(new Double(geometry.getX()).intValue(), new Double(geometry.getY()).intValue());
            stepMeta.setDraw(true);
            stepMeta.setCopies(1);
            return new ResponseMeta(cell.getId(), stepMeta, null);
        } else {
            return next.parse(cell, transMeta);
        }
    }

    @Override
    public void callback(TransMeta transMeta, Map<String, String> idNameMapping) {

    }
}
