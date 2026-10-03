import { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import api, { getErrorMessage } from "../services/api.js";
import { saveAuth, homePathForRole } from "../services/auth.js";

export default function Login() {
  const navigate = useNavigate();
  const location = useLocation();
  const [form, setForm] = useState({ email: "", password: "" });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      const res = await api.post("/auth/login", form);
      saveAuth(res.data);
      navigate(homePathForRole(res.data.role)); // STUDENT / ADMIN / STAFF dashboards
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <form className="auth-card" onSubmit={handleSubmit}>
        <h1>Complaint Desk</h1>
        <p className="muted">Sign in to your account</p>

        {location.state?.message && <div className="alert alert-success">{location.state.message}</div>}
        {error && <div className="alert alert-error">{error}</div>}

        <label>Email</label>
        <input type="email" name="email" value={form.email} onChange={handleChange} placeholder="you@example.com" required />

        <label>Password</label>
        <input type="password" name="password" value={form.password} onChange={handleChange} required />

        <button className="btn btn-primary btn-block" disabled={loading}>
          {loading ? "Signing in..." : "Login"}
        </button>

        <p className="center">
          New student? <Link to="/register">Create an account</Link>
        </p>
      </form>
    </div>
  );
}
