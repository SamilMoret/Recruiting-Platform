import React from 'react'
import {Briefcase} from 'lucide-react'
import { useNavigate } from 'react-router-dom'

const Header = () => {
    const isAuthenticated = true;
    const user = {fullName: "Alex", role: "employer"};
    const navigate = useNavigate();

  return (
    <header>
      <div className="container mx-auto px-4">
        <div className="flex items-center justify-between h-16">
          {/* Logo */}
          <div className="flex items-center space-x-3">
            <div className="w-8 h-8 bg-gradient-to-r from-blue-600 to-purple-600 rounded-lg flex items-center justify-center">
              <Briefcase className="w-5 h-5 text-white" />
            </div>
            <span className="text-xl font-bold text-gray-900">Recruiting Platform</span>
          </div>
          {/* Navigation */}
          <nav className="hidden md:flex items-center space-x-8">
            <button
              onClick={() => navigate('/find-jobs')}
              className="text-gray-600 hover:text-gray-900 transition-colors font-medium bg-transparent border-none cursor-pointer"
            >
              Find Jobs
            </button>
            <button
              onClick={() => navigate(
                isAuthenticated && user?.role === "employer"
                  ? "/employer-dashboard"
                  : "/login"
              )}
              className="text-gray-600 hover:text-gray-900 transition-colors font-medium bg-transparent border-none cursor-pointer"
            >
              For Employers
            </button>
            {/* Auth Buttons */}
            <div>
              {isAuthenticated ? (
                <div className="flex items-center space-x-3">
                  <span>Welcome, {user?.fullName}</span>
                  <button
                    onClick={() => navigate(user?.role === "employer" ? "/employer-dashboard" : "/find-jobs")}
                    className="bg-gradient-to-r from-blue-600 to-purple-600 text-white px-6 py-2 rounded-lg font-medium hover:from-blue-700 hover:to-purple-700 transition-all duration-300 shadow-sm hover:shadow-md"
                  >
                    Dashboard
                  </button>
                </div>
              ) : (
                <>
                  <button
                    onClick={() => navigate('/login')}
                    className="text-gray-600 hover:text-gray-900 transition-colors font-medium px-4 py-2 rounded-lg hover:bg-gray-500 bg-transparent border-none cursor-pointer"
                  >
                    Login
                  </button>
                  <button
                    onClick={() => navigate('/signup')}
                    className="bg-gradient-to-r from-blue-600 to-purple-600 text-white px-6 py-2 rounded-lg font-medium hover:from-blue-700 hover:to-purple-700 transition-all duration-300 shadow-sm hover:shadow-md border-none cursor-pointer"
                  >
                    Sign Up
                  </button>
                </>
              )}
            </div>
          </nav>
        </div>
      </div>
    </header>
  );

  
}

export default Header