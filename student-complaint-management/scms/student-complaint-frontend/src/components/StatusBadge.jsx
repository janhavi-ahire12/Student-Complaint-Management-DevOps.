import { prettyStatus } from "../services/utils.js";

export default function StatusBadge({ status }) {
  return <span className={`badge badge-${status}`}>{prettyStatus(status)}</span>;
}
