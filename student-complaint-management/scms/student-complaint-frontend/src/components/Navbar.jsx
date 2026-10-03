import { NavLink, useNavigate } from "react-router-dom";
import { getUser, logout } from "../services/auth.js";

const LINKS = {
  STUDENT: [
    { to: "/student", label: "Dashboard", end: true },
    { to: "/student/submit", label: "Submit Complaint" },
    { to: "/student/complaints", label: "My Complaints" },
    { to: "/student/profile", label: "Profile" },
  ],
  ADMIN: [
    { to: "/admin", label: "Dashboard", end: true },
    { to: "/admin/complaints", label: "Complaints" },
    { to: "/admin/students", label: "Students" },
  ],
  STAFF: [{ to: "/staff", label: "Dashboard", end: true }],
};

export default function Navbar() {
  const navigate = useNavigate();
  const user = getUser();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <header className="navbar">
      <div className="navbar-inner">
        <div className="brand">Complaint Desk</div>
        <nav className="nav-links">
          {(LINKS[user?.role] || []).map((l) => (
            <NavLink key={l.to} to={l.to} end={l.end} className={({ isActive }) => (isActive ? "active" : "")}>
              {l.label}
            </NavLink>
          ))}
        </nav>
        <div className="nav-user">
          <span>
            {user?.name} <small>({user?.role})</small>
          </span>
          <button className="btn btn-outline btn-sm" onClick={handleLogout}>
            Logout
          </button>
        </div>
      </div>
    </header>
  );
}
