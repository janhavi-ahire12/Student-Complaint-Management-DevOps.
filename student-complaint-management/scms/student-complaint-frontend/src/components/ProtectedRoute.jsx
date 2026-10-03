import { Navigate } from "react-router-dom";
import Navbar from "./Navbar.jsx";
import { getUser, homePathForRole } from "../services/auth.js";

// Only lets a logged-in user with the correct role open the page
export default function ProtectedRoute({ role, children }) {
  const user = getUser();

  if (!user) return <Navigate to="/login" replace />;
  if (role && user.role !== role) return <Navigate to={homePathForRole(user.role)} replace />;

  return (
    <>
      <Navbar />
      <main className="container">{children}</main>
    </>
  );
}
