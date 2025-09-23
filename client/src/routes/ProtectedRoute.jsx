import React from "react";
import { Navigate, Outlet, useLocation } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const ProtectedRoute = ({ requiredRole }) => {
  const { user, loading } = useAuth();
  const location = useLocation();

  // Wait for auth state to finish loading
  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  // If not logged in, redirect to login
  if (!user) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  // If user is admin but not on admin route, redirect to admin dashboard
  if (user.role === "admin" && requiredRole !== "admin") {
    return <Navigate to="/admin-dashboard" replace />;
  }

  // If role is required and user doesn't have it, redirect to home
  if (requiredRole && user.role !== requiredRole) {
    return <Navigate to="/" replace />;
  }

  // User is authenticated and has correct role, render protected content
  return <Outlet />;
};

export default ProtectedRoute;