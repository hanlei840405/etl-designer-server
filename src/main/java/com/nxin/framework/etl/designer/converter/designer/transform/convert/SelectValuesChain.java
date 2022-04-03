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
import org.pentaho.di.core.row.value.ValueMetaBase;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.selectvalues.SelectMetadataChange;
import org.pentaho.di.trans.steps.selectvalues.SelectValuesMeta;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class SelectValuesChain extends TransformConvertChain {

    @Override
    public ResponseMeta parse(mxCell cell, TransMeta transMeta) throws JsonProcessingException {
        if (cell.isVertex() && "SelectValuesMeta".equalsIgnoreCase(cell.getStyle())) {
            DeferredElementImpl value = (DeferredElementImpl) cell.getValue();
            SelectValuesMeta selectValuesMeta = new SelectValuesMeta();
            Map<String, Object> formAttributes = objectMapper.readValue(value.getAttribute("form"), new TypeReference<Map<String, Object>>() {
            });
            String stepName = (String) formAttributes.get("name");
            Boolean sortByName = (Boolean) formAttributes.get("sortByName");
            List<String> removeSourceList = new ArrayList<>(0);
            List<Map<String, Object>> selects = (List<Map<String, Object>>) formAttributes.get("selects");
            List<SelectValuesMeta.SelectField> selectFieldList = new ArrayList<>(0);
            for (Map<String, Object> fieldMapping : selects) {
                SelectValuesMeta.SelectField selectField = new SelectValuesMeta.SelectField();
                selectField.setName((String) fieldMapping.get("source"));
                selectField.setRename((String) fieldMapping.get("target"));
                if (!ObjectUtils.isEmpty(fieldMapping.get("lengthValue"))) {
                    selectField.setLength((int) fieldMapping.get("lengthValue"));
                } else {
                    selectField.setLength(-1);
                }
                if (!ObjectUtils.isEmpty(fieldMapping.get("accuracy"))) {
                    selectField.setPrecision((int) fieldMapping.get("accuracy"));
                } else {
                    selectField.setPrecision(-1);
                }
                selectFieldList.add(selectField);
            }
            List<Map<String, Object>> removes = (List<Map<String, Object>>) formAttributes.get("removes");
            for (Map<String, Object> fieldMapping : removes) {
                removeSourceList.add((String) fieldMapping.get("source"));
            }
            List<Map<String, Object>> metaDataList = (List<Map<String, Object>>) formAttributes.get("metaData");
            List<SelectMetadataChange> selectMetadataChangeList = new ArrayList<>(0);
            for (Map<String, Object> fieldMapping : metaDataList) {
                SelectMetadataChange selectMetadataChange = new SelectMetadataChange(selectValuesMeta);
                selectMetadataChange.setName((String) fieldMapping.get("source"));
                selectMetadataChange.setType(ValueMetaFactory.getIdForValueMeta((String) fieldMapping.get("category")));
                if (!ObjectUtils.isEmpty(fieldMapping.get("lengthValue"))) {
                    selectMetadataChange.setLength((int) fieldMapping.get("lengthValue"));
                } else {
                    selectMetadataChange.setLength(-1);
                }
                if (!ObjectUtils.isEmpty(fieldMapping.get("accuracy"))) {
                    selectMetadataChange.setPrecision((int) fieldMapping.get("accuracy"));
                } else {
                    selectMetadataChange.setPrecision(-1);
                }
                selectMetadataChange.setStorageType(ValueMetaBase.getStorageType((String) fieldMapping.get("binary")));
                selectMetadataChange.setConversionMask((String) fieldMapping.get("formatValue"));
                if ("Y".equals(fieldMapping.get("dateFormatLenient"))) {
                    selectMetadataChange.setDateFormatLenient(true);
                } else {
                    selectMetadataChange.setDateFormatLenient(false);
                }
                selectMetadataChange.setDateFormatLocale((String) fieldMapping.get("locale"));
                selectMetadataChange.setDateFormatTimeZone((String) fieldMapping.get("timezone"));
                if ("Y".equals(fieldMapping.get("lenientNumberConversion"))) {
                    selectMetadataChange.setLenientStringToNumber(true);
                } else {
                    selectMetadataChange.setLenientStringToNumber(false);
                }
                selectMetadataChange.setEncoding((String) fieldMapping.get("encoding"));
                selectMetadataChange.setDecimalSymbol((String) fieldMapping.get("decimal"));
                selectMetadataChange.setGroupingSymbol((String) fieldMapping.get("groupBy"));
                selectMetadataChange.setCurrencySymbol((String) fieldMapping.get("currency"));
                selectMetadataChangeList.add(selectMetadataChange);
            }
            selectValuesMeta.setSelectingAndSortingUnspecifiedFields(sortByName);
            selectValuesMeta.setSelectFields(selectFieldList.toArray(new SelectValuesMeta.SelectField[0]));
            selectValuesMeta.setDeleteName(removeSourceList.toArray(new String[0]));
            selectValuesMeta.setMeta(selectMetadataChangeList.toArray(new SelectMetadataChange[0]));
            int parallel = (int) formAttributes.get(Constant.ETL_PARALLEL);
            StepMeta stepMeta = new StepMeta(stepName, selectValuesMeta);
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
