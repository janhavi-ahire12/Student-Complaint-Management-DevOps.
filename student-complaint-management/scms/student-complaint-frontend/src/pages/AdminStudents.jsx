import { useEffect, useState } from "react";
import api, { getErrorMessage } from "../services/api.js";
import { formatDate } from "../services/utils.js";

export default function AdminStudents() {
  const [students, setStudents] = useState([]);
  const [search, setSearch] = useState("");
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  const load = () => {
    api.get("/students")
      .then((res) => setStudents(res.data))
      .catch((err) => setError(getErrorMessage(err)));
  };

  useEffect(load, []);

  const handleDelete = async (student) => {
    if (!window.confirm(`Delete student ${student.name}?`)) return;
    setError("");
    setMessage("");
    try {
      await api.delete(`/students/${student.id}`);
      setMessage("Student deleted.");
      load();
    } catch (err) {
      setError(getErrorMessage(err));
    }
  };

  const q = search.trim().toLowerCase();
  const shown = students.filter(
    (s) => s.name.toLowerCase().includes(q) || s.studentId.toLowerCase().includes(q) || s.email.toLowerCase().includes(q)
  );

  return (
    <>
      <div className="page-head">
        <h1>Students</h1>
        <input
          className="search"
          placeholder="Search by name, ID or email"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
      </div>

      {error && <div className="alert alert-error">{error}</div>}
      {message && <div className="alert alert-success">{message}</div>}

      <div className="card table-wrap">
        {shown.length === 0 ? (
          <p className="muted">No students found.</p>
        ) : (
          <table>
            <thead>
              <tr>
                <th>ID</th><th>Student ID</th><th>Name</th><th>Email</th><th>Phone</th>
                <th>Course</th><th>Year</th><th>Department</th><th>Registered</th><th></th>
              </tr>
            </thead>
            <tbody>
              {shown.map((s) => (
                <tr key={s.id}>
                  <td>{s.id}</td>
                  <td>{s.studentId}</td>
                  <td>{s.name}</td>
                  <td>{s.email}</td>
                  <td>{s.phone || "-"}</td>
                  <td>{s.course}</td>
                  <td>{s.year}</td>
                  <td>{s.department || "-"}</td>
                  <td>{formatDate(s.createdAt)}</td>
                  <td><button className="btn btn-danger btn-sm" onClick={() => handleDelete(s)}>Delete</button></td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </>
  );
}
