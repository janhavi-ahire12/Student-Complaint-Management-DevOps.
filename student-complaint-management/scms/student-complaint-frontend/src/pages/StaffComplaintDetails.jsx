import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import api, { getErrorMessage } from "../services/api.js";
import ComplaintView from "../components/ComplaintView.jsx";

// Staff can only move: ASSIGNED -> IN_PROGRESS -> RESOLVED
const NEXT_STATUS = {
  ASSIGNED: "IN_PROGRESS",
  IN_PROGRESS: "RESOLVED",
};

export default function StaffComplaintDetails() {
  const { id } = useParams();
  const [complaint, setComplaint] = useState(null);
  const [updates, setUpdates] = useState([]);
  const [remarks, setRemarks] = useState("");
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  const load = () => {
    Promise.all([api.get(`/complaints/${id}`), api.get(`/complaints/${id}/updates`)])
      .then(([c, u]) => {
        setComplaint(c.data);
        setUpdates(u.data);
      })
      .catch((err) => setError(getErrorMessage(err)));
  };

  useEffect(load, [id]);

  const nextStatus = complaint ? NEXT_STATUS[complaint.status] : null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setMessage("");
    try {
      await api.put(`/complaints/${id}/status`, { status: nextStatus, remarks });
      setMessage(`Status changed to ${nextStatus}.`);
      setRemarks("");
      load();
    } catch (err) {
      setError(getErrorMessage(err));
    }
  };

  return (
    <>
      <div className="page-head">
        <h1>Assigned complaint</h1>
        <Link to="/staff" className="btn btn-outline">Back to dashboard</Link>
      </div>

      {error && <div className="alert alert-error">{error}</div>}
      {message && <div className="alert alert-success">{message}</div>}

      {complaint && (
        <>
          <ComplaintView complaint={complaint} updates={updates} />

          <div className="card">
            <h3>Update status</h3>
            {nextStatus ? (
              <form onSubmit={handleSubmit}>
                <p>
                  Move this complaint from <strong>{complaint.status}</strong> to <strong>{nextStatus}</strong>.
                </p>
                <label>
                  {nextStatus === "RESOLVED" ? "Resolution remarks (required)" : "Remarks (optional)"}
                </label>
                <textarea
                  rows={4}
                  value={remarks}
                  onChange={(e) => setRemarks(e.target.value)}
                  required={nextStatus === "RESOLVED"}
                  placeholder={nextStatus === "RESOLVED" ? "Describe how the problem was fixed" : "Add a note for the student"}
                />
                <button className="btn btn-primary">
                  {nextStatus === "RESOLVED" ? "Mark as resolved" : "Start work"}
                </button>
              </form>
            ) : (
              <p className="muted">This complaint is {complaint.status}. No further action is needed.</p>
            )}
          </div>
        </>
      )}
    </>
  );
}
