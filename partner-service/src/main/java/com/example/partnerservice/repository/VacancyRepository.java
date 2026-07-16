package com.example.partnerservice.repository;

import com.example.partnerservice.model.Vacancy;
import com.example.partnerservice.model.enums.VacancyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VacancyRepository extends JpaRepository<Vacancy, UUID> {
    List<Vacancy> findByPartnerId(UUID partnerId);
    List<Vacancy> findByIsActiveTrue();
    List<Vacancy> findByVacancyType(VacancyType vacancyType);
}
