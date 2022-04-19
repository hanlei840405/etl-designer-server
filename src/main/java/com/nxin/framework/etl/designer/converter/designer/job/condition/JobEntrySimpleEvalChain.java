package com.nxin.framework.etl.designer.converter.designer.job.condition;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.nxin.framework.etl.designer.converter.designer.job.JobConvertChain;
import com.nxin.framework.etl.designer.converter.designer.transform.ResponseMeta;
import com.nxin.framework.etl.designer.enums.Constant;
import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl;
import lombok.extern.slf4j.Slf4j;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entries.simpleeval.JobEntrySimpleEval;
import org.pentaho.di.job.entry.JobEntryCopy;

import java.io.IOException;
import java.util.Map;

@Slf4j
public class JobEntrySimpleEvalChain extends JobConvertChain {

    @Override
    public ResponseMeta parse(mxCell cell, JobMeta jobMeta) throws IOException {
        if (cell.isVertex() && "JobEntrySimpleEval".equalsIgnoreCase(cell.getStyle())) {
            DeferredElementImpl value = (DeferredElementImpl) cell.getValue();
            Map<String, Object> formAttributes = objectMapper.readValue(value.getAttribute("form"), new TypeReference<Map<String, Object>>() {
            });
            String name = (String) formAttributes.get("name");
            String mode = (String) formAttributes.get("mode");
            String fieldName = (String) formAttributes.get("fieldName");
            String variableName = (String) formAttributes.get("variableName");
            String category = (String) formAttributes.get("category");
            boolean success = (boolean) formAttributes.get("success");
            String condition = (String) formAttributes.get("condition");
            String valueForm = (String) formAttributes.get("value");
            String min = (String) formAttributes.get("min");
            String max = (String) formAttributes.get("max");
            JobEntrySimpleEval jobEntrySimpleEval = new JobEntrySimpleEval();
            jobEntrySimpleEval.setName(name);
            int valuetype = 0;
            for (int i = 0; i < Constant.VALUE_TYPE_CODE.length; i++) {
                if (Constant.VALUE_TYPE_CODE[i].equals(mode)) {
                    valuetype = i;
                    break;
                }
            }
            jobEntrySimpleEval.valuetype = valuetype;
            jobEntrySimpleEval.setFieldName(fieldName);
            jobEntrySimpleEval.setVariableName(variableName);
            int fieldtype = 0;
            for (int i = 0; i < Constant.FIELD_TYPE_CODE.length; i++) {
                if (Constant.FIELD_TYPE_CODE[i].equals(category)) {
                    fieldtype = i;
                    break;
                }
            }
            jobEntrySimpleEval.fieldtype = fieldtype;
            jobEntrySimpleEval.setSuccessWhenVarSet(success);
            if (Constant.NUMBER.equals(category)) {
                jobEntrySimpleEval.successnumbercondition = JobEntrySimpleEval.getSuccessNumberConditionByCode(condition);
            } else if (Constant.BOOLEAN.equals(category)) {
                int successbooleancondition = 0;
                for (int i = 0; i < Constant.SUCCESS_CONDITION_BOOLEAN_CODE.length; i++) {
                    if (Constant.SUCCESS_CONDITION_BOOLEAN_CODE[i].equals(condition)) {
                        successbooleancondition = i;
                        break;
                    }
                }
                jobEntrySimpleEval.successbooleancondition = successbooleancondition;
            } else {
                int successcondition = 0;
                for (int i = 0; i < Constant.SUCCESS_CONDITION_CODE.length; i++) {
                    if (Constant.SUCCESS_CONDITION_CODE[i].equals(condition)) {
                        successcondition = i;
                        break;
                    }
                }
                jobEntrySimpleEval.successcondition = successcondition;
            }
            jobEntrySimpleEval.setCompareValue(valueForm);
            jobEntrySimpleEval.setMinValue(min);
            jobEntrySimpleEval.setMaxValue(max);
            JobEntryCopy jobEntryCopy = new JobEntryCopy(jobEntrySimpleEval);
            mxGeometry geometry = cell.getGeometry();
            jobEntryCopy.setLocation(new Double(geometry.getX()).intValue(), new Double(geometry.getY()).intValue());
            jobEntryCopy.setDrawn();
            if (formAttributes.containsKey(Constant.ETL_PARALLEL)) {
                boolean parallel = (boolean) formAttributes.get(Constant.ETL_PARALLEL);
                jobEntryCopy.setLaunchingInParallel(parallel);
            }
            return new ResponseMeta(cell.getId(), jobEntryCopy, null);
        } else {
            return next.parse(cell, jobMeta);
        }
    }

    @Override
    public void callback(JobMeta jobMeta) {

    }
}
