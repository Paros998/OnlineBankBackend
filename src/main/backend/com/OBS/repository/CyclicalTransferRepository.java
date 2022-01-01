package com.OBS.repository;

import com.OBS.entity.CyclicalTransfer;
import com.OBS.entity.Transfer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CyclicalTransferRepository extends JpaRepository<CyclicalTransfer,Long>, JpaSpecificationExecutor<CyclicalTransfer> {
    List<CyclicalTransfer> findAllByClient_clientId(Long clientId);

    List<CyclicalTransfer> findComingByClient_clientIdOrderByReTransferDateDesc(Long clientId, Pageable pageable);
}