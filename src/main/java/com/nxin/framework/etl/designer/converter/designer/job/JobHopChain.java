package com.nxin.framework.etl.designer.converter.designer.job;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;
import com.nxin.framework.etl.designer.converter.designer.transform.ResponseMeta;
import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl;
import lombok.extern.slf4j.Slf4j;
import org.pentaho.di.job.JobHopMeta;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Map;

@Slf4j
public class JobHopChain extends JobConvertChain {

    public JobHopChain() {
    }

    @Override
    public ResponseMeta parse(mxCell cell, JobMeta jobMeta) throws IOException {
        if (cell.isEdge()) {
            mxICell sourceCell = cell.getSource();
            mxICell targetCell = cell.getTarget();
            DeferredElementImpl sourceValue = (DeferredElementImpl) sourceCell.getValue();
            DeferredElementImpl targetValue = (DeferredElementImpl) targetCell.getValue();
            Map<String, Object> sourceAttributes = objectMapper.readValue(sourceValue.getAttribute("form"), new TypeReference<Map<String, Object>>() {
            });
            Map<String, Object> targetAttributes = objectMapper.readValue(targetValue.getAttribute("form"), new TypeReference<Map<String, Object>>() {
            });
            String sourceName;
            if ("JobEntrySpecial".equals(sourceCell.getStyle())) {
                sourceName = "Start";
            } else {
                sourceName = (String) sourceAttributes.get("name");
            }
            String targetName = (String) targetAttributes.get("name");
            JobEntryCopy sourceJobEntryCopy = jobMeta.findJobEntry(sourceName);
            JobEntryCopy targetJobEntryCopy = jobMeta.findJobEntry(targetName);
            JobHopMeta transHopMeta = new JobHopMeta(sourceJobEntryCopy, targetJobEntryCopy);
            if (sourceValue.hasAttribute("link")) {
                Map<String, String> linkMap = objectMapper.readValue(sourceValue.getAttribute("link"), new TypeReference<Map<String, String>>() {
                });
                if (StringUtils.hasLength(linkMap.get(targetCell.getId()))) {
                    transHopMeta.setEvaluation(!linkMap.get(targetCell.getId()).equals("2"));
                }
            }
            return new ResponseMeta(cell.getId(), transHopMeta);
        } else {
            return next.parse(cell, jobMeta);
        }
    }

    @Override
    public void callback(JobMeta jobMeta) {

    }
}
