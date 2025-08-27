import React, { Children, useEffect, useState } from 'react'
// import moment from " moment"//
import{useNavigate} from 'react-router-dom';
import axiosInstance from '../../utils/axiosInstance';
import { API_PATHS } from '../../utils/apiPaths';
import DashBoardLayout from '../../components/layout/DashBoardLayout';
import LoadingSpinner from '../../components/LoadingSpinner';
import { Briefcase, TrendingUp } from 'lucide-react';

const Card = ({className, children}) =>{
  return <div
    className={`bg-white rounded-xl border border-gray-100 shadow-sm hover:shadow-md transition-shadow duration-200 ${className}`}
  >
    <div className={ "p-6"}>{children}</div>
  </div>
};

const StatCard = ({ 
  title,
  value,
  icon : Icon,
  trend,
  trendValue,
  color }) => {
 const colorClasses = {
    blue: "from-blue-500 to-blue-600",
    green: "from-emerald-500 to-emerald-600",
    purple: "from-violet-500 to-violet-600",
    orange: "from-orange-500 to-orange-600"
 };

 return (
  <Card
    className={`bg-gradient-to-b ${colorClasses[color]} text-white border-0`} 
  >
    <div className='flex items-center justify-between'>
      <div>
         <p className='text-white/80 text-sm font-medium'>{title}</p>
         <p className='text-3xl font-bold mt-1'>{value}</p>
         {trend && (
            <div className='flex items-center mt-2 text-sm'>
               <TrendingUp className='h-4 w-4 mr-1' />
               <span className=''>{trendValue}</span>
            </div>
         )}
      </div>
      <div className='bg-white/10 p-3 rounded-xl'>
        <Icon className="h-6 w-6" /> 
      </div>
      
    </div>

  </Card>
 );

};

const EmployerDashboard = () => {
  const navigate = useNavigate();

  const [dashboardData, setDashboardData] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

  const getDashboardOverView = async () => {
    try {
      setIsLoading(true);
      const response = await axiosInstance.get(API_PATHS.DASHBOARD.OVERVIEW);
      if (response.status === 200) {
        setDashboardData(response.data);
      }
    } catch (error) {
      console.error("Error fetching dashboard overview:", error);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    getDashboardOverView();
  }, []);

  return (
    <DashBoardLayout activeMenu="employer">
      {isLoading ? <LoadingSpinner /> :
        <div className='max-w-7xl mx-auto space-y-8'>
          {/* Dashboard Stats */}
          <div className='grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6'>
            <StatCard
              title="Active Jobs"
              value={dashboardData?.counts?.totalActiveJobs || 0}
              icon={Briefcase}
              trend={true}
              trendValue={`${dashboardData?.counts?.trends?.activeJobs|| 0}`}
              color="blue"
            />
          </div>
        </div>
      }
    </DashBoardLayout>
  );
}

export default EmployerDashboard;
