import { request } from '@/utils/request';

export const getChallenges = () => {
  return request({ url: '/api/challenge/list', method: 'GET' });
};

export const getMyChallenges = () => {
  return request({ url: '/api/challenge/my', method: 'GET' });
};

export const joinChallenge = (challengeId: number) => {
  return request({ url: '/api/challenge/join', method: 'POST', data: { challengeId } });
};

export const checkin = (data: { challengeId: number; actualValue?: number; note?: string; photoUrl?: string }) => {
  return request({ url: '/api/challenge/checkin', method: 'POST', data });
};

export const getCheckinList = (challengeId: number) => {
  return request({ url: '/api/challenge/checkin/list', method: 'GET', data: { challengeId } });
};
