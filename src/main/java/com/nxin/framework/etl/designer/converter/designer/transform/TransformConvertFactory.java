package com.nxin.framework.etl.designer.converter.designer.transform;

import com.nxin.framework.etl.designer.converter.designer.ConvertFactory;
import com.nxin.framework.etl.designer.converter.designer.transform.convert.*;
import com.nxin.framework.etl.designer.converter.designer.transform.input.*;
import com.nxin.framework.etl.designer.converter.designer.transform.lookup.DatabaseJoinChain;
import com.nxin.framework.etl.designer.converter.designer.transform.lookup.DatabaseLookupChain;
import com.nxin.framework.etl.designer.converter.designer.transform.lookup.RestChain;
import com.nxin.framework.etl.designer.converter.designer.transform.output.*;
import com.nxin.framework.etl.designer.converter.designer.transform.process.*;
import com.nxin.framework.etl.designer.converter.designer.transform.shell.*;
import com.nxin.framework.etl.designer.converter.designer.transform.streaming.*;
import com.nxin.framework.etl.designer.service.designer.DatasourceService;
import com.nxin.framework.etl.designer.service.designer.ShellService;
import org.pentaho.di.core.exception.KettleException;

import java.util.ArrayList;
import java.util.List;

public class TransformConvertFactory extends ConvertFactory {
    private static ThreadLocal<List<TransformConvertChain>> transformConvertChains = ThreadLocal.withInitial(() -> new ArrayList<>(0));

    private static TransformConvertChain beginChain;

    public static List<TransformConvertChain> getTransformConvertChains() {
        return transformConvertChains.get();
    }

    public static void destroyTransformConvertChains() {
        transformConvertChains.remove();
    }

    public static void init(DatasourceService datasourceService, ShellService shellService) {
        TransformConvertChain beginChain = new BeginChain();
        TransformConvertChain tableInputChain = new TableInputChain();
        TransformConvertChain tableOutputChain = new TableOutputChain();
        TransformConvertChain tableInsertUpdateChain = new TableInsertUpdateChain();
        TransformConvertChain tableUpdateChain = new TableUpdateChain();
        TransformConvertChain tableDeleteChain = new TableDeleteChain();
        TransformConvertChain databaseLookupChain = new DatabaseLookupChain();
        TransformConvertChain databaseJoinChain = new DatabaseJoinChain();
        TransformConvertChain execSqlChain = new ExecSqlChain();
        TransformConvertChain javaScriptChain = new JavaScriptChain();
        TransformConvertChain dummyChain = new DummyChain();
        TransformConvertChain detectEmptyStreamChain = new DetectEmptyStreamChain();
        TransformConvertChain detectLastRowChain = new DetectLastRowChain();
        TransformConvertChain mailChain = new MailChain();
        TransformConvertChain writeToLogChain = new WriteToLogChain();
        TransformConvertChain dataGridChain = new DataGridChain();
        TransformConvertChain transHopChain = new TransHopChain();
        TransformConvertChain getVariableChain = new GetVariableChain();
        TransformConvertChain setVariableChain = new SetVariableChain();
        TransformConvertChain switchCaseChain = new SwitchCaseChain();
        TransformConvertChain constantChain = new ConstantChain();
        TransformConvertChain denormaliserChain = new DenormaliserChain();
        TransformConvertChain fieldSplitterChain = new FieldSplitterChain();
        TransformConvertChain flattenerChain = new FlattenerChain();
        TransformConvertChain normaliserChain = new NormaliserChain();
        TransformConvertChain replaceStringChain = new ReplaceStringChain();
        TransformConvertChain selectValuesChain = new SelectValuesChain();
        TransformConvertChain splitFieldToRowsChain = new SplitFieldToRowsChain();
        TransformConvertChain stringCutChain = new StringCutChain();
        TransformConvertChain uniqueRowsChain = new UniqueRowsChain();
        TransformConvertChain uniqueRowsByHashSetChain = new UniqueRowsByHashSetChain();
        TransformConvertChain valueMapperChain = new ValueMapperChain();
        TransformConvertChain jsonInputChain = new JsonInputChain();
        TransformConvertChain jsonOutputChain = new JsonOutputChain();
        TransformConvertChain rowGeneratorChain = new RowGeneratorChain();
        TransformConvertChain userDefinedJavaClassChain = new UserDefinedJavaClassChain();
        TransformConvertChain excelWriterChain = new ExcelWriterChain();
        TransformConvertChain elasticSearchBulkChain = new ElasticSearchBulkChain();
        TransformConvertChain randomValueChain = new RandomValueChain();
        TransformConvertChain concatFieldsChain = new ConcatFieldsChain();
        TransformConvertChain recordsFromStreamChain = new RecordsFromStreamChain();
        TransformConvertChain kafkaProducerOutputChain = new KafkaProducerOutputChain();
        TransformConvertChain kafkaConsumerInputChain = new KafkaConsumerInputChain();
        TransformConvertChain jmsProducerOutputChain = new JmsProducerChain();
        TransformConvertChain jmsConsumerInputChain = new JmsConsumerChain();
        TransformConvertChain setValueFieldChain = new SetValueFieldChain();
        TransformConvertChain mongodbOutputChain = new MongodbOutputChain();
        TransformConvertChain rowsToResultChain = new RowsToResultChain();
        TransformConvertChain rowsFromResultChain = new RowsFromResultChain();
        TransformConvertChain restChain = new RestChain();
        TransformConvertChain endChain = new EndChain();
        tableInputChain.setDatasourceService(datasourceService);
        tableOutputChain.setDatasourceService(datasourceService);
        tableInsertUpdateChain.setDatasourceService(datasourceService);
        tableUpdateChain.setDatasourceService(datasourceService);
        tableDeleteChain.setDatasourceService(datasourceService);
        databaseLookupChain.setDatasourceService(datasourceService);
        databaseJoinChain.setDatasourceService(datasourceService);
        execSqlChain.setDatasourceService(datasourceService);
        kafkaConsumerInputChain.setShellService(shellService);
        jmsConsumerInputChain.setShellService(shellService);
        beginChain.setNext(tableInputChain);
        tableInputChain.setNext(tableOutputChain);
        tableOutputChain.setNext(tableInsertUpdateChain);
        tableInsertUpdateChain.setNext(tableUpdateChain);
        tableUpdateChain.setNext(tableDeleteChain);
        tableDeleteChain.setNext(databaseLookupChain);
        databaseLookupChain.setNext(databaseJoinChain);
        databaseJoinChain.setNext(execSqlChain);
        execSqlChain.setNext(javaScriptChain);
        javaScriptChain.setNext(dummyChain);
        dummyChain.setNext(detectEmptyStreamChain);
        detectEmptyStreamChain.setNext(detectLastRowChain);
        detectLastRowChain.setNext(mailChain);
        mailChain.setNext(writeToLogChain);
        writeToLogChain.setNext(dataGridChain);
        dataGridChain.setNext(getVariableChain);
        getVariableChain.setNext(setVariableChain);
        setVariableChain.setNext(switchCaseChain);
        switchCaseChain.setNext(constantChain);
        constantChain.setNext(denormaliserChain);
        denormaliserChain.setNext(fieldSplitterChain);
        fieldSplitterChain.setNext(flattenerChain);
        flattenerChain.setNext(normaliserChain);
        normaliserChain.setNext(replaceStringChain);
        replaceStringChain.setNext(selectValuesChain);
        selectValuesChain.setNext(splitFieldToRowsChain);
        splitFieldToRowsChain.setNext(stringCutChain);
        stringCutChain.setNext(uniqueRowsChain);
        uniqueRowsChain.setNext(uniqueRowsByHashSetChain);
        uniqueRowsByHashSetChain.setNext(valueMapperChain);
        valueMapperChain.setNext(jsonInputChain);
        jsonInputChain.setNext(jsonOutputChain);
        jsonOutputChain.setNext(rowGeneratorChain);
        rowGeneratorChain.setNext(userDefinedJavaClassChain);
        userDefinedJavaClassChain.setNext(excelWriterChain);
        excelWriterChain.setNext(elasticSearchBulkChain);
        elasticSearchBulkChain.setNext(concatFieldsChain);
        concatFieldsChain.setNext(recordsFromStreamChain);
        recordsFromStreamChain.setNext(kafkaProducerOutputChain);
        kafkaProducerOutputChain.setNext(kafkaConsumerInputChain);
        kafkaConsumerInputChain.setNext(jmsProducerOutputChain);
        jmsProducerOutputChain.setNext(jmsConsumerInputChain);
        jmsConsumerInputChain.setNext(randomValueChain);
        randomValueChain.setNext(setValueFieldChain);
        setValueFieldChain.setNext(mongodbOutputChain);
        mongodbOutputChain.setNext(rowsToResultChain);
        rowsToResultChain.setNext(rowsFromResultChain);
        rowsFromResultChain.setNext(restChain);
        restChain.setNext(transHopChain);
        transHopChain.setNext(endChain);
        TransformConvertFactory.beginChain = beginChain;
    }

    public static TransformConvertChain getInstance() throws KettleException {
        return beginChain;
    }
}
