import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import AdminLayout from "../../components/layout/AdminLayout";
import axiosInstance from "../../utils/axiosInstance";
import { API_PATHS } from "../../utils/apiPaths";

const AdmDashboard = () => {
  const { t } = useTranslation();
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
      <div className="p-6 min-h-screen bg-gray-50">
        <div className="max-w-5xl mx-auto">
          <h1 className="text-3xl font-bold mb-8 text-gray-800">
            {t("admDashboard.title")}
          </h1>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="bg-white rounded-xl shadow-lg p-8 flex flex-col items-center">
              <span className="text-gray-500 text-lg mb-2">{t("admDashboard.activeUsers")}</span>
              <span className="text-4xl font-bold text-green-600">
                {loading ? t("admDashboard.loading") : stats.users}
              </span>
            </div>
            <div className="bg-white rounded-xl shadow-lg p-8 flex flex-col items-center">
              <span className="text-gray-500 text-lg mb-2">{t("admDashboard.activeCompanies")}</span>
              <span className="text-4xl font-bold text-blue-600">
                {loading ? t("admDashboard.loading") : stats.companies}
              </span>
            </div>
          </div>
        </div>
      </div>
    </AdminLayout>
  );
};

export default AdmDashboard;
