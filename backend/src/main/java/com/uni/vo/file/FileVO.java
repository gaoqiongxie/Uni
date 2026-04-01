package com.uni.vo.file;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 文件上传返回VO
 */
@Data
@Accessors(chain = true)
public class FileVO {

    /** 附件ID */
    private Long id;

    /** 文件名 */
    private String fileName;

    /** 文件大小(字节) */
    private Long fileSize;

    /** 文件类型 */
    private String fileType;

    /** 访问URL */
    private String url;

    /** 缩略图URL */
    private String thumbnailUrl;
}
