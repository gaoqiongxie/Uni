export interface Moment {
  id: number;
  userId: number;
  nickname?: string;
  avatarUrl?: string;
  content: string;
  images?: string;
  type?: string;
  likeCount: number;
  commentCount: number;
  liked?: boolean;
  createTime: string;
}

export interface Comment {
  id: number;
  userId: number;
  nickname?: string;
  avatarUrl?: string;
  content: string;
  createTime: string;
}

export interface Friend {
  id: number;
  userId: number;
  nickname: string;
  avatarUrl?: string;
}
