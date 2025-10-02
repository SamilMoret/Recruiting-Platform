import React from "react";
import { useTranslation } from "react-i18next";
import AdminLayout from "../../components/layout/AdminLayout";

const CompanyDashboard = () => {
  const { t } = useTranslation();
  return (
    <AdminLayout activeMenu="admin-company">
      <div className="p-6">
        <h1 className="text-2xl font-bold mb-4">{t("companyDashboard.title")}</h1>
        <p>{t("companyDashboard.description")}</p>
      </div>
    </AdminLayout>
  );
};

export default CompanyDashboard;