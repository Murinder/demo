package com.example.partnerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePartnerDto {
    private String companyName;
    private String contactInfo;
    private String website;
    private String industry;
    private String logoUrl;
    private String description;
}
