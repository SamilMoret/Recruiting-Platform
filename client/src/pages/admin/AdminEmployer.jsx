import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import AdminLayout from "../../components/layout/AdminLayout";
import axiosInstance from "../../utils/axiosInstance";
import { API_PATHS } from "../../utils/apiPaths";

const AdminEmployer = () => {
  const { t } = useTranslation();
  const [employers, setEmployers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState("");
  const [filterStatus, setFilterStatus] = useState("all");

  useEffect(() => {
    fetchEmployers();
  }, []);

  const fetchEmployers = () => {
    setLoading(true);
    axiosInstance
      .get(API_PATHS.ADMIN.GET_ALL_EMPLOYERS)
      .then((res) => {
        setEmployers(res.data);
        setLoading(false);
      })
      .catch((error) => {
        console.error("Error fetching employers:", error);
        setLoading(false);
      });
  };

  const handleToggleStatus = (employerId, currentStatus) => {
    const endpoint = !currentStatus ? "enable" : "disable";
    axiosInstance
      .put(`${API_PATHS.ADMIN.GET_ALL_EMPLOYERS}/${employerId}/${endpoint}`)
      .then(() => {
        setEmployers((emps) =>
          emps.map((emp) =>
            emp.id === employerId ? { ...emp, active: !currentStatus } : emp
          )
        );
      })
      .catch((error) => {
        console.error("Error updating employer status:", error);
      });
  };

  const handleDeleteEmployer = (employerId) => {
  if (window.confirm(t("adminEmployer.confirmDelete"))) {
      axiosInstance
        .delete(`${API_PATHS.ADMIN.GET_ALL_EMPLOYERS}/${employerId}`)
        .then(() => {
          setEmployers((emps) => emps.filter((emp) => emp.id !== employerId));
        })
        .catch((error) => {
          console.error("Error deleting employer:", error);
        });
    }
  };

  // Filter employers based on search term and status
  const filteredEmployers = employers.filter((emp) => {
    const matchesSearch = 
      emp.name?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      emp.companyName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      emp.email?.toLowerCase().includes(searchTerm.toLowerCase());
    
    const matchesStatus = 
      filterStatus === "all" ||
      (filterStatus === "active" && emp.active) ||
      (filterStatus === "disabled" && !emp.active);
    
    return matchesSearch && matchesStatus;
  });

  return (
    <AdminLayout activeMenu="admin-employer">
      <div className="p-4 sm:p-6 min-h-screen bg-gray-50">
        <div className="max-w-7xl mx-auto">
          {/* Header */}
          <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between mb-6">
            <h1 className="text-2xl sm:text-3xl font-bold text-gray-800 mb-4 sm:mb-0">
              {t("adminEmployer.title")}
            </h1>
            <div className="flex flex-col sm:flex-row gap-2 sm:gap-4">
              <div className="text-sm text-gray-600 bg-white px-3 py-2 rounded-lg shadow">
                {t("adminEmployer.total", { count: employers.length })}
              </div>
              <div className="text-sm text-gray-600 bg-white px-3 py-2 rounded-lg shadow">
                {t("adminEmployer.active", { count: employers.filter(emp => emp.active).length })}
              </div>
            </div>
          </div>

          {/* Filters */}
          <div className="bg-white rounded-xl shadow-sm p-4 mb-6">
            <div className="flex flex-col sm:flex-row gap-4">
              {/* Search */}
              <div className="flex-1">
                <input
                  type="text"
                  placeholder={t("adminEmployer.searchPlaceholder")}
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                />
              </div>
              
              {/* Status Filter */}
              <div className="sm:w-48">
                <select
                  value={filterStatus}
                  onChange={(e) => setFilterStatus(e.target.value)}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                >
                  <option value="all">{t("adminEmployer.status.all")}</option>
                  <option value="active">{t("adminEmployer.status.active")}</option>
                  <option value="disabled">{t("adminEmployer.status.disabled")}</option>
                </select>
              </div>

              {/* Refresh Button */}
              <button
                onClick={fetchEmployers}
                className="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors"
              >
                {t("adminEmployer.refresh")}
              </button>
            </div>
          </div>

          {/* Content */}
          <div className="bg-white rounded-xl shadow-lg overflow-hidden">
            {loading ? (
              <div className="p-8 text-center">
                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500 mx-auto mb-4"></div>
                <p className="text-gray-500">{t("adminEmployer.loading")}</p>
              </div>
            ) : filteredEmployers.length === 0 ? (
              <div className="p-8 text-center">
                <p className="text-gray-500">
                  {searchTerm || filterStatus !== "all" 
                    ? t("adminEmployer.noMatch")
                    : t("adminEmployer.noEmployers")}
                </p>
                {(searchTerm || filterStatus !== "all") && (
                  <button
                    onClick={() => {
                      setSearchTerm("");
                      setFilterStatus("all");
                    }}
                    className="mt-2 px-4 py-2 text-blue-500 hover:text-blue-700"
                  >
                    {t("adminEmployer.clearFilters")}
                  </button>
                )}
              </div>
            ) : (
              <>
                {/* Desktop Table View */}
                <div className="hidden lg:block overflow-x-auto">
                  <table className="min-w-full divide-y divide-gray-200">
                    <thead className="bg-gray-50">
                      <tr>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          {t("adminEmployer.table.id")}
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          {t("adminEmployer.table.name")}
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          {t("adminEmployer.table.company")}
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          {t("adminEmployer.table.email")}
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          {t("adminEmployer.table.status")}
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          {t("adminEmployer.table.actions")}
                        </th>
                      </tr>
                    </thead>
                    <tbody className="bg-white divide-y divide-gray-200">
                      {filteredEmployers.map((emp, idx) => (
                        <tr
                          key={emp.id}
                          className={`${idx % 2 === 0 ? "bg-white" : "bg-gray-50"} hover:bg-gray-100 transition-colors`}
                        >
                          <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                            #{emp.id}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                            {emp.name}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                            <div className="max-w-xs truncate" title={emp.companyName}>
                              {emp.companyName}
                            </div>
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                            <div className="max-w-xs truncate" title={emp.email}>
                              {emp.email}
                            </div>
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap">
                            <span
                              className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${
                                emp.active
                                  ? "bg-green-100 text-green-800"
                                  : "bg-red-100 text-red-800"
                              }`}
                            >
                              {emp.active ? t("adminEmployer.status.active") : t("adminEmployer.status.disabled")}
                            </span>
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                            <div className="flex space-x-2">
                              <button
                                onClick={() => handleToggleStatus(emp.id, emp.active)}
                                className={`px-3 py-1 rounded text-xs font-medium transition-colors ${
                                  emp.active
                                    ? "bg-red-100 text-red-700 hover:bg-red-200"
                                    : "bg-green-100 text-green-700 hover:bg-green-200"
                                }`}
                              >
                                {emp.active ? t("adminEmployer.disable") : t("adminEmployer.enable")}
                              </button>
                              <button
                                onClick={() => handleDeleteEmployer(emp.id)}
                                className="px-3 py-1 bg-gray-100 text-gray-700 rounded text-xs font-medium hover:bg-gray-200 transition-colors"
                              >
                                {t("adminEmployer.delete")}
                              </button>
                            </div>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>

                {/* Mobile Card View */}
                <div className="lg:hidden">
                  <div className="divide-y divide-gray-200">
                    {filteredEmployers.map((emp) => (
                      <div key={emp.id} className="p-4 hover:bg-gray-50 transition-colors">
                        {/* Card Header */}
                        <div className="flex items-start justify-between mb-3">
                          <div className="flex-1 min-w-0">
                            <h3 className="text-lg font-medium text-gray-900 mb-1 truncate">
                              {emp.name}
                            </h3>
                            <p className="text-sm text-gray-600 mb-1 truncate" title={emp.companyName}>
                              {emp.companyName}
                            </p>
                            <p className="text-sm text-gray-500">ID: #{emp.id}</p>
                          </div>
                          <span
                            className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ml-2 ${
                              emp.active
                                ? "bg-green-100 text-green-800"
                                : "bg-red-100 text-red-800"
                            }`}
                          >
                            {emp.active ? t("adminEmployer.status.active") : t("adminEmployer.status.disabled")}
                          </span>
                        </div>

                        {/* Email */}
                        <div className="mb-3">
                          <p className="text-sm text-gray-600 break-all">{emp.email}</p>
                        </div>

                        {/* Actions */}
                        <div className="flex flex-col sm:flex-row gap-2">
                          <button
                            onClick={() => handleToggleStatus(emp.id, emp.active)}
                            className={`flex-1 px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
                              emp.active
                                ? "bg-red-500 text-white hover:bg-red-600"
                                : "bg-green-500 text-white hover:bg-green-600"
                            }`}
                          >
                            {emp.active ? t("adminEmployer.disable") : t("adminEmployer.enable")}
                          </button>
                          <button
                            onClick={() => handleDeleteEmployer(emp.id)}
                            className="flex-1 px-4 py-2 bg-gray-500 text-white rounded-lg text-sm font-medium hover:bg-gray-600 transition-colors"
                          >
                            {t("adminEmployer.delete")}
                          </button>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              </>
            )}
          </div>

          {/* Results Summary */}
          {!loading && (
            <div className="mt-4 text-sm text-gray-600 text-center">
              {t("adminEmployer.showing", { filtered: filteredEmployers.length, total: employers.length })}
              {filterStatus !== "all" && (
                <span className="ml-2 text-blue-600">
                  • {t(`adminEmployer.status.${filterStatus}`)}
                </span>
              )}
            </div>
          )}
        </div>
      </div>
    </AdminLayout>
  );
};

export default AdminEmployer;