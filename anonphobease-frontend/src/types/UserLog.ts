import { LogType } from "@/types/LogType";
import { RelatedEntityType } from "@/types/RelatedEntityType";

export interface UserLog {
  id: string;
  userId: string;
  userName: string;
  logType: LogType;
  createdAt: string;
  details: string;
  relatedEntityId: string;
  relatedEntityType: RelatedEntityType;
  relatedEntityName: string;
  relatedEntityExtra: string;
}
