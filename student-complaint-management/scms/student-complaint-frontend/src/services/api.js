import axios from "axios";

// Spring Boot backend
const api = axios.create({
  baseURL: "http://localhost:8081/api",
});

// Attach JWT to every request
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// If the token expired / is invalid, go back to login
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const isAuthCall = error.config && error.config.url && error.config.url.includes("/auth/");
    if (error.response && error.response.status === 401 && !isAuthCall) {
      localStorage.clear();
      window.location.href = "/login";
    }
    return Promise.reject(error);
  }
);

export function getErrorMessage(error) {
  if (error.response && error.response.data && error.response.data.message) {
    return error.response.data.message;
  }
  if (error.request) {
    return "Cannot reach the server. Is the backend running on port 8081?";
  }
  return "Something went wrong. Please try again.";
}

export default api;
