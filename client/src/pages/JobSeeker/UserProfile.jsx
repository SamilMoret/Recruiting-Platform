import {useEffect, useState} from 'react'
import {Save, X, Trash2} from "lucide-react"
import { useAuth } from '../../context/AuthContext'
import axiosInstance from '../../utils/axiosInstance'
import { API_PATHS } from '../../utils/apiPaths'
import toast from 'react-hot-toast'
import uploadImage from '../../utils/uploadImage'
import Navbar from '../../components/layout/Navbar'
import { Link } from 'react-router-dom'

const UserProfile = () => {

  const { user, updateUser } = useAuth();

  const [profileData, setProfileData] = useState({
    name: user?.name || '',
    email: user?.email || '',
    avatar: user?.avatar || '',
    resume: user?.resume || ''
  });

  const [formData, setFormData] = useState({...profileData});
  const [uploading, setUploading] = useState({avatar: false, logo: false});
  const [saving, setSaving] = useState(false);

  // Fetch user profile from server on mount
  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const res = await axiosInstance.get(API_PATHS.AUTH.GET_PROFILE);
        if (res.status === 200 && res.data) {
          setProfileData({
            name: res.data.name || '',
            email: res.data.email || '',
            avatar: res.data.avatar || '',
            resume: res.data.resume || ''
          });
          setFormData({
            name: res.data.name || '',
            email: res.data.email || '',
            avatar: res.data.avatar || '',
            resume: res.data.resume || ''
          });
          updateUser(res.data);
        }
      } catch (err) {
        console.error('Failed to fetch profile', err);
      }
    };
    fetchProfile();
  }, []);

 const handleInputChange = (field, value) => {
    setFormData(prev => ({ ...prev, [field]: value }));
  }
  
  const handleImageUpload = async (file, type) => {
    setUploading(prev => ({ ...prev, [type]: true }));
    try {
      const imgUploadRes = await uploadImage(file);
      const avatarUrl = imgUploadRes.imageUrl || "";
      // Update both formData and profileData with server URL
      setFormData(prev => ({ ...prev, [type]: avatarUrl }));
      setProfileData(prev => ({ ...prev, [type]: avatarUrl }));
    } catch (error) {
      console.error('image upload failed', error);
    } finally {
      setUploading(prev => ({ ...prev, [type]: false }));
    }
  }

  const handleImageChange  = async(e, type) => {
    const file = e.target.files[0];
    if (file) {
      const previewUrl = URL.createObjectURL(file);
      setFormData(prev => ({ ...prev, [type]: previewUrl })); // Show preview immediately
      await handleImageUpload(file, type); // Upload and update with server URL
    }
  };

  const handleSave = async () => {
    setSaving(true);

    try {
     const response = await axiosInstance.put(
       API_PATHS.AUTH.UPDATE_PROFILE,
       formData
     );
     if (response.status === 200) {
       toast.success('Profile updated successfully');
       setProfileData({...formData});
       updateUser({...formData});
     }
    } catch (error) {
      console.error('Error updating profile',error);
    } finally {
      setSaving(false);
    }
  }

  const handleCancel = () =>{
    setFormData(...profileData);
  }
  
  const DeleteResume = async () => {
    setSaving(true);
    try {
      const response = await axiosInstance.post(
        '/api/user/resume', // correct path for user resume deletion
        {
          resumeUrl: user.resume || ""
        }
      );
      toast.success('Resume deleted successfully');
      setProfileData(prev => ({ ...prev, resume: '' }));
      updateUser({ ...user, resume: '' });
    } catch (error) {
      console.error('Error deleting resume');
    } finally {
      setSaving(false);
    }
  };

  useEffect(() => {
    const userData = {
      name: user?.name || '',
      email: user?.email || '',
      avatar: user?.avatar || '',
      resume: user?.resume || ''
    }
    setProfileData({...userData});
    setFormData({...userData});
    return () => {};
  }, [user]);

  return (
    <div className='bg-gradient-to-br from-blue-50 via-white to-purple-50'>
      <Navbar />

      <div className='min-w-screen bg-gray-50 py-8 mt-16 lg:m-20'>
        <div className='max-w-4xl mx-auto'>
          <div className='bg-white rounded-xl shadow-lg overflow-hidden'>
            {/* Header */}
            <div className='bg-gradient-to-r from-blue-500 to-blue-600 px-8 py-6 justify-between items-center'>
              <h1 className='text-xl font-medium text-white'>Profile</h1>
            </div>

            <div className='p-8'>
              <div className='space-y-6'>
                <div className='flex items-center space-x-4'>
                  <div className='relative'>
                    <img
                     src={formData?.avatar}
                     alt="Avatar"
                     className='w-20 h-20 rounded-full object-cover border border-gray-200' 
                    />
                    {uploading?.avatar &&(
                     <div className='absolute inset-0 bg-black bg-opacity-50 rounded-full flex items-center justify-center'>
                      <div className='animate-spin w-6 h-6 border-2 border-blue-600 border-t-transparent rounded-full'/>
                     </div>
                    )}
                  </div>

                  <div>
                    <label className="block">
                      <span className='sr-only'>Choose avatar</span>
                      <input 
                        type="file"
                        accept="image/*"
                        onChange={(e) => handleImageChange(e, 'avatar')}
                        className="block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100 transition-colors"
                      />
                    </label>
                  </div>
                </div>

                {/* Name input */}
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Full Name
                  </label>
                  <input 
                    type="text" 
                    value={formData.name}
                    onChange={(e) => handleInputChange('name', e.target.value)}
                    className='w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all'
                    placeholder='Enter your full name'
                  />
                </div>

                {/* Email (read-onl) */}
                <div >
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Email Address
                  </label>
                  <input 
                    type="email"
                    value={formData.email}
                    disabled
                    className='w-full px-4 py-3 border border-gray-300 rounded-lg bg-gray-50 text-gray-500'
                  />
                </div>

                {/* resume */}
                {user?.resume ? (
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Resume
                    </label>

                    <div className='flex items-center gap-2'>
                      <button
                        className='bg-blue-600 text-white px-4 py-2 rounded-lg font-semibold hover:bg-blue-700 transition-colors flex items-center gap-2'
                        onClick={() => window.open(user.resume, '_blank')}
                      >
                        Download
                      </button>
                      <button 
                        className='cursor-pointer'
                        onClick={DeleteResume}
                        >
                          <Trash2 className='w-5 h-5 text-red-500' />
                        </button>
                    </div>
                  </div>
                ):(
                  <label className='block'>
                    <span className=''>Choose File</span>
                    <input type="file"
                       accept=".pdf,.doc,.docx"
                       onChange={(e) => handleImageChange(e, 'resume')}
                        className="block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100 transition-colors"
                     />
                  </label>
                )}
                      
              </div>

              {/* Action Buttons */}
              <div className='flex justify-end space-x-4 mt-8 pt-6 border-t'>
                <Link
                  onClick={handleImageChange} 
                  to='/find-jobs'
                  className='px-6 py-3 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors flex items-center space-x-2'
                >
                  <X className='w-4 h-4' />
                  <span className=''>Cancel</span>
                </Link>
                <button
                  onClick={handleSave} 
                  disabled={saving || uploading.avatar || uploading.logo}
                  className='px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors flex items-center space-x-2'
                >
                 {saving ?(
                  <div className='w-4 h-4 border-2 border-blue-600 border-t-transparent rounded-full animate-spin'></div>
                 ) : (
                  <Save className='w-4 h-4' />
                 )} 
                 <span>{saving ? 'Saving...' : 'Save Changes'}</span>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default UserProfile