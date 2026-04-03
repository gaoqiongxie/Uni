/**
 * 社交功能类型定义
 */

/** 动态 */
export interface Moment {
  id: number
  userId: number
  userNickname: string
  userAvatar?: string
  content: string
  images?: string[]
  likeCount: number
  commentCount: number
  isLiked: boolean
  shareWeight?: number
  shareExercise?: number
  weightValue?: number
  exerciseMinutes?: number
  exerciseCalories?: number
  createTime: string
}

/** 评论 */
export interface MomentComment {
  id: number
  momentId: number
  userId: number
  userNickname: string
  userAvatar?: string
  content: string
  parentId?: number
  parentUserNickname?: string
  likeCount: number
  isLiked: boolean
  createTime: string
  children?: MomentComment[]
}

/** 用户关注 */
export interface UserFollow {
  userId: number
  nickname: string
  avatar?: string
  isFollowing: boolean
  followingCount?: number
  followerCount?: number
  momentCount?: number
}

/** 发布动态参数 */
export interface PublishMomentParams {
  content: string
  images?: string
  shareWeight?: number
  shareExercise?: number
}

/** 发表评论参数 */
export interface AddCommentParams {
  content: string
  parentId?: number
}
