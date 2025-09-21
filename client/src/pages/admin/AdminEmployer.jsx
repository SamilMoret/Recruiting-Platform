import React, { useEffect, useState } from "react";
import AdminLayout from "../../components/layout/AdminLayout";
import axiosInstance from "../../utils/axiosInstance";
import { API_PATHS } from "../../utils/apiPaths";

const AdminEmployer = () => {
  const [employers, setEmployers] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    axiosInstance
      .get(API_PATHS.ADMIN.GET_ALL_EMPLOYERS)
      .then((res) => {
        setEmployers(res.data);
        setLoading(false);
      })
      .catch(() => setLoading(false));
  }, []);

  return (
    <AdminLayout activeMenu="admin-employer">
      <div className="p-6">
        <h1 className="text-2xl font-bold mb-4">
          Admin Employer Management
        </h1>
        {loading ? (
          <p>Loading employers...</p>
        ) : (
          <table className="min-w-full border">
            <thead>
              <tr>
                <th className="border px-4 py-2">ID</th>
                <th className="border px-4 py-2">Company Name</th>
                <th className="border px-4 py-2">Email</th>
                {/* Add more columns as needed */}
              </tr>
            </thead>
            <tbody>
              {employers.map((emp) => (
                <tr key={emp.id}>
                  <td className="border px-4 py-2">{emp.id}</td>
                  <td className="border px-4 py-2">{emp.companyName}</td>
                  <td className="border px-4 py-2">{emp.email}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </AdminLayout>
  );
};

export default AdminEmployer;
