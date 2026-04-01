package com.uni.service;

import com.uni.vo.file.FileVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务接口
 */
public interface FileService {

    /**
     * 上传文件（本地存储模式）
     *
     * @param file          文件
     * @param userId        用户ID
     * @param attachmentType 附件类型(1.餐食图片,2.体重照片,3.运动照片,4.其他)
     * @param uploadSource  上传来源(1.手机拍照,2.相册选择)
     * @return 文件VO
     */
    FileVO uploadFile(MultipartFile file, Long userId, Integer attachmentType, Integer uploadSource);

    /**
     * 根据附件ID列表获取文件URL列表
     *
     * @param attachmentIds 附件ID(逗号分隔)
     * @return URL列表
     */
    java.util.List<String> getFileUrls(String attachmentIds);

    /**
     * 根据附件ID获取文件URL
     *
     * @param attachmentId 附件ID
     * @return URL
     */
    String getFileUrl(Long attachmentId);
}
