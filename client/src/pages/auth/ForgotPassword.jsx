import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import axios from "axios";
import { API_PATHS, BASE_URL } from "../../utils/apiPaths";

const ForgotPassword = () => {
  const { t } = useTranslation();
  const [email, setEmail] = useState("");
  const [submitted, setSubmitted] = useState(false);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    try {
      await axios.post(`${BASE_URL}${API_PATHS.AUTH.FORGOT_PASSWORD}`, { email });
      setSubmitted(true);
    } catch (err) {
      setError(
        err.response?.data?.message ||
        "There was a problem sending the reset email. Please try again."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-md mx-auto mt-16 p-8 bg-white rounded shadow">
  <h2 className="text-2xl font-bold mb-6 text-center">{t('forgotPassword.title')}</h2>
      {submitted ? (
        <div className="text-green-600 text-center">
          {t('forgotPassword.successMessage')}
        </div>
      ) : (
        <form onSubmit={handleSubmit}>
          <label className="block mb-2 text-sm font-medium text-gray-700">
            {t('forgotPassword.emailLabel')}
          </label>
          <input
            type="email"
            className="w-full px-4 py-2 mb-4 border rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
            placeholder={t('forgotPassword.emailPlaceholder')}
            disabled={loading}
          />
          {error && (
            <div className="text-red-600 mb-4 text-sm">{error}</div>
          )}
          <button
            type="submit"
            className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700 transition-colors"
            disabled={loading}
          >
            {loading ? t('forgotPassword.sending') : t('forgotPassword.sendButton')}
          </button>
        </form>
      )}
    </div>
  );
};

export default ForgotPassword;