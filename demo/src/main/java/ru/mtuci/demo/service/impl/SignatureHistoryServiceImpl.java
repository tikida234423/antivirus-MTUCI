package ru.mtuci.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.demo.model.ApplicationSignature;
import ru.mtuci.demo.model.ApplicationSignatureHistory;
import ru.mtuci.demo.repository.SignatureHistoryRepository;
import ru.mtuci.demo.service.SignatureHistoryService;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class SignatureHistoryServiceImpl implements SignatureHistoryService {
    private final SignatureHistoryRepository signatureHistoryRepository;

    public void createSignatureHistory(ApplicationSignature signature) {
        ApplicationSignatureHistory signatureHistory = new ApplicationSignatureHistory();
        signatureHistory.setSignatureId(signature.getId());
        signatureHistory.setVersionCreatedAt(new Date());
        signatureHistory.setThreatName(signature.getThreatName());
        signatureHistory.setFirstBytes(signature.getFirstBytes());
        signatureHistory.setRemainderHash(signature.getRemainderHash());
        signatureHistory.setRemainderLength(signature.getRemainderLength());
        signatureHistory.setFileType(signature.getFileType());
        signatureHistory.setOffsetStart(signature.getOffsetStart());
        signatureHistory.setOffsetEnd(signature.getOffsetEnd());
        signatureHistory.setDigitalSignature(signature.getDigitalSignature());
        signatureHistory.setUpdatedAt(signature.getUpdatedAt());
        signatureHistory.setStatus(signature.getStatus());

        signatureHistoryRepository.save(signatureHistory);
    }
}
