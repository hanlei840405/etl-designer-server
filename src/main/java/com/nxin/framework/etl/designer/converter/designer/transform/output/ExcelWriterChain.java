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
import org.pentaho.di.trans.steps.excelwriter.ExcelWriterStepField;
import org.pentaho.di.trans.steps.excelwriter.ExcelWriterStepMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class ExcelWriterChain extends TransformConvertChain {
    @Override
    public ResponseMeta parse(mxCell cell, TransMeta transMeta) throws JsonProcessingException {
        if (cell.isVertex() && "ExcelWriterStepMeta".equalsIgnoreCase(cell.getStyle())) {
            DeferredElementImpl value = (DeferredElementImpl) cell.getValue();
            Map<String, Object> formAttributes = objectMapper.readValue(value.getAttribute("form"), new TypeReference<Map<String, Object>>() {
            });
            ExcelWriterStepMeta excelWriterStepMeta = new ExcelWriterStepMeta();
            List<ExcelWriterStepField> excelWriterStepFields = new ArrayList<>(0);
            String stepName = (String) formAttributes.get("name");
            String filename = (String) formAttributes.get("filename");
            int rowNumber = (int) formAttributes.get("rowNumber");
            String suffix = (String) formAttributes.get("suffix");
            String dateFormat = (String) formAttributes.get("dateFormat");
            String fileOutputMode = (String) formAttributes.get("fileOutputMode");
            String sheetName = (String) formAttributes.get("sheetName");
            String sheetOutputMode = (String) formAttributes.get("sheetOutputMode");
            String startPoint = (String) formAttributes.get("startPoint");
            String cellOutputMode = (String) formAttributes.get("cellOutputMode");
            int deleteLines = (int) formAttributes.get("deleteLines");
            int emptyLines = (int) formAttributes.get("emptyLines");
            boolean useDateFormat = (boolean) formAttributes.get("useDateFormat");
            boolean useStream = (boolean) formAttributes.get("useStream");
            boolean useStepInfoSuffix = (boolean) formAttributes.get("useStepInfoSuffix");
            boolean useDateSuffix = (boolean) formAttributes.get("useDateSuffix");
            boolean useTimeSuffix = (boolean) formAttributes.get("useTimeSuffix");
            boolean generateMode = (boolean) formAttributes.get("generateMode");
            boolean nameInResult = (boolean) formAttributes.get("nameInResult");
            boolean useHeader = (boolean) formAttributes.get("useHeader");
            boolean useFooter = (boolean) formAttributes.get("useFooter");
            boolean autoSize = (boolean) formAttributes.get("autoSize");
            boolean recalculate = (boolean) formAttributes.get("recalculate");
            boolean keepStyle = (boolean) formAttributes.get("keepStyle");
            boolean append = (boolean) formAttributes.get("append");
            boolean deleteHeader = (boolean) formAttributes.get("deleteHeader");
            boolean asActiveSheet = (boolean) formAttributes.get("asActiveSheet");
            int parallel = (int) formAttributes.get(Constant.ETL_PARALLEL);

            excelWriterStepMeta.setFileName(filename);
            excelWriterStepMeta.setExtension(suffix);
            excelWriterStepMeta.setSplitEvery(rowNumber);
            excelWriterStepMeta.setDateTimeFormat(dateFormat);
            excelWriterStepMeta.setIfFileExists(fileOutputMode);
            excelWriterStepMeta.setSheetname(sheetName);
            excelWriterStepMeta.setIfSheetExists(sheetOutputMode);
            excelWriterStepMeta.setStartingCell(startPoint);
            excelWriterStepMeta.setRowWritingMethod(cellOutputMode);
            excelWriterStepMeta.setAppendOffset(deleteLines);
            excelWriterStepMeta.setAppendEmpty(emptyLines);
            excelWriterStepMeta.setSpecifyFormat(useDateFormat);
            excelWriterStepMeta.setStreamingData(useStream);
            excelWriterStepMeta.setStepNrInFilename(useStepInfoSuffix);
            excelWriterStepMeta.setDateInFilename(useDateSuffix);
            excelWriterStepMeta.setTimeInFilename(useTimeSuffix);
            excelWriterStepMeta.setDoNotOpenNewFileInit(generateMode);
            excelWriterStepMeta.setAddToResultFiles(nameInResult);
            excelWriterStepMeta.setHeaderEnabled(useHeader);
            excelWriterStepMeta.setFooterEnabled(useFooter);
            excelWriterStepMeta.setAutoSizeColums(autoSize);
            excelWriterStepMeta.setForceFormulaRecalculation(recalculate);
            excelWriterStepMeta.setLeaveExistingStylesUnchanged(keepStyle);
            excelWriterStepMeta.setAppendLines(append);
            excelWriterStepMeta.setAppendOmitHeader(deleteHeader);
            excelWriterStepMeta.setMakeSheetActive(asActiveSheet);
            String folder = (String) ConvertFactory.getVariable().get("attachmentDir");
            excelWriterStepMeta.setFileName(folder + filename);
            List<Map<String, String>> parameters = (List<Map<String, String>>) formAttributes.get("parameters");
            for (Map<String, String> parameter : parameters) {
                ExcelWriterStepField excelWriterStepField = new ExcelWriterStepField();
                excelWriterStepField.setName(parameter.get("field"));
                excelWriterStepField.setType(parameter.get("category"));
                excelWriterStepField.setFormat(parameter.get("format"));
                excelWriterStepField.setStyleCell(parameter.get("style"));
                excelWriterStepField.setTitle(parameter.get("title"));
                excelWriterStepField.setTitleStyleCell(parameter.get("headerStyle"));
                if ("Y".equals(parameter.get("category"))) {
                    excelWriterStepField.setFormula(true);
                } else {
                    excelWriterStepField.setFormula(false);
                }
                excelWriterStepField.setHyperlinkField(parameter.get("link"));
                excelWriterStepFields.add(excelWriterStepField);
            }
            excelWriterStepMeta.setOutputFields(excelWriterStepFields.toArray(new ExcelWriterStepField[0]));
            StepMeta stepMeta = new StepMeta(stepName, excelWriterStepMeta);
            stepMeta.setCopies(parallel);
            mxGeometry geometry = cell.getGeometry();
            if (formAttributes.containsKey("distribute")) {
                boolean distribute = (boolean) formAttributes.get("distribute");
                stepMeta.setDistributes(distribute);
            }
            stepMeta.setLocation(new Double(geometry.getX()).intValue(), new Double(geometry.getY()).intValue());
            stepMeta.setDraw(true);
            if (!ConvertFactory.getVariable().containsKey("attachments")) {
                ConvertFactory.getVariable().put("attachments", new ArrayList<Map<String, Object>>(0));
            }
            return new ResponseMeta(cell.getId(), stepMeta, null);
        } else {
            return next.parse(cell, transMeta);
        }
    }

    @Override
    public void callback(TransMeta transMeta, Map<String, String> idNameMapping) {

    }
}
