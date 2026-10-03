import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api, { getErrorMessage } from "../services/api.js";
import { CATEGORIES, PRIORITIES } from "../services/utils.js";

export default function SubmitComplaint() {
  const navigate = useNavigate();
  const [form, setForm] = useState({ title: "", description: "", category: "ELECTRICAL", priority: "MEDIUM" });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      const res = await api.post("/complaints", form);
      navigate(`/student/complaints/${res.data.id}`);
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <div className="page-head"><h1>Submit a complaint</h1></div>
      <form className="card" onSubmit={handleSubmit}>
        {error && <div className="alert alert-error">{error}</div>}

        <label>Title</label>
        <input name="title" value={form.title} onChange={handleChange} maxLength={150} placeholder="Short summary of the problem" required />

        <div className="form-grid">
          <div>
            <label>Category</label>
            <select name="category" value={form.category} onChange={handleChange}>
              {CATEGORIES.map((c) => (<option key={c} value={c}>{c}</option>))}
            </select>
          </div>
          <div>
            <label>Priority</label>
            <select name="priority" value={form.priority} onChange={handleChange}>
              {PRIORITIES.map((p) => (<option key={p} value={p}>{p}</option>))}
            </select>
          </div>
        </div>

        <label>Description</label>
        <textarea name="description" rows={6} value={form.description} onChange={handleChange} placeholder="Explain what happened, where, and since when" required />

        <button className="btn btn-primary" disabled={loading}>
          {loading ? "Submitting..." : "Submit complaint"}
        </button>
      </form>
    </>
  );
}
