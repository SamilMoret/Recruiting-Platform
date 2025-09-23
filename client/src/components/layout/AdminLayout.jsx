import React from 'react';
import { Briefcase, Building2, Users, LogOut, Menu, X } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

const ADMIN_MENU = [
  { id: 'admin-dashboard', name: 'Dashboard', icon: Briefcase },
  { id: 'admin-company', name: 'Company', icon: Building2 },
  { id: 'admin-user', name: 'User', icon: Users },
];

const AdminLayout = ({ activeMenu, children }) => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [sideBarOpen, setSideBarOpen] = React.useState(false);
  const [activeNavItem, setActiveNavItem] = React.useState(activeMenu || 'admin-dashboard');
  const [isMobile, setIsMobile] = React.useState(false);

  React.useEffect(() => {
    const handleResize = () => {
      const mobile = window.innerWidth < 768;
      setIsMobile(mobile);
      if (!mobile) setSideBarOpen(false);
    };
    handleResize();
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  const handleNavigation = (itemId) => {
    setActiveNavItem(itemId);
    navigate(`/${itemId}`);
    if (isMobile) setSideBarOpen(false);
  };

  const toggleSideBar = () => setSideBarOpen(!sideBarOpen);
  const sidebarCollapsed = !isMobile && false;

  return (
    <div className="flex h-screen bg-gray-50">
      {/* Sidebar */}
      <div
        className={`fixed inset-y-0 left-0 z-50 transition-transform duration-300 transform ${
          isMobile ? (sideBarOpen ? 'translate-x-0' : '-translate-x-full') : 'translate-x-0'
        } ${sidebarCollapsed ? 'w-16' : 'w-64'} bg-white border-r border-gray-200`}
      >
        <div className="flex items-center h-16 border-b border-gray-200 pl-6">
          {!sidebarCollapsed ? (
            <Link className="flex items-center space-x-3" to="/">
              <div className="h-8 w-8 bg-gradient-to-br from-blue-600 to-blue-700 rounded-lg flex items-center justify-center">
                <Briefcase className="h-5 w-5 text-white" />
              </div>
              <span className="text-gray-900 font-bold text-xl">Admin Panel</span>
            </Link>
          ) : (
            <div className="h-8 w-8 bg-gradient-to-br from-blue-600 to-blue-700 rounded-xl flex items-center justify-center">
              <Building2 className="h-5 w-5 text-white" />
            </div>
          )}
        </div>
        <nav className="p-4 space-y-2">
          {ADMIN_MENU.map((item) => (
            <button
              key={item.id}
              onClick={() => handleNavigation(item.id)}
              className={`w-full flex items-center px-3 py-2.5 text-sm font-medium rounded-lg ${
                activeNavItem === item.id
                  ? 'bg-blue-50 text-blue-700 shadow-sm shadow-blue-50'
                  : 'text-gray-700 hover:bg-gray-50 hover:text-gray-900'
              }`}
            >
              <item.icon className={`w-5 h-5 flex-shrink-0 ${activeNavItem === item.id ? 'text-blue-600' : 'text-gray-500'}`} />
              {!sidebarCollapsed && <span className="ml-3 truncate">{item.name}</span>}
            </button>
          ))}
        </nav>
        <div className="absolute bottom-4 left-4 right-4">
          <button
            className="w-full flex items-center px-3 py-2.5 text-sm font-medium rounded-lg text-gray-600 hover:bg-gray-50 transition-all duration-200"
            onClick={logout}
          >
            <LogOut className="w-5 h-5 flex-shrink-0 text-gray-500" />
            {!sidebarCollapsed && <span className="">Logout</span>}
          </button>
        </div>
      </div>
      {/* Main Content */}
      <div
        className={`flex-1 flex flex-col transition-all duration-300 ${
          isMobile ? 'ml-0' : sidebarCollapsed ? 'ml-16' : 'ml-64'
        }`}
      >
        {/* Top Navbar */}
        <header className="bg-white/80 backdrop:blur-sm border-b border-gray-200 h-16 flex items-center justify-between px-6 sticky top-0 z-30">
          <div className="flex items-center space-x-4">
            {isMobile && (
              <button
                onClick={toggleSideBar}
                className="p-2 rounded-xl hover:bg-gray-100 transition-colors duration-200"
              >
                {sideBarOpen ? <X className="h-5 w=5 text-gray-600" /> : <Menu className="h-5 w-5 text-gray-600" />}
              </button>
            )}
            <div>
              <h1 className="text-base font-semibold text-shadow-gray-900">Welcome Admin</h1>
              <p className="text-sm text-gray-500 hidden sm:block">Manage the platform from here</p>
            </div>
          </div>
        </header>
        <main className="flex-1 overflow-y-auto p-6">{children}</main>
      </div>
    </div>
  );
};

export default AdminLayout;
