package com.nxin.framework.etl.designer.converter.designer.transform;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;
import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl;
import lombok.extern.slf4j.Slf4j;
import org.pentaho.di.trans.TransHopMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepErrorMeta;
import org.pentaho.di.trans.step.StepMeta;

import java.util.Map;

@Slf4j
public class TransHopChain extends TransformConvertChain {

    public TransHopChain() {
    }

    @Override
    public ResponseMeta parse(mxCell cell, TransMeta transMeta) throws JsonProcessingException {
        if (cell.isEdge()) {
            mxICell sourceCell = cell.getSource();
            mxICell targetCell = cell.getTarget();
            DeferredElementImpl sourceValue = (DeferredElementImpl) sourceCell.getValue();
            DeferredElementImpl targetValue = (DeferredElementImpl) targetCell.getValue();
            Map<String, Object> sourceAttributes = objectMapper.readValue(sourceValue.getAttribute("form"), new TypeReference<Map<String, Object>>() {
            });
            Map<String, Object> targetAttributes = objectMapper.readValue(targetValue.getAttribute("form"), new TypeReference<Map<String, Object>>() {
            });
            String sourceName = (String) sourceAttributes.get("name");
            String targetName = (String) targetAttributes.get("name");
            StepMeta sourceStep = transMeta.findStep(sourceName);
            StepMeta targetStep = transMeta.findStep(targetName);

            if (sourceValue.hasAttribute("error")) {
                Map<String, Object> errorAttributes = objectMapper.readValue(sourceValue.getAttribute("error"), new TypeReference<Map<String, Object>>() {
                });
                log.info("errorAttributes: {}", errorAttributes);
                if (!errorAttributes.isEmpty()) {
                    StepErrorMeta errorMeta = new StepErrorMeta(transMeta, sourceStep, targetStep);
                    errorMeta.setEnabled(true);
                    errorMeta.setSourceStep(sourceStep);
                    errorMeta.setTargetStep(targetStep);
//                    String errorColumnCode = String.valueOf(errorAttributes.get("errorColumnCode"));
//                    if (StringUtils.hasLength(errorColumnCode)) {
//                        errorMeta.setErrorCodesValuename((String) errorAttributes.get("errorColumnCode"));
//                    }
//
//                    String errorMaxCount = String.valueOf(errorAttributes.get("errorMaxCount"));
//                    if (StringUtils.hasLength(errorMaxCount)) {
//                        errorMeta.setMaxErrors(errorMaxCount);
//                    }
//
//                    String errorColumnName = String.valueOf(errorAttributes.get("errorColumnName"));
//                    if (StringUtils.hasLength(errorColumnName)) {
//                        errorMeta.setErrorFieldsValuename(errorColumnName);
//                    }
//
//                    String errorRate = String.valueOf(errorAttributes.get("errorRate"));
//                    if (StringUtils.hasLength(errorRate)) {
//                        errorMeta.setMaxPercentErrors(errorRate);
//                    }
//
//                    String errorMinRows = String.valueOf(errorAttributes.get("errorMinRows"));
//                    if (StringUtils.hasLength(errorMinRows)) {
//                        errorMeta.setMinPercentRows(errorMinRows);
//                    }
//
//                    String errorCountName = String.valueOf(errorAttributes.get("errorCountName"));
//                    if (StringUtils.hasLength(errorCountName)) {
//                        errorMeta.setNrErrorsValuename(errorCountName);
//                    }
//
//                    String errorColumnDescription = String.valueOf(errorAttributes.get("errorColumnDescription"));
//                    if (StringUtils.hasLength(errorColumnDescription)) {
//                        errorMeta.setErrorDescriptionsValuename(errorColumnDescription);
//                    }
                    sourceStep.setStepErrorMeta(errorMeta);
                }
            }
            TransHopMeta transHopMeta = new TransHopMeta(sourceStep, targetStep);
            return new ResponseMeta(cell.getId(), transHopMeta);
        } else {
            return next.parse(cell, transMeta);
        }
    }

    @Override
    public void callback(TransMeta transMeta, Map<String, String> idNameMapping) {

    }
}
