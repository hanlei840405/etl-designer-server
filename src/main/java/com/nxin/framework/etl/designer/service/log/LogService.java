package com.nxin.framework.etl.designer.service.log;

import com.nxin.framework.etl.designer.vo.log.*;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class LogService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String SQL_CHANNEL = "SELECT `ID_BATCH`,\n" +
            "    `CHANNEL_ID`,\n" +
            "    `LOG_DATE`,\n" +
            "    `LOGGING_OBJECT_TYPE`,\n" +
            "    `OBJECT_NAME`,\n" +
            "    `OBJECT_COPY`,\n" +
            "    `REPOSITORY_DIRECTORY`,\n" +
            "    `FILENAME`,\n" +
            "    `OBJECT_ID`,\n" +
            "    `OBJECT_REVISION`,\n" +
            "    `PARENT_CHANNEL_ID`,\n" +
            "    `ROOT_CHANNEL_ID`\n" +
            "FROM `log_etl_transform_channel` WHERE CHANNEL_ID=?;";

    private static final String SQL_CHANNELS = "SELECT `ID_BATCH`,\n" +
            "    `CHANNEL_ID`,\n" +
            "    `LOG_DATE`,\n" +
            "    `LOGGING_OBJECT_TYPE`,\n" +
            "    `OBJECT_NAME`,\n" +
            "    `OBJECT_COPY`,\n" +
            "    `REPOSITORY_DIRECTORY`,\n" +
            "    `FILENAME`,\n" +
            "    `OBJECT_ID`,\n" +
            "    `OBJECT_REVISION`,\n" +
            "    `PARENT_CHANNEL_ID`,\n" +
            "    `ROOT_CHANNEL_ID`\n" +
            "FROM `log_etl_transform_channel` WHERE ROOT_CHANNEL_ID=?;";

    private static final String SQL_TRANS = "SELECT `ID_BATCH`,\n" +
            "    `CHANNEL_ID`,\n" +
            "    `TRANSNAME`,\n" +
            "    `STATUS`,\n" +
            "    `LINES_READ`,\n" +
            "    `LINES_WRITTEN`,\n" +
            "    `LINES_UPDATED`,\n" +
            "    `LINES_INPUT`,\n" +
            "    `LINES_OUTPUT`,\n" +
            "    `LINES_REJECTED`,\n" +
            "    `ERRORS`,\n" +
            "    `STARTDATE`,\n" +
            "    `ENDDATE`,\n" +
            "    `LOGDATE`,\n" +
            "    `DEPDATE`,\n" +
            "    `REPLAYDATE`,\n" +
            "    `LOG_FIELD`\n" +
            "FROM `log_etl_transform` WHERE CHANNEL_ID in (:channelIds);\n";

    private static final String SQL_STEP = "SELECT `ID_BATCH`,\n" +
            "    `CHANNEL_ID`,\n" +
            "    `LOG_DATE`,\n" +
            "    `TRANSNAME`,\n" +
            "    `STEPNAME`,\n" +
            "    `STEP_COPY`,\n" +
            "    `LINES_READ`,\n" +
            "    `LINES_WRITTEN`,\n" +
            "    `LINES_UPDATED`,\n" +
            "    `LINES_INPUT`,\n" +
            "    `LINES_OUTPUT`,\n" +
            "    `LINES_REJECTED`,\n" +
            "    `ERRORS`\n" +
            "FROM `log_etl_transform_step` WHERE CHANNEL_ID in (:channelIds);\n";

    private static final String SQL_JOB = "SELECT `ID_JOB`,\n" +
            "    `CHANNEL_ID`,\n" +
            "    `JOBNAME`,\n" +
            "    `STATUS`,\n" +
            "    `LINES_READ`,\n" +
            "    `LINES_WRITTEN`,\n" +
            "    `LINES_UPDATED`,\n" +
            "    `LINES_INPUT`,\n" +
            "    `LINES_OUTPUT`,\n" +
            "    `LINES_REJECTED`,\n" +
            "    `ERRORS`,\n" +
            "    `STARTDATE`,\n" +
            "    `ENDDATE`,\n" +
            "    `LOGDATE`,\n" +
            "    `DEPDATE`,\n" +
            "    `REPLAYDATE`,\n" +
            "    `LOG_FIELD`\n" +
            "FROM `log_etl_job` WHERE CHANNEL_ID in (:channelIds);\n";

    private static final String SQL_HISTORY_JOB_ENTRY = "SELECT `ID_BATCH`,\n" +
            "    `CHANNEL_ID`,\n" +
            "    `LOG_DATE`,\n" +
            "    `TRANSNAME`,\n" +
            "    `STEPNAME`,\n" +
            "    `LINES_READ`,\n" +
            "    `LINES_WRITTEN`,\n" +
            "    `LINES_UPDATED`,\n" +
            "    `LINES_INPUT`,\n" +
            "    `LINES_OUTPUT`,\n" +
            "    `LINES_REJECTED`,\n" +
            "    `ERRORS`,\n" +
            "    `RESULT`,\n" +
            "    `NR_RESULT_ROWS`,\n" +
            "    `NR_RESULT_FILES`\n" +
            "FROM `history_log_etl_job_entry` WHERE ID_BATCH=?;\n";

    private static final String SQL_HISTORY_JOB = "SELECT `ID_JOB`,\n" +
            "    `CHANNEL_ID`,\n" +
            "    `JOBNAME`,\n" +
            "    `STATUS`,\n" +
            "    `LINES_READ`,\n" +
            "    `LINES_WRITTEN`,\n" +
            "    `LINES_UPDATED`,\n" +
            "    `LINES_INPUT`,\n" +
            "    `LINES_OUTPUT`,\n" +
            "    `LINES_REJECTED`,\n" +
            "    `ERRORS`,\n" +
            "    `STARTDATE`,\n" +
            "    `ENDDATE`,\n" +
            "    `LOGDATE`,\n" +
            "    `DEPDATE`,\n" +
            "    `REPLAYDATE`,\n" +
            "    `LOG_FIELD`\n" +
            "FROM `history_log_etl_job` WHERE CHANNEL_ID in (:channelIds);\n";

    private static final String SQL_HISTORY_STEP = "SELECT `ID_BATCH`,\n" +
            "    `CHANNEL_ID`,\n" +
            "    `LOG_DATE`,\n" +
            "    `TRANSNAME`,\n" +
            "    `STEPNAME`,\n" +
            "    `STEP_COPY`,\n" +
            "    `LINES_READ`,\n" +
            "    `LINES_WRITTEN`,\n" +
            "    `LINES_UPDATED`,\n" +
            "    `LINES_INPUT`,\n" +
            "    `LINES_OUTPUT`,\n" +
            "    `LINES_REJECTED`,\n" +
            "    `ERRORS`\n" +
            "FROM `history_log_etl_transform_step` WHERE CHANNEL_ID=?;\n";

    private static final String SQL_HISTORY_CHANNELS = "SELECT `ID_BATCH`,\n" +
            "    `CHANNEL_ID`,\n" +
            "    `LOG_DATE`,\n" +
            "    `LOGGING_OBJECT_TYPE`,\n" +
            "    `OBJECT_NAME`,\n" +
            "    `OBJECT_COPY`,\n" +
            "    `REPOSITORY_DIRECTORY`,\n" +
            "    `FILENAME`,\n" +
            "    `OBJECT_ID`,\n" +
            "    `OBJECT_REVISION`,\n" +
            "    `PARENT_CHANNEL_ID`,\n" +
            "    `ROOT_CHANNEL_ID`\n" +
            "FROM `history_log_etl_transform_channel` WHERE ROOT_CHANNEL_ID=?;";

    public List<ChannelLogVo> channelLog(String channelId) {
        return jdbcTemplate.query(SQL_CHANNEL, (rs, i) -> {
            Integer idBatch = rs.getInt("ID_BATCH");
            String channelId1 = rs.getString("CHANNEL_ID");
            String loggingObjectType = rs.getString("LOGGING_OBJECT_TYPE");
            String objectName = rs.getString("OBJECT_NAME");
            String objectCopy = rs.getString("OBJECT_COPY");
            String repositoryDirectory = rs.getString("REPOSITORY_DIRECTORY");
            String filename = rs.getString("FILENAME");
            String objectId = rs.getString("OBJECT_ID");
            String objectRevision = rs.getString("OBJECT_REVISION");
            String parentChannelId = rs.getString("PARENT_CHANNEL_ID");
            String rootChannelId = rs.getString("ROOT_CHANNEL_ID");
            ChannelLogVo channelLog = ChannelLogVo.builder().build();
            channelLog.setIdBatch(idBatch);
            channelLog.setChannelId(channelId1);
            channelLog.setLoggingObjectType(loggingObjectType);
            channelLog.setObjectName(objectName);
            channelLog.setObjectCopy(objectCopy);
            channelLog.setRepositoryDirectory(repositoryDirectory);
            channelLog.setFilename(filename);
            channelLog.setObjectId(objectId);
            channelLog.setObjectRevision(objectRevision);
            channelLog.setParentChannelId(parentChannelId);
            channelLog.setRootChannelId(rootChannelId);
            if (rs.getTimestamp("LOG_DATE") != null) {
                channelLog.setLogDate(new Date(rs.getTimestamp("LOG_DATE").getTime()));
            }
            return channelLog;
        }, channelId);
    }

    public List<ChannelLogVo> channelLogs(String channelId) {
        return jdbcTemplate.query(SQL_CHANNELS, (rs, i) -> {
            Integer idBatch = rs.getInt("ID_BATCH");
            String channelId1 = rs.getString("CHANNEL_ID");
            String loggingObjectType = rs.getString("LOGGING_OBJECT_TYPE");
            String objectName = rs.getString("OBJECT_NAME");
            String objectCopy = rs.getString("OBJECT_COPY");
            String repositoryDirectory = rs.getString("REPOSITORY_DIRECTORY");
            String filename = rs.getString("FILENAME");
            String objectId = rs.getString("OBJECT_ID");
            String objectRevision = rs.getString("OBJECT_REVISION");
            String parentChannelId = rs.getString("PARENT_CHANNEL_ID");
            String rootChannelId = rs.getString("ROOT_CHANNEL_ID");
            ChannelLogVo channelLog = ChannelLogVo.builder().build();
            channelLog.setIdBatch(idBatch);
            channelLog.setChannelId(channelId1);
            channelLog.setLoggingObjectType(loggingObjectType);
            channelLog.setObjectName(objectName);
            channelLog.setObjectCopy(objectCopy);
            channelLog.setRepositoryDirectory(repositoryDirectory);
            channelLog.setFilename(filename);
            channelLog.setObjectId(objectId);
            channelLog.setObjectRevision(objectRevision);
            channelLog.setParentChannelId(parentChannelId);
            channelLog.setRootChannelId(rootChannelId);
            if (rs.getTimestamp("LOG_DATE") != null) {
                channelLog.setLogDate(new Date(rs.getTimestamp("LOG_DATE").getTime()));
            }
            return channelLog;
        }, channelId);
    }

    public List<TransformLogVo> transformLog(List<String> channelIds) {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        return namedParameterJdbcTemplate.query(SQL_TRANS, Collections.singletonMap("channelIds", channelIds), (rs, i) -> {
            int idBatch = rs.getInt("ID_BATCH");
            String channelId1 = rs.getString("CHANNEL_ID");
            String transName = rs.getString("TRANSNAME");
            String status = rs.getString("STATUS");
            Long linesRead = rs.getLong("LINES_READ");
            Long linesWritten = rs.getLong("LINES_WRITTEN");
            Long linesUpdated = rs.getLong("LINES_UPDATED");
            Long linesInput = rs.getLong("LINES_INPUT");
            Long linesOutput = rs.getLong("LINES_OUTPUT");
            Long linesRejected = rs.getLong("LINES_REJECTED");
            Long errors = rs.getLong("ERRORS");
            String logField = rs.getString("LOG_FIELD");
            TransformLogVo transformLog = TransformLogVo.builder().build();
            transformLog.setIdBatch(idBatch);
            transformLog.setChannelId(channelId1);
            transformLog.setTransName(transName);
            transformLog.setStatus(status);
            transformLog.setLinesRead(linesRead);
            transformLog.setLinesWritten(linesWritten);
            transformLog.setLinesUpdated(linesUpdated);
            transformLog.setLinesInput(linesInput);
            transformLog.setLinesOutput(linesOutput);
            transformLog.setLinesRejected(linesRejected);
            transformLog.setErrors(errors);
            transformLog.setLogField(logField);
            if (rs.getTimestamp("STARTDATE") != null) {
                transformLog.setStartDate(new Date(rs.getTimestamp("STARTDATE").getTime()));
            }
            if (rs.getTimestamp("ENDDATE") != null) {
                transformLog.setEndDate(new Date(rs.getTimestamp("ENDDATE").getTime()));
            }
            if (rs.getTimestamp("LOGDATE") != null) {
                transformLog.setLogDate(new Date(rs.getTimestamp("LOGDATE").getTime()));
            }
            if (rs.getTimestamp("DEPDATE") != null) {
                transformLog.setDepDate(new Date(rs.getTimestamp("DEPDATE").getTime()));
            }
            return transformLog;
        });
    }

    public List<StepLogVo> stepLog(List<String> channelIds) {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        return namedParameterJdbcTemplate.query(SQL_STEP, Collections.singletonMap("channelIds", channelIds), (rs, i) -> {
            String name = rs.getString("STEPNAME");
            String logDate = rs.getString("LOG_DATE");
            Integer stepCopy = rs.getInt("STEP_COPY");
            Long read = rs.getLong("LINES_READ");
            Long written = rs.getLong("LINES_WRITTEN");
            Long updated = rs.getLong("LINES_UPDATED");
            Long input = rs.getLong("LINES_INPUT");
            Long output = rs.getLong("LINES_OUTPUT");
            Long rejected = rs.getLong("LINES_REJECTED");
            Long errors = rs.getLong("ERRORS");
            StepLogVo stepLog = StepLogVo.builder().build();
            stepLog.setName(name);
            if (logDate != null) {
                stepLog.setLogDate(logDate);
            }
            stepLog.setStepCopy(stepCopy);
            stepLog.setRead(read);
            stepLog.setWritten(written);
            stepLog.setUpdated(updated);
            stepLog.setInput(input);
            stepLog.setOutput(output);
            stepLog.setRejected(rejected);
            stepLog.setErrors(errors);
            return stepLog;
        });
    }

    public List<JobLogVo> jobLog(List<String> channelIds) {
        if (channelIds.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        return namedParameterJdbcTemplate.query(SQL_JOB, Collections.singletonMap("channelIds", channelIds), (rs, i) -> {
            int idJob = rs.getInt("ID_JOB");
            String channelId1 = rs.getString("CHANNEL_ID");
            String jobName = rs.getString("JOBNAME");
            String status = rs.getString("STATUS");
            Long linesRead = rs.getLong("LINES_READ");
            Long linesWritten = rs.getLong("LINES_WRITTEN");
            Long linesUpdated = rs.getLong("LINES_UPDATED");
            Long linesInput = rs.getLong("LINES_INPUT");
            Long linesOutput = rs.getLong("LINES_OUTPUT");
            Long linesRejected = rs.getLong("LINES_REJECTED");
            Long errors = rs.getLong("ERRORS");
            String logField = rs.getString("LOG_FIELD");
            JobLogVo jobLog = JobLogVo.builder().build();
            jobLog.setIdJob(idJob);
            jobLog.setChannelId(channelId1);
            jobLog.setJobName(jobName);
            jobLog.setStatus(status);
            jobLog.setLinesRead(linesRead);
            jobLog.setLinesWritten(linesWritten);
            jobLog.setLinesUpdated(linesUpdated);
            jobLog.setLinesInput(linesInput);
            jobLog.setLinesOutput(linesOutput);
            jobLog.setLinesRejected(linesRejected);
            jobLog.setErrors(errors);
            jobLog.setLogField(logField);
            if (rs.getTimestamp("STARTDATE") != null) {
                jobLog.setStartDate(new Date(rs.getTimestamp("STARTDATE").getTime()));
            }
            if (rs.getTimestamp("ENDDATE") != null) {
                jobLog.setEndDate(new Date(rs.getTimestamp("ENDDATE").getTime()));
            }
            if (rs.getTimestamp("LOGDATE") != null) {
                jobLog.setLogDate(new Date(rs.getTimestamp("LOGDATE").getTime()));
            }
            if (rs.getTimestamp("DEPDATE") != null) {
                jobLog.setDepDate(new Date(rs.getTimestamp("DEPDATE").getTime()));
            }
            return jobLog;
        });
    }

    public List<JobEntryLogVo> historyJobEntryLog(int id) {
        return jdbcTemplate.query(SQL_HISTORY_JOB_ENTRY, (rs, i) -> {
            int idBatch = rs.getInt("ID_BATCH");
            String channelId1 = rs.getString("CHANNEL_ID");
            String transName = rs.getString("TRANSNAME");
            String stepName = rs.getString("STEPNAME");
            String result = rs.getString("RESULT");
            Long linesRead = rs.getLong("LINES_READ");
            Long linesWritten = rs.getLong("LINES_WRITTEN");
            Long linesUpdated = rs.getLong("LINES_UPDATED");
            Long linesInput = rs.getLong("LINES_INPUT");
            Long linesOutput = rs.getLong("LINES_OUTPUT");
            Long linesRejected = rs.getLong("LINES_REJECTED");
            Long errors = rs.getLong("ERRORS");
            Long nrResultRows = rs.getLong("NR_RESULT_ROWS");
            Long nrResultFiles = rs.getLong("NR_RESULT_FILES");
            JobEntryLogVo jobEntryLog = JobEntryLogVo.builder().build();
            jobEntryLog.setIdBatch(idBatch);
            jobEntryLog.setChannelId(channelId1);
            jobEntryLog.setTransName(transName);
            jobEntryLog.setStepName(stepName);
            jobEntryLog.setLinesRead(linesRead);
            jobEntryLog.setLinesWritten(linesWritten);
            jobEntryLog.setLinesUpdated(linesUpdated);
            jobEntryLog.setLinesInput(linesInput);
            jobEntryLog.setLinesOutput(linesOutput);
            jobEntryLog.setLinesRejected(linesRejected);
            jobEntryLog.setErrors(errors);
            jobEntryLog.setResult(result);
            jobEntryLog.setNrResultRows(nrResultRows);
            jobEntryLog.setNrResultFiles(nrResultFiles);
            if (rs.getTimestamp("LOG_DATE") != null) {
                jobEntryLog.setLogDate(new Date(rs.getTimestamp("LOG_DATE").getTime()));
            }
            return jobEntryLog;
        }, id);
    }

    public List<JobLogVo> historyJobLog(List<String> channelIds) {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        return namedParameterJdbcTemplate.query(SQL_HISTORY_JOB, Collections.singletonMap("channelIds", channelIds), (rs, i) -> {
            int idJob = rs.getInt("ID_JOB");
            String channelId1 = rs.getString("CHANNEL_ID");
            String jobName = rs.getString("JOBNAME");
            String status = rs.getString("STATUS");
            Long linesRead = rs.getLong("LINES_READ");
            Long linesWritten = rs.getLong("LINES_WRITTEN");
            Long linesUpdated = rs.getLong("LINES_UPDATED");
            Long linesInput = rs.getLong("LINES_INPUT");
            Long linesOutput = rs.getLong("LINES_OUTPUT");
            Long linesRejected = rs.getLong("LINES_REJECTED");
            Long errors = rs.getLong("ERRORS");
            String logField = rs.getString("LOG_FIELD");
            JobLogVo jobLog = JobLogVo.builder().build();
            jobLog.setIdJob(idJob);
            jobLog.setChannelId(channelId1);
            jobLog.setJobName(jobName);
            jobLog.setStatus(status);
            jobLog.setLinesRead(linesRead);
            jobLog.setLinesWritten(linesWritten);
            jobLog.setLinesUpdated(linesUpdated);
            jobLog.setLinesInput(linesInput);
            jobLog.setLinesOutput(linesOutput);
            jobLog.setLinesRejected(linesRejected);
            jobLog.setErrors(errors);
            jobLog.setLogField(logField);
            if (rs.getTimestamp("STARTDATE") != null) {
                jobLog.setStartDate(new Date(rs.getTimestamp("STARTDATE").getTime()));
            }
            if (rs.getTimestamp("ENDDATE") != null) {
                jobLog.setEndDate(new Date(rs.getTimestamp("ENDDATE").getTime()));
            }
            if (rs.getTimestamp("LOGDATE") != null) {
                jobLog.setLogDate(new Date(rs.getTimestamp("LOGDATE").getTime()));
            }
            if (rs.getTimestamp("DEPDATE") != null) {
                jobLog.setDepDate(new Date(rs.getTimestamp("DEPDATE").getTime()));
            }
            return jobLog;
        });
    }

    public List<StepLogVo> historyStepLog(String channelId) {
        return jdbcTemplate.query(SQL_HISTORY_STEP, (rs, rowNum) -> {
            String name = rs.getString("STEPNAME");
            String logDate = rs.getString("LOG_DATE");
            Integer stepCopy = rs.getInt("STEP_COPY");
            Long read = rs.getLong("LINES_READ");
            Long written = rs.getLong("LINES_WRITTEN");
            Long updated = rs.getLong("LINES_UPDATED");
            Long input = rs.getLong("LINES_INPUT");
            Long output = rs.getLong("LINES_OUTPUT");
            Long rejected = rs.getLong("LINES_REJECTED");
            Long errors = rs.getLong("ERRORS");
            StepLogVo stepLog = StepLogVo.builder().build();
            stepLog.setName(name);
            if (logDate != null) {
                stepLog.setLogDate(logDate);
            }
            stepLog.setStepCopy(stepCopy);
            stepLog.setRead(read);
            stepLog.setWritten(written);
            stepLog.setUpdated(updated);
            stepLog.setInput(input);
            stepLog.setOutput(output);
            stepLog.setRejected(rejected);
            stepLog.setErrors(errors);
            return stepLog;
        }, channelId);
    }

    public List<ChannelLogVo> historyChannelLogs(String channelId) {
        return jdbcTemplate.query(SQL_HISTORY_CHANNELS, (rs, i) -> {
            Integer idBatch = rs.getInt("ID_BATCH");
            String channelId1 = rs.getString("CHANNEL_ID");
            String loggingObjectType = rs.getString("LOGGING_OBJECT_TYPE");
            String objectName = rs.getString("OBJECT_NAME");
            String objectCopy = rs.getString("OBJECT_COPY");
            String repositoryDirectory = rs.getString("REPOSITORY_DIRECTORY");
            String filename = rs.getString("FILENAME");
            String objectId = rs.getString("OBJECT_ID");
            String objectRevision = rs.getString("OBJECT_REVISION");
            String parentChannelId = rs.getString("PARENT_CHANNEL_ID");
            String rootChannelId = rs.getString("ROOT_CHANNEL_ID");
            ChannelLogVo channelLog = ChannelLogVo.builder().build();
            channelLog.setIdBatch(idBatch);
            channelLog.setChannelId(channelId1);
            channelLog.setLoggingObjectType(loggingObjectType);
            channelLog.setObjectName(objectName);
            channelLog.setObjectCopy(objectCopy);
            channelLog.setRepositoryDirectory(repositoryDirectory);
            channelLog.setFilename(filename);
            channelLog.setObjectId(objectId);
            channelLog.setObjectRevision(objectRevision);
            channelLog.setParentChannelId(parentChannelId);
            channelLog.setRootChannelId(rootChannelId);
            if (rs.getTimestamp("LOG_DATE") != null) {
                channelLog.setLogDate(new Date(rs.getTimestamp("LOG_DATE").getTime()));
            }
            return channelLog;
        }, channelId);
    }

    public void clearJobLogField() {
        jdbcTemplate.update("delete from log_etl_transform_channel where log_date<curdate()");
        jdbcTemplate.update("delete from log_etl_job where logdate<curdate()");
        jdbcTemplate.update("delete from log_etl_job_entry where log_date<curdate()");
        jdbcTemplate.update("delete from log_etl_transform where logdate<curdate()");
        jdbcTemplate.update("delete from log_etl_transform_step where log_date<curdate()");
    }
}