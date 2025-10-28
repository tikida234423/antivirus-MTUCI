package ru.mtuci.demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.mtuci.demo.model.ApplicationSignature;
import ru.mtuci.demo.model.SignatureStatus;

import java.util.List;
import java.util.UUID;

public interface SignatureService {

    List<ApplicationSignature> getAllActualSignatures(SignatureStatus status);

    List<ApplicationSignature> getSignaturesUpdatedAfter(String since);

    List<ApplicationSignature> getSignaturesByGuids(List<UUID> guids);

    ApplicationSignature addSignature(String threatName, String firstBytes, String hash, Integer remainderLength,
                                             String fileType, Integer offsetStart, Integer offsetEnd, Long userId) throws JsonProcessingException;

    String deleteSignature(UUID signatureUUID, Long userId);

    ApplicationSignature updateSignature(UUID signatureUUID, String threatName, String firstBytes, String hash, Integer remainderLength,
                                                String fileType, Integer offsetStart, Integer offsetEnd, Long userId, SignatureStatus status) throws JsonProcessingException;

    String makeHash(String input);

    String makeSignature(String res);

}
