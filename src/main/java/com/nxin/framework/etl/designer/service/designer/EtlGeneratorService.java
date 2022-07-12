package com.nxin.framework.etl.designer.service.designer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableMap;
import com.mxgraph.io.mxCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;
import com.nxin.framework.etl.designer.converter.designer.ConvertFactory;
import com.nxin.framework.etl.designer.converter.designer.job.JobConvertChain;
import com.nxin.framework.etl.designer.converter.designer.job.JobConvertFactory;
import com.nxin.framework.etl.designer.converter.designer.transform.ResponseMeta;
import com.nxin.framework.etl.designer.converter.designer.transform.TransformConvertChain;
import com.nxin.framework.etl.designer.converter.designer.transform.TransformConvertFactory;
import com.nxin.framework.etl.designer.entity.designer.Shell;
import com.nxin.framework.etl.designer.enums.Constant;
import com.nxin.framework.etl.designer.utils.ZipUtils;
import lombok.extern.slf4j.Slf4j;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.encryption.Encr;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.*;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.trans.TransMeta;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class EtlGeneratorService {
    @Value("${attachment.dir}")
    private String attachmentDir;
    @Value("${setting.trans.records-size}")
    private int recordsSize;
    @Value("${setting.trans.row-size}")
    private int rowSize;
    @Value("${setting.trans.feedback}")
    private boolean feedback;
    @Value("${setting.trans.thread-manage-priority}")
    private boolean threadManagePriority;
    @Value("${etl.log.datasource.name}")
    private String etlLogDatasourceName;
    @Value("${etl.log.datasource.type}")
    private String etlLogDatasourceType;
    @Value("${etl.log.datasource.access}")
    private String etlLogDatasourceAccess;
    @Value("${etl.log.datasource.host}")
    private String etlLogDatasourceHost;
    @Value("${etl.log.datasource.port}")
    private String etlLogDatasourcePort;
    @Value("${etl.log.datasource.schema}")
    private String etlLogDatasourceSchema;
    @Value("${etl.log.datasource.username}")
    private String etlLogDatasourceUsername;
    @Value("${etl.log.datasource.password}")
    private String etlLogDatasourcePassword;

    public Map<String, Object> getTransMeta(Shell shell, Long tenantId, boolean prod) {
        String content = ZipUtils.uncompress(shell.getContent()), name = shell.getName();
        try {
            StringBuilder builder = new StringBuilder(attachmentDir);
            String env = prod ? "prod/" : "design/";
            builder.append(tenantId).append(File.separator).append(shell.getProject().getId()).append(File.separator).append(shell.getId()).append(File.separator).append(env);
            File folder = new File(builder.toString());
            if (!folder.exists()) {
                folder.mkdirs();
            }
            ConvertFactory.getVariable().put("attachmentDir", builder.toString());
            Document document = XMLHandler.loadXMLString(content);
            mxCodec codec = new mxCodec();
            mxGraph graph = new mxGraph();
            codec.decode(document.getDocumentElement(), graph.getModel());
            Object[] elements = graph.getChildCells(graph.getDefaultParent());
            TransformConvertChain transformConvertChain = TransformConvertFactory.getInstance();
            TransMeta transMeta = new TransMeta();
            transMeta.setFeedbackSize(rowSize);
            transMeta.setFeedbackShown(feedback);
            transMeta.setSizeRowset(recordsSize);
            transMeta.setTransformationType(TransMeta.TransformationType.Normal);
            transMeta.setUsingThreadPriorityManagment(threadManagePriority);
            DatabaseMeta databaseMeta = new DatabaseMeta(etlLogDatasourceName, etlLogDatasourceType, etlLogDatasourceAccess, etlLogDatasourceHost, etlLogDatasourceSchema, etlLogDatasourcePort, etlLogDatasourceUsername, Constant.PASSWORD_ENCRYPTED_PREFIX + Encr.encryptPassword(etlLogDatasourcePassword));
            transMeta.addDatabase(databaseMeta);
            transMeta.setClearingLog(true);
            TransLogTable transLogTable = transMeta.getTransLogTable();
            StepLogTable stepLogTable = transMeta.getStepLogTable();
            ChannelLogTable channelLogTable = transMeta.getChannelLogTable();
//            MetricsLogTable metricsLogTable = transMeta.getMetricsLogTable();
//            PerformanceLogTable performanceLogTable = transMeta.getPerformanceLogTable();
            transLogTable.setConnectionName(etlLogDatasourceName);
            transLogTable.setTableName("log_etl_transform");
            stepLogTable.setConnectionName(etlLogDatasourceName);
            stepLogTable.setTableName("log_etl_transform_step");
            channelLogTable.setConnectionName(etlLogDatasourceName);
            channelLogTable.setTableName("log_etl_transform_channel");
//            metricsLogTable.setConnectionName(etlLogDatasourceName);
//            metricsLogTable.setTableName("log_etl_transform_metrics");
//            performanceLogTable.setConnectionName(etlLogDatasourceName);
//            performanceLogTable.setTableName("log_etl_transform_performance");
            transMeta.setLogLevel(LogLevel.DETAILED);
            List<String> referenceIds = new ArrayList<>(0);
            Map<String, String> idNameMapping = new HashMap<>(0);
            for (Object element : elements) {
                mxCell cell = (mxCell) element;
                ResponseMeta responseMeta = transformConvertChain.parse(cell, transMeta);
                if (responseMeta.getStepMeta() != null) {
                    idNameMapping.put(responseMeta.getId(), responseMeta.getStepMeta().getName());
                }
                if (cell.isVertex()) {
                    transMeta.addStep(responseMeta.getStepMeta());
                    if (responseMeta.getDatabaseMeta() != null && Arrays.stream(transMeta.getDatabaseNames()).noneMatch(databaseName -> databaseName.equals(responseMeta.getDatabaseMeta().getName()))) {
                        transMeta.addDatabase(responseMeta.getDatabaseMeta());
                    }
                } else {
                    transMeta.addTransHop(responseMeta.getTransHopMeta());
                }
            }
            for (TransformConvertChain chain : TransformConvertFactory.getTransformConvertChains()) {
                chain.callback(transMeta, idNameMapping);
            }
            if (ConvertFactory.getVariable().containsKey("references")) {
                referenceIds = (List<String>) ConvertFactory.getVariable().get("references");
            }
            TransformConvertFactory.destroyTransformConvertChains();
            TransformConvertFactory.destroyVariable();
            transMeta.setName(name);
            return ImmutableMap.of("transMeta", transMeta, "referenceIds", String.join(",", referenceIds));
        } catch (JsonProcessingException | KettleException e) {
            log.error("getTransMeta error: {}, {}", e, e.getStackTrace()[0].toString());
        }
        return null;
    }

    public Map<String, Object> getJobMeta(Shell shell) {
        String content = ZipUtils.uncompress(shell.getContent()), name = shell.getName();
        try {
            Document document = XMLHandler.loadXMLString(content);
            mxCodec codec = new mxCodec();
            mxGraph graph = new mxGraph();
            codec.decode(document.getDocumentElement(), graph.getModel());
            Object[] elements = graph.getChildCells(graph.getDefaultParent());
            JobConvertChain jobConvertChain = JobConvertFactory.getInstance();
            JobMeta jobMeta = new JobMeta();

            DatabaseMeta databaseMeta = new DatabaseMeta(etlLogDatasourceName, etlLogDatasourceType, etlLogDatasourceAccess, etlLogDatasourceHost, etlLogDatasourceSchema, etlLogDatasourcePort, etlLogDatasourceUsername, Constant.PASSWORD_ENCRYPTED_PREFIX + Encr.encryptPassword(etlLogDatasourcePassword));
            jobMeta.addDatabase(databaseMeta);
            jobMeta.setClearingLog(true);
            JobLogTable jobLogTable = jobMeta.getJobLogTable();
            JobEntryLogTable jobEntryLogTable = jobMeta.getJobEntryLogTable();
            ChannelLogTable channelLogTable = jobMeta.getChannelLogTable();
            jobLogTable.setConnectionName(etlLogDatasourceName);
            jobLogTable.setTableName("log_etl_job");
            jobEntryLogTable.setConnectionName(etlLogDatasourceName);
            jobEntryLogTable.setTableName("log_etl_job_entry");
            channelLogTable.setConnectionName(etlLogDatasourceName);
            channelLogTable.setTableName("log_etl_transform_channel");
            jobMeta.setLogLevel(LogLevel.DETAILED);

            List<String> referenceIds = new ArrayList<>(0);
            for (Object element : elements) {
                mxCell cell = (mxCell) element;
                ResponseMeta responseMeta = jobConvertChain.parse(cell, jobMeta);
                if (cell.isVertex()) {
                    jobMeta.addJobEntry(responseMeta.getJobEntryCopy());
                    if (responseMeta.getDatabaseMeta() != null && Arrays.stream(jobMeta.getDatabaseNames()).noneMatch(databaseName -> databaseName.equals(responseMeta.getDatabaseMeta().getName()))) {
                        jobMeta.addDatabase(responseMeta.getDatabaseMeta());
                    }
                } else {
                    jobMeta.addJobHop(responseMeta.getJobHopMeta());
                }
            }
            if (ConvertFactory.getVariable().containsKey("references")) {
                referenceIds = (List<String>) ConvertFactory.getVariable().get("references");
            }
            JobConvertFactory.destroyVariable();
            jobMeta.setName(name);
            return ImmutableMap.of("jobMeta", jobMeta, "referenceIds", String.join(",", referenceIds));
        } catch (KettleException | IOException e) {
            log.error("getTransMeta error: {}, {}", e.toString(), e.getStackTrace()[0].toString());
        }
        return null;
    }
}
