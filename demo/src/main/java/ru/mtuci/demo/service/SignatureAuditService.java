package ru.mtuci.demo.service;

import ru.mtuci.demo.model.ApplicationSignatureAudit;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface SignatureAuditService {

    void createSignatureAudit(UUID signatureId, Long userId, String changedType, Date changedAt, String fieldChanged);

    List<ApplicationSignatureAudit> getAllAuditRecords();

}
