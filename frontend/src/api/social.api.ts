import { request } from '@/utils/request'
import type { Moment, MomentComment, UserFollow, PublishMomentParams, AddCommentParams } from '@/types/social'
import type { PageResult } from '@/types/common'

/**
 * 社交功能 API
 */

/** 发布动态 */
export function publishMoment(params: PublishMomentParams) {
  return request.post<number>('/social/moment', params)
}

/** 删除动态 */
export function deleteMoment(momentId: number) {
  return request.delete<void>(`/social/moment/${momentId}`)
}

/** 获取动态列表(社区) */
export function getMomentList(page = 1, size = 20) {
  return request.get<PageResult<Moment>>('/social/moments', { page, size })
}

/** 获取我关注的动态 */
export function getFollowingMoments(page = 1, size = 20) {
  return request.get<PageResult<Moment>>('/social/moments/following', { page, size })
}

/** 获取用户动态 */
export function getUserMoments(userId: number, page = 1, size = 20) {
  return request.get<PageResult<Moment>>(`/social/user/${userId}/moments`, { page, size })
}

/** 获取动态详情 */
export function getMomentDetail(momentId: number) {
  return request.get<Moment>(`/social/moment/${momentId}`)
}

/** 点赞/取消点赞 */
export function toggleLike(momentId: number) {
  return request.post<boolean>(`/social/moment/${momentId}/like`)
}

/** 发表评论 */
export function addComment(momentId: number, params: AddCommentParams) {
  return request.post<number>(`/social/moment/${momentId}/comment`, params)
}

/** 删除评论 */
export function deleteComment(commentId: number) {
  return request.delete<void>(`/social/comment/${commentId}`)
}

/** 获取评论列表 */
export function getMomentComments(momentId: number) {
  return request.get<MomentComment[]>(`/social/moment/${momentId}/comments`)
}

/** 关注/取消关注 */
export function toggleFollow(targetUserId: number) {
  return request.post<boolean>(`/social/user/${targetUserId}/follow`)
}

/** 获取关注列表 */
export function getFollowingList(userId: number) {
  return request.get<UserFollow[]>(`/social/user/${userId}/following`)
}

/** 获取粉丝列表 */
export function getFollowerList(userId: number) {
  return request.get<UserFollow[]>(`/social/user/${userId}/followers`)
}

/** 检查是否已关注 */
export function isFollowing(targetUserId: number) {
  return request.get<boolean>(`/social/user/${targetUserId}/is-following`)
}

/** 获取用户统计 */
export function getUserStats(userId: number) {
  return request.get<UserFollow>(`/social/user/${userId}/stats`)
}
