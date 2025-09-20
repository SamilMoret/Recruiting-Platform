import React, {createContext, useContext, useState, useEffect, use} from "react";
import { Navigate, Outlet, useLocation } from "react-router-dom";
import axios from "axios";

const AuthContext = createContext();

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error("useAuth must be used within an AuthProvider");
    }
    return context;
};

export const AuthProvider = ({children}) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [isAuthenticated, setIsAuthenticated] = useState(false);

    useEffect(() => {
        checkAuthStatus();
    }, []);
    
    const checkAuthStatus = async () => {
        try {
            const token = localStorage.getItem("token");
            const userStr = localStorage.getItem("user");
            if (token && userStr) {
                const userData = JSON.parse(userStr);
                setUser(userData);
                setIsAuthenticated(true);
            }
        } catch (error) {
            console.error("Error checking auth status:", error);
            logout();
        }finally {
            setLoading(false);
        }
    };

    const login = (userData, token) => {
        localStorage.setItem("token", token);
        localStorage.setItem("user", JSON.stringify(userData));

        setUser(userData);
        setIsAuthenticated(true);
    };

    const logout = () => { 
        localStorage.removeItem("token");
        localStorage.removeItem("refreshToken");
        localStorage.removeItem("user");
        
        setUser(null);
        setIsAuthenticated(false);
        window.location.href='/'
    };

    const updateUser = (updateUserData) => {
        const newUserdata = { ...user, ...updateUserData };
        localStorage.setItem("user", JSON.stringify(newUserdata));
        setUser(newUserdata);
    };

    const value = {
        user,
        setUser,
        loading,
        isAuthenticated,
        login,
        logout,
        updateUser,
        checkAuthStatus
    };

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
};

const ProtectedRoute = ({ requiredRole }) => {
  const { user, loading } = useAuth();
  const location = useLocation();

  // Debug logging
  console.log('ProtectedRoute user:', user, 'loading:', loading, 'requiredRole:', requiredRole);

  // Wait for auth state to finish loading
  if (loading) {
    return <div>Loading...</div>; // Or a spinner
  }

  // If not logged in, redirect to login
  if (!user) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  // If role is required and user does not have it, redirect to home
  if (requiredRole && user.role !== requiredRole) {
    return <Navigate to="/" replace />;
  }

  return <Outlet />;
};

const axiosInstance = axios.create({
  baseURL: "http://localhost:5000", // Replace with your API base URL
  timeout: 10000, // Request timeout
  headers: {
    "Content-Type": "application/json",
  },
});

axiosInstance.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
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
        console.error("Token refresh failed:", refreshError);
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