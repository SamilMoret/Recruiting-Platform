import React from "react";
import AdminLayout from "../../components/layout/AdminLayout";

const AdminUser = () => {
  return (
    <AdminLayout activeMenu="admin-user">
      <div className="p-6">
        <h1 className="text-2xl font-bold mb-4">Admin User Management</h1>
        <p>This is the admin user management page. Add user management features here.</p>
      </div>
    </AdminLayout>
  );
};

export default AdminUser;