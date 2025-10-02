import {
  ArrowLeft,
  Bookmark,
  Grid,
  List
} from "lucide-react";
import { useAuth } from "../../context/AuthContext";
import { useNavigate } from "react-router-dom";
import axiosInstance from "../../utils/axiosInstance";
import { API_PATHS } from "../../utils/apiPaths";
import { useEffect, useState } from "react";
import Navbar from "../../components/layout/Navbar";
import JobCard from "../../components/Cards/JobCard";
import toast from "react-hot-toast";
import { useTranslation } from 'react-i18next';

const SavedJobs = () => {
  const { t } = useTranslation();
  const {user} = useAuth();
  const navigate = useNavigate();

  const [savedJobList, setSavedJobList] = useState([]);
  const [viewMode, setViewMode] = useState("grid");

  const getSavedJobs = async () => {
    try {
      const response = await axiosInstance.get(API_PATHS.JOBS.GET_SAVED_JOBS);
      setSavedJobList(response.data);
    } catch (error) {
      toast.error("Failed to fetch saved jobs", error);
    }
  };

  const handleUnsavedJobs = async (jobId) => {
    try {
      await axiosInstance.delete(API_PATHS.JOBS.UNSAVE_JOB(jobId));
  setSavedJobList((prev) => prev.filter((savedJob) => savedJob.job._id !== jobId));
      toast.success("Job unsaved successfully");
    } catch (error) {
      toast.error("Failed to unsave job! Try again later.");
    }
  };

  useEffect(() => {
    if(user){
      getSavedJobs();
    }
  }, [user]);

  return (
    <div className="bg-gradient-to-br from-blue-50 via-white to-purple-50">
      <Navbar />

      <div className="container mx-auto pt-24">
        {savedJobList && (
          <div className="bg-white p-6 rounded-lg">
            <div className="flex items-center justify-between mb-8">
              <div className="flex items-center gap-4">
                <button
                onClick={() => navigate(-1)} 
                className="group flex items-center space-x-2 px-3.5 py-2.5 text-sm font-medium text-gray-600 hover:text-white bg-white/50 hover:bg-gradient-to-r hover:from-blue-500 hover:to-blue-600 border border-gray-200 hover:border-transparent rounded-xl transition-all duration-300 shadow-lg shadow-gray-100 hover:shadow-xl transform hover:translate-y-0.5"
                >
                  <ArrowLeft className="h-4 w-4 transition-transform group-hover:translate-x-1" />
                </button>
                <h1 className="text-lg lg:text-xl font-semibold leading-tight text-gray-900">
                  {t('savedJobs.title', 'Saved Jobs')}
                </h1>
              </div>

              <div className="flex items-center gap-3 lg:gap-4">
                <div className="flex items-center border border-gray-200 rounded-xl p-1 bg-white">
                  <button
                    onClick={() => setViewMode("grid")}
                    className={`p-2 rounded-lg transition-colors ${
                      viewMode === "grid" 
                      ? "bg-blue-600 text-white shadow-sm" 
                      : "text-gray-600 hover:text-gray-900 hover:bg-gray-100"
                    }`}
                  >
                    <Grid  className="h-4 w-4"/>
                  </button>
                  <button
                    onClick={() => setViewMode("list")}
                    className={`p-2 rounded-lg transition-colors ${
                      viewMode === "list"
                        ? "bg-blue-600 text-white shadow-sm"
                        : "text-gray-600 hover:text-gray-900 hover:bg-gray-100"
                    }`}
                  >
                    <List className="h-4 w-4"/>
                  </button>
                </div>
              </div>
            </div>

            {/* Content sections */}
            <div className="px-0 pb-8 space-y-8">
              {/* Job Grid */}
              {savedJobList.length === 0 ? (
                <div className="text-center py-16 lg:py-20 bg-white/60 backdrop-blur-xl border border-white/20">
                  <div className="">
                    <Bookmark className="h-16 w-16 mx-auto" />
                  </div>
                  <h3 className="text-xl lg:text-2xl font-bold text-gray-900 mb-3">
                    {t('savedJobs.emptyTitle', "You haven't saved any jobs yet.")}
                  </h3>
                  <p className="text-gray-600 mb-6">
                    {t('savedJobs.emptyDescription', "Start saving jobs that interest you to view them later.")}
                  </p>
                  <button
                    onClick={() => navigate("/find-jobs")}
                    className="bg-blue-600 text-white px-6 py-3 rounded-xl font-semibold hover:bg-blue-700 transition-colors "
                  >
                    {t('savedJobs.browseJobs', 'Browse Jobs')}
                  </button>
                </div>
              ) : (
                <>
                <div className=
                  {viewMode === "grid" 
                  ? "grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-2 lg:gap-6"
                  : "space-y-4 lg:space-y-6"}
                  >
                  {savedJobList.map((savedJob) => (
                    <JobCard
                      key={savedJob._id}
                      job={savedJob?.job}
                      onClick={() => navigate(`/job/${savedJob?.job?._id}`)}
                      onToggleSave={() =>handleUnsavedJobs(savedJob?.job?._id)}
                      saved
                    />
                  ))}
                </div>
                </>
              )}
            </div>
          </div>
        )}
      </div>
    </div>
  )
}

export default SavedJobs