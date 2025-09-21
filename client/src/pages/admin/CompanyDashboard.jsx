import React from "react";
import AdminLayout from "../../components/layout/AdminLayout";

const CompanyDashboard = () => {
  return (
    <AdminLayout activeMenu="admin-company">
      <div className="p-6">
        <h1 className="text-2xl font-bold mb-4">Admin Company Management</h1>
        <p>This is the admin company management page. Add company management features here.</p>
      </div>
    </AdminLayout>
  );
};

export default CompanyDashboard;