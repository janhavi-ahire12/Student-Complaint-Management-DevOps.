import { useEffect, useState } from "react";
import api, { getErrorMessage } from "../services/api.js";

export default function StudentProfile() {
  const [profile, setProfile] = useState(null);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  useEffect(() => {
    api.get("/students/me")
      .then((res) => setProfile(res.data))
      .catch((err) => setError(getErrorMessage(err)));
  }, []);

  const handleChange = (e) => setProfile({ ...profile, [e.target.name]: e.target.value });

  const handleSave = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");
    try {
      const body = {
        name: profile.name,
        phone: profile.phone,
        studentId: profile.studentId,
        course: profile.course,
        year: Number(profile.year),
        department: profile.department,
      };
      const res = await api.put(`/students/${profile.id}`, body);
      setProfile(res.data);
      // keep the name shown in the navbar up to date
      const user = JSON.parse(localStorage.getItem("user"));
      localStorage.setItem("user", JSON.stringify({ ...user, name: res.data.name }));
      setSuccess("Profile updated successfully.");
    } catch (err) {
      setError(getErrorMessage(err));
    }
  };

  if (!profile) return <div className="card">{error ? <div className="alert alert-error">{error}</div> : "Loading..."}</div>;

  return (
    <>
      <div className="page-head"><h1>My profile</h1></div>
      <form className="card" onSubmit={handleSave}>
        {error && <div className="alert alert-error">{error}</div>}
        {success && <div className="alert alert-success">{success}</div>}

        <div className="form-grid">
          <div>
            <label>Full name</label>
            <input name="name" value={profile.name || ""} onChange={handleChange} required />
          </div>
          <div>
            <label>Email (cannot be changed)</label>
            <input value={profile.email} disabled />
          </div>
          <div>
            <label>Phone</label>
            <input name="phone" value={profile.phone || ""} onChange={handleChange} maxLength={15} />
          </div>
          <div>
            <label>Student ID</label>
            <input name="studentId" value={profile.studentId || ""} onChange={handleChange} required />
          </div>
          <div>
            <label>Course</label>
            <input name="course" value={profile.course || ""} onChange={handleChange} required />
          </div>
          <div>
            <label>Year</label>
            <select name="year" value={profile.year} onChange={handleChange}>
              {[1, 2, 3, 4].map((y) => (<option key={y} value={y}>{y}</option>))}
            </select>
          </div>
          <div>
            <label>Department</label>
            <input name="department" value={profile.department || ""} onChange={handleChange} />
          </div>
        </div>
        <button className="btn btn-primary">Save changes</button>
      </form>
    </>
  );
}
