package ru.mtuci.demo.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.demo.configuration.JwtTokenProvider;
import ru.mtuci.demo.model.*;
import ru.mtuci.demo.model.request.*;
import ru.mtuci.demo.service.SignatureAuditService;
import ru.mtuci.demo.service.SignatureService;
import ru.mtuci.demo.service.impl.UserDetailsServiceImpl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/signature")
@RequiredArgsConstructor
public class SignatureController {

    private final SignatureService signatureService;
    private final SignatureAuditService signatureAuditService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsServiceImpl userDetailsService;

    @GetMapping("/actual")
    public ResponseEntity<?> getAllActualSignatures() {
        try {
            List<ApplicationSignature> signatures = signatureService.getAllActualSignatures(SignatureStatus.ACTUAL);
            return ResponseEntity.ok(signatures);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Oops, something went wrong....");
        }
    }

    @PostMapping("/updated-after")
    public ResponseEntity<?> getSignaturesUpdatedAfter(@RequestBody SignaturesUpdatedAfterRequest request) {
        try {
            List<ApplicationSignature> signatures = signatureService.getSignaturesUpdatedAfter(request.getSince());
            return ResponseEntity.ok(Objects.requireNonNullElse(signatures, "Invalid date"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Oops, something went wrong....");
        }
    }

    @PostMapping("/by-guids")
    public ResponseEntity<?> getSignaturesByGuids(@RequestBody SignaturesByGuidsRequest request) {
        try {
            List<ApplicationSignature> signatures = signatureService.getSignaturesByGuids(request.getGuids());
            return ResponseEntity.ok(signatures);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Oops, something went wrong....");
        }
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('modification')")
    public ResponseEntity<?> addSignature(@RequestBody SignaturesAddRequest request, HttpServletRequest req) {
        try {
            String email = jwtTokenProvider.getUsername(req.getHeader("Authorization").substring(7));
            ApplicationUser user = userDetailsService.getUserByEmail(email);

            ApplicationSignature signature = signatureService.addSignature(request.getThreatName(), request.getFirstBytes(), request.getHash(),
                    request.getRemainderLength(), request.getFileType(), request.getOffsetStart(),
                    request.getOffsetEnd(), user.getId());

            return ResponseEntity.ok(signature);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Oops, something went wrong....");
        }
    }

    @PostMapping("/delete")
    @PreAuthorize("hasAnyAuthority('modification')")
    public ResponseEntity<?> deleteSignature(@RequestBody SignaturesDeleteRequest request, HttpServletRequest req) {
        try {
            String email = jwtTokenProvider.getUsername(req.getHeader("Authorization").substring(7));
            ApplicationUser user = userDetailsService.getUserByEmail(email);

            String result = signatureService.deleteSignature(request.getSignatureUUID(), user.getId());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Oops, something went wrong....");
        }
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('modification')")
    public ResponseEntity<?> updateSignature(@RequestBody SignaturesUpdateRequest request, HttpServletRequest req) {
        try {
            String email = jwtTokenProvider.getUsername(req.getHeader("Authorization").substring(7));
            ApplicationUser user = userDetailsService.getUserByEmail(email);

            ApplicationSignature signature = signatureService.updateSignature(request.getSignatureId(),
                    request.getThreatName(), request.getFirstBytes(), request.getHash(), request.getRemainderLength(),
                    request.getFileType(), request.getOffsetStart(), request.getOffsetEnd(), user.getId(), request.getStatus());

            return ResponseEntity.ok(Objects.requireNonNullElse(signature, "Signature not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Oops, something went wrong....");
        }
    }

    @PostMapping("/by-status")
    @PreAuthorize("hasAnyAuthority('modification')")
    public ResponseEntity<?> getSignaturesByStatus(@RequestBody SignaturesByStatusRequest request) {
        try {
            List<ApplicationSignature> signatures = signatureService.getAllActualSignatures(request.getStatus());
            return ResponseEntity.ok(signatures);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Oops, something went wrong....");
        }
    }

    @GetMapping("/audit")
    @PreAuthorize("hasAnyAuthority('modification')")
    public ResponseEntity<?> signatureAudit() {
        try {
            List<ApplicationSignatureAudit> auditRecords = signatureAuditService.getAllAuditRecords();
            return ResponseEntity.ok(auditRecords);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Oops, something went wrong....");
        }
    }

    @GetMapping(value = "/data")
    public ResponseEntity<?> getSignatureData() {
        try{
            List<ApplicationSignature> signatures = signatureService.getAllActualSignatures(SignatureStatus.ACTUAL);

            int signatureCount = signatures.size();

            ByteArrayOutputStream dataOutputStream = new ByteArrayOutputStream();
            writeLongLE(dataOutputStream, signatureCount);
            for (ApplicationSignature signature : signatures) {
                writeSignatureDataToStream(dataOutputStream, signature);
            }

            byte[] data = dataOutputStream.toByteArray();

            return ResponseEntity.ok(data);
        }
        catch (IOException ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/manifest")
    public ResponseEntity<?> getSignatureManifest() {
        try{
            List<ApplicationSignature> signatures = signatureService.getAllActualSignatures(SignatureStatus.ACTUAL);

            int signatureCount = signatures.size();
            List<String> signatureEntries = new ArrayList<>();

            for (ApplicationSignature signature : signatures) {
                String entry = signature.getId() + ":" + signature.getDigitalSignature();
                signatureEntries.add(entry);

            }

            byte[] data = createManifest(signatureCount, signatureEntries);

            return ResponseEntity.ok(data);
        }
        catch (IOException ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<MultiValueMap<String, Object>> buildMultipartResponse(byte [] manifest, byte[] data) {
        ByteArrayResource manifestRes = new ByteArrayResource(manifest) {
            @Override
            public String getFilename() {
                return "manifest.bin";
            }
        };

        ByteArrayResource dataRes = new ByteArrayResource(data) {
            @Override
            public String getFilename() {
                return "data.bin";
            }
        };

        LinkedMultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("manifest", new HttpEntity<>(manifestRes, createHeaders("manifest.bin")));
        parts.add("data", new HttpEntity<>(dataRes, createHeaders("data.bin")));

        return ResponseEntity.ok().contentType(MediaType.parseMediaType("multipart/mixed")).body(parts);
    }

    private HttpHeaders createHeaders(String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return headers;
    }

    private void writeUuidLE(ByteArrayOutputStream baos, UUID uuid) {
        writeLongLE(baos, uuid.getMostSignificantBits());
        writeLongLE(baos, uuid.getLeastSignificantBits());
    }

    private void writeLongLE(ByteArrayOutputStream baos, long value) {
        baos.write((byte) (value & 0xFF));
        baos.write((byte) (value >> 8 & 0xFF));
        baos.write((byte) (value >> 16 & 0xFF));
        baos.write((byte) (value >> 24 & 0xFF));
        baos.write((byte) (value >> 32 & 0xFF));
        baos.write((byte) (value >> 40 & 0xFF));
        baos.write((byte) (value >> 48 & 0xFF));
        baos.write((byte) (value >> 56 & 0xFF));
    }

    private void writeIntLE(ByteArrayOutputStream baos, int value) {
        baos.write((byte) (value & 0xFF));
        baos.write((byte) (value >> 8 & 0xFF));
        baos.write((byte) (value >> 16 & 0xFF));
        baos.write((byte) (value >> 24 & 0xFF));
    }

    private void writeStringLE(ByteArrayOutputStream baos, String value, boolean lenNeed) {
        byte[] bytes = value.getBytes();
        if (lenNeed) {
            writeLongLE(baos, bytes.length);
        }
        baos.writeBytes(bytes);
    }

    private void writeSignatureDataToStream(ByteArrayOutputStream baos, ApplicationSignature signature) throws IOException {
        writeUuidLE(baos, signature.getId());

        writeStringLE(baos, signature.getThreatName(), true);

        byte[] hexFirstBytes = hexStringToByteArray(signature.getFirstBytes());
        baos.write(hexFirstBytes);

        byte[] hexHashBytes = hexStringToByteArray(signature.getRemainderHash());
        baos.write(hexHashBytes);

        writeIntLE(baos, signature.getRemainderLength());

        writeStringLE(baos, signature.getFileType(), true);

        writeLongLE(baos, signature.getOffsetStart());

        writeLongLE(baos, signature.getOffsetEnd());
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    private byte[] createManifest(int signatureCount, List<String> signatureEntries) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        writeIntLE(baos, signatureCount);

        for (String entry : signatureEntries) {
            writeStringLE(baos, entry, true);
        }

        String manifestHash = signatureService.makeHash(baos.toString(StandardCharsets.UTF_8));
        writeStringLE(baos, signatureService.makeSignature(manifestHash), true);

        return baos.toByteArray();
    }
}