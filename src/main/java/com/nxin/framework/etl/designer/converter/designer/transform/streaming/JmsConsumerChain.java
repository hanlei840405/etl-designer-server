package com.nxin.framework.etl.designer.converter.designer.transform.streaming;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.nxin.framework.etl.designer.converter.designer.transform.ResponseMeta;
import com.nxin.framework.etl.designer.converter.designer.transform.TransformConvertChain;
import com.nxin.framework.etl.designer.entity.designer.Shell;
import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl;
import lombok.extern.slf4j.Slf4j;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.jms.JmsConsumerMeta;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

@Slf4j
public class JmsConsumerChain extends TransformConvertChain {

    @Override
    public ResponseMeta parse(mxCell cell, TransMeta transMeta) throws JsonProcessingException {
        if (cell.isVertex() && "JmsConsumerMeta".equalsIgnoreCase(cell.getStyle())) {
            DeferredElementImpl value = (DeferredElementImpl) cell.getValue();
            JmsConsumerMeta jmsConsumerMeta = new JmsConsumerMeta();
            Map<String, Object> formAttributes = objectMapper.readValue(value.getAttribute("form"), new TypeReference<Map<String, Object>>() {
            });
            String stepName = (String) formAttributes.get("name");
            String connection = (String) formAttributes.get("connection");
            String url = (String) formAttributes.get("url");
            String destinationType = (String) formAttributes.get("destinationType");
            String destinationName = (String) formAttributes.get("destinationName");
            Integer shellId = (Integer) formAttributes.get("shellId");
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
            int timout = 0;
            if (!ObjectUtils.isEmpty(formAttributes.get("timout"))) {
                batches = (int) formAttributes.get("timout");
            }
            String username = (String) formAttributes.get("username");
            String password = (String) formAttributes.get("password");
            String returnFieldByStep = (String) formAttributes.get("returnFieldByStep");
            List<Map<String, String>> fields = (List<Map<String, String>>) formAttributes.get("fields");
            jmsConsumerMeta.receiveTimeout = "" + timout;
            for (Map<String, String> field : fields) {
                if ("key".equals(field.get("inputName"))) {
                    jmsConsumerMeta.messageField = field.get("outputName");
                } else if ("message".equals(field.get("inputName"))) {
                    jmsConsumerMeta.destinationField = field.get("outputName");
                } else if ("topic".equals(field.get("inputName"))) {
                    jmsConsumerMeta.messageId = field.get("outputName");
                } else if ("partition".equals(field.get("inputName"))) {
                    jmsConsumerMeta.jmsTimestamp = field.get("outputName");
                } else if ("offset".equals(field.get("inputName"))) {
                    jmsConsumerMeta.jmsRedelivered = field.get("outputName");
                }
            }
            Shell transformShell = getShellService().one(shellId.longValue());
            if (transformShell.isExecutable()) {
                jmsConsumerMeta.setFileName(transformShell.getXml());
                jmsConsumerMeta.setTransformationPath(transformShell.getXml());
            }
            jmsConsumerMeta.jmsDelegate.connectionType = connection;
            jmsConsumerMeta.jmsDelegate.destinationType = destinationType;
            jmsConsumerMeta.jmsDelegate.destinationName = destinationName;
            if ("amq".equals(connection)) {
                jmsConsumerMeta.jmsDelegate.amqUsername = username;
                jmsConsumerMeta.jmsDelegate.amqPassword = password;
                jmsConsumerMeta.jmsDelegate.amqUrl = url;
            } else {
                jmsConsumerMeta.jmsDelegate.ibmUsername = username;
                jmsConsumerMeta.jmsDelegate.ibmPassword = password;
                jmsConsumerMeta.jmsDelegate.ibmUrl = url;
            }
            jmsConsumerMeta.setBatchDuration("" + duration);
            jmsConsumerMeta.setBatchSize("" + records);
            jmsConsumerMeta.setParallelism("" + batches);
            jmsConsumerMeta.setSubStep(returnFieldByStep);
            StepMeta stepMeta = new StepMeta(stepName, jmsConsumerMeta);
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
