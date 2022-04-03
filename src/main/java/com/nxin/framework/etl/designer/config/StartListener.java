package com.nxin.framework.etl.designer.config;

import com.nxin.framework.etl.designer.converter.designer.job.JobConvertFactory;
import com.nxin.framework.etl.designer.converter.designer.transform.TransformConvertFactory;
import com.nxin.framework.etl.designer.service.designer.DatasourceService;
import com.nxin.framework.etl.designer.service.designer.ShellService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class StartListener implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    private DatasourceService datasourceService;
    @Autowired
    private ShellService shellService;
    @Value("${attachment.dir}")
    private String attachmentDir;
    @Value("${dev.dir}")
    private String devDir;
    @Value("${publish.dir}")
    private String publishDir;
    @Value("${production.dir}")
    private String productionDir;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private MetricsEndpoint metricsEndpoint;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        TransformConvertFactory.init(datasourceService, shellService);
        JobConvertFactory.init(datasourceService, shellService);
        File attachment = new File(attachmentDir);
        if (!attachment.exists()) {
            attachment.mkdirs();
        }
        File dev = new File(devDir);
        if (!dev.exists()) {
            dev.mkdirs();
        }
        File publish = new File(publishDir);
        if (!publish.exists()) {
            publish.mkdirs();
        }
        File production = new File(productionDir);
        if (!production.exists()) {
            production.mkdirs();
        }
        Runnable runnable = () -> {
            try {
                String hostname = InetAddress.getLocalHost().getHostAddress();
                Map<String, Object> response = new HashMap<>(0);
                Map<String, Object> measurement = new HashMap<>(0);
                response.put("hostname", hostname);
                response.put("measure", measurement);
                while (true) {
                    response.put("time", new SimpleDateFormat("HH:mm:ss").format(new Date()));
                    MetricsEndpoint.MetricResponse maxMemoryMetric = metricsEndpoint.metric("jvm.memory.max", null); //JVM最大内存
                    MetricsEndpoint.MetricResponse committedMemoryMetric = metricsEndpoint.metric("jvm.memory.committed", null); //JVM可用内存
                    MetricsEndpoint.MetricResponse usedMemoryMetric = metricsEndpoint.metric("jvm.memory.used", null); //JVM已用内存
                    MetricsEndpoint.MetricResponse threadMetric = metricsEndpoint.metric("jvm.threads.live", null); //JVM当前活跃线程数
                    MetricsEndpoint.MetricResponse systemCpuMetric = metricsEndpoint.metric("system.cpu.usage", null); //系统CPU使用率
                    MetricsEndpoint.MetricResponse processCpuMetric = metricsEndpoint.metric("process.cpu.usage", null); //当前进程CPU使用率
                    MetricsEndpoint.MetricResponse systemLoadMetric = metricsEndpoint.metric("system.load.average.1m", null); //系统CPU平均负载
                    MetricsEndpoint.MetricResponse processFileOpenMetric = metricsEndpoint.metric("process.files.open", null); //当前打开句柄数
                    measurement.put("maxMemoryMetric", String.format("%.2f", maxMemoryMetric.getMeasurements().get(0).getValue() / 1024 / 1024 / 1024));
                    measurement.put("committedMemoryMetric", String.format("%.2f", committedMemoryMetric.getMeasurements().get(0).getValue() / 1024 / 1024 / 1024));
                    measurement.put("usedMemoryMetric", String.format("%.2f", usedMemoryMetric.getMeasurements().get(0).getValue() / 1024 / 1024 / 1024));
                    measurement.put("threadMetric", String.format("%f", threadMetric.getMeasurements().get(0).getValue()));
                    measurement.put("systemCpuMetric", String.format("%.2f", systemCpuMetric.getMeasurements().get(0).getValue() * 100));
                    measurement.put("processCpuMetric", String.format("%.2f", processCpuMetric.getMeasurements().get(0).getValue() * 100));
                    if (systemLoadMetric != null) {
                        measurement.put("systemLoadMetric", String.format("%.2f", systemLoadMetric.getMeasurements().get(0).getValue()));
                    }
                    if (processFileOpenMetric != null) {
                        measurement.put("processFileOpenMetric", String.format("%f", processFileOpenMetric.getMeasurements().get(0).getValue()));
                    }
                    simpMessagingTemplate.convertAndSend("metrics", response);
                    TimeUnit.SECONDS.sleep(5L);
                }
            } catch (UnknownHostException e) {
                log.error(e.toString());
            } catch (InterruptedException e) {
                log.error(e.toString());
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
//        KettleLogStore.init();
//        KettleLogStore.getAppender().addLoggingEventListener(log -> {
//            LogMessage logMessage = (LogMessage) log.getMessage();
//            simpMessagingTemplate.convertAndSend(logMessage.getLogChannelId(), logMessage.getMessage());
//            log.getMessage();
//        });
    }
}
