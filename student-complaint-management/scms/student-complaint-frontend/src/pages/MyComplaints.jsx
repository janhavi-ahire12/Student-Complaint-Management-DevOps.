import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api, { getErrorMessage } from "../services/api.js";
import StatusBadge from "../components/StatusBadge.jsx";
import { STATUSES, formatDate } from "../services/utils.js";

export default function MyComplaints() {
  const [complaints, setComplaints] = useState([]);
  const [filter, setFilter] = useState("ALL");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get("/complaints")
      .then((res) => setComplaints(res.data))
      .catch((err) => setError(getErrorMessage(err)))
      .finally(() => setLoading(false));
  }, []);

  const shown = filter === "ALL" ? complaints : complaints.filter((c) => c.status === filter);

  return (
    <>
      <div className="page-head">
        <h1>My complaints</h1>
        <div className="btn-row">
          <select value={filter} onChange={(e) => setFilter(e.target.value)}>
            <option value="ALL">All statuses</option>
            {STATUSES.map((s) => (<option key={s} value={s}>{s}</option>))}
          </select>
          <Link to="/student/submit" className="btn btn-primary">New complaint</Link>
        </div>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      <div className="card table-wrap">
        {loading ? (
          <p>Loading...</p>
        ) : shown.length === 0 ? (
          <p className="muted">No complaints found. Use "New complaint" to submit one.</p>
        ) : (
          <table>
            <thead>
              <tr>
                <th>ID</th><th>Title</th><th>Category</th><th>Priority</th><th>Status</th><th>Submitted</th><th></th>
              </tr>
            </thead>
            <tbody>
              {shown.map((c) => (
                <tr key={c.id}>
                  <td>#{c.id}</td>
                  <td>{c.title}</td>
                  <td>{c.category}</td>
                  <td><span className={`prio prio-${c.priority}`}>{c.priority}</span></td>
                  <td><StatusBadge status={c.status} /></td>
                  <td>{formatDate(c.createdAt)}</td>
                  <td><Link to={`/student/complaints/${c.id}`} className="btn btn-outline btn-sm">View</Link></td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </>
  );
}
