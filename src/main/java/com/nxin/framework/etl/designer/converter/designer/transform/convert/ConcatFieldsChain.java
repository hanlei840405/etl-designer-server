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
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.concatfields.ConcatFieldsMeta;
import org.pentaho.di.trans.steps.textfileoutput.TextFileField;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class ConcatFieldsChain extends TransformConvertChain {

    @Override
    public ResponseMeta parse(mxCell cell, TransMeta transMeta) throws JsonProcessingException {
        if (cell.isVertex() && "ConcatFieldsMeta".equalsIgnoreCase(cell.getStyle())) {
            DeferredElementImpl value = (DeferredElementImpl) cell.getValue();
            ConcatFieldsMeta concatFieldsMeta = new ConcatFieldsMeta();
            Map<String, Object> formAttributes = objectMapper.readValue(value.getAttribute("form"), new TypeReference<Map<String, Object>>() {
            });
            String stepName = (String) formAttributes.get("name");
            String target = (String) formAttributes.get("target");
            int length;
            if (ObjectUtils.isEmpty(formAttributes.get("lengthValue"))) {
                length = (int) formAttributes.get("lengthValue");
            } else {
                length = 0;
            }
            String separator = (String) formAttributes.get("separator");
            String enclosure = (String) formAttributes.get("enclosure");
            List<TextFileField> textFileFields = new ArrayList<>(0);
            List<Map<String, Object>> fieldMappingData = (List<Map<String, Object>>) formAttributes.get("parameters");
            for (Map<String, Object> fieldMapping : fieldMappingData) {
                TextFileField textFileField = new TextFileField();
                textFileField.setName((String) fieldMapping.get("source"));
                textFileField.setType(ValueMetaFactory.getIdForValueMeta((String) fieldMapping.get("category")));
                if (!ObjectUtils.isEmpty(fieldMapping.get("lengthValue"))) {
                    textFileField.setLength((int) fieldMapping.get("lengthValue"));
                } else {
                    textFileField.setLength(-1);
                }
                if (!ObjectUtils.isEmpty(fieldMapping.get("accuracy"))) {
                    textFileField.setPrecision((int) fieldMapping.get("accuracy"));
                } else {
                    textFileField.setPrecision(-1);
                }
                textFileField.setFormat((String) fieldMapping.get("formatValue"));
                textFileField.setCurrencySymbol((String) fieldMapping.get("currency"));
                textFileField.setDecimalSymbol((String) fieldMapping.get("decimal"));
                textFileField.setGroupingSymbol((String) fieldMapping.get("groupBy"));
                textFileField.setNullString((String) fieldMapping.get("emptyValue"));
                int removeBlank = 0;
                for (int i = 0; i < Constant.TRIM_TYPE_CODE.length; i++) {
                    if (Constant.TRIM_TYPE_CODE[i].equals(fieldMapping.get("removeBlank"))) {
                        removeBlank = i;
                        break;
                    }
                }
                textFileField.setTrimType(removeBlank);
                textFileFields.add(textFileField);
            }
            concatFieldsMeta.setTargetFieldName(target);
            concatFieldsMeta.setTargetFieldLength(length);
            concatFieldsMeta.setSeparator(separator);
            concatFieldsMeta.setEnclosure(enclosure);
            concatFieldsMeta.setOutputFields(textFileFields.toArray(new TextFileField[0]));
            int parallel = (int) formAttributes.get(Constant.ETL_PARALLEL);
            StepMeta stepMeta = new StepMeta(stepName, concatFieldsMeta);
            stepMeta.setCopies(parallel);
            mxGeometry geometry = cell.getGeometry();
            stepMeta.setLocation(new Double(geometry.getX()).intValue(), new Double(geometry.getY()).intValue());
            stepMeta.setDraw(true);
            return new ResponseMeta(cell.getId(), stepMeta, null);
        } else {
            return next.parse(cell, transMeta);
        }
    }

//    @Override
//    public void parse(Long branchId, StepMeta stepMeta, mxGraph graph) throws JsonProcessingException {
//        if (stepMeta.getStepMetaInterface() instanceof TableInputMeta) {
//            String name = stepMeta.getName();
//            TableInputMeta tableInputMeta = (TableInputMeta) stepMeta.getStepMetaInterface();
//            String databaseName = tableInputMeta.getDatabaseMeta().getName();
//            Datasource datasource = datasourceService.one(branchId, databaseName);
//            String sql = tableInputMeta.getSQL();
//            boolean lazyConversionActive = tableInputMeta.isLazyConversionActive();
//            boolean executeEachInputRow = tableInputMeta.isExecuteEachInputRow();
//            boolean variableReplacementActive = tableInputMeta.isVariableReplacementActive();
//            String rowLimit = tableInputMeta.getRowLimit();
//            int x = stepMeta.getLocation().x;
//            int y = stepMeta.getLocation().y;
//            Document doc = mxDomUtils.createDocument();
//            Element node = doc.createElement("data");
//            Map<String, Object> formAttributes = new HashMap<>(0);
//            formAttributes.put("datasource", datasource.getId());
//            formAttributes.put("sql", sql);
//            formAttributes.put("lazyConversionActive", lazyConversionActive);
//            formAttributes.put("executeEachInputRow", executeEachInputRow);
//            formAttributes.put("variableReplacementActive", variableReplacementActive);
//            formAttributes.put("rowLimit", Integer.valueOf(rowLimit));
//            node.setAttribute("title", "表输入");
//            node.setAttribute("id", name);
//            node.setAttribute("form", objectMapper.writeValueAsString(formAttributes));
//            graph.insertVertex(graph.getDefaultParent(), null, node, x, y, 45, 45, "TableInputMeta");
//        } else {
//            next.parse(branchId, stepMeta, graph);
//        }
//    }
//
//    @Override
//    public void parse(TransHopMeta transHopMeta, mxGraph graph) throws JsonProcessingException {
//        next.parse(transHopMeta, graph);
//    }

    @Override
    public void callback(TransMeta transMeta, Map<String, String> idNameMapping) {

    }
}
