package com.nxin.framework.etl.designer.controller.designer;

import com.nxin.framework.etl.designer.entity.basic.User;
import com.nxin.framework.etl.designer.entity.designer.Datasource;
import com.nxin.framework.etl.designer.enums.Constant;
import com.nxin.framework.etl.designer.enums.DatasourceType;
import com.nxin.framework.etl.designer.service.DynamicQueryDataService;
import com.nxin.framework.etl.designer.service.basic.UserService;
import com.nxin.framework.etl.designer.service.designer.DatasourceService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.exception.KettleValueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@PreAuthorize("hasAuthority('ROOT') or hasAuthority('DATASOURCE') or hasAuthority('DESIGNER') or hasAuthority('REPORT') or hasAuthority('LAYOUT')")
@Slf4j
@RestController
@RequestMapping
public class PreviewDataController {
    @Autowired
    private UserService userService;
    @Autowired
    private DatasourceService datasourceService;
    @Autowired
    private DynamicQueryDataService dynamicQueryDataService;
    public static final int MAX_BINARY_STRING_PREVIEW_SIZE = 1000000;

    @PostMapping("/preview")
    public ResponseEntity<Map<String, Object>> preview(@RequestBody Payload payload, Principal principal) {
        User loginUser = userService.one(principal.getName());
        Datasource datasource = datasourceService.one(payload.datasourceId, loginUser.getTenant().getId());
        if (datasource != null && datasource.getProject() != null && datasource.getProject().getUsers().contains(loginUser)) {
            try {
                Map<String, Object> result = dynamicQueryDataService.preview(datasource.getName(), DatasourceType.getValue(datasource.getCategory()), datasource.getHost(), datasource.getSchemaName(), datasource.getPort().toString(), datasource.getUsername(), datasource.getPassword(), payload.sql);
                return ResponseEntity.ok(result);
            } catch (KettleDatabaseException | KettleValueException e) {
                return ResponseEntity.status(Constant.EXCEPTION_SQL_GRAMMAR).build();
            }
        }
        return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
    }

    @PostMapping("/structure")
    public ResponseEntity<List<Map<String, Object>>> structure(@RequestBody Payload payload, Principal principal) {
        User loginUser = userService.one(principal.getName());
        Datasource datasource = datasourceService.one(payload.datasourceId, loginUser.getTenant().getId());
        if (datasource != null && datasource.getProject() != null && datasource.getProject().getUsers().contains(loginUser)) {
            List<Map<String, Object>> response = dynamicQueryDataService.structure(datasource.getName(), DatasourceType.getValue(datasource.getCategory()), datasource.getHost(), datasource.getSchemaName(), datasource.getPort().toString(), datasource.getUsername(), datasource.getPassword(), payload.category, payload.name);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
    }

    @Data
    public static class Payload implements Serializable {
        private Long datasourceId;
        private String name;
        private String category;
        private String sql;
        private List<String> filenames;
        private String sheetName;
    }
}
