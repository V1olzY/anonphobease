import { ChatDTO } from "@/types/ChatDTO";
import { Phobia } from "@/types/Phobia";
import { Language } from "@/types/Language";

export interface ChatsResponse {
  chats: ChatDTO[];
  phobias: Phobia[];
  languages: Language[];
}
