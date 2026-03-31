package com.uni.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 附件表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_attachment")
public class AttachmentEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 用户id */
    private Long userId;

    /** 文件名 */
    private String fileName;

    /** 文件大小(字节) */
    private Long fileSize;

    /** 文件类型(image/jpeg等) */
    private String fileType;

    /** 文件扩展名 */
    private String fileExtension;

    /** 存储路径 */
    private String storagePath;

    /** OSS访问URL */
    private String ossUrl;

    /** 缩略图URL */
    private String thumbnailUrl;

    /** 附件类型(1.餐食图片,2.体重照片,3.运动照片,4.其他) */
    private Integer attachmentType;

    /** 上传来源(1.手机拍照,2.相册选择) */
    private Integer uploadSource;

    /** 创建人Ad */
    private String createUser;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 修改人Ad */
    private String updateUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime editTime;

    @TableLogic
    private Integer deleteFlag;
}
