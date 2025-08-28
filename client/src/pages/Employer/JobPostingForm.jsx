import DashBoardLayout from '../../components/layout/DashBoardLayout'
import { useState, useEffect } from 'react';
import {
  AlertCircle,
  MapPin,
  DollarSign,
  Briefcase,
  Users,
  Send,
  Eye,
} from 'lucide-react';
import axiosInstance from '../../utils/axiosInstance';
import { API_PATHS } from '../../utils/apiPaths';
import { useLocation, useNavigate } from 'react-router-dom';
import { CATEGORIES, JOB_TYPES } from '../../utils/data';
import toast from 'react-hot-toast';
import InputField from '../../components/Input/InputField';
import SelectField from '../../components/Input/SelectField';
import TextAreaField from '../../components/Input/TextAreaField';

function JobPostingForm() {

  const navigate = useNavigate();
  const location = useLocation();
  const jobId = location.state?.jobId || null;

  const [formData, setFormData] = useState({
    jobTitle: '',
    location: '',
    category: '',
    jobType: '',
    description: '',
    requirements: '',
    salaryMin: '',
    salaryMax: ''
  });

  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isPreview, setIsPreview] = useState(false);

  const handleInputChange = (field, value) => {
    setFormData ((prev) => ({
      ...prev,
      [field]:value,
    }));

    // clear erro when user starts typing
    if(errors[field]){
      setErrors((prev)=> ({
        ...prev,
        [field]: "",
      }))
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
  }

  // form validation helper
  const validateForm = (formData) =>{
    const errors = {};

    return errors;
  };

    const isFormValid = () =>{
      const validationErrors = validateForm(formData);
      return Object.keys(validationErrors).length === 0;
    };
    
  return (
         <DashBoardLayout activeMenu="post-job">
            <div className='min-h-screen bg-gradient-to-br from-slate-50 via-blue-50/30 to-purple-50/20 py-8 px-4 sm:px-6 lg:px-8'>
              <div className='max-w-4xl mx-auto'>
                <div className='bg-white shadow-xl rounded-2xl p-6'>
                  <div className='flex items-center justify-between mb-8'>
                    <div>
                        <h2 className='text-xl font-bold bg-gradient-to-r from-gray-900 to-gray-600 bg-clip-text text-transparent'>
                          Post a New Job
                        </h2>
                        <p className='text-sm text-gray-600 mt-1'>
                          Fill in the details below to post a new job opening.
                        </p>
                    </div>
                    <div className='flex items-center space-x-2'>
                      <button
                        onClick={() => setIsPreview(true)}
                        disabled={!isFormValid()}
                        className='group flex items-center space-x-2 px-6 py-3 text-sm font-medium text-gray-600 hover:text-white/50 hover:bg-gradient-to-r hover:from-blue-500 hover:to-blue-600 border border-gray-200 hover:border-transparent rounded-xl transition-all duration-300 shadow-lg shadow-gray-100 hover:shadow-xl transform hover:translate-y-0.5'
                      >
                        <Eye className='h-4 w-4 transition-transform group-hover:-translate-x-1' />
                        <span>Preview</span>
                      </button>
                    </div>
                  </div>
                  <div className='space-y-6'>
                    {/* Job Title */}
                    <InputField
                      label="Job Title"
                      id="jobTitle"
                      placeholder="e.g., Senior Frontend Developer"
                      value={formData.jobTitle}
                      onChange={(e) => handleInputChange('jobTitle', e.target.value)}
                      error={errors.jobTitle}
                      required
                      icon={Briefcase}
                    />
                      {/* Location & Remote */}
                      <div className='space-y-4'>
                        <div className='flex flex-col sm:items-end sm:flex-row sm:space-x-4 space-y-4 sm:space-y-0'>
                          <div className='flex-1'>
                            <InputField
                              label="location"
                              id="location"
                              placeholder="e.g., New York, NY"
                              value={formData.location}
                              onChange={(e)=>{
                                handleInputChange("location",e.target.value)
                              }}
                              error={errors.location}
                              icon={MapPin}
                            />
                          </div>
                        </div>
                      </div>
                      {/*  category & job type */}
                      <div className='grid grid-cols-1 sm:grid-cols-2 gap-4'>
                        <SelectField
                          label="Category"
                          id="category"
                          value={formData.category}
                          onChange={(e) => handleInputChange('category', e.target.value)}
                          options={CATEGORIES}
                          placeholder="Select a category"
                          error={errors.category}
                          requiredA
                          icon={Users} 
                        />
                        <SelectField
                          label="Job Type"
                          id="jobType"
                          value={formData.jobType}
                          onChange={(e) => handleInputChange('jobType', e.target.value)}
                          options={JOB_TYPES}
                          placeholder="Select job type"
                          error={errors.jobType}
                          required
                          icon={Briefcase}
                         />
                      </div>
                      {/* Description */}
                      <TextAreaField
                        label="Job Description"
                        id="description"
                        placeholder="Describe the role and responsibilities..."
                        value={formData.description}
                        onChange={(e) => handleInputChange('description', e.target.value)}
                        error={errors.description}
                        helperText="include key responsibilities, day-to-day tasks, and team structure."
                        required
                      />
                      {/* Requirements */}
                      <TextAreaField
                        label="Requirements"
                        id="requirements"
                        placeholder="List the required skills and qualifications..."
                        value={formData.requirements}
                        onChange={(e) => handleInputChange('requirements', e.target.value)}
                        error={errors.requirements}
                        helperText="Include necessary skills, experience, and educational background."
                        required />
                  </div>
                </div>
              </div>
            </div>
         </DashBoardLayout>
         
  )
}

export default JobPostingForm;
