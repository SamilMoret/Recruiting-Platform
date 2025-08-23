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
          {/* Navigation links - hidden on mobile */}
          <nav className="hidden md:flex items-center space-x-8">
            <a
              onClick={() => navigate('/find-jobs')}
              className="text-gray-600 hover:text-gray-900 transition-colors font-medium bg-transparent border-none cursor-pointer"
            >
                Encontrar Vagas
            </a>
            <a  
              onClick={() => navigate(
                isAuthenticated && user?.role === "employer"
                  ? "/employer-dashboard"
                  : "/login"
              )}
              className="text-gray-600 hover:text-gray-900 transition-colors font-medium bg-transparent border-none cursor-pointer"
            >
              Para Empresas
            </a>
            </nav>
            <a
              onClick={() => navigate('/find-candidates')}
              className="text-gray-600 hover:text-gray-900 transition-colors font-medium bg-transparent border-none cursor-pointer"
            >
              Encontrar Candidatos
            </a>
            {/* Auth Buttons */}
            <div>
              {isAuthenticated ? (
                <div className="flex items-center space-x-3">
                  <span>Ola, {user?.fullName}</span>
                  <a
                    href={
                        user?.role === "employer" 
                        ? "/employer-dashboard" 
                        : "/find-jobs"
                    }
                    className="bg-gradient-to-r from-blue-600 to-purple-600 text-white px-6 py-2 rounded-lg font-medium hover:from-blue-700 hover:to-purple-700 transition-all duration-300 shadow-sm hover:shadow-md"
                    >
                    Dashboard
                  </a>
                </div>
              ) : (
                <>
                  <a
                    href={() => navigate('/login')}
                    className="text-gray-600 hover:text-gray-900 transition-colors font-medium px-4 py-2 rounded-lg hover:bg-gray-500 bg-transparent border-none cursor-pointer"
                  >
                    Login
                  </a>
                  <a
                    href="/signup"
                    className="bg-gradient-to-r from-blue-600 to-purple-600 text-white px-6 py-2 rounded-lg font-medium hover:from-blue-700 hover:to-purple-700 transition-all duration-300 shadow-sm hover:shadow-md border-none cursor-pointer"
                  >
                    Sign Up
                  </a>
                </>
              )}
            </div>
        </div>
      </div>
    </header>
  );

  
}

export default Header