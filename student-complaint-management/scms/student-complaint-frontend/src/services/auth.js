// Very simple auth helpers - JWT and user info are kept in localStorage.

export function saveAuth(data) {
  localStorage.setItem("token", data.token);
  localStorage.setItem(
    "user",
    JSON.stringify({
      userId: data.userId,
      name: data.name,
      email: data.email,
      role: data.role,
      profileId: data.profileId,
    })
  );
}

export function getUser() {
  const raw = localStorage.getItem("user");
  if (!raw || !localStorage.getItem("token")) return null;
  try {
    return JSON.parse(raw);
  } catch (e) {
    return null;
  }
}

export function logout() {
  localStorage.removeItem("token");
  localStorage.removeItem("user");
}

export function homePathForRole(role) {
  if (role === "ADMIN") return "/admin";
  if (role === "STAFF") return "/staff";
  return "/student";
}
