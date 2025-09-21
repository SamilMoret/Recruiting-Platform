import React, { useEffect, useState } from "react";
import AdminLayout from "../../components/layout/AdminLayout";
import axiosInstance from "../../utils/axiosInstance";
import { API_PATHS } from "../../utils/apiPaths";

const AdmDashboard = () => {
  const [stats, setStats] = useState({ users: 0, companies: 0 });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    axiosInstance
      .get(API_PATHS.ADMIN.GET_STATS)
      .then((res) => {
        setStats(res.data);
        setLoading(false);
      })
      .catch(() => setLoading(false));
  }, []);

  return (
    <AdminLayout activeMenu="admin-dashboard">
      <div className="p-6">
        <h1 className="text-2xl font-bold mb-6">Admin Company Dashboard</h1>
        {loading ? (
          <p>Loading stats...</p>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="bg-white shadow rounded p-6 text-center">
              <h2 className="text-lg font-semibold mb-2">Active Users</h2>
              <p className="text-3xl font-bold">{stats.users}</p>
            </div>
            <div className="bg-white shadow rounded p-6 text-center">
              <h2 className="text-lg font-semibold mb-2">Active Companies</h2>
              <p className="text-3xl font-bold">{stats.companies}</p>
            </div>
          </div>
        )}
      </div>
    </AdminLayout>
  );
};

export default AdmDashboard;
