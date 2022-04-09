package com.nxin.framework.etl.designer;

import org.pentaho.big.data.kettle.plugins.kafka.KafkaConsumerInputMeta;
import org.pentaho.big.data.kettle.plugins.kafka.KafkaProducerOutputMeta;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.plugins.PluginFolder;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.trans.step.jms.JmsConsumerMeta;
import org.pentaho.di.trans.step.jms.JmsProducerMeta;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@EnableTransactionManagement
@EnableSwagger2
@EnableScheduling
@SpringBootApplication(exclude = MongoAutoConfiguration.class)
public class EtlDesignerApplication {

    public static void main(String[] args) throws KettleException {
//        StepPluginType.getInstance().getPluginFolders().add(new PluginFolder("classpath:plugins", false, true));
        KettleEnvironment.init();
        PluginRegistry.addPluginType( StepPluginType.getInstance() );
        PluginRegistry.init();
        if ( !Props.isInitialized() ) {
            Props.init( 0 );
        }
        StepPluginType.getInstance().handlePluginAnnotation(
                KafkaConsumerInputMeta.class,
                KafkaConsumerInputMeta.class.getAnnotation( org.pentaho.di.core.annotations.Step.class ),
                Collections.emptyList(), false, null );
        StepPluginType.getInstance().handlePluginAnnotation(
                KafkaProducerOutputMeta.class,
                KafkaProducerOutputMeta.class.getAnnotation( org.pentaho.di.core.annotations.Step.class ),
                Collections.emptyList(), false, null );
//        StepPluginType.getInstance().handlePluginAnnotation(
//                JmsProducerMeta.class,
//                JmsProducerMeta.class.getAnnotation(org.pentaho.di.core.annotations.Step.class),
//                Collections.emptyList(), true, null);
//        StepPluginType.getInstance().handlePluginAnnotation(
//                JmsConsumerMeta.class,
//                JmsConsumerMeta.class.getAnnotation(org.pentaho.di.core.annotations.Step.class),
//                Collections.emptyList(), true, null);
        SpringApplication.run(EtlDesignerApplication.class, args);
    }

}
