package com.example.sharedlib.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DocumentEvent extends BaseEvent {
    private UUID documentId;
    private UUID templateId;
    private UUID generatedFor;
    private UUID generatedBy;
    private String documentType;
    private String templateName;
}
