package com.example.partnerservice.integration;

import com.example.partnerservice.dto.CreatePartnerDto;
import com.example.partnerservice.model.Partner;
import com.example.partnerservice.repository.PartnerRepository;
import com.example.testsupport.BaseIntegrationTest;
import com.example.testsupport.security.TestSecurityHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class PartnerControllerIT extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PartnerRepository partnerRepository;

    private final UUID partnerId = UUID.randomUUID();
    private final UUID adminId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        partnerRepository.deleteAll();
    }

    @Test
    void registerPartner_ValidData_Returns201AndPersists() throws Exception {
        CreatePartnerDto dto = CreatePartnerDto.builder()
                .companyName("TechCorp")
                .contactInfo("contact@techcorp.com")
                .website("https://techcorp.com")
                .industry("IT")
                .description("Technology company")
                .build();

        mockMvc.perform(TestSecurityHelper.withPartner(
                        post("/api/v1/partners/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)),
                        partnerId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.companyName").value("TechCorp"));

        assertThat(partnerRepository.count()).isEqualTo(1);
    }

    @Test
    void getAllPartners_Authenticated_Returns200() throws Exception {
        partnerRepository.save(Partner.builder()
                .companyName("Company A")
                .contactInfo("a@a.com")
                .isActive(true)
                .build());

        mockMvc.perform(TestSecurityHelper.withStudent(
                        get("/api/v1/partners"), UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getPartnerById_Existing_Returns200() throws Exception {
        Partner partner = partnerRepository.save(Partner.builder()
                .companyName("Specific Corp")
                .contactInfo("info@specific.com")
                .isActive(true)
                .build());

        mockMvc.perform(TestSecurityHelper.withStudent(
                        get("/api/v1/partners/" + partner.getId()), UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName").value("Specific Corp"));
    }

    @Test
    void getPartnerById_NonExistent_Returns404() throws Exception {
        mockMvc.perform(TestSecurityHelper.withStudent(
                        get("/api/v1/partners/" + UUID.randomUUID()), UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllPartners_WithoutAuth_Returns403() throws Exception {
        mockMvc.perform(get("/api/v1/partners"))
                .andExpect(status().isForbidden());
    }
}
