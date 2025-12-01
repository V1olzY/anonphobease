import { ActionType } from "@/types/ActionType";

export interface Report {
  id: string;
  chatId: string;
  chatName: string;
  reportedUserId: string;
  reportedUsername: string;
  reporterUserId: string;
  reporterUsername: string;
  moderatorId?: string;
  moderatorName?: string;
  messageId?: string;
  messageContent?: string;
  reason: string;
  isResolved: boolean;
  createdAt: Date;
  resolvedAt?: Date;
  actionTaken?: ActionType;
  actionReason?: string;
}
