import axios from "axios";
import { BASE_URL } from "./apiPaths";
import toast from "react-hot-toast";

const axiosInstance = axios.create({
  baseURL: BASE_URL,
  timeout: 80000, // 80 seconds timeout
  headers: {
    "Content-Type": "application/json",
    Accept: "application/json",
  },
});

// request interceptor
axiosInstance.interceptors.request.use(
  (config) => {
    // Do not attach token for login, register, or refresh
    if (
      config.url.includes("/api/auth/login") ||
      config.url.includes("/api/auth/register") ||
      config.url.includes("/api/auth/refresh")
    ) {
      return config;
    }
    const token = localStorage.getItem("token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// response interceptor with refresh logic
axiosInstance.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    if (
      error.response &&
      error.response.status === 401 &&
      !originalRequest._retry
    ) {
      originalRequest._retry = true;
      const refreshToken = localStorage.getItem("refreshToken");
      if (!refreshToken) {
        // Log out and redirect to login if no refresh token
        localStorage.removeItem("token");
        localStorage.removeItem("refreshToken");
        localStorage.removeItem("user");
        toast.error("Session expired. Please log in again.");
        setTimeout(() => {
          window.location.href = "/login";
        }, 1500);
        return Promise.reject(new Error("No refresh token"));
      }
      try {
        // Use absolute URL for refresh if needed
        const res = await axios.post(`${BASE_URL}/api/auth/refresh`, {
          refreshToken,
        });
        const { token, refreshToken: newRefreshToken, user } = res.data;
        localStorage.setItem("token", token);
        if (newRefreshToken) {
          localStorage.setItem("refreshToken", newRefreshToken);
        }
        if (user) {
          localStorage.setItem("user", JSON.stringify(user));
        }
        originalRequest.headers.Authorization = `Bearer ${token}`;
        return axiosInstance(originalRequest);
      } catch (refreshError) {
        // Log out and redirect to login on refresh failure
        localStorage.removeItem("token");
        localStorage.removeItem("refreshToken");
        localStorage.removeItem("user");
        toast.error("Session expired. Please log in again.");
        setTimeout(() => {
          window.location.href = "/login";
        }, 1500);
        return Promise.reject(refreshError);
      }
    }
    return Promise.reject(error);
  }
);

export default axiosInstance;
