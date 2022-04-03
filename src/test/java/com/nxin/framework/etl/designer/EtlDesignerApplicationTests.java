package com.nxin.framework.etl.designer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nxin.framework.etl.designer.exception.UsernameExistedException;
import com.nxin.framework.etl.designer.task.ClearLogTask;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.pentaho.big.data.kettle.plugins.kafka.KafkaConsumerInputMeta;
import org.pentaho.big.data.kettle.plugins.kafka.KafkaProducerOutputMeta;
import org.pentaho.di.core.KettleClientEnvironment;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannel;
import org.pentaho.di.core.plugins.PluginFolder;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.jms.JmsConsumerMeta;
import org.pentaho.di.trans.step.jms.JmsProducerMeta;
import org.pentaho.di.trans.steps.mongodboutput.MongoDbOutputMeta;
import org.pentaho.mongo.MongoDbException;
import org.pentaho.mongo.wrapper.MongoClientWrapper;
import org.pentaho.mongo.wrapper.MongoWrapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;

@Slf4j
@SpringBootTest
class EtlDesignerApplicationTests {

    @Autowired
    private ClearLogTask clearLogTask;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void contextLoads() throws UsernameExistedException {
        clearLogTask.clear();
    }

    @Test
    void testMongo() throws MongoDbException, KettleException {

        StepPluginType.getInstance().getPluginFolders().add(new PluginFolder("classpath:plugins", false, true));
        KettleClientEnvironment.init();
        PluginRegistry.addPluginType(StepPluginType.getInstance());
        PluginRegistry.init();
        if (!Props.isInitialized()) {
            Props.init(0);
        }
        StepPluginType.getInstance().handlePluginAnnotation(
                KafkaConsumerInputMeta.class,
                KafkaConsumerInputMeta.class.getAnnotation(org.pentaho.di.core.annotations.Step.class),
                Collections.emptyList(), true, null);
        StepPluginType.getInstance().handlePluginAnnotation(
                KafkaProducerOutputMeta.class,
                KafkaProducerOutputMeta.class.getAnnotation(org.pentaho.di.core.annotations.Step.class),
                Collections.emptyList(), true, null);
        StepPluginType.getInstance().handlePluginAnnotation(
                JmsProducerMeta.class,
                JmsProducerMeta.class.getAnnotation(org.pentaho.di.core.annotations.Step.class),
                Collections.emptyList(), true, null);
        StepPluginType.getInstance().handlePluginAnnotation(
                JmsConsumerMeta.class,
                JmsConsumerMeta.class.getAnnotation(org.pentaho.di.core.annotations.Step.class),
                Collections.emptyList(), true, null);
//        SimpleLoggingObject spoonLoggingObject = new SimpleLoggingObject("SPOON", LoggingObjectType.SPOON, null);
//        spoonLoggingObject.setContainerObjectId(UUID.randomUUID().toString());
        MongoDbOutputMeta meta = new MongoDbOutputMeta();
        meta.setHostnames("localhost");
        meta.setPort("27017");
        TransMeta transMeta = new TransMeta();
        StepMeta stepMeta = new StepMeta("mongo", meta);
        transMeta.addStep(stepMeta);
        LogChannel logChannel = new LogChannel();
        MongoClientWrapper wrapper = MongoWrapperUtil.createMongoClientWrapper(meta, transMeta, logChannel);
        try {
            log.info("dbNames: {}", wrapper.getCollectionsNames("nxin"));
        } finally {
            wrapper.dispose();
        }
    }
}
