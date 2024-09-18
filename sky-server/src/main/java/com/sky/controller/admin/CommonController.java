package com.sky.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {

    private static final String GITEE_API_URL = "https://gitee.com/api/v5/repos/{owner}/{repo}/contents/{path}/skyimg";
    private static final String ACCESS_TOKEN = "e0638074ac7e9b565afd9cc3bfbd1630"; // 你需要在这里填写你的 Gitee access_token
    private static final String OWNER = "liu-bingduo"; // Gitee 用户名
    private static final String REPO = "pic-bed"; // 仓库名称

    /**
     * 文件上传到 Gitee
     * @param file
     * @return
     */
    @PostMapping("upload")
    @ApiOperation("文件上传到 Gitee")
    public Result<String> upload(@RequestParam("file") MultipartFile file){
        log.info("文件上传：{}", file);

        try {
            // 获取文件原始名称
            String originalFilename = file.getOriginalFilename();
            // 获取文件后缀名
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            // 生成新的文件名称
            String newFileName = UUID.randomUUID().toString() + extension;

            // 将文件内容转换为 base64
            String base64FileContent = java.util.Base64.getEncoder().encodeToString(file.getBytes());

            // 上传文件到 Gitee
            String giteeResponse = uploadToGitee(newFileName, base64FileContent);

            // 解析 Gitee 响应并返回 URL
            String fileUrl = parseGiteeResponse(giteeResponse, newFileName);
            log.info(fileUrl);
            return Result.success(fileUrl);

        } catch (IOException e) {
            return Result.error(MessageConstant.UPLOAD_FAILED);
        }
    }

    // 上传文件到 Gitee 仓库
    private String uploadToGitee(String fileName, String base64Content) throws IOException {
        RestTemplate restTemplate = new RestTemplate();

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        // 构建请求体
        Map<String, String> body = new HashMap<>();
        body.put("access_token", ACCESS_TOKEN);
        body.put("content", base64Content);
        body.put("message", "upload file " + fileName);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);

        // 发送 POST 请求到 Gitee API
        String filePath = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
        ResponseEntity<String> response = restTemplate.exchange(
                GITEE_API_URL.replace("{owner}", OWNER)
                        .replace("{repo}", REPO)
                        .replace("{path}", filePath),
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        return response.getBody();
    }

    // 解析 Gitee 返回的响应，提取出图片 URL
    private String parseGiteeResponse(String response, String fileName) {
        // 假设 Gitee 返回的格式包含 content -> download_url
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
            Map<String, Object> contentMap = (Map<String, Object>) responseMap.get("content");
            return (String) contentMap.get("download_url");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
