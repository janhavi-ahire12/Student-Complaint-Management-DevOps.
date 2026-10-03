import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import api, { getErrorMessage } from "../services/api.js";
import { getUser } from "../services/auth.js";
import ComplaintView from "../components/ComplaintView.jsx";

// Used by STUDENT (own complaints) and ADMIN (any complaint)
export default function ComplaintDetails() {
  const { id } = useParams();
  const user = getUser();
  const [complaint, setComplaint] = useState(null);
  const [updates, setUpdates] = useState([]);
  const [error, setError] = useState("");

  useEffect(() => {
    Promise.all([api.get(`/complaints/${id}`), api.get(`/complaints/${id}/updates`)])
      .then(([c, u]) => {
        setComplaint(c.data);
        setUpdates(u.data);
      })
      .catch((err) => setError(getErrorMessage(err)));
  }, [id]);

  const backPath = user.role === "ADMIN" ? "/admin/complaints" : "/student/complaints";

  return (
    <>
      <div className="page-head">
        <h1>Complaint details</h1>
        <Link to={backPath} className="btn btn-outline">Back to list</Link>
      </div>
      {error && <div className="alert alert-error">{error}</div>}
      {complaint && <ComplaintView complaint={complaint} updates={updates} />}
    </>
  );
}
