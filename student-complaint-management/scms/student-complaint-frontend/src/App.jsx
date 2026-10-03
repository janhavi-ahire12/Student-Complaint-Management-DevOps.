import { Routes, Route, Navigate } from "react-router-dom";
import ProtectedRoute from "./components/ProtectedRoute.jsx";

import Login from "./pages/Login.jsx";
import Register from "./pages/Register.jsx";
import StudentDashboard from "./pages/StudentDashboard.jsx";
import StudentProfile from "./pages/StudentProfile.jsx";
import SubmitComplaint from "./pages/SubmitComplaint.jsx";
import MyComplaints from "./pages/MyComplaints.jsx";
import ComplaintDetails from "./pages/ComplaintDetails.jsx";
import AdminDashboard from "./pages/AdminDashboard.jsx";
import AdminComplaints from "./pages/AdminComplaints.jsx";
import AdminStudents from "./pages/AdminStudents.jsx";
import StaffDashboard from "./pages/StaffDashboard.jsx";
import StaffComplaintDetails from "./pages/StaffComplaintDetails.jsx";

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/login" replace />} />
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />

      {/* STUDENT */}
      <Route path="/student" element={<ProtectedRoute role="STUDENT"><StudentDashboard /></ProtectedRoute>} />
      <Route path="/student/profile" element={<ProtectedRoute role="STUDENT"><StudentProfile /></ProtectedRoute>} />
      <Route path="/student/submit" element={<ProtectedRoute role="STUDENT"><SubmitComplaint /></ProtectedRoute>} />
      <Route path="/student/complaints" element={<ProtectedRoute role="STUDENT"><MyComplaints /></ProtectedRoute>} />
      <Route path="/student/complaints/:id" element={<ProtectedRoute role="STUDENT"><ComplaintDetails /></ProtectedRoute>} />

      {/* ADMIN */}
      <Route path="/admin" element={<ProtectedRoute role="ADMIN"><AdminDashboard /></ProtectedRoute>} />
      <Route path="/admin/complaints" element={<ProtectedRoute role="ADMIN"><AdminComplaints /></ProtectedRoute>} />
      <Route path="/admin/complaints/:id" element={<ProtectedRoute role="ADMIN"><ComplaintDetails /></ProtectedRoute>} />
      <Route path="/admin/students" element={<ProtectedRoute role="ADMIN"><AdminStudents /></ProtectedRoute>} />

      {/* STAFF */}
      <Route path="/staff" element={<ProtectedRoute role="STAFF"><StaffDashboard /></ProtectedRoute>} />
      <Route path="/staff/complaints/:id" element={<ProtectedRoute role="STAFF"><StaffComplaintDetails /></ProtectedRoute>} />

      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
}
