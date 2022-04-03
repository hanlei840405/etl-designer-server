package com.nxin.framework.etl.designer.converter.designer.transform.output;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.nxin.framework.etl.designer.converter.designer.ConvertFactory;
import com.nxin.framework.etl.designer.converter.designer.transform.ResponseMeta;
import com.nxin.framework.etl.designer.converter.designer.transform.TransformConvertChain;
import com.nxin.framework.etl.designer.enums.Constant;
import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl;
import lombok.extern.slf4j.Slf4j;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.jsonoutput.JsonOutputField;
import org.pentaho.di.trans.steps.jsonoutput.JsonOutputMeta;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class JsonOutputChain extends TransformConvertChain {
    @Override
    public ResponseMeta parse(mxCell cell, TransMeta transMeta) throws JsonProcessingException {
        if (cell.isVertex() && "JsonOutputMeta".equalsIgnoreCase(cell.getStyle())) {
            DeferredElementImpl value = (DeferredElementImpl) cell.getValue();
            Map<String, Object> formAttributes = objectMapper.readValue(value.getAttribute("form"), new TypeReference<Map<String, Object>>() {
            });
            JsonOutputMeta jsonOutputMeta = new JsonOutputMeta();
            List<JsonOutputField> jsonOutputFields = new ArrayList<>(0);
            String stepName = (String) formAttributes.get("name");
            String jsonName = (String) formAttributes.get("jsonName");
            String targetMode = (String) formAttributes.get("targetMode");
            int rowNumber = 0;
            if (!ObjectUtils.isEmpty(formAttributes.get(rowNumber))) {
                rowNumber = (int) formAttributes.get("rowNumber");
            }
            String outputValue = (String) formAttributes.get("outputValue");
            String filename = (String) formAttributes.get("filename");
            String suffix = (String) formAttributes.get("suffix");
            boolean compatibleMode = (boolean) formAttributes.get("compatibleMode");
            boolean append = (boolean) formAttributes.get("append");
            boolean makeDir = (boolean) formAttributes.get("makeDir");
            boolean servlet = (boolean) formAttributes.get("servlet");
            boolean appendDate = (boolean) formAttributes.get("appendDate");
            boolean addendTime = (boolean) formAttributes.get("addendTime");
            int parallel = (int) formAttributes.get(Constant.ETL_PARALLEL);

            jsonOutputMeta.setOutputValue(outputValue);
            jsonOutputMeta.setJsonBloc(jsonName);
            jsonOutputMeta.setNrRowsInBloc(String.valueOf(rowNumber));
            if (StringUtils.hasLength(targetMode)) {
                jsonOutputMeta.setOperationType(Integer.valueOf(targetMode));
            } else {
                jsonOutputMeta.setOperationType(0);
            }
            jsonOutputMeta.setCompatibilityMode(compatibleMode);
            jsonOutputMeta.setEncoding(Constant.ENCODING_UTF_8);
            String folder = (String) ConvertFactory.getVariable().get("attachmentDir");
            jsonOutputMeta.setFileName(folder + filename);
            jsonOutputMeta.setExtension(suffix);
            jsonOutputMeta.setFileAppended(append);
            jsonOutputMeta.setDateInFilename(appendDate);
            jsonOutputMeta.setTimeInFilename(addendTime);
            jsonOutputMeta.setServletOutput(servlet);
            jsonOutputMeta.setDoNotOpenNewFileInit(makeDir);
            List<Map<String, String>> parameters = (List<Map<String, String>>) formAttributes.get("parameters");
            for (Map<String, String> parameter : parameters) {
                JsonOutputField jsonOutputField = new JsonOutputField();
                jsonOutputField.setFieldName(parameter.get("field"));
                jsonOutputField.setElementName(parameter.get("element"));
                jsonOutputFields.add(jsonOutputField);
            }
            jsonOutputMeta.setOutputFields(jsonOutputFields.toArray(new JsonOutputField[0]));
            StepMeta stepMeta = new StepMeta(stepName, jsonOutputMeta);
            stepMeta.setCopies(parallel);
            mxGeometry geometry = cell.getGeometry();
            if (formAttributes.containsKey("distribute")) {
                boolean distribute = (boolean) formAttributes.get("distribute");
                stepMeta.setDistributes(distribute);
            }
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
