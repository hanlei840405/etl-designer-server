package com.nxin.framework.etl.designer.controller.designer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nxin.framework.etl.designer.dto.CrudDto;
import com.nxin.framework.etl.designer.enums.Constant;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.cluster.state.ClusterStateRequestBuilder;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PreAuthorize("hasAuthority('ROOT') or hasAuthority('DESIGNER')")
@RestController
@RequestMapping
public class ElasticSearchController {
    private ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/elasticSearch/test")
    public ResponseEntity<Map<String, Object>> test(@RequestBody CrudDto crudDto) throws JsonProcessingException {
        Map<String, Object> payload = objectMapper.readValue(crudDto.getPayload(), new TypeReference<Map<String, Object>>() {
        });
        List<Map<String, String>> servers = (List<Map<String, String>>) payload.get("servers");
        List<Map<String, String>> settings = (List<Map<String, String>>) payload.get("settings");
        Settings.Builder settingsBuilder = Settings.builder();
        settingsBuilder.put(Settings.Builder.EMPTY_SETTINGS);
        for (Map<String, String> setting : settings) {
            settingsBuilder.put(setting.get("setting"), setting.get("value"));
        }
        try (PreBuiltTransportClient client = new PreBuiltTransportClient(settingsBuilder.build())) {
            for (Map<String, String> server : servers) {
                client.addTransportAddress(new TransportAddress(
                        InetAddress.getByName(server.get("address")),
                        Integer.parseInt(server.get("port"))));
            }
            AdminClient admin = client.admin();
            ClusterStateRequestBuilder clusterBld = admin.cluster().prepareState();
            ActionFuture<ClusterStateResponse> lafClu = clusterBld.execute();
            ClusterStateResponse cluResp = lafClu.actionGet();
            String name = cluResp.getClusterName().value();
            ClusterState cluState = cluResp.getState();
            int numNodes = cluState.getNodes().getSize();
            Map<String, Object> response = new HashMap<>(0);
            response.put("name", name);
            response.put("numNodes", numNodes);
            clusterBld.clear();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(Constant.EXCEPTION_CONNECTION_FAILURE).build();
        }
    }
}