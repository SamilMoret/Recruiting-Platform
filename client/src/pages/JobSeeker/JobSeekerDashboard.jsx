import { useState, useEffect, use } from "react"
import {Search, Filter, Grid, List, X} from "lucide-react"
import LoadingSpinner from "../../components/LoadingSpinner"
import axiosInstance from "../../utils/axiosInstance"
import { API_PATHS } from "../../utils/apiPaths"
import { useNavigate} from "react-router-dom"
import toast from "react-hot-toast"
import { useAuth} from "../../context/AuthContext"
import Navbar from "../../components/layout/Navbar"
import FilterContent from "./components/FilterContent"
import SearchHeader from "./components/SearchHeader"
import JobCard from "../../components/Cards/JobCard"


const JobSeekerDashboard = () => {

  const { user } = useAuth();
  const navigate = useNavigate();
  
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(false);
  const [viewMode, setViewMode] = useState("grid");
  const [showMobileFilters, setShowMobileFilters] = useState(false);
  const [error, setError] = useState(null);

  // filter status
  const [filters, setFilters] = useState({
    keyword: "",
    location: "",
    category: "",
    type: "",
    minSalary: "",
    maxSalary: "",
  });

  //sidebar collapse status
  const [expandedSections, setExpandedSections] = useState({
    tobType: true,
    salary: true,
    categories: true,
  });

  // function to fetch jobs from API
  const fetchJobs = async () => {
    try {
      setLoading(true); 
      setError(null);

      //build query parameters
      const params = new URLSearchParams();

      if(filters.keyword) params.append("keyword", filters.keyword);
      if(filters.location) params.append("location", filters.location);
      if(filters.category) params.append("category", filters.category);
      if(filters.type) params.append("type", filters.type);
      if(filters.minSalary) params.append("minSalary", filters.minSalary);
      if(filters.maxSalary) params.append("maxSalary", filters.maxSalary);
      if(user && user._id) params.append("userId", user._id);

      const response = await axiosInstance.get(
        `${API_PATHS.JOBS.GET_ALL_JOBS}?${params.toString()}`
      );

      const jobsData = Array.isArray(response.data) ? response.data : response.data.jobs || [];

      setJobs(jobsData);

    } catch (error) {
      console.error("Error fetching jobs:", error);
      setError("Failed to fetch jobs. Please try again later.");
      setJobs([]);
    } finally {
      setLoading(false);
    }
  }

  // fetch jobs when filters change(debounced)
  useEffect(()=>{
    const timeoutId = setTimeout(() => {
      const apiFilters = {
        keyword: filters.keyword,
        location: filters.location,
        category: filters.category,
        type: filters.type,
        minSalary: filters.minSalary,
        maxSalary: filters.maxSalary,
        experience: filters.experience,
        remoteOnly: filters.remoteOnly,
      };

      // only call api if there are meaningful filters
      const hasFilters = Object.values(apiFilters).some(
        (value) => 
          value !== "" &&
          value !== false &&
          value !== null &&
          value !== undefined
      );

      if (hasFilters) {
        fetchJobs(apiFilters);
      }else{
        fetchJobs();
      }

    },500);

  

    return () => clearTimeout(timeoutId);
  }, [filters, user]);

  const handleFilterChange = (key, value) => {
    setFilters((prevFilters) => ({
      ...prevFilters,
      [key]: value,
    }));
  };

  const toggleSection = (section) => {
    setExpandedSections((prevSections) => ({
      ...prevSections,
      [section]: !prevSections[section],
    }));
  }

  const clearAllFilters = () => {
    setFilters({
      keyword: "",
      location: "",
      category: "",
      type: "",
      minSalary: "",
      maxSalary: "",
    });
  };
  const MobileFilterOverlay = () => (
    <div
      className={`fixed inset-0 lg:hidden ${
        showMobileFilters ? "block" : "hidden"
      }`}
    >
      <div
        className="fixed inset-0 bg-black/50" 
        onClick={() => setShowMobileFilters(false)}
      >
        <div className="fixed inset-y-0 right-0 w-full max-sm: bg-white shadow-xl">
          <div className="flex items-center justify-between p-6 border-b border-gray-200">
            <h3 className="font-bold text-gray-900 text-lg">Filters</h3>
            <button
              onClick={() => setShowMobileFilters(false)}
              className="p-2 hover:bg-gray-100 rounded-xl transition-colors"
            >
              <X className="" />
            </button>
          </div>
          <div>
            <FilterContent
              toggleSection={toggleSection}
              clearAllFilters={clearAllFilters}
              expandedSections={expandedSections}
              filters={filters}
              handleFilterChange={handleFilterChange}
            />
          </div>
        </div>
      </div>
    </div>
  );

  const toggleSaveJob = async (jobId, isSaved) => {
    try {
      if (isSaved) {
        await axiosInstance.delete(API_PATHS.JOBS.UNSAVE_JOB(jobId));
        toast.success("Job removed from saved jobs");
      } else {
        await axiosInstance.post(API_PATHS.JOBS.SAVE_JOB(jobId));
        toast.success("Job saved successfully");
      }
      // Optimistically update local jobs state
      setJobs((prevJobs) =>
        prevJobs.map((job) =>
          job._id === jobId ? { ...job, isSaved: !isSaved } : job
        )
      );
    } catch (error) {
      console.log("Error:", error);
      toast.error("Something went wrong! Try again later");
    }
  } 

  const applyToJob = async (jobId) => {
    console.log("Applying to job:", jobId);
    try {
      if (jobId) {
        await axiosInstance.post(API_PATHS.APPLICATIONS.APPLY_TO_JOB(jobId));
      
        toast.success("Applied to job successfully");
      }
      fetchJobs();
    } catch (error) {
      console.log("Error:", error);
      const errorMsg = error?.response?.data?.message;
      toast.error(errorMsg || "Something went wrong! Try again later");
    }
  }

  if (loading) {
    return <LoadingSpinner />;
  }

  if (!loading && jobs.length === 0) {
    return (
      <div className="text-center py-16 bg-white/60 backdrop-blur-xl rounded-2xl border border-white/20">
        <div className="text-gray-400 mb-6">
          <Search className="w-16 h-1/6 mx-auto" />
        </div>
        <h3 className="text-xl lg:text-2xl font-bold text-gray-900 mb-3">
          No jobs found
        </h3>
        <p className="text-gray-600 mb-6">
          Try adjusting your search criteria or filters
        </p>
        <button
          onClick={clearAllFilters}
          className="bg-blue-600 text-white px-6 py-3 rounded-xl font-semibold hover:bg-blue-700 transition-colors"
        >
          Clear all filters
        </button>
      </div>
    );
  }


  return (
    <div className="bg-gradient-to-br from-blue-50 via-white to bg-purple-50">
      <Navbar />
          <div className="min-h-screen mt-16">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
              {/* Search Header */}
              <SearchHeader
                filters={filters}
                handleFilterChange={handleFilterChange}
              />

              <div className="flex gap-6 lg:gap-8">
                {/* Desktop sidebar Filter */}
                <div className="hidden lg:block w-80 flex-shrink-0">
                  <div className="bg-white/80 backdrop-blur-xl rounded-2xl shadow-lg border border-white/20 p-6 sticky top-20">
                    <h3 className="font-bold text-gray-900 text-xl mb-6">
                      Filter Jobs 
                    </h3>
                    <FilterContent
                      toggleSection={toggleSection}
                      clearAllFilters={clearAllFilters}
                      expandedSections={expandedSections}
                      filters={filters}
                      handleFilterChange={handleFilterChange}
                    />
                  </div>
                </div>

                {/* Maincontent */}
                <div className="flex-1 min-w-0">
                  {/* Results Header */}
                  <div className="flex flex-col lg:flex-row lg:items-center justify-between mb-6 lg:mb-8 gap-4 lg:space-y-0">
                    <div>
                      <p className=" text-gray-600 text-sm lg:text-base">
                        <span className=" font-bold text-gray-900">
                          {jobs.length}{" "}
                        </span>
                        jobs
                      </p>
                    </div>

                    <div className="flex items-center justify-between lg:justify-end gap-4">
                      {/* Mobile filter button */}
                      <button
                        onClick={() => setShowMobileFilters(true)}
                        className="lg:hidden flex items-center gap-2 bg-white px-4 py-2 rounded-xl border border-gray-200 font-medium text-gray-700 hover:bg-gray-50 transition-colors"
                      >
                        <Filter className="w-4 h-4" />
                        Filters
                      </button>

                      <div className="flex items-center gap-3 lg:gap-4">
                        <div className="flex items-center border border-gray-200 rounded-xl p-1 bg-white">
                          <button
                            onClick={()=>{setViewMode("grid")}} 
                            className={`p-2 rounded-lg transition-colors ${
                              viewMode === "grid"
                              ? "bg-blue-600 text-white shadow-sm"
                              : "text-gray-600 hover:text-gray-900 hover:bg-gray-100"
                            }`}
                          >
                            <Grid className="w-4 h-4" />
                          </button>
                          <button
                             onClick={()=>{setViewMode("list")}} 
                             className={`p-2 rounded-lg transition-colors ${
                               viewMode === "list"
                               ? "bg-blue-600 text-white shadow-sm"
                               : "text-gray-600 hover:text-gray-900 hover:bg-gray-100"
                             }`}
                          >
                            <List className="w-4 h-4" />
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>

                  {/* Job Grid */}
                  {jobs.length === 0 ? (
                    <div className="text-center py-16 bg-white/60 backdrop-blur-xl rounded-2xl border border-white/20">
                      <div className="text-gray-400 mb-6">
                        <Search className="w-16 h-1/6 mx-auto" />
                      </div>
                      <h3 className="text-xl lg:text-2xl font-bold text-gray-900 mb-3">
                        No jobs found
                      </h3>
                      <p className="text-gray-600 mb-6">
                        Try adjustin you search criteria or filters 
                      </p>
                      <button
                        onClick={clearAllFilters}
                        className="bg-blue-600 text-white px-6 py-3 rounded-xl font-semibold hover:bg-blue-700 transition-colors"
                      >
                        Clear all filters
                      </button>
                    </div>
                  ):(
                    <>
                      <div
                        className={
                          viewMode === "grid" 
                            ? "grid grid-cols-1 lg:grid-cols-2 gap-4 lg:gap-6"
                            : "space-y-4 lg:space-y-6"
                        }>
                        {jobs.map((job) => (
                          <JobCard
                            key={job._id}
                            job={job}
                            onClick={() => navigate(`/job/${job._id}`)}
                            onToggleSave={() => toggleSaveJob(job._id, job.isSaved)}
                            onApply={() => applyToJob(job._id)}
                          />
                        ))}
                      </div> 
                    </>
                  )}
                </div>
              </div>

            </div>

            {/* Mobile Filter Overlay */}
            <MobileFilterOverlay />
          </div>
    </div>
  )
}

export default JobSeekerDashboard