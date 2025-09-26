import React from 'react'
import { useTranslation } from 'react-i18next';

const StatusBadge = ({status}) => {
    const { t } = useTranslation();
    const statusConfig = {
        Applied: "bg-gray-100 text-gray-800",
        Inreview: "bg-yellow-100 text-yellow-800",
        Hired: "bg-green-100 text-green-800",
        Rejected: "bg-red-100 text-red-800",
    }

    return(
        <span
        className={`px-3 py-1 rounded text-sm font-medium${
            statusConfig[status] || "bg-gray-100 text-gray-800"
        }`}>
            {t(`statusBadge.${status}`)}
        </span>
    )

}

export default StatusBadge