package com.nxin.framework.etl.designer.service.analysis;

import com.nxin.framework.etl.designer.entity.analysis.Metadata;
import com.nxin.framework.etl.designer.entity.analysis.Model;
import com.nxin.framework.etl.designer.entity.basic.Tenant;
import com.nxin.framework.etl.designer.enums.Constant;
import com.nxin.framework.etl.designer.repository.analysis.MetadataRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MetadataService {
    @Autowired
    private MetadataRepository metadataRepository;

    public void save(Model model, List<Metadata> metadataList, Tenant tenant) {
        if (!metadataList.isEmpty()) {
            Map<String, Metadata> records = metadataList.stream().collect(Collectors.toMap(Metadata::getColumnCode, metadata -> metadata));
            List<Metadata> persisted = metadataRepository.findByModelIdAndStatus(model.getId(), Constant.ACTIVE);
            Map<String, Metadata> persistedMap = persisted.stream().collect(Collectors.toMap(Metadata::getColumnCode, metadata -> metadata));
            List<Metadata> insertOrUpdateList = new ArrayList<>(0);
            persistedMap.keySet().forEach(k -> {
                Metadata metadata = persistedMap.get(k);
                if (!records.containsKey(k)) { // delete
                    metadata.setStatus(Constant.INACTIVE);
                    insertOrUpdateList.add(metadata);
                } else { // update
                    BeanUtils.copyProperties(records.get(k), metadata, "id", "model", "createTime", "creator");
                    metadata.setStatus(Constant.ACTIVE);
                    insertOrUpdateList.add(metadata);
                    records.remove(k);
                }
            });
            records.values().forEach(metadata -> {
                metadata.setTenant(tenant);
                metadata.setStatus(Constant.ACTIVE);
                metadata.setModel(model);
            });
            insertOrUpdateList.addAll(records.values());
            metadataRepository.saveAll(insertOrUpdateList);
        }
    }
}
