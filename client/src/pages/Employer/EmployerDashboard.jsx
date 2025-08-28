import React, { Children, useEffect, useState } from 'react'
import moment from "moment";
import{useNavigate} from 'react-router-dom';
import axiosInstance from '../../utils/axiosInstance';
import { API_PATHS } from '../../utils/apiPaths';
import DashBoardLayout from '../../components/layout/DashBoardLayout';
import LoadingSpinner from '../../components/LoadingSpinner';
import { Briefcase, CheckCircle2, TrendingUp, Users, Plus, Building2 } from 'lucide-react';
import JobDashBoardCard from '../../components/Cards/JobDashBoardCard';
import ApplicantDashboardCard from '../../components/Cards/ApplicantDashboardCard';

const Card = ({className, children, title, subtitle, headerAction}) =>{
  return <div 
      className={`bg-white rounded-xl border border-gray-100 shadow-sm hover:shadow-md transition-shadow duration-200 ${className}`}>
      {(title || headerAction) && (
        <div className="flex items-center justify-between p-6 pb-4">
          <div>
            {title && (
              <h3 className='text-lg font-semibold text-gray-900'>{title}</h3>
            )}
            {subtitle && (
              <p className='text-sm text-gray-500 mt-1'>{subtitle}</p>
            )}
          </div>
          {headerAction}
      </div>
      )}
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
        // console.log("total applicants :", response.data?.counts?.totalApplications);
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
      {isLoading ? <LoadingSpinner /> : (
        <div className='max-w-7xl mx-auto space-y-8 mb-96'>
          {/* Dashboard Stats */}
          <div className='grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6'>
            <StatCard
              title="Active Jobs"
              value={dashboardData?.counts?.totalActiveJobs || 0}
              icon={Briefcase}
              trend={true}
              trendValue={`${dashboardData?.counts?.trends?.activeJobs|| 0}%`}
              color="blue"
            />

            <StatCard
              title="Total Applicants"
              value={dashboardData?.counts?.totalApplications || 0}
              icon={Users}
              trend={true}
              trendValue={`${dashboardData?.counts?.trends?.applications || 0}%`}
              color="green"
            />

            <StatCard
              title="Hired"
              value={dashboardData?.counts?.totalHires || 0}
              icon={CheckCircle2}
              trend={true}
              trendValue={`${dashboardData?.counts?.trends?.hired || 0}%`}
              color="purple"
            />

          </div>

          {/* Recent Activities */}
          <div className='grid grid-cols-1 lg:grid-cols-2 gap-8'>
              <Card
                title="Recent Job Posts" 
                subtitle="Your lates job posting"
                headerAction={
                  <button
                    className='text-sm text-blue-600 hover:text-blue-700 font-medium' 
                    onClick={() => navigate('/manage-jobs')}
                  >
                    View all
                  </button>
                }
              >
                <div className='space-y-3'>
                    {dashboardData?.data?.recentJobs
                    ?.slice(0.3) 
                    ?.map((job, index) =>(
                      <JobDashBoardCard key={index} job ={job} />
                    ))}
                  </div> 
                </Card> 
              <Card
                title="Recent Applications"
                subtitle="Latest candidate applications"
                headerAction={
                  <button
                    className='text-sm text-blue-600 hover:text-blue-700 font-medium'
                    onClick={() => navigate('/manage-jobs')}
                  >
                    View all
                  </button>
                }
              >
                <div className='space-y-3'>
                  {dashboardData?.data?.recentApplications
                  ?.slice(0,3)
                  ?.map((data, index) => (
                    <ApplicantDashboardCard 
                      key={index}
                      applicant ={data?.applicant || ""}
                      position={data?.job?.title || ""} 
                      time={moment(data?.updatedAt).fromNow() || ""}
                      />
                  ))}
                </div>
              </Card>
          </div>

          {/* Quick Actions */}
          <Card
            title="Quick Actions"
            subtitle="Common tasks to get you started"
          >
            <div className='grid grid-cols-1 sm:grid-cols-3 gap-4'>
              {[
                {
                  title: "Post a New Job",
                  icon: Plus,
                  color: "bg-blue-50 text-blue-700",
                 path: "/post-job"
                },
                {
                  title: "Review Applications",
                  icon: Users,
                  color: "bg-green-50 text-green-700",
                  path: "/manage-jobs"
                },
                {
                  title: "Company Settings",
                  icon: Building2,
                  color: "bg-purple-50 text-purple-700",
                  path: "/company-profile"
                },
              ].map((action, index) => (
                    <button
                        key={index}
                        className="flex items-center space-x-3 p-4 rounded-xl border border-gray-100 hover:border-gray-200 hover:shadow-sm transition-all duration-200 text-left"
                        onClick={() => navigate(action.path)}
                      >
                        <div className={`p-2 rounded-lg ${action.color}`}>
                          <action.icon className="h-5" />
                        </div>
                        <span className="font-medium text-gray-900">
                          {action.title}
                        </span>
                      </button>
                  ))}
            </div>
          </Card>
        </div>
      )}
    </DashBoardLayout>
  );
}

export default EmployerDashboard;
