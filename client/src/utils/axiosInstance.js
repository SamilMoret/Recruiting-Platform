import axios from "axios";
import { BASE_URL } from "./apiPaths";

// Function to check if user has an active session
const hasActiveSession = () => {
  const token = localStorage.getItem("token");
  const user = localStorage.getItem("user");
  return !!(token && user);
};

// Function to handle session logout with specific messages
const handleSessionLogout = (
  message = "Session expired. Please log in again."
) => {
  // Clear all auth data
  localStorage.removeItem("token");
  localStorage.removeItem("refreshToken");
  localStorage.removeItem("user");

  // Show alert
  alert(message);

  // Redirect to login
  window.location.href = "/login";
};

const axiosInstance = axios.create({
  baseURL: BASE_URL,
  timeout: 80000, // 80 seconds timeout
  headers: {
    "Content-Type": "application/json",
    Accept: "application/json",
  },
});

// Request interceptor - adds auth token to requests
axiosInstance.interceptors.request.use(
  (config) => {
    // Normalize URL to avoid false positives with query params or absolute URLs
    const url = config.url.replace(BASE_URL, "");
    const isAuthRoute =
      url.startsWith("/api/auth/login") ||
      url.startsWith("/api/auth/register") ||
      url.startsWith("/api/auth/refresh");

    // Don't send Authorization header for auth routes
    if (isAuthRoute) {
      delete config.headers.Authorization;
      return config;
    }

    // Add token for all other routes
    const token = localStorage.getItem("token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor - handles token refresh, session expiration, and account status
axiosInstance.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // Don't handle errors for auth routes (login, register, refresh)
    const url = originalRequest.url.replace(BASE_URL, "");
    const isAuthRoute =
      url.startsWith("/api/auth/login") ||
      url.startsWith("/api/auth/register") ||
      url.startsWith("/api/auth/refresh");

    if (isAuthRoute) {
      // Let auth routes handle their own errors
      return Promise.reject(error);
    }

    // Check if user has active session for any error handling
    const hadActiveSession = hasActiveSession();

    // Handle 401 errors (token expired) for protected routes only
    if (
      error.response &&
      error.response.status === 401 &&
      !originalRequest._retry
    ) {
      originalRequest._retry = true;

      const refreshToken = localStorage.getItem("refreshToken");
      if (!refreshToken) {
        // No refresh token available
        if (hadActiveSession) {
          handleSessionLogout("Your session has expired. Please log in again.");
        }
        return Promise.reject(new Error("No refresh token"));
      }

      try {
        // Attempt to refresh the token
        const res = await axios.post(`${BASE_URL}/api/auth/refresh`, {
          refreshToken,
        });

        const { token, refreshToken: newRefreshToken, user } = res.data;

        // Update stored tokens and user data
        localStorage.setItem("token", token);
        if (newRefreshToken) {
          localStorage.setItem("refreshToken", newRefreshToken);
        }
        if (user) {
          localStorage.setItem("user", JSON.stringify(user));
        }

        // Retry the original request with new token
        originalRequest.headers.Authorization = `Bearer ${token}`;
        return axiosInstance(originalRequest);
      } catch (refreshError) {
        // Refresh failed - user had active session but it's now invalid
        if (hadActiveSession) {
          handleSessionLogout(
            "Your session has expired and could not be renewed. Please log in again."
          );
        }
        return Promise.reject(refreshError);
      }
    }

    // Handle account status and permission errors for users with active sessions
    if (error.response && hadActiveSession) {
      const { status, data } = error.response;

      // Handle 403 Forbidden - could be disabled account or access denied
      if (status === 403) {
        const errorMessage = data?.message || data?.error || "";

        // Check for account disabled/suspended messages
        if (
          errorMessage.toLowerCase().includes("disabled") ||
          errorMessage.toLowerCase().includes("suspended") ||
          errorMessage.toLowerCase().includes("deactivated") ||
          errorMessage.toLowerCase().includes("blocked") ||
          errorMessage.toLowerCase().includes("banned")
        ) {
          handleSessionLogout(
            "Your account has been disabled by an administrator. Please contact support for assistance."
          );
          return Promise.reject(error);
        }

        // Check for specific error codes that indicate account issues
        if (
          data?.errorCode === "ACCOUNT_DISABLED" ||
          data?.errorCode === "ACCOUNT_SUSPENDED" ||
          data?.errorCode === "USER_DEACTIVATED" ||
          data?.errorCode === "USER_BLOCKED"
        ) {
          handleSessionLogout(
            "Your account has been disabled by an administrator. Please contact support for assistance."
          );
          return Promise.reject(error);
        }

        // Generic access denied - could be role/permission issue
        handleSessionLogout(
          "Access denied. Your account permissions may have changed. Please log in again."
        );
        return Promise.reject(error);
      }

      // Handle account status checks in response data for any status code
      if (data) {
        // Check for account status indicators
        if (
          data.accountStatus === "DISABLED" ||
          data.accountStatus === "SUSPENDED" ||
          data.accountStatus === "DEACTIVATED" ||
          data.accountStatus === "BLOCKED"
        ) {
          handleSessionLogout(
            "Your account has been disabled by an administrator. Please contact support for assistance."
          );
          return Promise.reject(error);
        }

        // Check for user status in user object
        if (
          data.user?.status === "DISABLED" ||
          data.user?.status === "SUSPENDED" ||
          data.user?.isActive === false
        ) {
          handleSessionLogout(
            "Your account has been disabled by an administrator. Please contact support for assistance."
          );
          return Promise.reject(error);
        }
      }

      // Handle 422 (Unprocessable Entity) which might indicate account issues
      if (status === 422 && data?.message) {
        const message = data.message.toLowerCase();
        if (
          message.includes("account") &&
          (message.includes("disabled") || message.includes("suspended"))
        ) {
          handleSessionLogout(
            "Your account has been disabled by an administrator. Please contact support for assistance."
          );
          return Promise.reject(error);
        }
      }
    }

    // Don't handle server errors (500+) - let the app handle these
    if (error.response && error.response.status >= 500) {
      console.error("Server error occurred:", error);
    }

    return Promise.reject(error);
  }
);

export default axiosInstance;
