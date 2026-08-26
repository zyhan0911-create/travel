package com.example.travel.Controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UploadController {

    private final String supabaseUrl = "https://ryybxlewunlxavehxjcp.supabase.co";
    private final String serviceRoleKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJ5eWJ4bGV3dW5seGF2ZWh4amNwIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc4NzU1MzEyMiwiZXhwIjoyMTAzMTI5MTIyfQ.VaE4olYVWHcyi02U5GqQs4iiKhYwTjob-KS4EMdSE2M";
    private final String bucketName = "zyhan0911-createsOrg";

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("上传文件不能为空");
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            String newFilename = UUID.randomUUID().toString() + extension;

            String uploadApiUrl = supabaseUrl + "/storage/v1/object/" + bucketName + "/" + newFilename;

            HttpHeaders headers = new HttpHeaders();
            headers.set("apikey", serviceRoleKey);
            headers.set("Authorization", "Bearer " + serviceRoleKey);
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new org.springframework.core.io.ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return newFilename;
                }
            });

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(uploadApiUrl, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                String publicUrl = supabaseUrl + "/storage/v1/object/public/" + bucketName + "/" + newFilename;
                return ResponseEntity.ok(publicUrl);
            } else {
                return ResponseEntity.status(500).body("同步到 Supabase 失败：" + response.getBody());
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("图片上传异常：" + e.getMessage());
        }
    }
}