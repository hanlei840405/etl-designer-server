package com.nxin.framework.etl.designer.converter.designer.transform.input;

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
import org.pentaho.di.trans.steps.jsoninput.JsonInputField;
import org.pentaho.di.trans.steps.jsoninput.JsonInputMeta;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class JsonInputChain extends TransformConvertChain {

    @Override
    public ResponseMeta parse(mxCell cell, TransMeta transMeta) throws JsonProcessingException {
        if (cell.isVertex() && "JsonInputMeta".equalsIgnoreCase(cell.getStyle())) {
            DeferredElementImpl value = (DeferredElementImpl) cell.getValue();
            JsonInputMeta jsonInputMeta = new JsonInputMeta();
            Map<String, Object> formAttributes = objectMapper.readValue(value.getAttribute("form"), new TypeReference<Map<String, Object>>() {
            });
            String stepName = (String) formAttributes.get("name");
            boolean inFields = (boolean) formAttributes.get("inFields");
            String valueField = (String) formAttributes.get("valueField");
            boolean ignoreEmptyFile = (boolean) formAttributes.get("ignoreEmptyFile");
            boolean notFailIfNoFile = (boolean) formAttributes.get("notFailIfNoFile");
            boolean ignoreMissingPath = (boolean) formAttributes.get("ignoreMissingPath");
            boolean defaultPathLeafToNull = (boolean) formAttributes.get("defaultPathLeafToNull");
            int rowLimit = (int) formAttributes.get("rowLimit");
            boolean includeFilename = (boolean) formAttributes.get("includeFilename");
            String filenameField = (String) formAttributes.get("filenameField");
            boolean includeRowNumber = (boolean) formAttributes.get("includeRowNumber");
            String rowNumberField = (String) formAttributes.get("rowNumberField");
            jsonInputMeta.setInFields(inFields);
            jsonInputMeta.setFieldValue(valueField);
            jsonInputMeta.setIgnoreEmptyFile(ignoreEmptyFile);
            jsonInputMeta.setDoNotFailIfNoFile(notFailIfNoFile);
            jsonInputMeta.setIgnoreMissingPath(ignoreMissingPath);
            jsonInputMeta.setDefaultPathLeafToNull(defaultPathLeafToNull);
            jsonInputMeta.setRowLimit(rowLimit);
            jsonInputMeta.setIncludeFilename(includeFilename);
            jsonInputMeta.setFilenameField(filenameField);
            jsonInputMeta.setIncludeFilename(includeFilename);
            jsonInputMeta.setIncludeRowNumber(includeRowNumber);
            jsonInputMeta.setRowNumberField(rowNumberField);
            List<Map<String, String>> sourceFiles = (List<Map<String, String>>) formAttributes.get("sourceFileData");
            List<String> fileNameList = new ArrayList<>(0);
            List<String> fileMaskList = new ArrayList<>(0);
            List<String> excludeFileMaskList = new ArrayList<>(0);
            List<String> fileRequiredList = new ArrayList<>(0);
            List<String> includeSubFoldersList = new ArrayList<>(0);
            String folder = (String) ConvertFactory.getVariable().get("attachmentDir");
            for (Map<String, String> sourceFile : sourceFiles) {
                fileNameList.add(folder + sourceFile.get("path"));
                fileMaskList.add(sourceFile.get("wildcard"));
                excludeFileMaskList.add(sourceFile.get("excludeWildcard"));
                fileRequiredList.add(sourceFile.get("required"));
                includeSubFoldersList.add(sourceFile.get("includeChildrenFolder"));
            }
            jsonInputMeta.inputFiles = new JsonInputMeta.InputFiles();
            jsonInputMeta.inputFiles.allocate(sourceFiles.size());
            jsonInputMeta.setFileName(fileNameList.toArray(new String[0]));
            jsonInputMeta.setFileMask(fileMaskList.toArray(new String[0]));
            jsonInputMeta.setExcludeFileMask(excludeFileMaskList.toArray(new String[0]));
            jsonInputMeta.setFileRequired(fileRequiredList.toArray(new String[0]));
            jsonInputMeta.setIncludeSubFolders(includeSubFoldersList.toArray(new String[0]));

            List<Map<String, Object>> parameters = (List<Map<String, Object>>) formAttributes.get("parameters");
            List<JsonInputField> jsonInputFields = new ArrayList<>(0);
            for (Map<String, Object> fieldMapping : parameters) {
                JsonInputField jsonInputField = new JsonInputField();
                jsonInputField.setName((String) fieldMapping.get("field"));
                jsonInputField.setPath((String) fieldMapping.get("path"));
                jsonInputField.setType((String) fieldMapping.get("category"));
                jsonInputField.setFormat((String) fieldMapping.get("formatValue"));
                if (!ObjectUtils.isEmpty(fieldMapping.get("lengthValue"))) {
                    jsonInputField.setLength((int) fieldMapping.get("lengthValue"));
                } else {
                    jsonInputField.setLength(-1);
                }
                if (!ObjectUtils.isEmpty(fieldMapping.get("accuracy"))) {
                    jsonInputField.setPrecision((int) fieldMapping.get("accuracy"));
                } else {
                    jsonInputField.setPrecision(-1);
                }
                jsonInputField.setCurrencySymbol((String) fieldMapping.get("currency"));
                jsonInputField.setDecimalSymbol((String) fieldMapping.get("decimal"));
                jsonInputField.setGroupSymbol((String) fieldMapping.get("groupBy"));
                jsonInputField.setTrimType((String) fieldMapping.get("removeBlank"));
                if ("Y".equals(fieldMapping.get("repeat"))) {
                    jsonInputField.setRepeated(true);
                } else {
                    jsonInputField.setRepeated(false);
                }
                jsonInputFields.add(jsonInputField);
            }
            jsonInputMeta.setInputFields(jsonInputFields.toArray(new JsonInputField[0]));
            int parallel = (int) formAttributes.get(Constant.ETL_PARALLEL);
            StepMeta stepMeta = new StepMeta(stepName, jsonInputMeta);
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
