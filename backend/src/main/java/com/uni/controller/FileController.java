package com.uni.controller;

import com.uni.common.Result;
import com.uni.service.FileService;
import com.uni.vo.file.FileVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件上传控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
@Tag(name = "文件管理", description = "文件上传与管理")
public class FileController {

    private final FileService fileService;

    @Operation(summary = "上传图片")
    @PostMapping("/upload")
    public Result<FileVO> upload(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "attachmentType", defaultValue = "1") Integer attachmentType,
            @RequestParam(value = "uploadSource", defaultValue = "2") Integer uploadSource) {
        log.info("文件上传: userId={}, fileName={}, size={}", userId, file.getOriginalFilename(), file.getSize());
        FileVO result = fileService.uploadFile(file, userId, attachmentType, uploadSource);
        return Result.success(result);
    }

    @Operation(summary = "批量上传图片")
    @PostMapping("/upload/batch")
    public Result<List<FileVO>> uploadBatch(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "attachmentType", defaultValue = "1") Integer attachmentType,
            @RequestParam(value = "uploadSource", defaultValue = "2") Integer uploadSource) {
        log.info("批量上传: userId={}, count={}", userId, files.length);
        List<FileVO> results = new java.util.ArrayList<>();
        for (MultipartFile file : files) {
            results.add(fileService.uploadFile(file, userId, attachmentType, uploadSource));
        }
        return Result.success(results);
    }

    @Operation(summary = "获取文件URL列表")
    @GetMapping("/urls")
    public Result<List<String>> getFileUrls(@RequestParam String attachmentIds) {
        return Result.success(fileService.getFileUrls(attachmentIds));
    }

    @Operation(summary = "获取单个文件URL")
    @GetMapping("/url/{id}")
    public Result<String> getFileUrl(@PathVariable Long id) {
        String url = fileService.getFileUrl(id);
        return Result.success(url);
    }
}
