export interface Challenge {
  id: number;
  title: string;
  description: string;
  type: string;
  typeName: string;
  targetValue?: number;
  targetUnit?: string;
  durationDays: number;
  joinedCount: number;
}

export interface UserChallenge {
  id: number;
  challengeId: number;
  title: string;
  type: string;
  durationDays: number;
  status: number;
  statusName: string;
  startDate: string;
  endDate: string;
  checkinCount: number;
  streakDays: number;
  completeRate: number;
  todayChecked: boolean;
}

export interface ChallengeCheckin {
  id: number;
  challengeId: number;
  checkinDate: string;
  actualValue?: number;
  note?: string;
  photoUrl?: string;
}
