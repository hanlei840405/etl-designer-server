package com.nxin.framework.etl.designer.converter.designer.transform.streaming;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.nxin.framework.etl.designer.converter.designer.ConvertFactory;
import com.nxin.framework.etl.designer.converter.designer.transform.ResponseMeta;
import com.nxin.framework.etl.designer.converter.designer.transform.TransformConvertChain;
import com.nxin.framework.etl.designer.entity.designer.Shell;
import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl;
import lombok.extern.slf4j.Slf4j;
import org.pentaho.big.data.kettle.plugins.kafka.KafkaConsumerField;
import org.pentaho.big.data.kettle.plugins.kafka.KafkaConsumerInputMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.*;

@Slf4j
public class KafkaConsumerInputChain extends TransformConvertChain {

    @Override
    public ResponseMeta parse(mxCell cell, TransMeta transMeta) throws JsonProcessingException {
        if (cell.isVertex() && "KafkaConsumerInputMeta".equalsIgnoreCase(cell.getStyle())) {
            DeferredElementImpl value = (DeferredElementImpl) cell.getValue();
            KafkaConsumerInputMeta kafkaConsumerInputMeta = new KafkaConsumerInputMeta();
            Map<String, Object> formAttributes = objectMapper.readValue(value.getAttribute("form"), new TypeReference<Map<String, Object>>() {
            });
            String stepName = (String) formAttributes.get("name");
            String servers = (String) formAttributes.get("servers");
            Integer shellId = (Integer) formAttributes.get("shellId");
            List<Map<String, String>> topics = (List<Map<String, String>>) formAttributes.get("topics");
            String consumerGroup = (String) formAttributes.get("consumerGroup");
            String commitMode = (String) formAttributes.get("commitMode");
            int duration = 0;
            if (!ObjectUtils.isEmpty(formAttributes.get("duration"))) {
                duration = (int) formAttributes.get("duration");
            }
            int records = 0;
            if (!ObjectUtils.isEmpty(formAttributes.get("records"))) {
                records = (int) formAttributes.get("records");
            }
            int batches = 1;
            if (!ObjectUtils.isEmpty(formAttributes.get("batches"))) {
                batches = (int) formAttributes.get("batches");
            }
            String returnFieldByStep = (String) formAttributes.get("returnFieldByStep");
            List<Map<String, String>> fields = (List<Map<String, String>>) formAttributes.get("fields");
            for (Map<String, String> field : fields) {
                if ("key".equals(field.get("inputName"))) {
                    kafkaConsumerInputMeta.setKeyField(new KafkaConsumerField(KafkaConsumerField.Name.KEY, field.get("outputName"), KafkaConsumerField.Type.valueOf(field.get("category"))));
                } else if ("message".equals(field.get("inputName"))) {
                    kafkaConsumerInputMeta.setMessageField(new KafkaConsumerField(KafkaConsumerField.Name.MESSAGE, field.get("outputName"), KafkaConsumerField.Type.valueOf(field.get("category"))));
                } else if ("topic".equals(field.get("inputName"))) {
                    kafkaConsumerInputMeta.setTopicField(new KafkaConsumerField(KafkaConsumerField.Name.TOPIC, field.get("outputName"), KafkaConsumerField.Type.valueOf(field.get("category"))));
                } else if ("partition".equals(field.get("inputName"))) {
                    kafkaConsumerInputMeta.setPartitionField(new KafkaConsumerField(KafkaConsumerField.Name.PARTITION, field.get("outputName"), KafkaConsumerField.Type.valueOf(field.get("category"))));
                } else if ("offset".equals(field.get("inputName"))) {
                    kafkaConsumerInputMeta.setOffsetField(new KafkaConsumerField(KafkaConsumerField.Name.OFFSET, field.get("outputName"), KafkaConsumerField.Type.valueOf(field.get("category"))));
                } else if ("timestamp".equals(field.get("inputName"))) {
                    kafkaConsumerInputMeta.setTimestampField(new KafkaConsumerField(KafkaConsumerField.Name.TIMESTAMP, field.get("outputName"), KafkaConsumerField.Type.valueOf(field.get("category"))));
                }
            }
            List<Map<String, String>> options = (List<Map<String, String>>) formAttributes.get("options");
            Map<String, String> config = new HashMap<>(0);
            for (Map<String, String> option : options) {
                String v = "";
                if (StringUtils.hasLength(option.get("value"))) {
                    v = option.get("value");
                }
                config.put(option.get("name"), v);
            }
            kafkaConsumerInputMeta.setConfig(config);
            Shell transformShell = getShellService().one(shellId.longValue());
            if (transformShell.isExecutable()) {
                kafkaConsumerInputMeta.setFileName(transformShell.getXml());
                kafkaConsumerInputMeta.setTransformationPath(transformShell.getXml());
            }
            kafkaConsumerInputMeta.setDirectBootstrapServers(servers);
            for (Map<String, String> topic : topics) {
                kafkaConsumerInputMeta.addTopic(topic.get("topic"));
            }
            kafkaConsumerInputMeta.setConnectionType(KafkaConsumerInputMeta.ConnectionType.DIRECT);
            kafkaConsumerInputMeta.setConsumerGroup(consumerGroup);
            kafkaConsumerInputMeta.setAutoCommit("record".equals(commitMode));
            kafkaConsumerInputMeta.setBatchDuration("" + duration);
            kafkaConsumerInputMeta.setBatchSize("" + records);
            kafkaConsumerInputMeta.setParallelism("" + batches);
            kafkaConsumerInputMeta.setSubStep(returnFieldByStep);
            StepMeta stepMeta = new StepMeta(stepName, kafkaConsumerInputMeta);
            mxGeometry geometry = cell.getGeometry();
            stepMeta.setLocation(new Double(geometry.getX()).intValue(), new Double(geometry.getY()).intValue());
            stepMeta.setDraw(true);
            List<String> references = new ArrayList<>(0);
            if (StringUtils.hasLength(transformShell.getReference())) {
                references.addAll(Arrays.asList(transformShell.getReference().split(",")));
            }
            references.add(shellId.toString());
            ConvertFactory.getVariable().put("references", references);
            return new ResponseMeta(cell.getId(), stepMeta, null);
        } else {
            return next.parse(cell, transMeta);
        }
    }

    @Override
    public void callback(TransMeta transMeta, Map<String, String> idNameMapping) {

    }
}
