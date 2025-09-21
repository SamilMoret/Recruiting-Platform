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
      <div className="p-6">
        <h1 className="text-2xl font-bold mb-4">Admin User Management</h1>
        {loading ? (
          <p>Loading users...</p>
        ) : (
          <table className="min-w-full border">
            <thead>
              <tr>
                <th className="border px-4 py-2">ID</th>
                <th className="border px-4 py-2">Name</th>
                <th className="border px-4 py-2">Email</th>
                <th className="border px-4 py-2">Status</th>
                <th className="border px-4 py-2">Actions</th>
              </tr>
            </thead>
            <tbody>
              {users.map(user => (
                <tr key={user.id}>
                  <td className="border px-4 py-2">{user.id}</td>
                  <td className="border px-4 py-2">{user.name}</td>
                  <td className="border px-4 py-2">{user.email}</td>
                  <td className="border px-4 py-2">
                    {user.disabled ? "Disabled" : "Active"}
                  </td>
                  <td className="border px-4 py-2">
                    <button
                      className={
                        user.disabled
                          ? "bg-green-500 text-white px-3 py-1 rounded"
                          : "bg-red-500 text-white px-3 py-1 rounded"
                      }
                      onClick={() => handleToggleStatus(user.id, user.disabled)}
                    >
                      {user.disabled ? "Enable" : "Disable"}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </AdminLayout>
  );
};

export default AdminUser;