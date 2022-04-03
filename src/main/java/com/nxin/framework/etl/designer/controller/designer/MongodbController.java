package com.nxin.framework.etl.designer.controller.designer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nxin.framework.etl.designer.dto.CrudDto;
import org.pentaho.di.core.logging.LogChannel;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.mongodboutput.MongoDbOutputMeta;
import org.pentaho.mongo.MongoDbException;
import org.pentaho.mongo.wrapper.MongoClientWrapper;
import org.pentaho.mongo.wrapper.MongoWrapperUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;

@PreAuthorize("hasAuthority('ROOT') or hasAuthority('DESIGNER')")
@RestController
@RequestMapping("/mongo")
public class MongodbController {
    private ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/dbNames")
    public ResponseEntity<List<String>> dbNames(@RequestBody CrudDto crudDto) throws JsonProcessingException, MongoDbException {
        MongoDbOutputMeta meta = new MongoDbOutputMeta();
        Map<String, Object> payload = objectMapper.readValue(crudDto.getPayload(), new TypeReference<Map<String, Object>>() {
        });
        meta.setHostnames((String) payload.get("host"));
        meta.setPort((String) payload.get("port"));
        meta.setUseAllReplicaSetMembers((Boolean) payload.get("replica"));
        meta.setAuthenticationDatabaseName((String) payload.get("authenticateDB"));
        meta.setAuthenticationMechanism((String) payload.get("mechanism"));
        meta.setAuthenticationUser((String) payload.get("username"));
        meta.setAuthenticationPassword((String) payload.get("password"));
        meta.setUseKerberosAuthentication((Boolean) payload.get("kerberos"));
        TransMeta transMeta = new TransMeta();
        StepMeta stepMeta = new StepMeta("mongo", meta);
        transMeta.addStep(stepMeta);
        LogChannel logChannel = new LogChannel();
        MongoClientWrapper wrapper = MongoWrapperUtil.createMongoClientWrapper(meta, transMeta, logChannel);
        try {
            return ResponseEntity.ok(wrapper.getDatabaseNames());
        } finally {
            wrapper.dispose();
        }
    }

    @PostMapping("/collections")
    public ResponseEntity<Set<String>> collections(@RequestBody CrudDto crudDto) throws JsonProcessingException, MongoDbException {
        MongoDbOutputMeta meta = new MongoDbOutputMeta();
        Map<String, Object> payload = objectMapper.readValue(crudDto.getPayload(), new TypeReference<Map<String, Object>>() {
        });
        meta.setHostnames((String) payload.get("host"));
        meta.setPort((String) payload.get("port"));
        meta.setUseAllReplicaSetMembers((Boolean) payload.get("replica"));
        meta.setAuthenticationDatabaseName((String) payload.get("authenticateDB"));
        meta.setAuthenticationMechanism((String) payload.get("mechanism"));
        meta.setAuthenticationUser((String) payload.get("username"));
        meta.setAuthenticationPassword((String) payload.get("password"));
        meta.setUseKerberosAuthentication((Boolean) payload.get("kerberos"));
        TransMeta transMeta = new TransMeta();
        StepMeta stepMeta = new StepMeta("mongo", meta);
        transMeta.addStep(stepMeta);
        LogChannel logChannel = new LogChannel();
        MongoClientWrapper wrapper = MongoWrapperUtil.createMongoClientWrapper(meta, transMeta, logChannel);
        try {
            return ResponseEntity.ok(wrapper.getCollectionsNames((String) payload.get("database")));
        } finally {
            wrapper.dispose();
        }
    }

    @PostMapping("/writeConcerns")
    public ResponseEntity<List<String>> writeConcerns(@RequestBody CrudDto crudDto) throws JsonProcessingException, MongoDbException {
        MongoDbOutputMeta meta = new MongoDbOutputMeta();
        Map<String, Object> payload = objectMapper.readValue(crudDto.getPayload(), new TypeReference<Map<String, Object>>() {
        });
        meta.setHostnames((String) payload.get("host"));
        meta.setPort((String) payload.get("port"));
        meta.setUseAllReplicaSetMembers((Boolean) payload.get("replica"));
        meta.setAuthenticationDatabaseName((String) payload.get("authenticateDB"));
        meta.setAuthenticationMechanism((String) payload.get("mechanism"));
        meta.setAuthenticationUser((String) payload.get("username"));
        meta.setAuthenticationPassword((String) payload.get("password"));
        meta.setUseKerberosAuthentication((Boolean) payload.get("kerberos"));
        TransMeta transMeta = new TransMeta();
        StepMeta stepMeta = new StepMeta("mongo", meta);
        transMeta.addStep(stepMeta);
        LogChannel logChannel = new LogChannel();
        MongoClientWrapper wrapper = MongoWrapperUtil.createMongoClientWrapper(meta, transMeta, logChannel);
        try {
            return ResponseEntity.ok(wrapper.getLastErrorModes());
        } finally {
            wrapper.dispose();
        }
    }
}
