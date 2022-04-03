package com.nxin.framework.etl.designer.converter.designer.transform.process;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.nxin.framework.etl.designer.converter.designer.transform.ResponseMeta;
import com.nxin.framework.etl.designer.converter.designer.transform.TransformConvertChain;
import com.nxin.framework.etl.designer.enums.Constant;
import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl;
import lombok.extern.slf4j.Slf4j;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.writetolog.WriteToLogMeta;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class WriteToLogChain extends TransformConvertChain {

    @Override
    public ResponseMeta parse(mxCell cell, TransMeta transMeta) throws JsonProcessingException {
        if (cell.isVertex() && "WriteToLogMeta".equalsIgnoreCase(cell.getStyle())) {
            DeferredElementImpl value = (DeferredElementImpl) cell.getValue();
            WriteToLogMeta writeToLogMeta = new WriteToLogMeta();
            Map<String, Object> formAttributes = objectMapper.readValue(value.getAttribute("form"), new TypeReference<Map<String, Object>>() {
            });
            List<String> fields = new ArrayList<>(0);
            String stepName = (String) formAttributes.get("name");
            int level = (int) formAttributes.get("level");
            String logs = (String) formAttributes.get("logs");
            boolean printHeader = (boolean) formAttributes.get("printHeader");
            boolean useLimitRows = (boolean) formAttributes.get("useLimitRows");
            int limitRows;
            if (ObjectUtils.isEmpty(formAttributes.get("limitRows"))) {
                limitRows = 0;
            } else {
                limitRows = (int) formAttributes.get("limitRows");
            }
            writeToLogMeta.setDisplayHeader(printHeader);
            writeToLogMeta.setLogLevel(level);
            writeToLogMeta.setLogMessage(logs);
            writeToLogMeta.setLimitRows(useLimitRows);
            writeToLogMeta.setLimitRowsNumber(limitRows);
            List<Map<String, String>> parameters = (List<Map<String, String>>) formAttributes.get("parameters");
            for (Map<String, String> fieldMapping : parameters) {
                fields.add(fieldMapping.get("field"));
            }
            writeToLogMeta.setFieldName(fields.toArray(new String[0]));
            int parallel = (int) formAttributes.get(Constant.ETL_PARALLEL);
            StepMeta stepMeta = new StepMeta(stepName, writeToLogMeta);
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

    }
}
