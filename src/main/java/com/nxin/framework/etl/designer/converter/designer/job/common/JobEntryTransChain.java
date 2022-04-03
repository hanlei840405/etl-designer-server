package com.nxin.framework.etl.designer.converter.designer.job.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.nxin.framework.etl.designer.converter.designer.ConvertFactory;
import com.nxin.framework.etl.designer.converter.designer.job.JobConvertChain;
import com.nxin.framework.etl.designer.converter.designer.transform.ResponseMeta;
import com.nxin.framework.etl.designer.entity.designer.Shell;
import com.nxin.framework.etl.designer.enums.Constant;
import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl;
import lombok.extern.slf4j.Slf4j;
import org.pentaho.di.core.ObjectLocationSpecificationMethod;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entries.trans.JobEntryTrans;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
public class JobEntryTransChain extends JobConvertChain {

    @Override
    public ResponseMeta parse(mxCell cell, JobMeta jobMeta) throws IOException {
        if (cell.isVertex() && "JobEntryTrans".equalsIgnoreCase(cell.getStyle())) {
            DeferredElementImpl value = (DeferredElementImpl) cell.getValue();
            Map<String, Object> formAttributes = objectMapper.readValue(value.getAttribute("form"), new TypeReference<Map<String, Object>>() {
            });
            String name = (String) formAttributes.get("name");
            Integer shellId = (Integer) formAttributes.get("shellId");
            List<String> arguments = new ArrayList<>(0);
            List<String> parameters = new ArrayList<>(0);
            List<String> parameterFieldNames = new ArrayList<>(0);
            List<String> parameterValues = new ArrayList<>(0);
            JobEntryTrans jobEntryTrans = new JobEntryTrans(name);
            jobEntryTrans.setSpecificationMethod(ObjectLocationSpecificationMethod.FILENAME);
            if (formAttributes.containsKey("executeEachRow")) {
                boolean executeEachRow = (boolean) formAttributes.get("executeEachRow");
                jobEntryTrans.execPerRow = executeEachRow;
            }
            if (formAttributes.containsKey("clearRecords")) {
                boolean clearRecords = (boolean) formAttributes.get("clearRecords");
                jobEntryTrans.clearResultRows = clearRecords;
            }
            if (formAttributes.containsKey("clearFileRecords")) {
                boolean clearFileRecords = (boolean) formAttributes.get("clearFileRecords");
                jobEntryTrans.clearResultFiles = clearFileRecords;
            }
            if (formAttributes.containsKey("waitRemoteFinished")) {
                boolean waitRemoteFinished = (boolean) formAttributes.get("waitRemoteFinished");
                jobEntryTrans.waitingToFinish = waitRemoteFinished;
            }
            if (formAttributes.containsKey("notice")) {
                boolean notice = (boolean) formAttributes.get("notice");
                jobEntryTrans.followingAbortRemotely = notice;
            }
            List<Map<String, String>> resultArguments = (List<Map<String, String>>) formAttributes.get("resultArguments");
            for (Map<String, String> resultArgument : resultArguments) {
                arguments.add(resultArgument.get("argument"));
            }
            if (!arguments.isEmpty()) {
                jobEntryTrans.argFromPrevious = true;
                jobEntryTrans.arguments = arguments.toArray(new String[0]);
            } else {
                jobEntryTrans.arguments = new String[0];
            }
            List<Map<String, String>> nameArguments = (List<Map<String, String>>) formAttributes.get("nameArguments");
            for (Map<String, String> nameArgument : nameArguments) {
                parameters.add(nameArgument.get("name"));
                parameterFieldNames.add(nameArgument.get("field"));
                parameterValues.add(nameArgument.get("value"));
            }
            if (!parameters.isEmpty()) {
                jobEntryTrans.paramsFromPrevious = true;
                jobEntryTrans.setPassingAllParameters(true);
                jobEntryTrans.parameters = parameters.toArray(new String[0]);
                jobEntryTrans.parameterFieldNames = parameterFieldNames.toArray(new String[0]);
                jobEntryTrans.parameterValues = parameterValues.toArray(new String[0]);
            } else {
                jobEntryTrans.parameters = new String[0];
                jobEntryTrans.parameterFieldNames = new String[0];
                jobEntryTrans.parameterValues = new String[0];
            }
            Shell transformShell = getShellService().one(shellId.longValue());
            if (transformShell.isExecutable()) {
                jobEntryTrans.setFileName(transformShell.getXml());
            }
            JobEntryCopy jobEntryCopy = new JobEntryCopy(jobEntryTrans);
            mxGeometry geometry = cell.getGeometry();
            jobEntryCopy.setLocation(new Double(geometry.getX()).intValue(), new Double(geometry.getY()).intValue());
            jobEntryCopy.setDrawn();
            if (formAttributes.containsKey(Constant.ETL_PARALLEL)) {
                boolean parallel = (boolean) formAttributes.get(Constant.ETL_PARALLEL);
                jobEntryCopy.setLaunchingInParallel(parallel);
            }
            List<String> references = new ArrayList<>(0);
            if (StringUtils.hasLength(transformShell.getReference())) {
                Arrays.stream(transformShell.getReference().split(",")).forEach(id -> {
                    references.add(id);
                });
            }
            references.add(shellId.toString());
            ConvertFactory.getVariable().put("references", references);
            jobEntryCopy.getXML();
            return new ResponseMeta(cell.getId(), jobEntryCopy, null);
        } else {
            return next.parse(cell, jobMeta);
        }
    }

    @Override
    public void callback(JobMeta jobMeta) {

    }
}
