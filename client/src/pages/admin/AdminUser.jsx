import React, { useEffect, useState } from "react";
import AdminLayout from "../../components/layout/AdminLayout";
import axiosInstance from "../../utils/axiosInstance";
import { API_PATHS } from "../../utils/apiPaths";

const AdminUser = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    axiosInstance.get(API_PATHS.ADMIN.GET_ALL_USERS)
      .then(res => {
        setUsers(res.data);
        setLoading(false);
      })
      .catch(() => setLoading(false));
  }, []);

  const handleToggleStatus = (userId, currentlyDisabled) => {
    const endpoint = currentlyDisabled ? 'enable' : 'disable';
    axiosInstance.put(`${API_PATHS.ADMIN.GET_ALL_USERS}/${userId}/${endpoint}`)
      .then(() => {
        setUsers(users =>
          users.map(user =>
            user.id === userId ? { ...user, disabled: !currentlyDisabled } : user
          )
        );
      });
  };

  return (
    <AdminLayout activeMenu="admin-user">
      <div className="p-6 min-h-screen bg-gray-50">
        <div className="max-w-5xl mx-auto">
          <h1 className="text-3xl font-bold mb-8 text-gray-800">Admin User Management</h1>
          <div className="bg-white rounded-xl shadow-lg p-6">
            {loading ? (
              <p className="text-gray-500">Loading users...</p>
            ) : (
              <div className="overflow-x-auto">
                <table className="min-w-full divide-y divide-gray-200">
                  <thead className="bg-gray-100">
                    <tr>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">ID</th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Name</th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Email</th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Status</th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
                    </tr>
                  </thead>
                  <tbody className="bg-white divide-y divide-gray-100">
                    {users.map((user, idx) => (
                      <tr
                        key={user.id}
                        className={idx % 2 === 0 ? "bg-white" : "bg-gray-50 hover:bg-gray-100"}
                      >
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700">{user.id}</td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700">{user.name}</td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700">{user.email}</td>
                        <td className="px-6 py-4 whitespace-nowrap">
                          <span className={`inline-block px-2 py-1 rounded-full text-xs font-semibold ${user.disabled ? "bg-red-100 text-red-600" : "bg-green-100 text-green-600"}`}>
                            {user.disabled ? "Disabled" : "Active"}
                          </span>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap">
                          <button
                            className={`transition-colors duration-150 px-4 py-1 rounded-lg font-medium text-sm shadow-sm focus:outline-none ${
                              user.disabled
                                ? "bg-green-500 hover:bg-green-600 text-white"
                                : "bg-red-500 hover:bg-red-600 text-white"
                            }`}
                            onClick={() => handleToggleStatus(user.id, user.disabled)}
                          >
                            {user.disabled ? "Enable" : "Disable"}
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </div>
      </div>
    </AdminLayout>
  );
};

export default AdminUser;