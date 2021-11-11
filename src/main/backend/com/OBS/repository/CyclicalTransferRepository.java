package com.OBS.repository;

import com.OBS.entity.CyclicalTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CyclicalTransferRepository extends JpaRepository<CyclicalTransfer,Long> {
    List<CyclicalTransfer> findAllByClient_clientId(Long clientId);
}
