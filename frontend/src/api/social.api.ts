import { request } from '@/utils/request';

export const addFriend = (friendId: number) => {
  return request({ url: '/api/social/friend/add', method: 'POST', data: { friendId } });
};

export const removeFriend = (friendId: number) => {
  return request({ url: '/api/social/friend/remove', method: 'POST', data: { friendId } });
};

export const getFriendList = () => {
  return request({ url: '/api/social/friend/list', method: 'GET' });
};

export const publishMoment = (data: { content: string; images?: string; type?: string }) => {
  return request({ url: '/api/social/moment/publish', method: 'POST', data });
};

export const getFriendMoments = (limit: number = 20) => {
  return request({ url: '/api/social/moment/list', method: 'GET', data: { limit } });
};

export const like = (targetType: string, targetId: number) => {
  return request({ url: '/api/social/like', method: 'POST', data: { targetType, targetId } });
};

export const unlike = (targetType: string, targetId: number) => {
  return request({ url: '/api/social/unlike', method: 'POST', data: { targetType, targetId } });
};

export const comment = (targetType: string, targetId: number, content: string) => {
  return request({ url: '/api/social/comment', method: 'POST', data: { targetType, targetId, content } });
};

export const getComments = (targetType: string, targetId: number) => {
  return request({ url: '/api/social/comment/list', method: 'GET', data: { targetType, targetId } });
};
