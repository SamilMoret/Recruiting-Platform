import React, { useState } from 'react'
import {motion} from 'framer-motion';
import {
  Mail,
  Lock,
  Eye,
  EyeOff,
  Loader,
  AlertCircle,
  CheckCircle
} from 'lucide-react'

const Login = () => {

  const [formData, setFormData] = useState({
    email: '',
    password: '',
    rememberMe: false
  });

  const [formState, setFormState] = useState({
    loading: false,
    errors: {},
    showPassword: false,
    success: false
  })

  //validation functions
  const validateEmail = (email) => {
  }
  const validatePassword = (password) => {
  }
  //hand input changes
  const handleInputChanges = (e) => {};

  const validationForm= () => {};

  const handleSubmit = async (e) => {};


  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 px-4">
      <motion 
      initial={{ opacity: 0, y: -20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0. }}
      className="bg-white p-8 rounded-xl shadow-lg max-w-md w-full"
      >
        <div className="text-center mb-8">
          <h2 className="text-2xl font-bold text-gray-900 mb-2">Welcome Back</h2>
          <p className="text-gray-600">Please enter your credentials to continue </p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-6">
          {/* Email */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Email Address
              </label>
            <div className="relative">
              <Mail className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
              <input
               type="email"
               name='email'
               value={formData.email}
               onChange={handleInputChanges}
               className={`w-full pl-10 py-3 rounded-lg border ${formState.errors.email 
                ? 'border-red-500' : 'border-gray-300'}
                focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-colors`}
               placeholder='Enter your email'
               />
            </div>
            {formState.errors.email && (
              <p className="text-red-500 text-sm mt-1 flex items-center">
                <AlertCircle className="w-4 h-4 mr-1" />
                {formState.errors.email}
              </p>
            )}
          </div>
          {/* Password */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Password</label>
            <div className="relative">
              <Lock className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
              <input
               type={formState.showPassword ? "text":"password"}
               name="password"
               value={formData.password}
               onChange={handleInputChanges}
               className={`w-full pl-10 pr-4 py-3 rounded-lg border
               ${formState.errors.password ? 'border-red-500' : 'border-gray-300'}
                focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-colors`}
               placeholder='Enter your password'
               />
               <button
                  type="button"
                  onClick={() => setFormState(prev =>({...prev, showPassword: !prev.showPassword}))}
                  className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600"
               >
                {formState.showPassword ? (
                  <EyeOff className="w-5 h-5" />
                ) : (
                  <Eye className="w-5 h-5" />
                )}
               </button>
            </div>
            {formState.errors.password && (
              <p className="text-red-500 text-sm mt-1 flex items-center">
                <AlertCircle className="w-4 h-4 mr-1" />
                {formState.errors.password}
              </p>
            )}
            
            </div>
            <div>
            {/* Submit Error */}
            {formState.errors.submit && (
              <div className="bg-red-50 border border-red-200 rounded-lg p-3">
                <p className="text-red-700 text-sm flex items-center">
                  <AlertCircle className="w-4 h-4 mr-2" />
                  {formState.errors.submit}
                </p>
              </div>
            )}
            {/* Submit Button */}
            <button
              type='submit'
              disabled={formState.loading}
              className="w-full bg-gradient-to-r from-blue-600 to-purple-600 text-white py-3 rounded-lg font-semibold hover:from-blue-700 hover:to-purple-700 transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center space-x-2"
            >
              {formState.loading ? (
                <>
                  <Loader className="w-5 h-5 animate-spin" />
                  <span> Signing In...</span>
                </>
              ) : (
                <span>Sign In</span>
              )}
            </button>

            {/* Sign Up link */}
            <div className="text-center">
              <p className="text-gray-600">
                Don't have an account?{' '}
                <a href="/signup" className="text-blue-600 hover:text-blue-700 font-medium">
                   Create one here 
                </a>
              </p>
            </div>
          </div>
        </form>

      </motion>
    </div>
  )
}

export default Login