export interface Ban {
  id: string;

  userId: string;
  username?: string;

  chatId: string;
  chatName?: string;

  banReason: string;

  messageId?: string;
  messageContent?: string;

  moderatorId: string;
  moderatorName?: string;

  bannedAt?: string | Date;
  expiresAt?: string | Date;
}
