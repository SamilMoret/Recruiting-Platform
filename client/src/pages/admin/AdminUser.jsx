import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import AdminLayout from "../../components/layout/AdminLayout";
import axiosInstance from "../../utils/axiosInstance";
import { API_PATHS } from "../../utils/apiPaths";

const AdminUser = () => {
  const { t } = useTranslation();
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      setLoading(true);
      setError(null);
      console.log("=== FETCHING USERS ===");
      console.log("API Endpoint:", API_PATHS.ADMIN.GET_ALL_USERS);
      
      const response = await axiosInstance.get(API_PATHS.ADMIN.GET_ALL_USERS);
      
      console.log("=== RAW API RESPONSE ===");
      console.log("Full response object:", response);
      console.log("Response status:", response.status);
      console.log("Response data:", response.data);
      console.log("Data type:", typeof response.data);
      console.log("Is array:", Array.isArray(response.data));
      
      if (Array.isArray(response.data)) {
        console.log("=== USER DATA DETAILS ===");
        console.log("Number of users:", response.data.length);
        response.data.forEach((user, index) => {
          console.log(`User ${index + 1}:`, {
            _id: user._id,
            name: user.name,
            email: user.email,
            role: user.role,
            active: user.active, // This is the key field!
            avatar: user.avatar,
            resume: user.resume,
            createdAt: user.createdAt,
            updatedAt: user.updatedAt,
            fullUserObject: user
          });
        });
      } else {
        console.log("=== UNEXPECTED DATA FORMAT ===");
        console.log("Expected array but got:", response.data);
      }
      
      setUsers(response.data);
    } catch (error) {
      console.log("=== ERROR FETCHING USERS ===");
      console.error("Error object:", error);
      console.error("Error message:", error.message);
      if (error.response) {
        console.error("Error response status:", error.response.status);
        console.error("Error response data:", error.response.data);
        console.error("Error response headers:", error.response.headers);
      }
  setError(t("adminUser.loadError"));
    } finally {
      setLoading(false);
      console.log("=== FETCH USERS COMPLETED ===");
    }
  };

  const handleToggleStatus = async (userId, currentlyActive) => {
    try {
      // Use the correct API endpoints from API_PATHS
      const endpoint = currentlyActive 
        ? API_PATHS.ADMIN.DISABLE_USER(userId)
        : API_PATHS.ADMIN.ENABLE_USER(userId);
      
      console.log(`Making API call to: ${endpoint}`);
      console.log(`User ${userId} is currently active: ${currentlyActive}`);
      
      const response = await axiosInstance.put(endpoint);
      
      console.log(`${currentlyActive ? 'Disable' : 'Enable'} user response:`, response.data);
      
      // Refresh the data from backend to get updated status
      console.log("Refreshing user data from backend to ensure consistency...");
      await fetchUsers();
      
      // // Show success message
      // alert(`User ${currentlyActive ? 'disabled' : 'enabled'} successfully`);
      
    } catch (error) {
      console.error(`Error ${currentlyActive ? 'disabling' : 'enabling'} user:`, error);
  alert(t(`adminUser.${currentlyActive ? 'disableError' : 'enableError'}`));
      
      // Log the full error for debugging
      if (error.response) {
        console.error('API Error Response:', error.response.data);
        console.error('API Error Status:', error.response.status);
      }
    }
  };

  const handleRefresh = () => {
    fetchUsers();
  };

  if (loading && users.length === 0) {
    return (
      <AdminLayout activeMenu="admin-user">
        <div className="p-6 min-h-screen bg-gray-50 flex items-center justify-center">
          <div className="text-center">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500 mx-auto mb-4"></div>
            <p className="text-gray-600">{t("adminUser.loading")}</p>
          </div>
        </div>
      </AdminLayout>
    );
  }

  return (
    <AdminLayout activeMenu="admin-user">
      {/* Replace the table with this responsive flex layout */}
<div className="space-y-4">
  {/* Header for larger screens */}
  <div className="hidden md:grid md:grid-cols-7 gap-4 p-4 bg-gray-100 rounded-lg font-medium text-gray-500 text-sm">
  <div>{t("adminUser.table.id")}</div>
  <div>{t("adminUser.table.avatarName")}</div>
  <div>{t("adminUser.table.email")}</div>
  <div>{t("adminUser.table.status")}</div>
  <div>{t("adminUser.table.resume")}</div>
  <div>{t("adminUser.table.created")}</div>
  <div>{t("adminUser.table.actions")}</div>
  </div>

  {/* User cards */}
  {users.map((user, idx) => (
    <div
      key={user._id}
      className={`${
        idx % 2 === 0 ? "bg-white" : "bg-gray-50"
      } rounded-lg border border-gray-200 p-4 hover:bg-gray-100 transition-colors duration-150`}
    >
      {/* Mobile layout (stacked) */}
      <div className="md:hidden space-y-3">
        <div className="flex items-center space-x-3">
          {user.avatar ? (
            <img 
              src={user.avatar} 
              alt={user.name}
              className="w-10 h-10 rounded-full object-cover"
              onError={(e) => {
                e.target.style.display = 'none';
              }}
            />
          ) : (
            <div className="w-10 h-10 bg-gray-300 rounded-full flex items-center justify-center">
              <span className="text-sm text-gray-600">
                {user.name?.charAt(0)?.toUpperCase() || '?'}
              </span>
            </div>
          )}
          <div className="flex-1">
            <div className="font-medium text-gray-900">#{user._id} - {user.name || t("adminUser.na")}</div>
            <div className="text-sm text-gray-600">{user.email || t("adminUser.na")}</div>
          </div>
          <span
            className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
              user.active
                ? "bg-green-100 text-green-800"
                : "bg-red-100 text-red-800"
            }`}
          >
            {user.active ? t("adminUser.status.active") : t("adminUser.status.disabled")}
          </span>
        </div>
        
        <div className="flex flex-wrap gap-2 text-sm">
          <span className="bg-gray-100 px-2 py-1 rounded text-gray-700">
            {t("adminUser.resume")}: {user.resume ? t("adminUser.yes") : t("adminUser.no")}
          </span>
          <span className="bg-gray-100 px-2 py-1 rounded text-gray-700">
            {t("adminUser.created")}: {user.createdAt ? new Date(user.createdAt).toLocaleDateString() : t("adminUser.na")}
          </span>
        </div>

        <button
          className={`w-full inline-flex items-center justify-center px-3 py-2 rounded-md text-sm font-medium transition-colors duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2 ${
            user.active
              ? "bg-red-600 hover:bg-red-700 text-white focus:ring-red-500"
              : "bg-green-600 hover:bg-green-700 text-white focus:ring-green-500"
          }`}
          onClick={() => handleToggleStatus(user._id, user.active)}
        >
          {user.active ? (
            <>
              <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M18.364 18.364A9 9 0 005.636 5.636m12.728 12.728L5.636 5.636m12.728 12.728L18.364 5.636M5.636 18.364l12.728-12.728" />
              </svg>
              {t("adminUser.disable")}
            </>
          ) : (
            <>
              <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              {t("adminUser.enable")}
            </>
          )}
        </button>
      </div>

      {/* Desktop layout (grid) */}
      <div className="hidden md:grid md:grid-cols-7 gap-4 items-center">
  <div className="font-medium text-gray-900">#{user._id}</div>
        
        <div className="flex items-center space-x-3">
          {user.avatar ? (
            <img 
              src={user.avatar} 
              alt={user.name}
              className="w-8 h-8 rounded-full object-cover"
              onError={(e) => {
                e.target.style.display = 'none';
              }}
            />
          ) : (
            <div className="w-8 h-8 bg-gray-300 rounded-full flex items-center justify-center">
              <span className="text-xs text-gray-600">
                {user.name?.charAt(0)?.toUpperCase() || '?'}
              </span>
            </div>
          )}
          <span className="text-sm text-gray-700">{user.name || t("adminUser.na")}</span>
        </div>

  <div className="text-sm text-gray-700">{user.email || t("adminUser.na")}</div>

        <span
          className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
            user.active
              ? "bg-green-100 text-green-800"
              : "bg-red-100 text-red-800"
          }`}
        >
          <div
            className={`w-1.5 h-1.5 rounded-full mr-1.5 ${
              user.active ? "bg-green-400" : "bg-red-400"
            }`}
          ></div>
          {user.active ? t("adminUser.status.active") : t("adminUser.status.disabled")}
        </span>

        <div className="text-sm text-gray-700">
          {user.resume ? (
            <span className="text-green-600 flex items-center">
              <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
              {t("adminUser.yes")}
            </span>
          ) : (
            <span className="text-gray-400">{t("adminUser.no")}</span>
          )}
        </div>

        <div className="text-sm text-gray-700">
          {user.createdAt ? new Date(user.createdAt).toLocaleDateString() : t("adminUser.na")}
        </div>

        <button
          className={`inline-flex items-center px-3 py-1.5 rounded-md text-sm font-medium transition-colors duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2 ${
            user.active
              ? "bg-red-600 hover:bg-red-700 text-white focus:ring-red-500"
              : "bg-green-600 hover:bg-green-700 text-white focus:ring-green-500"
          }`}
          onClick={() => handleToggleStatus(user._id, user.active)}
        >
          {user.active ? (
            <>
              <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M18.364 18.364A9 9 0 005.636 5.636m12.728 12.728L5.636 5.636m12.728 12.728L18.364 5.636M5.636 18.364l12.728-12.728" />
              </svg>
              {t("adminUser.disable")}
            </>
          ) : (
            <>
              <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              {t("adminUser.enable")}
            </>
          )}
        </button>
      </div>
    </div>
  ))}
</div>
    </AdminLayout>
  );
};

export default AdminUser;