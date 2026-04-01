package com.uni.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uni.common.BizCode;
import com.uni.common.BizException;
import com.uni.entity.AttachmentEntity;
import com.uni.mapper.AttachmentMapper;
import com.uni.service.FileService;
import com.uni.vo.file.FileVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件服务实现
 * <p>
 * 支持两种存储模式：
 * 1. 本地存储（默认，开发阶段使用）
 * 2. 七牛云OSS（生产环境，配置app.qiniu后自动启用）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl extends ServiceImpl<AttachmentMapper, AttachmentEntity> implements FileService {

    /** 允许上传的图片类型 */
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/webp", "image/bmp"
    );

    /** 最大文件大小 10MB */
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    @Value("${app.qiniu.access-key:}")
    private String qiniuAccessKey;

    @Value("${app.qiniu.domain:}")
    private String qiniuDomain;

    /** 本地文件存储根路径 */
    @Value("${app.file.upload-path:./uploads}")
    private String uploadPath;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileVO uploadFile(MultipartFile file, Long userId, Integer attachmentType, Integer uploadSource) {
        // 参数校验
        if (file == null || file.isEmpty()) {
            throw new BizException(BizCode.BAD_REQUEST, "上传文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        String contentType = file.getContentType();
        long fileSize = file.getSize();

        // 文件大小校验
        if (fileSize > MAX_FILE_SIZE) {
            throw new BizException(BizCode.FILE_SIZE_EXCEEDED, "文件大小不能超过10MB");
        }

        // 文件类型校验
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new BizException(BizCode.FILE_FORMAT_NOT_SUPPORT, "仅支持 JPG、PNG、GIF、WebP 格式的图片");
        }

        // 获取文件扩展名
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        }

        // 生成存储路径：按日期分目录
        String dateDir = java.time.LocalDate.now().toString().replace("-", "/");
        String storageFileName = UUID.randomUUID().toString().replace("-", "") + extension;
        String relativePath = "meal/" + dateDir + "/" + storageFileName;

        String fileUrl;
        String storagePath;

        // 根据配置选择存储方式：有七牛配置就用OSS，否则本地
        boolean useQiniu = qiniuAccessKey != null && !qiniuAccessKey.isEmpty()
                && !qiniuAccessKey.equals("your_qiniu_access_key");

        if (useQiniu) {
            // 七牛云上传（生产环境）
            storagePath = relativePath;
            fileUrl = uploadToQiniu(file, relativePath);
        } else {
            // 本地存储（开发环境）
            try {
                Path fullDir = Paths.get(uploadPath, relativePath).getParent();
                Files.createDirectories(fullDir);
                Path fullPath = Paths.get(uploadPath, relativePath);
                Files.write(fullPath, file.getBytes());
                storagePath = fullPath.toString();
                fileUrl = "/files/" + relativePath;
                log.info("文件本地存储成功: {}", storagePath);
            } catch (IOException e) {
                log.error("文件存储失败", e);
                throw new BizException(BizCode.INTERNAL_ERROR, "文件存储失败，请重试");
            }
        }

        // 保存附件记录到数据库
        AttachmentEntity attachment = new AttachmentEntity()
                .setUserId(userId)
                .setFileName(originalFilename)
                .setFileSize(fileSize)
                .setFileType(contentType)
                .setFileExtension(extension)
                .setStoragePath(storagePath)
                .setOssUrl(useQiniu ? fileUrl : "")
                .setAttachmentType(attachmentType != null ? attachmentType : 1)
                .setUploadSource(uploadSource != null ? uploadSource : 1)
                .setCreateUser(String.valueOf(userId))
                .setUpdateUser(String.valueOf(userId))
                .setDeleteFlag(0);

        save(attachment);

        // 构建返回VO
        FileVO vo = new FileVO();
        vo.setId(attachment.getId());
        vo.setFileName(originalFilename);
        vo.setFileSize(fileSize);
        vo.setFileType(contentType);
        vo.setUrl(fileUrl);

        return vo;
    }

    @Override
    public List<String> getFileUrls(String attachmentIds) {
        if (attachmentIds == null || attachmentIds.isBlank()) {
            return new ArrayList<>();
        }
        String[] ids = attachmentIds.split(",");
        List<String> urls = new ArrayList<>();
        for (String idStr : ids) {
            try {
                Long id = Long.parseLong(idStr.trim());
                String url = getFileUrl(id);
                if (url != null) {
                    urls.add(url);
                }
            } catch (NumberFormatException e) {
                log.warn("无效的附件ID: {}", idStr);
            }
        }
        return urls;
    }

    @Override
    public String getFileUrl(Long attachmentId) {
        AttachmentEntity attachment = getById(attachmentId);
        if (attachment == null || attachment.getDeleteFlag() == 1) {
            return null;
        }
        // 优先返回OSS URL
        if (attachment.getOssUrl() != null && !attachment.getOssUrl().isEmpty()) {
            return attachment.getOssUrl();
        }
        return attachment.getStoragePath();
    }

    /**
     * 上传到七牛云
     */
    private String uploadToQiniu(MultipartFile file, String key) {
        try {
            com.qiniu.storage.Configuration cfg = new com.qiniu.storage.Configuration();
            com.qiniu.storage.UploadManager uploadManager = new com.qiniu.storage.UploadManager(cfg);
            com.qiniu.util.Auth auth = com.qiniu.util.Auth.create(qiniuAccessKey,
                    "your_qiniu_secret_key"); // TODO: 从配置注入
            String upToken = auth.uploadToken("uni-app");

            byte[] uploadBytes = file.getBytes();
            com.qiniu.http.Response response = uploadManager.put(uploadBytes, key, upToken);
            if (!response.isOK()) {
                throw new BizException(BizCode.INTERNAL_ERROR, "文件上传到云存储失败");
            }

            // 拼接访问URL
            String domain = qiniuDomain.endsWith("/") ? qiniuDomain : qiniuDomain + "/";
            return domain + key;
        } catch (Exception e) {
            log.error("七牛云上传失败", e);
            throw new BizException(BizCode.INTERNAL_ERROR, "文件上传失败，请重试");
        }
    }
}
