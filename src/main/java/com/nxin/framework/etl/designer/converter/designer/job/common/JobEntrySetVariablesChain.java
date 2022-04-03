package com.nxin.framework.etl.designer.converter.designer.job.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.nxin.framework.etl.designer.converter.designer.job.JobConvertChain;
import com.nxin.framework.etl.designer.converter.designer.transform.ResponseMeta;
import com.nxin.framework.etl.designer.enums.Constant;
import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl;
import lombok.extern.slf4j.Slf4j;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entries.setvariables.JobEntrySetVariables;
import org.pentaho.di.job.entry.JobEntryCopy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class JobEntrySetVariablesChain extends JobConvertChain {

    @Override
    public ResponseMeta parse(mxCell cell, JobMeta jobMeta) throws IOException {
        if (cell.isVertex() && "JobEntrySetVariables".equalsIgnoreCase(cell.getStyle())) {
            List<String> names = new ArrayList<>(0);
            List<String> values = new ArrayList<>(0);
            List<Integer> scopes = new ArrayList<>(0);
            DeferredElementImpl value = (DeferredElementImpl) cell.getValue();
            Map<String, Object> formAttributes = objectMapper.readValue(value.getAttribute("form"), new TypeReference<Map<String, Object>>() {
            });
            String name = (String) formAttributes.get("name");
            List<Map<String, String>> parameters = (List<Map<String, String>>) formAttributes.get("parameters");
            for (Map<String, String> parameter : parameters) {
                names.add(parameter.get("name"));
                values.add(parameter.get("value"));
                scopes.add(JobEntrySetVariables.getVariableType(parameter.get("scope")));
            }
            JobEntrySetVariables jobEntrySetVariables = new JobEntrySetVariables(name);
            jobEntrySetVariables.variableName = names.toArray(new String[0]);
            jobEntrySetVariables.variableValue = values.toArray(new String[0]);
            jobEntrySetVariables.variableType = scopes.stream().mapToInt(Integer::valueOf).toArray();
            if (formAttributes.containsKey("replaceVars")) {
                boolean replaceVars = (boolean) formAttributes.get("replaceVars");
                jobEntrySetVariables.replaceVars = replaceVars;
            }
            JobEntryCopy jobEntryCopy = new JobEntryCopy(jobEntrySetVariables);

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
