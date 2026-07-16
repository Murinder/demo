package com.example.partnerservice.service;

import com.example.partnerservice.dto.AgreementDto;
import com.example.partnerservice.dto.CreateAgreementDto;
import com.example.partnerservice.model.Agreement;
import com.example.partnerservice.model.Partner;
import com.example.partnerservice.model.enums.AgreementStatus;
import com.example.partnerservice.repository.AgreementRepository;
import com.example.partnerservice.repository.PartnerRepository;
import com.example.sharedlib.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgreementServiceTest {

    @Mock
    private AgreementRepository agreementRepository;

    @Mock
    private PartnerRepository partnerRepository;

    @InjectMocks
    private AgreementService agreementService;

    private Partner createPartner() {
        return Partner.builder()
                .id(UUID.randomUUID())
                .companyName("Test Partner")
                .isActive(true)
                .build();
    }

    @Test
    void create_ValidDto_SavesAndReturnsDto() {
        Partner partner = createPartner();
        UUID partnerId = partner.getId();
        UUID projectId = UUID.randomUUID();

        CreateAgreementDto dto = CreateAgreementDto.builder()
                .partnerId(partnerId)
                .projectId(projectId)
                .documentPath("/docs/agreement.pdf")
                .expiresAt(OffsetDateTime.now().plusMonths(6))
                .description("Partnership agreement")
                .build();

        when(partnerRepository.findById(partnerId)).thenReturn(Optional.of(partner));
        when(agreementRepository.save(any(Agreement.class))).thenAnswer(inv -> {
            Agreement a = inv.getArgument(0);
            a.setId(UUID.randomUUID());
            return a;
        });

        AgreementDto result = agreementService.create(dto);

        assertNotNull(result);
        assertEquals(partnerId, result.getPartnerId());
        assertEquals(projectId, result.getProjectId());
        assertEquals("/docs/agreement.pdf", result.getDocumentPath());
        assertEquals("Partnership agreement", result.getDescription());
        verify(agreementRepository).save(any(Agreement.class));
    }

    @Test
    void create_NonExistingPartner_ThrowsException() {
        UUID partnerId = UUID.randomUUID();
        CreateAgreementDto dto = CreateAgreementDto.builder()
                .partnerId(partnerId)
                .projectId(UUID.randomUUID())
                .documentPath("/docs/test.pdf")
                .build();

        when(partnerRepository.findById(partnerId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> agreementService.create(dto));
        verify(agreementRepository, never()).save(any());
    }

    @Test
    void getById_ExistingAgreement_ReturnsDto() {
        Partner partner = createPartner();
        UUID agreementId = UUID.randomUUID();
        Agreement agreement = Agreement.builder()
                .id(agreementId)
                .partner(partner)
                .projectId(UUID.randomUUID())
                .documentPath("/docs/found.pdf")
                .status(AgreementStatus.DRAFT)
                .version(1)
                .build();

        when(agreementRepository.findById(agreementId)).thenReturn(Optional.of(agreement));

        AgreementDto result = agreementService.getById(agreementId);

        assertEquals(agreementId, result.getId());
        assertEquals("DRAFT", result.getStatus());
        assertEquals(1, result.getVersion());
    }

    @Test
    void getById_NonExistingAgreement_ThrowsException() {
        UUID id = UUID.randomUUID();
        when(agreementRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> agreementService.getById(id));
    }

    @Test
    void getByPartnerId_ReturnsList() {
        Partner partner = createPartner();
        UUID partnerId = partner.getId();
        Agreement a1 = Agreement.builder().id(UUID.randomUUID()).partner(partner).projectId(UUID.randomUUID()).documentPath("/a.pdf").status(AgreementStatus.DRAFT).version(1).build();
        Agreement a2 = Agreement.builder().id(UUID.randomUUID()).partner(partner).projectId(UUID.randomUUID()).documentPath("/b.pdf").status(AgreementStatus.SIGNED).version(1).build();

        when(agreementRepository.findByPartnerId(partnerId)).thenReturn(List.of(a1, a2));

        List<AgreementDto> result = agreementService.getByPartnerId(partnerId);

        assertEquals(2, result.size());
    }

    @Test
    void updateStatus_ToSigned_SetsSignedAt() {
        Partner partner = createPartner();
        UUID agreementId = UUID.randomUUID();
        Agreement agreement = Agreement.builder()
                .id(agreementId)
                .partner(partner)
                .projectId(UUID.randomUUID())
                .documentPath("/docs/to-sign.pdf")
                .status(AgreementStatus.DRAFT)
                .version(1)
                .build();

        when(agreementRepository.findById(agreementId)).thenReturn(Optional.of(agreement));
        when(agreementRepository.save(any(Agreement.class))).thenAnswer(inv -> inv.getArgument(0));

        AgreementDto result = agreementService.updateStatus(agreementId, AgreementStatus.SIGNED);

        assertEquals("SIGNED", result.getStatus());
        assertNotNull(result.getSignedAt());
    }

    @Test
    void updateStatus_ToExpired_DoesNotSetSignedAt() {
        Partner partner = createPartner();
        UUID agreementId = UUID.randomUUID();
        Agreement agreement = Agreement.builder()
                .id(agreementId)
                .partner(partner)
                .projectId(UUID.randomUUID())
                .documentPath("/docs/expire.pdf")
                .status(AgreementStatus.SENT)
                .version(1)
                .build();

        when(agreementRepository.findById(agreementId)).thenReturn(Optional.of(agreement));
        when(agreementRepository.save(any(Agreement.class))).thenAnswer(inv -> inv.getArgument(0));

        AgreementDto result = agreementService.updateStatus(agreementId, AgreementStatus.EXPIRED);

        assertEquals("EXPIRED", result.getStatus());
        assertNull(result.getSignedAt());
    }

    @Test
    void updateStatus_NonExistingAgreement_ThrowsException() {
        UUID id = UUID.randomUUID();
        when(agreementRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> agreementService.updateStatus(id, AgreementStatus.SIGNED));
    }
}
