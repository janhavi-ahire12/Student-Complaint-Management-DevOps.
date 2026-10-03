import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api, { getErrorMessage } from "../services/api.js";

export default function Register() {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    name: "",
    email: "",
    password: "",
    phone: "",
    studentId: "",
    course: "",
    year: 1,
    department: "",
  });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      await api.post("/auth/register", { ...form, year: Number(form.year) });
      navigate("/login", { state: { message: "Registration successful. Please login." } });
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <form className="auth-card wide" onSubmit={handleSubmit}>
        <h1>Student registration</h1>
        <p className="muted">Create your account to submit complaints</p>

        {error && <div className="alert alert-error">{error}</div>}

        <div className="form-grid">
          <div>
            <label>Full name</label>
            <input name="name" value={form.name} onChange={handleChange} required />
          </div>
          <div>
            <label>Email</label>
            <input type="email" name="email" value={form.email} onChange={handleChange} required />
          </div>
          <div>
            <label>Password (min 6 characters)</label>
            <input type="password" name="password" value={form.password} onChange={handleChange} minLength={6} required />
          </div>
          <div>
            <label>Phone</label>
            <input name="phone" value={form.phone} onChange={handleChange} maxLength={15} />
          </div>
          <div>
            <label>Student ID</label>
            <input name="studentId" value={form.studentId} onChange={handleChange} placeholder="CSE2024002" required />
          </div>
          <div>
            <label>Course</label>
            <input name="course" value={form.course} onChange={handleChange} placeholder="B.E. CSE (AI & ML)" required />
          </div>
          <div>
            <label>Year</label>
            <select name="year" value={form.year} onChange={handleChange}>
              {[1, 2, 3, 4].map((y) => (
                <option key={y} value={y}>{y}</option>
              ))}
            </select>
          </div>
          <div>
            <label>Department</label>
            <input name="department" value={form.department} onChange={handleChange} />
          </div>
        </div>

        <button className="btn btn-primary btn-block" disabled={loading}>
          {loading ? "Creating account..." : "Register"}
        </button>
        <p className="center">
          Already registered? <Link to="/login">Login</Link>
        </p>
      </form>
    </div>
  );
}
