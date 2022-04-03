package com.nxin.framework.etl.designer.repository.analysis;

import com.nxin.framework.etl.designer.entity.analysis.Metadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface MetadataRepository extends JpaRepository<Metadata, Long> {
    List<Metadata> findByModelIdAndStatus(Long modelId, String status);
}
