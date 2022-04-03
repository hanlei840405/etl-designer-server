package com.nxin.framework.etl.designer.task;

import com.nxin.framework.etl.designer.entity.designer.RunningProcess;
import com.nxin.framework.etl.designer.enums.Constant;
import com.nxin.framework.etl.designer.service.designer.RunningProcessService;
import com.nxin.framework.etl.designer.service.log.LogService;
import com.nxin.framework.etl.designer.utils.ZipUtils;
import org.pentaho.di.www.CarteObjectEntry;
import org.pentaho.di.www.CarteSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.transaction.Transactional;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@Service
public class ClearLogTask {

    @Autowired
    private LogService logService;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private RunningProcessService runningProcessService;

    /**
     * 保留当天日志
     */
    @Scheduled(cron = "0 1 0 * * ?")
    @Transactional
    public void clear() {
        jdbcTemplate.batchUpdate("insert into history_log_etl_transform_channel select * from log_etl_transform_channel where log_date >= curdate() - 1 and log_date < curdate()");
        jdbcTemplate.batchUpdate("insert into history_log_etl_job_entry select * from log_etl_job_entry where log_date >= curdate() - 1 and log_date < curdate()");
        jdbcTemplate.batchUpdate("insert into history_log_etl_transform_step select * from log_etl_transform_step where log_date >= curdate() - 1 and log_date < curdate()");

        List<Map<String, Object>> jobs = jdbcTemplate.queryForList("select ID_JOB, CHANNEL_ID, JOBNAME, STATUS, LINES_READ, LINES_WRITTEN, LINES_UPDATED, LINES_INPUT, LINES_OUTPUT, LINES_REJECTED, ERRORS, STARTDATE, ENDDATE, LOGDATE, DEPDATE, REPLAYDATE, LOG_FIELD FROM log_etl_job WHERE status <> 'START' AND logdate >= curdate() - 1 AND logdate < curdate()");
        jdbcTemplate.batchUpdate("insert into history_log_etl_job values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                preparedStatement.setInt(1, (int) jobs.get(i).get("ID_JOB"));
                preparedStatement.setString(2, (String) jobs.get(i).get("CHANNEL_ID"));
                preparedStatement.setString(3, (String) jobs.get(i).get("JOBNAME"));
                preparedStatement.setString(4, (String) jobs.get(i).get("STATUS"));
                preparedStatement.setLong(5, ObjectUtils.isEmpty(jobs.get(i).get("LINES_READ")) ? null : (long) jobs.get(i).get("LINES_READ"));
                preparedStatement.setLong(6, ObjectUtils.isEmpty(jobs.get(i).get("LINES_WRITTEN")) ? null : (long) jobs.get(i).get("LINES_WRITTEN"));
                preparedStatement.setLong(7, ObjectUtils.isEmpty(jobs.get(i).get("LINES_UPDATED")) ? null : (long) jobs.get(i).get("LINES_UPDATED"));
                preparedStatement.setLong(8, ObjectUtils.isEmpty(jobs.get(i).get("LINES_INPUT")) ? null : (long) jobs.get(i).get("LINES_INPUT"));
                preparedStatement.setLong(9, ObjectUtils.isEmpty(jobs.get(i).get("LINES_OUTPUT")) ? null : (long) jobs.get(i).get("LINES_OUTPUT"));
                preparedStatement.setLong(10, ObjectUtils.isEmpty(jobs.get(i).get("LINES_REJECTED")) ? null : (long) jobs.get(i).get("LINES_REJECTED"));
                preparedStatement.setLong(11, ObjectUtils.isEmpty(jobs.get(i).get("ERRORS")) ? null : (long) jobs.get(i).get("ERRORS"));
                java.util.Date startDate = Date.from(((LocalDateTime) jobs.get(i).get("STARTDATE")).atZone(ZoneId.systemDefault()).toInstant());
                preparedStatement.setTimestamp(12, new Timestamp(startDate.getTime()));
                java.util.Date endDate = Date.from(((LocalDateTime) jobs.get(i).get("ENDDATE")).atZone(ZoneId.systemDefault()).toInstant());
                preparedStatement.setTimestamp(13, new Timestamp(endDate.getTime()));
                java.util.Date logDate = Date.from(((LocalDateTime) jobs.get(i).get("LOGDATE")).atZone(ZoneId.systemDefault()).toInstant());
                preparedStatement.setTimestamp(14, new Timestamp(logDate.getTime()));
                java.util.Date depDate = Date.from(((LocalDateTime) jobs.get(i).get("DEPDATE")).atZone(ZoneId.systemDefault()).toInstant());
                preparedStatement.setTimestamp(15, new Timestamp(depDate.getTime()));
                java.util.Date replayDate = Date.from(((LocalDateTime) jobs.get(i).get("REPLAYDATE")).atZone(ZoneId.systemDefault()).toInstant());
                preparedStatement.setTimestamp(16, new Timestamp(replayDate.getTime()));
                preparedStatement.setString(17, ZipUtils.compress((String) jobs.get(i).get("LOG_FIELD")));
            }

            @Override
            public int getBatchSize() {
                return jobs.size();
            }
        });
        List<Map<String, Object>> transforms = jdbcTemplate.queryForList("select ID_BATCH, CHANNEL_ID, TRANSNAME, STATUS, LINES_READ, LINES_WRITTEN, LINES_UPDATED, LINES_INPUT, LINES_OUTPUT, LINES_REJECTED, ERRORS, STARTDATE, ENDDATE, LOGDATE, DEPDATE, REPLAYDATE, compress(LOG_FIELD) FROM log_etl_transform WHERE status <> 'START' AND logdate >= curdate() - 1 AND logdate < curdate()");
        jdbcTemplate.batchUpdate("insert into history_log_etl_transform values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                preparedStatement.setInt(1, (int) transforms.get(i).get("ID_BATCH"));
                preparedStatement.setString(2, (String) transforms.get(i).get("CHANNEL_ID"));
                preparedStatement.setString(3, (String) transforms.get(i).get("JOBNAME"));
                preparedStatement.setString(4, (String) transforms.get(i).get("STATUS"));
                preparedStatement.setLong(5, (long) transforms.get(i).get("LINES_READ"));
                preparedStatement.setLong(6, (long) transforms.get(i).get("LINES_WRITTEN"));
                preparedStatement.setLong(7, (long) transforms.get(i).get("LINES_UPDATED"));
                preparedStatement.setLong(8, (long) transforms.get(i).get("LINES_INPUT"));
                preparedStatement.setLong(9, (long) transforms.get(i).get("LINES_OUTPUT"));
                preparedStatement.setLong(10, (long) transforms.get(i).get("LINES_REJECTED"));
                preparedStatement.setLong(11, (long) transforms.get(i).get("ERRORS"));
                java.util.Date startDate = Date.from(((LocalDateTime) transforms.get(i).get("STARTDATE")).atZone(ZoneId.systemDefault()).toInstant());
                preparedStatement.setTimestamp(12, new Timestamp(startDate.getTime()));
                java.util.Date endDate = Date.from(((LocalDateTime) transforms.get(i).get("ENDDATE")).atZone(ZoneId.systemDefault()).toInstant());
                preparedStatement.setTimestamp(13, new Timestamp(endDate.getTime()));
                java.util.Date logDate = Date.from(((LocalDateTime) transforms.get(i).get("LOGDATE")).atZone(ZoneId.systemDefault()).toInstant());
                preparedStatement.setTimestamp(14, new Timestamp(logDate.getTime()));
                java.util.Date depDate = Date.from(((LocalDateTime) transforms.get(i).get("DEPDATE")).atZone(ZoneId.systemDefault()).toInstant());
                preparedStatement.setTimestamp(15, new Timestamp(depDate.getTime()));
                java.util.Date replayDate = Date.from(((LocalDateTime) transforms.get(i).get("REPLAYDATE")).atZone(ZoneId.systemDefault()).toInstant());
                preparedStatement.setTimestamp(16, new Timestamp(replayDate.getTime()));
                preparedStatement.setString(17, ZipUtils.compress((String) transforms.get(i).get("LOG_FIELD")));
            }

            @Override
            public int getBatchSize() {
                return transforms.size();
            }
        });
        logService.clearJobLogField();
    }
}
