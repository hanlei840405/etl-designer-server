package com.nxin.framework.etl.designer.converter.designer.transform.convert;

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
import org.pentaho.di.trans.steps.splitfieldtorows.SplitFieldToRowsMeta;

import java.util.Map;

@Slf4j
public class SplitFieldToRowsChain extends TransformConvertChain {

    @Override
    public ResponseMeta parse(mxCell cell, TransMeta transMeta) throws JsonProcessingException {
        if (cell.isVertex() && "SplitFieldToRowsMeta".equalsIgnoreCase(cell.getStyle())) {
            DeferredElementImpl value = (DeferredElementImpl) cell.getValue();
            SplitFieldToRowsMeta splitFieldToRowsMeta = new SplitFieldToRowsMeta();
            Map<String, Object> formAttributes = objectMapper.readValue(value.getAttribute("form"), new TypeReference<Map<String, Object>>() {
            });
            String stepName = (String) formAttributes.get("name");
            String field = (String) formAttributes.get("field");
            String target = (String) formAttributes.get("target");
            String delimiter = (String) formAttributes.get("delimiter");
            Boolean splitByReg = (Boolean) formAttributes.get("splitByReg");
            Boolean useLineNo = (Boolean) formAttributes.get("useLineNo");
            Boolean resetLineNo = (Boolean) formAttributes.get("resetLineNo");
            String lineName = (String) formAttributes.get("lineName");
            splitFieldToRowsMeta.setSplitField(field);
            splitFieldToRowsMeta.setNewFieldname(target);
            splitFieldToRowsMeta.setDelimiter(delimiter);
            splitFieldToRowsMeta.setDelimiterRegex(splitByReg);
            splitFieldToRowsMeta.setIncludeRowNumber(useLineNo);
            splitFieldToRowsMeta.setResetRowNumber(resetLineNo);
            splitFieldToRowsMeta.setRowNumberField(lineName);
            int parallel = (int) formAttributes.get(Constant.ETL_PARALLEL);
            StepMeta stepMeta = new StepMeta(stepName, splitFieldToRowsMeta);
            stepMeta.setCopies(parallel);
            mxGeometry geometry = cell.getGeometry();
            stepMeta.setLocation(new Double(geometry.getX()).intValue(), new Double(geometry.getY()).intValue());
            stepMeta.setDraw(true);
            return new ResponseMeta(cell.getId(), stepMeta, null);
        } else {
            return next.parse(cell, transMeta);
        }
    }

    @Override
    public void callback(TransMeta transMeta, Map<String, String> idNameMapping) {

    }
}
