

import { ChevronDown } from "lucide-react"
import { useTranslation } from 'react-i18next';
import { useNavigate } from "react-router-dom"



const ProfileDropDown = ({
    isOpen,
    onToggle,
    avatar,
    companyName,
    email,
    onLogout,
    userRole
}) => {
   const { t } = useTranslation();
   const navigate = useNavigate(); 
  return <div className="relative">
        <button
            onClick={onToggle}
            className="flex items-center space-x-3 p-2 rounded-xl hover:bg-gray-50 transition-colors duration-200"
        >
                <span className="flex items-center gap-2">
                    {/* Avatar or fallback icon */}
                    {avatar ? (
                        <img 
                            src={avatar} 
                            alt="Avatar" 
                            className="h-9 w-9 object-cover rounded-xl"
                        />
                    ) : (
                        <div className="h-9 w-9 rounded-xl bg-blue-100 flex items-center justify-center border-2 border-blue-400">
                            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                                <path stroke="#2563eb" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" d="M15.75 9A3.75 3.75 0 1 1 8.25 9a3.75 3.75 0 0 1 7.5 0zM4.5 19.25a7.25 7.25 0 0 1 15 0v.25A2.25 2.25 0 0 1 17.25 21.75h-10.5A2.25 2.25 0 0 1 4.5 19.5v-.25z" />
                            </svg>
                        </div>
                    )}
                    {/* Company name or user name */}
                    <span className="hidden sm:block text-sm font-medium text-gray-900">{companyName}</span>
                    {/* Always show dropdown arrow */}
                    <ChevronDown className="h-4 w-4 text-gray-400" />
                </span>
        </button>

        {isOpen && (
            <div className="absolute right-0 mt-2 w-56 bg-white rounded-xl shadow-lg border border-white-100 py-2 z-50">
                <div className="px-4 py-3 border-b border-gray-100">
                    <p className="text-xs text-gray-900">{companyName}</p>
                    <p className="text-xs text-gray-500">{email}</p>
                </div>
                <a
                    onClick={() => navigate(userRole === 'jobseeker' ? '/profile' : '/company-profile')}
                    className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
                >
                    {t('profileDropdown.viewProfile')}
                </a>
                    <div className="border-t border-gray-100 mt-2 pt-2">
                        <a 
                            href="#"
                            onClick={onLogout}
                            className="block px-4 py-2 text-sm text-red-600 hover:bg-red-50 transition-colors"
                        >
                            {t('profileDropdown.signOut')}
                        </a>
                    </div>
                    </div>
        )}
  </div>
  
};

export default ProfileDropDown