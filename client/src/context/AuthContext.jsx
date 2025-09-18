import React, {createContext, useContext, useState, useEffect, use} from "react";
import { Navigate, Outlet, useLocation } from "react-router-dom";

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