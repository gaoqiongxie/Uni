package com.uni.controller;

import com.uni.common.Result;
import com.uni.dto.BodyCompositionDTO;
import com.uni.service.BodyCompositionService;
import com.uni.vo.BodyCompositionVO;
import com.uni.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 身体成分 Controller
 * Sprint 11 - 周一体脂率 & 身体成分追踪
 */
@RestController
@RequestMapping("/api/body-composition")
public class BodyCompositionController {

    private final BodyCompositionService bodyCompositionService;

    public BodyCompositionController(BodyCompositionService bodyCompositionService) {
        this.bodyCompositionService = bodyCompositionService;
    }

    /**
     * 记录身体成分
     * POST /api/body-composition/record
     */
    @PostMapping("/record")
    public Result<BodyCompositionVO> record(@RequestBody BodyCompositionDTO dto, HttpServletRequest request) {
        Long userId = getUserId(request);
        BodyCompositionVO vo = bodyCompositionService.record(dto, userId);
        return Result.success(vo);
    }

    /**
     * 获取今日身体成分
     * GET /api/body-composition/today
     */
    @GetMapping("/today")
    public Result<BodyCompositionVO> getTodayRecord(HttpServletRequest request) {
        Long userId = getUserId(request);
        BodyCompositionVO vo = bodyCompositionService.getTodayRecord(userId);
        return Result.success(vo);
    }

    /**
     * 获取最新身体成分
     * GET /api/body-composition/latest
     */
    @GetMapping("/latest")
    public Result<BodyCompositionVO> getLatestRecord(HttpServletRequest request) {
        Long userId = getUserId(request);
        BodyCompositionVO vo = bodyCompositionService.getLatestRecord(userId);
        return Result.success(vo);
    }

    /**
     * 获取历史身体成分列表
     * GET /api/body-composition/history?limit=30
     */
    @GetMapping("/history")
    public Result<List<BodyCompositionVO>> getHistoryList(
            @RequestParam(required = false) Integer limit,
            HttpServletRequest request) {
        Long userId = getUserId(request);
        List<BodyCompositionVO> list = bodyCompositionService.getHistoryList(userId, limit);
        return Result.success(list);
    }

    /**
     * 从请求中获取用户ID
     */
    private Long getUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return JwtUtil.getUserIdFromToken(token);
    }
}
