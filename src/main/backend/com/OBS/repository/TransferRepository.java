package com.OBS.repository;

import com.OBS.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface TransferRepository extends JpaRepository<Transfer,Long>, JpaSpecificationExecutor<Transfer> {
    List<Transfer> findRecentTransfersByClient_clientIdOrderByTransferDateDesc(Long client_id, Pageable pageable);
    List<Transfer> findAllByClient_clientId(Long client_id);

}