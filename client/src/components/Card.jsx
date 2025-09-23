import React from "react";

const Card = ({ title, subtitle, children }) => (
  <div className="bg-white rounded-xl shadow p-6 mb-4">
    <h2 className="text-lg font-bold mb-1">{title}</h2>
    {subtitle && <p className="text-gray-500 mb-4">{subtitle}</p>}
    {children}
  </div>
);

export default Card;