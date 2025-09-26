import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
} from "react-router-dom";
import { Toaster } from "react-hot-toast";
import LandingPage from "./pages/LandingPage/LandingPage";
import SignUp from "./pages/Auth/SignUp";
import Login from "./pages/auth/Login";
import JobSeekerDashboard from "./pages/JobSeeker/JobSeekerDashboard";
import JobDetails from "./pages/JobSeeker/JobDetails";
import SavedJobs from "./pages/JobSeeker/SavedJobs";
import UserProfile from "./pages/JobSeeker/UserProfile";
import EmployerDashboard from "./pages/Employer/EmployerDashboard";
import JobPostingForm from "./pages/Employer/JobPostingForm";
import ManageJobs from "./pages/Employer/ManageJobs";
import ApplicationViewer from "./pages/Employer/ApplicationViewer";
import EmployerProfilePage from "./pages/Employer/EmployerProfilePage";
import ProtectedRoute from "./routes/ProtectedRoute";

import { AuthProvider } from "./context/AuthContext";
import ForgotPassword from "./pages/auth/ForgotPassword";
import AdmDashboard from "./pages/admin/AdmDashboard";
import AdminUser from "./pages/admin/AdminUser";
import CompanyDashboard from "./pages/admin/CompanyDashboard";
import AdminEmployer from "./pages/admin/AdminEmployer";
import { useTranslation } from 'react-i18next';
import ReactCountryFlag from 'react-country-flag';
import { useState, useRef, useEffect } from 'react';


function App() {
  const { i18n } = useTranslation();
  const [showMenu, setShowMenu] = useState(false);
  const menuRef = useRef(null);

  // Map language codes to flags and names
  const languages = [
    { code: 'en', country: 'US', name: 'English' },
    { code: 'es', country: 'ES', name: 'Español' },
    { code: 'pt', country: 'PT', name: 'Português' },
  ];
  const currentLang = languages.find(l => l.code === i18n.language) || languages[0];

  // Close menu on outside click
  useEffect(() => {
    function handleClickOutside(event) {
      if (menuRef.current && !menuRef.current.contains(event.target)) {
        setShowMenu(false);
      }
    }
    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  return (
    <AuthProvider>
      <Router>
        {/* Floating Language Button */}
        <div style={{ position: 'fixed', top: 16, right: 16, zIndex: 2000 }} ref={menuRef}>
          <button
            onClick={() => setShowMenu(v => !v)}
            style={{
              background: 'white',
              border: '1px solid #ddd',
              borderRadius: '50%',
              width: '2.5em',
              height: '2.5em',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              boxShadow: '0 2px 8px rgba(0,0,0,0.08)',
              cursor: 'pointer',
            }}
            title={currentLang.name}
          >
            <ReactCountryFlag countryCode={currentLang.country} svg style={{ width: '2em', height: '2em', background: 'none', borderRadius: '0', padding: '0' }} />
          </button>
          {showMenu && (
            <div style={{
              position: 'absolute',
              top: '3em',
              right: 0,
              background: 'white',
              border: '1px solid #ddd',
              borderRadius: '0.5em',
              boxShadow: '0 2px 8px rgba(0,0,0,0.12)',
              padding: '0.5em 0.75em',
              minWidth: '8em',
              display: 'flex',
              flexDirection: 'column',
              gap: '0.5em',
            }}>
              {languages.filter(l => l.code !== currentLang.code).map(lang => (
                <button
                  key={lang.code}
                  onClick={() => { i18n.changeLanguage(lang.code); setShowMenu(false); }}
                  style={{
                    background: 'none',
                    border: 'none',
                    display: 'flex',
                    alignItems: 'center',
                    gap: '0.5em',
                    fontSize: '1em',
                    cursor: 'pointer',
                    padding: '0.25em 0',
                  }}
                >
                  <ReactCountryFlag countryCode={lang.country} svg style={{ width: '1.5em', height: '1.5em', background: 'none', borderRadius: '0', padding: '0' }} />
                  {lang.name}
                </button>
              ))}
            </div>
          )}
        </div>
        {/* ...existing code... */}
        <Routes>
          {/* Public Routes */}
          <Route path="/" element={<LandingPage />} />
          <Route path="/signup" element={<SignUp />} />
          <Route path="/login" element={<Login />} />
          <Route path="/forgot-password" element={<ForgotPassword />} />
          {/* <Route path="/reset-password" element={<ResetPassword />} /> */}

          {/* Admin Protected Routes */}
          <Route element={<ProtectedRoute requiredRole="admin" />}>
            <Route path="/admin-dashboard" element={<AdmDashboard />} />
            <Route path="/admin-user" element={<AdminUser />} />
            <Route path="/admin-company" element={<AdminEmployer />} />
          </Route>

          {/* Employer Protected Routes */}
          <Route element={<ProtectedRoute requiredRole="employer" />}>
            <Route path="/employer-dashboard" element={<EmployerDashboard />} />
            <Route path="/post-job" element={<JobPostingForm />} />
            <Route path="/manage-jobs" element={<ManageJobs />} />
            <Route path="/applicants" element={<ApplicationViewer />} />
            <Route path="/company-profile" element={<EmployerProfilePage />} />
          </Route>

          {/* JobSeeker Protected Routes */}
          <Route element={<ProtectedRoute requiredRole="jobseeker" />}>
            <Route path="/find-jobs" element={<JobSeekerDashboard />} />
            <Route path="/job/:jobId" element={<JobDetails />} />
            <Route path="/saved-jobs" element={<SavedJobs />} />
            <Route path="/profile" element={<UserProfile />} />
          </Route>

          {/* Catch all route - redirect to home */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </Router>

      <Toaster
        toastOptions={{
          className: "",
          style: {
            fontSize: "13px",
          },
        }}
      />
    </AuthProvider>
  );
}

export default App;