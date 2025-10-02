import React from 'react'
import { useState, useEffect } from 'react'
import { useTranslation } from 'react-i18next';
import {
    Briefcase,
    Bookmark,
}from "lucide-react"
import {Link, useNavigate} from "react-router-dom"
import { useAuth } from '../../context/AuthContext'
import ProfileDropDown from './ProfileDropDown'

const Navbar = () => {
    const { t } = useTranslation();
    const {user, logout, isAuthenticated} = useAuth();
    const navigate = useNavigate();
    const [profileDropDownOpen, setProfileDropDownOpen] = useState(false);

    useEffect(() => {
        const handleClickOutside = () => {
            if (profileDropDownOpen ) {
                setProfileDropDownOpen(false);
            }
        };

        document.addEventListener('click', handleClickOutside);
        return () => {
            document.removeEventListener('click', handleClickOutside);
        };
    }, [profileDropDownOpen]);

  return ( <header className='fixed top-0 left-0 right-0 z-50 bg-white/95 border-b backdrop:blur-sm border-gray-100'>
    <div className='container mx-auto px-4'>
        <div className='flex items-center justify-between h-16'>
            {/* logo */}
            <Link to='/find-jobs' className='flex items-center space-x-3' >
                <div className='w-8 h-8 bg-gradient-to-r from-blue-500 to-blue-600 rounded-lg flex items-center justify-center'>
                    <Briefcase className='w-5 h-5 text-white' />
                </div>
                <span className=' text-lg font-bold text-gray-900'>{t('navbar.panelTitle')}</span>
            </Link>

            {/* Auth Buttons */}
            <div className='flex items-center space-x-3'>
                {user && (
                    <button
                     className='p-2 rounded-xl hover: transition-colors duration-200 relative'                   
                     onClick={() => navigate('/saved-jobs')}
                    >
                        <Bookmark className='w-5 h-5 text-gray-500' />
                    </button>
                )}

                {isAuthenticated ? (
                    <ProfileDropDown 
                        isOpen={profileDropDownOpen}
                        onToggle={(e) => {
                            e.stopPropagation();
                            setProfileDropDownOpen(!profileDropDownOpen);
                        }}
                        avatar={user?.avatar || ""}
                        companyName={user?.role === "employer" ? user?.companyName : user?.name}
                        email={user?.email || ""}
                        userRole={user?.role || ""}
                        onLogout={logout}
                    />
                ):(
                                <>
                                        <a 
                                         href='/login' 
                                         className='text-gray-600 hover:text-gray-900 transition-colors font-medium px-4 py-2 rounded-lg hover:bg-gray-500'
                                         >
                                               {t('navbar.login')}
                                        </a>
                                        <a
                                         href="/signup"
                                         className='bg-gradient-to-r from-blue-600 to-purple-600 text-white px-6 py-2 rounded-lg font-medium hover:from-blue-700 hover:to-purple-700 transition-all duration-300 shadow-sm hover:shadow-md'
                                         >
                                            {t('navbar.signup')}
                                         </a>
                                </>)}

            </div>
        </div>
    </div>
  </header>
  )
}

export default Navbar