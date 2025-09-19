import axios from "axios";
import { BASE_URL } from "./apiPaths";

const axiosInstance = axios.create({
  baseURL: BASE_URL,
  timeout: 80000, //80 seconds timeout
  headers: {
    "Content-Type": "application/json",
    Accept: "application/json",
  },
});

// request interceptor
axiosInstance.interceptors.request.use(
  (config) => {
    const accessToken = localStorage.getItem("token");
    if (accessToken) {
      config.headers.Authorization = `Bearer ${accessToken}`;
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
    // Don't try to refresh token on login or register endpoints
    if (
      originalRequest.url.includes("/api/auth/login") ||
      originalRequest.url.includes("/api/auth/register")
    ) {
      return Promise.reject(error);
    }
    if (
      error.response &&
      error.response.status === 401 &&
      !originalRequest._retry
    ) {
      originalRequest._retry = true;
      try {
        const refreshToken = localStorage.getItem("refreshToken");
        if (!refreshToken) throw new Error("No refresh token");

        // Call your refresh endpoint
        const res = await axios.post(`${BASE_URL}/auth/refresh`, {
          refreshToken,
        });

        const { token, refreshToken: newRefreshToken, user } = res.data;
        localStorage.setItem("token", token);
        if (newRefreshToken)
          localStorage.setItem("refreshToken", newRefreshToken);
        if (user) localStorage.setItem("user", JSON.stringify(user));

        // Update Authorization header and retry original request
        originalRequest.headers.Authorization = `Bearer ${token}`;
        return axiosInstance(originalRequest);
      } catch (refreshError) {
        // Refresh failed, log out
        localStorage.removeItem("token");
        localStorage.removeItem("refreshToken");
        localStorage.removeItem("user");
        alert("Session expired, please log in again.");
        window.location.href = "/";
        return Promise.reject(refreshError);
      }
    }
    return Promise.reject(error);
  }
);

export default axiosInstance;
