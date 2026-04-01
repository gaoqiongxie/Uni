package com.uni.common;

import lombok.Data;

import java.util.List;

/**
 * 通用分页结果
 */
@Data
public class PageResult<T> {

    private List<T> records;
    private long total;
    private int page;
    private int size;

    public PageResult() {}

    public PageResult(List<T> records, long total, int page, int size) {
        this.records = records;
        this.total = total;
        this.page = page;
        this.size = size;
    }

    /**
     * 构建分页结果（MyBatis-Plus IPage 转 PageResult）
     */
    public static <T> PageResult<T> of(com.baomidou.mybatisplus.core.metadata.IPage<T> page) {
        PageResult<T> result = new PageResult<>();
        result.setRecords(page.getRecords());
        result.setTotal(page.getTotal());
        result.setPage((int) page.getCurrent());
        result.setSize((int) page.getSize());
        return result;
    }

    /**
     * 构建分页结果（手动分页）
     */
    public static <T> PageResult<T> of(List<T> allList, int page, int size) {
        long total = allList.size();
        int fromIndex = Math.min((page - 1) * size, allList.size());
        int toIndex = Math.min(fromIndex + size, allList.size());
        List<T> records = allList.subList(fromIndex, toIndex);
        return new PageResult<>(records, total, page, size);
    }
}
