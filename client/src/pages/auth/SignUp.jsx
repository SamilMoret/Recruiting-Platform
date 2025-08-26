import React, { useState } from 'react';
import {motion} from 'framer-motion';
import {
  User,
  Mail,
  Lock,
  Upload,
  Eye,
  EyeOff,
  UserCheck,
  Building2,
  CheckCircle,
  AlertCircle,
  Loader,
  ShoppingBag,
} from 'lucide-react';
import { validateEmail, validateAvatar, validatePassword } from '../../utils/helper';
import axiosInstance from '../../utils/axiosInstance';
import { API_PATHS } from '../../utils/apiPaths';
import uploadImage from '../../utils/uploadImage';
import { useAuth } from '../../context/AuthContext';


const SignUp = () => {

  const { login } = useAuth();

  const [formData, setFormData] = useState({
    fullName: '',
    email: '',
    password: '',
    role: '',
    avatar: null,
  });

  const [formState, setFormState] = useState({
    loading: false,
    errors: {},
    showPassword: false,
    avatarPreview: null,
    success: false,
  });
    
     //hand input changes
  const handleInputChanges = (e) => {
    const { name, value, } = e.target;
    setFormData(prev => ({ 
      ...prev, [name]: value 
    }));
    // Clear error when user starts typing
    if (formState.errors[name]) {
      setFormState(prev => ({
        ...prev,
        errors: {
          ...prev.errors,
          [name]: ''
        }
      }));
    }
  };

    const handleRoleChange = (role) => {

        setFormData(prev => ({ ...prev, role }));
        if(formState.errors.role){
          setFormState(prev => ({
            ...prev,
                errors: {
              ...prev.errors,
              role: ''
            }
          }));
        }

    };

    const handleAvatarChange = (e) => {

      const file = e.target.files[0];
      if (file) {
        const error = validateAvatar(file);
        if (error) {
          setFormState(prev => ({
            ...prev,
            errors: {
              ...prev.errors,
              avatar: error
            }
          }));
          return;
        }
        setFormData(prev => ({ ...prev, avatar: file }));

       // Generate preview
       const reader = new FileReader();
       reader.onloadend = () => {
         setFormState(prev => ({
           ...prev,
           avatarPreview: reader.result,
           errors: {
             ...prev.errors,
             avatar: ''
           }
         }));
       };
       reader.readAsDataURL(file);
      }
    };

  const validateForm = () => {

      const error = {
        fullName: !formData.fullName ? "Enter full name" : "",
        email: validateEmail(formData.email),
        password: validatePassword(formData.password),
        role: !formData.role ? "Select a role" : "",
        avatar: "",
      };
      //remove empty errors
      Object.keys(error).forEach(key => {
        if (!error[key]) delete error[key];
      });

      setFormState(prev => ({
        ...prev,
        errors: error,
      }));
      return Object.keys(error).length === 0;
  };

    const handleSubmit = async (e) => {
      e.preventDefault();
      if (!validateForm()) return;

      setFormState(prev => ({ ...prev, loading: true }));

      try {

        let avatarUrl = "";

        //Upload image if present
        if(formData.avatar){
          const imgUploadRes = await uploadImage(formData.avatar);
          avatarUrl = imgUploadRes.url || "";
        }

        const response = await axiosInstance.post(API_PATHS.AUTH.REGISTER, {
          name: formData.fullName,
          email: formData.email,
          password: formData.password,
          role: formData.role,
          avatar: avatarUrl || "",
        });

        // handle successful registration
        setFormState(prev => ({
          ...prev,
          loading: false,
          success: true,
          errors: {},
        }));

        const {token} = response.data;
        if(token){
          login(response.data, token);

          // Redirect based on role
          setTimeout(() => {
            window.location.href =
              formData.role === "employer"
                ? "/employer-dashboard"
                : "/find-jobs";
          }, 2000);
        }


      }catch(error){

        console.error("Error ", error);

        setFormState(prev => ({
          ...prev,
          loading: false,
          errors: {
            submit:
            error.response?.data?.message ||
            "Failed to create account"
          }
        }));
      }}

    if(formState.success){
        return (
          <div className="min-h-screen flex items-center justify-center bg-gray-50 px-4">
            <motion.div
              initial={{ opacity: 0, scale: 0.9 }}
              animate={{ opacity: 1, scale: 1 }}
              className="bg-white p-8 rounded-xl shadow-lg max-w-md w-full text-center"
            >
              <CheckCircle className="w-16 h-16 text-green-500 mb-4"/>
              <h2 className="text-2xl font-bold text-gray-900 mb-2">
                Account Created Successfully!
                </h2>
              <p className="text-gray-600 mb-4">
                Welcome to the platform! your account has been successfully created.
              </p>
              <div className="animate-spin w-6 h-6 border-2 border-blue-600 border-t-transparent rounded-full"/>
              <p className="text-sm text-gray-500 mt-2"> Redirecting to your dashboard</p>
            </motion.div>
          </div>
        )
      }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 px-4 py-8">
        <motion.div className="bg-white p-8 rounded-xl shadow-lg max-w-md w-full"
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.6 }}
      >
        <div className="text-center mb-8">
          <h2 className="text-xl font-bold text-gray-600">
            Create Account
          </h2>
          <p className="">
            Join thousands of professionals finding their dream jobs
          </p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-6">
          {/* Full Name */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Full Name *
            </label>
            <div className="relative">
              <User className='absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-5 w-5'/>
              <input
               type="text"
               name="fullName"
               value={formData.fullName}
               onChange={handleInputChanges}
               className={`w-full pl-10 pr-4 py-3 rounded-lg border ${
                formState.errors.fullName ? 'border-red-500' 
                : 'border-gray-300'}
                focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-colors`}
                placeholder='Enter your full name'
                />
            </div>
            {formState.errors.fullName && (
              <p className="text-sm text-red-500 mt-2 flex items-center">
                <AlertCircle  className="w-4 h-4 mr-1"/>
                {formState.errors.fullName}
                </p>
            )}
          </div>
          {/* Email */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Email *
            </label>
            <div className="relative">
              <Mail className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-5 w-5"/>
              <input
               type="email"
               name="email"
               value={formData.email}
               onChange={handleInputChanges}
               className={`w-full pl-10 pr-4 py-3 rounded-lg border ${
                formState.errors.email ? 'border-red-500' 
                : 'border-gray-300'}
                focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-colors`}
                placeholder='Enter your email address'
                />
            </div>
            {formState.errors.email && (
              <p className="text-sm text-red-500 mt-1 flex items-center">
                <AlertCircle  className="w-4 h-4 mr-1"/>
                {formState.errors.email}
                </p>
            )}
          </div>
          {/* Password */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Password *
            </label>
            <div className="relative">
              <Lock className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-5 w-5"/>
              <input
               type={formState.showPassword ? "text" : "password"}
               name='password'
               value={formData.password}
               onChange={handleInputChanges}
               className={`w-full pl-10 pr-4 py-3 rounded-lg border ${
                formState.errors.password ? 'border-red-500' 
                : 'border-gray-300'}
                focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-colors`}
               placeholder='create a strong password'
               />
                <button
                    type="button"
                    onClick={() => setFormState(prev =>({...prev, showPassword: !prev.showPassword}))}
                    className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600"
                >
                  {formState.showPassword ? <EyeOff className="w-5 h-5"/> : <Eye className="w-5 h-5"/>}
                </button>
            </div>
            {formState.errors.password && (
              <p className="text-sm text-red-500 mt-1 flex items-center">
                <AlertCircle  className="w-4 h-4 mr-1"/>
                {formState.errors.password}
                </p>
            )}
          </div>

          {/* Avatar Upload */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Profile Picture (optional)
            </label>
            <div className="flex items-center space-x-4">
              <div className="w-16 h-16 rounded-full bg-gray-200 flex items-center justify-center overflow-hidden">
                {formState.avatarPreview ? (
                  <img 
                  src={formState.avatarPreview} 
                  alt="Avatar Preview" 
                  className="w-full h-full object-cover"
                  />
                ):(
                  <User className="w-8 h-8 text-gray-400"/>
                )}
              </div>
              <div className=''>
                  <input
                   type="file" 
                   id='avatar'
                   accept=".jpg, .jpeg, .png"
                   onChange={handleAvatarChange}
                   className="hidden"
                   />
                   <label 
                   htmlFor='avatar' 
                   className="cursor-pointer bg-gray-50 border border-gray-300 rounded-lg px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-100 transition-colors flex items-center space-x-2"
                   >
                    <Upload className='h-4 w-4 mr-2'/>
                    <span>Upload Photo</span> 
                    </label>
                    <p className="text-xs text-gray-500 mt-1">JPG, PGN up to 5MB</p>
              </div>
            </div>
            {formState.errors.avatar && (
              <p className="text-sm text-red-500 mt-1 flex items-center">
                <AlertCircle  className="w-4 h-4 mr-1"/>
                {formState.errors.avatar}
                </p>
            )}
          </div>

          {/* Role Selection */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-3">
              I am a *
            </label>
            <div className="grid grid-cols-2 gap-4">
              <button
                type="button"
                onClick={() => handleRoleChange('jobseeker')}
                className={`p-4 rounded-lg border-2 transition-all ${
                  formData.role === 'jobseeker' 
                  ? 'border-blue-500  bg-blue-50 text-blue-700' 
                  : 'border-gray-200 hover:border-gray-300'
                }`}
                >
                  <UserCheck className="w-8 h-8 mx-auto mb-2"/>
                  <div className="font-medium">Job Seeker</div>
                  <div className="text-xs text-gray-500">
                    Looking for opportunities
                  </div>
                </button>  
              <button
                type="button"
                onClick={() => handleRoleChange('employer')}
                className={`p-4 rounded-lg border-2 transition-all ${
                  formData.role === 'employer' 
                  ? 'border-blue-500  bg-blue-50 text-blue-700' 
                  : 'border-gray-200 hover:border-gray-300'
                }`}
                >
                  <Building2 className="w-8 h-8 mx-auto mb-2"/>
                  <div className="font-medium">
                    Employer
                  </div>
                  <div className="text-xs text-gray-500">
                    Hiring
                  </div>
                </button>
            </div>
            {formState.errors.role && (
              <p className="text-sm text-red-500 mt-2 flex items-center">
                <AlertCircle  className="w-4 h-4 mr-1"/>
                {formState.errors.role}
                </p>
            )}
          </div>
          {/* Submit Error */}
          {formState.errors.submit && (
            <div className="bg-red-50 border border-red-200 text-red-500 p-4">
              <p className="text-sm text-red-500 mt-1 flex items-center">
                <AlertCircle  className="w-4 h-4 mr-2"/>
                {formState.errors.submit}
              </p>
            </div>
          )}

          {/* Submit Button */}
          <button
            type="submit"
            disabled={formState.loading}
            className="w-full bg-gradient-to-r from-blue-600 to-purple-600 text-white py-3 rounded-lg font-semibold hover:from-blue-700 hover:to-purple-700 transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center space-x-2"
          >
            {formState.loading ? (
              <>
                <Loader className="animate-spin w-5 h-5" />
                <span>Creating Account...</span>
              </>
            ) : (
              <span>Create Account</span>
            )}
          </button>
          
          {/* Login Link */}
          <div className="text-center">
            <p className="text-gray-600">
              Already have an account? {""}
              <a 
               href="/login"
               className="text-blue-600 hover:underline font-medium"
               >
                Sign in here
               </a>
            </p>
          </div>
        </form>
  </motion.div>
    </div>
  );
};

export default SignUp;