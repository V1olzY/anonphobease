import type { Phobia } from "./Phobia";
import type { Language } from "./Language";

export interface Chat {
  id: string;
  name: string;
  phobia: Phobia;
  language: Language;
}
