import { format } from "date-fns";

export function formatDate(dateStr: string) {
  if (!dateStr) return "";
  return format(new Date(dateStr), "dd.MM.yyyy HH:mm:ss");
}
