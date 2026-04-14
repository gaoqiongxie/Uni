export interface Challenge {
  id: number;
  title: string;
  description: string;
  type: string;
  targetValue: number;
  targetUnit: string;
  durationDays: number;
  coverUrl: string;
  joinedCount: number;
}

export interface UserChallenge {
  id: number;
  challengeId: number;
  title: string;
  type: string;
  status: number;
  startDate: string;
  endDate: string;
  durationDays: number;
  checkinCount: number;
  streakDays: number;
  completeRate: number;
  daysPassed: number;
  daysRemaining: number;
}
