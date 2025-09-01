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
  // console.log('user:', user);
  const {jobId} = useParams();
  console.log('jobId:', jobId);

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

                

                <div className="">
                  <h1>
                    {jobDetails?.title}
                  </h1>
                  <div className="">
                    <div className="">
                      <MapPin className="" />
                      <span className="">{jobDetails?.location}</span>
                    </div>
                </div>
              </div>
              {jobDetails.description ? (
                <StatusBadge status={jobDetails.applicationStatus} />
              ) : (
                <button className="" onClick={applyToJob}>
                  Apply Now
                </button>
              )}
            </div>

            {/* tags */}
            <div className="">
              <span className="">{jobDetails.category}</span>
              <span className="">{jobDetails.type}</span>
              <div className="">
                <Clock className="" />
                <span>{jobDetails.createdAt 
                ? moment(jobDetails.createdAt).format("MM DD YYYY") 
                : "N/A"}
                </span>
              </div>
            </div>
           </div>
          </div>
            {/* Job Details Section */}
             <div>
           </div>
      </div>
    )}
      </div>
    </div>

  )
}
export default JobDetails