package com.example.adminservice.repository;

import com.example.adminservice.model.entity.DataOperation;
import com.example.adminservice.model.enums.OperationStatus;
import com.example.adminservice.model.enums.OperationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DataOperationRepository extends JpaRepository<DataOperation, UUID> {

    List<DataOperation> findByOperationTypeOrderByStartedAtDesc(OperationType operationType);

    List<DataOperation> findByStatusOrderByStartedAtDesc(OperationStatus status);

    List<DataOperation> findAllByOrderByStartedAtDesc();
}
