import {
  MapPin,
  DollarSign,
  Building2,
  Clock,
  Users,
} from "lucide-react";
import {useAuth} from '../../context/AuthContext';
import { useParams } from "react-router-dom";
import axiosInstance from "../../utils/axiosInstance";
import { API_PATHS } from "../../utils/apiPaths";
import { useEffect, useState } from "react";
import toast from "react-hot-toast";
import Navbar from "../../components/layout/Navbar";
import moment from "moment";
import StatusBadge from  "../../components/layout/StatusBadge";

const JobDetails = () => {
  const {user} = useAuth();
  const {jobId} = useParams();

  const [jobDetails, setJobDetails] = useState(null);
 


  // Removed invalid getJobDetailsById assignment
  const getJobDetailsById = async () => {
    try {
      const response = await axiosInstance.get(
        API_PATHS.JOBS.GET_JOB_BY_ID(jobId),
        {
          params: { userId: user?._id || null }
        }
      );
      setJobDetails(response.data);
    } catch (error) {
      toast.error("Failed to fetch job details", error);
    }
  }
  const applyToJob = async () => {
    try {
      if (jobId) {
        await axiosInstance.post(API_PATHS.APPLICATIONS.APPLY_TO_JOB(jobId));
        toast.success("Successfully applied to the job");
      }
      getJobDetailsById();
    } catch (error) {
      console.error("Error applying to job:", error);
      const errorMsg = error?.response?.data?.message ;
      toast.error(errorMsg || "Failed to apply to the job");
    }
  };

  useEffect(() => {
    if(jobId && user){
      getJobDetailsById();
    }
  }, [jobId, user]);

  return (
    <div className="bg-gradient-to-br from-blue-50 via-white to-purple-50">
      <Navbar />
      
      <div className="container mx-auto pt-24">
        {/* Main content card */}
        {jobDetails && (
          <div className="bg-white p-6 rounded-lg">
            {/* Hero section with clean background */}
            <div className="relative px-0 pb-8 border-b border-gray-100">
              <div className="relative z-10">
                <div className="flex item-center gap-3 mb-6">
                  {jobDetails?.company?.companyLogo ? (
                    <img
                      src={jobDetails?.company?.companyLogo}
                      alt="Company Logo"
                      className="h-20 w-20 object-cover rounded-2xl border-4 border-white/20 shadow-lg"
                    />
                  ) : (
                    <div className="h-20 w-20 bg-gray-50 border-2 border-gray-200 rounded-2xl flex items-center justify-center">
                      <Building2 className="h-8 w-8 text-gray-400" />
                    </div>
                  )}

                

                <div className="flex-1">
                  <h1 className="text-lg lg:text-xl font-semibold mb-2 leading-tight text-gray-900 "  >
                    {jobDetails?.title}
                  </h1>
                  <div className="flex items-center space-x-4 text-gray-600">
                    <div className="flex items-center space-x-1">
                      <MapPin className="h-4 w-4" />
                      <span className="text-sm font-medium">{jobDetails?.location}</span>
                    </div>
                </div>
              </div>
              {jobDetails.description ? (
                <StatusBadge status={jobDetails.applicationStatus} />
              ) : (
                <button className="bg-gradient-to-r from-blue-50 to-blue-50 text-sm text-blue-700 hover:text-white px-6 py-2.5 rounded-xl hover:from-blue-500 hover:to-blue-600 transition-all duration-200 font-semibold transform hover:translate-y-0.5" onClick={applyToJob}>
                  Apply Now
                </button>
              )}
            </div>

            {/* tags */}
            <div className="flex flex-wrap gap-3">
              <span className="px-4 py-2 bg-blue-50 text-sm text-blue-700 font-semibold rounded-full border border-blue-200">{jobDetails.category}</span>
              <span className="px-4 py-2 bg-blue-50 text-sm text-purple-700 font-semibold rounded-full border border-purple-200">{jobDetails.type}</span>
              <div className="flex items-center space-x-1 px-4 py-2 bg-gray-50 text-sm text-gray-700 font-semibold rounded-full border border-gray-200">
                <Clock className="h-4 w-4" />
                <span>{jobDetails.createdAt 
                ? moment(jobDetails.createdAt).format("MM DD YYYY") 
                : "N/A"}
                </span>
              </div>
            </div>
           </div>
          </div>
            {/* Job Details Section */}
             <div className="px-0 pb-8 space-y-8">
                  {/* salary section */}
                  <div className="">
                    <div className=""></div>
                    <div className="">
                      <div className="" >
                        <div className="">
                          <DollarSign />
                        </div>
                        <div>
                          <h3 className="">
                            Compensation
                          </h3>
                          <div className="">
                            {jobDetails.salaryMin } - {jobDetails.salaryMax}
                            <span className="">
                              per year
                            </span>
                          </div>
                        </div>
                      </div>
                      <div className="">
                        {/* <User  className/> */}
                        <span>Competitive</span>
                      </div>
                    </div>
                  </div>
           </div>

           {/* Job Description*/}
      </div>
    )}
      </div>
    </div>

  )
}
export default JobDetails