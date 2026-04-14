import { request } from '@/utils/request';
import type { Challenge, UserChallenge } from '@/types/challenge';

export const challengeApi = {
  getList: () => request.get<Challenge[]>('/api/challenge/list'),
  join: (challengeId: number) => request.post(`/api/challenge/join/${challengeId}`),
  getMyChallenges: () => request.get<UserChallenge[]>('/api/challenge/my'),
  checkin: (challengeId: number, note?: string) => request.post(`/api/challenge/checkin/${challengeId}`, null, { params: { note } }),
};
