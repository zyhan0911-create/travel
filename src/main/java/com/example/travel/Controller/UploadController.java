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

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.service-role-key}")
    private String serviceRoleKey;

    @Value("${supabase.bucket-name:travel-images}")
    private String bucketName;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("上传文件不能为空");
        }

        try {
            // 1. 生成不重复的文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            String newFilename = UUID.randomUUID().toString() + extension;

            // 2. 拼接 Supabase Storage 上传 API 地址
            // 官方上传接口: POST /storage/v1/object/{bucketName}/{fileName}
            String uploadApiUrl = supabaseUrl + "/storage/v1/object/" + bucketName + "/" + newFilename;

            // 3. 准备请求头（适配 Supabase 新版 sb_secret_ 密钥）
            HttpHeaders headers = new HttpHeaders();
            headers.set("apikey", serviceRoleKey);                     // <-- 加上这一行
            headers.set("Authorization", "Bearer " + serviceRoleKey);
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // 4. 封装文件流
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            // 注意：Spring 的 RestTemplate 需要把 MultipartFile 转成 ByteArrayResource 才能正确通过 HTTP 发送
            body.add("file", new org.springframework.core.io.ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return newFilename;
                }
            });

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // 5. 发送请求到 Supabase
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(uploadApiUrl, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                // 6. 上传成功后，拼接 Supabase 的公开访问 URL
                // 公开访问格式: {supabaseUrl}/storage/v1/object/public/{bucketName}/{fileName}
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