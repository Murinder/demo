package com.example.adminservice.repository;

import com.example.adminservice.model.entity.UserRoleEntity;
import com.example.adminservice.model.enums.SystemRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRoleEntity, UserRoleEntity.UserRolePK> {

    List<UserRoleEntity> findByIdUserId(UUID userId);

    List<UserRoleEntity> findByIdRole(SystemRole role);

    void deleteByIdUserId(UUID userId);
}
