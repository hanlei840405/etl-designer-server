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
            jobEntrySimpleEval.valuetype = JobEntrySimpleEval.getValueTypeByDesc(mode);
            jobEntrySimpleEval.setFieldName(fieldName);
            jobEntrySimpleEval.setVariableName(variableName);
            jobEntrySimpleEval.fieldtype = JobEntrySimpleEval.getFieldTypeByDesc(category);
            jobEntrySimpleEval.setSuccessWhenVarSet(success);
            if (Constant.NUMBER.equals(category)) {
                jobEntrySimpleEval.successnumbercondition = JobEntrySimpleEval.getSuccessNumberConditionByCode(condition);
            } else if (Constant.BOOLEAN.equals(category)) {
                jobEntrySimpleEval.successbooleancondition = JobEntrySimpleEval.getSuccessBooleanConditionByDesc(condition);
            } else {
                jobEntrySimpleEval.successcondition = JobEntrySimpleEval.getSuccessConditionByDesc(condition);
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
