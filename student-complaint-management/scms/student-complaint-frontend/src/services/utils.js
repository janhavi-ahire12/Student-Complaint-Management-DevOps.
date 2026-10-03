export const CATEGORIES = ["ACADEMIC", "HOSTEL", "ELECTRICAL", "CLEANING", "MAINTENANCE", "OTHER"];
export const PRIORITIES = ["LOW", "MEDIUM", "HIGH"];
export const STATUSES = ["OPEN", "ASSIGNED", "IN_PROGRESS", "RESOLVED", "REJECTED"];

export function formatDate(value) {
  if (!value) return "-";
  return new Date(value).toLocaleString();
}

export function prettyStatus(status) {
  return status ? status.replace("_", " ") : "-";
}
