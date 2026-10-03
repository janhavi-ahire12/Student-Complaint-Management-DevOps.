import StatusBadge from "./StatusBadge.jsx";
import { formatDate } from "../services/utils.js";

// Shows complaint details + status history (used by student, admin and staff pages)
export default function ComplaintView({ complaint, updates }) {
  return (
    <>
      <div className="card">
        <div className="card-head">
          <h2>
            #{complaint.id} - {complaint.title}
          </h2>
          <StatusBadge status={complaint.status} />
        </div>

        <div className="detail-grid">
          <div><span className="muted">Category</span><p>{complaint.category}</p></div>
          <div><span className="muted">Priority</span><p><span className={`prio prio-${complaint.priority}`}>{complaint.priority}</span></p></div>
          <div><span className="muted">Submitted by</span><p>{complaint.studentName} ({complaint.studentCode})</p></div>
          <div><span className="muted">Assigned to</span><p>{complaint.assignedToName || "Not assigned yet"}</p></div>
          <div><span className="muted">Submitted on</span><p>{formatDate(complaint.createdAt)}</p></div>
          <div><span className="muted">Resolved on</span><p>{formatDate(complaint.resolvedAt)}</p></div>
        </div>

        <h4>Description</h4>
        <p className="pre">{complaint.description}</p>

        <h4>Resolution remarks</h4>
        <p className="pre">{complaint.resolutionRemarks || "No resolution remarks yet."}</p>
      </div>

      <div className="card">
        <h3>Status history</h3>
        {updates.length === 0 ? (
          <p className="muted">No updates yet.</p>
        ) : (
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Date</th>
                  <th>Updated by</th>
                  <th>Status change</th>
                  <th>Remarks</th>
                </tr>
              </thead>
              <tbody>
                {updates.map((u) => (
                  <tr key={u.id}>
                    <td>{formatDate(u.updatedAt)}</td>
                    <td>{u.updatedByName} <small className="muted">({u.updatedByRole})</small></td>
                    <td>
                      {u.oldStatus ? <StatusBadge status={u.oldStatus} /> : <span className="muted">-</span>}
                      {" \u2192 "}
                      {u.newStatus ? <StatusBadge status={u.newStatus} /> : "-"}
                    </td>
                    <td>{u.remarks || "-"}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </>
  );
}
