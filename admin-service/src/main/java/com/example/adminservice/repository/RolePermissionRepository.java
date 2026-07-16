package com.example.adminservice.repository;

import com.example.adminservice.model.entity.RolePermission;
import com.example.adminservice.model.enums.SystemRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermission.RolePermissionPK> {

    List<RolePermission> findByIdRole(SystemRole role);
}
