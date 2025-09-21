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

  const handleToggleStatus = (employerId, currentlyDisabled) => {
    const endpoint = currentlyDisabled ? "enable" : "disable";
    axiosInstance
      .put(
        `${API_PATHS.ADMIN.GET_ALL_EMPLOYERS}/${employerId}/${endpoint}`
      )
      .then(() => {
        setEmployers((emps) =>
          emps.map((emp) =>
            emp.id === employerId
              ? { ...emp, disabled: !currentlyDisabled }
              : emp
          )
        );
      });
  };

  return (
    <AdminLayout activeMenu="admin-employer">
      <div className="p-6 min-h-screen bg-gray-50">
        <div className="max-w-5xl mx-auto">
          <h1 className="text-3xl font-bold mb-8 text-gray-800">
            Admin Employer Management
          </h1>
          <div className="bg-white rounded-xl shadow-lg p-6">
            {loading ? (
              <p className="text-gray-500">Loading employers...</p>
            ) : (
              <div className="overflow-x-auto">
                <table className="min-w-full divide-y divide-gray-200">
                  <thead className="bg-gray-100">
                    <tr>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                        ID
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                        Name
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                        Company Name
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                        Email
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                        Status
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                        Actions
                      </th>
                    </tr>
                  </thead>
                  <tbody className="bg-white divide-y divide-gray-100">
                    {employers.map((emp, idx) => (
                      <tr
                        key={emp.id}
                        className={
                          idx % 2 === 0
                            ? "bg-white"
                            : "bg-gray-50 hover:bg-gray-100"
                        }
                      >
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700">
                          {emp.id}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700">
                          {emp.name}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700">
                          {emp.companyName}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700">
                          {emp.email}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap">
                          <span
                            className={`inline-block px-2 py-1 rounded-full text-xs font-semibold ${
                              emp.disabled
                                ? "bg-red-100 text-red-600"
                                : "bg-green-100 text-green-600"
                            }`}
                          >
                            {emp.disabled ? "Disabled" : "Active"}
                          </span>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap">
                          <button
                            className={`transition-colors duration-150 px-4 py-1 rounded-lg font-medium text-sm shadow-sm focus:outline-none ${
                              emp.disabled
                                ? "bg-green-500 hover:bg-green-600 text-white"
                                : "bg-red-500 hover:bg-red-600 text-white"
                            }`}
                            onClick={() => handleToggleStatus(emp.id, emp.disabled)}
                          >
                            {emp.disabled ? "Enable" : "Disable"}
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

export default AdminEmployer;
