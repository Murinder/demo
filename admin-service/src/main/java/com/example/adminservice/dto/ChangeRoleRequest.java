package com.example.adminservice.dto;

import com.example.adminservice.model.enums.SystemRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeRoleRequest {
    private SystemRole role;
}
