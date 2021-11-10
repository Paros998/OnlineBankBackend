package com.OBS.repository;

import com.OBS.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferRepository extends JpaRepository<Transfer,Long> {
    List<Transfer> findAllByClient_clientId(Long client_id);
}
