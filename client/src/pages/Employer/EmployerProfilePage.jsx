import {useState} from 'react'
import { Building2, Mail, Edit3 } from 'lucide-react'
import { useAuth } from '../../context/AuthContext'
import axiosInstance from '../../utils/axiosInstance'
import { API_PATHS } from '../../utils/apiPaths'
import toast from 'react-hot-toast'
import uploadImage from '../../utils/uploadImage'
import EditProfileDetails from '../Employer/EdifiProfileDetails'
import { useTranslation } from 'react-i18next';

import DashBoardLayout from '../../components/layout/DashBoardLayout'

const EmployerProfilePage = () => {
  const { t } = useTranslation();
  const {user, updateUser} = useAuth();
  
  const [profileData, setProfileData] = useState({
    name: user?.name || '',
    email: user?.email || '',
    avatar: user?.avatar || '',
    companyName: user?.companyName || '',
    companyDescription: user?.companyDescription || '',
    companyLogo: user?.companyLogo || '',
  });

  const [editMode, setEditMode] = useState(false);
  const [formData, setFormData] = useState(profileData);
  const [uploading, setUploading] = useState({avatar: false, logo: false});
  const [saving, setSaving] = useState(false);

 const handleInputChange = (field, value) => {
    setFormData((prev) => ({
      ...prev,
      [field]: value,
    }));
  };

  const handleImageUpload = async (file, type) => {
    setUploading((prev) => ({...prev, [type]: true}));

    try {
       const imgUploadRes = await uploadImage(file);
       const avatarUrl = imgUploadRes.imageUrl || ""; 

       //update from data with new image url
       const field = type === 'avatar' ? 'avatar' : 'companyLogo';
       handleInputChange(field, avatarUrl);
    } catch (error) {
      console.error("Image upload failed:", error);
    }finally{
      setUploading((prev) => ({...prev, [type]: false}));
    }
  };

  const handleImageChange = (e, type) =>{
      const file = e.target.files[0];
      if(file){
        //create  preview url
        const previewUrl = URL.createObjectURL(file);
        handleInputChange(type, previewUrl);

        // uploade image
        handleImageUpload(file, type);
      }
  }

  const handleSave = async () => {
    setSaving(true);
    try {
      const response = await axiosInstance.put(
        API_PATHS.AUTH.UPDATE_PROFILE, formData
      );
      if (response.status === 200) {
        toast.success(t("employerProfile.profileUpdated"));
        setProfileData({ ...formData });
        updateUser({ ...formData });
        setEditMode(false);
      }
    } catch (error) {
      toast.error(t("employerProfile.updateError"));
    } finally {
      setSaving(false);
    }
  };

  const handleCancel = () => {
    setFormData({...profileData});
    setEditMode(false);
  }

  if(editMode){
    return(
      <EditProfileDetails
        formData = {formData}
        handleImageChange = {handleImageChange}
        handleInputChange = {handleInputChange}
        handleSave = {handleSave}
        handleCancel = {handleCancel}
        saving = {saving}
        uploading = {uploading}
      />
    )
  }

  return (
    <DashBoardLayout activeMenu="company-profile">
      <div className='min-h-screen'>
        <div className='max-w-4xl mx-auto'>
          <div className='bg-white rounded-xl shadow-lg overflow-hidden'>
            {/* Header */}
            <div className='bg-gradient-to-r from-blue-500 to-blue-600 px-8 py-6 flex justify-between items-center'>
              <h1 className='textxl  font-medium text-white'>
                {t("employerProfile.title")}
              </h1>
              <button 
                onClick={() => setEditMode(true)} 
                className='bg-white/10 hover:bg-opacity-30 text-white px-4 py-2 rounded-lg transition-colors flex items-center space-x-2'
              >
                <Edit3 className='w-4 h-4' />
                <span>{t("employerProfile.editProfile")}</span>
              </button>
            </div>
            {/* Profile Content */}
            <div className='p-8'>
              <div className='grid grid-cols-1 md:grid-cols-2 gap-8'>
                {/* Personal Information */}
                <div className='space-y-6'>
                  <h2 className='text-lg font-semibold text-gray-800 border-b border-gray-200 pb-2'>
                    {t("employerProfile.personalInfo")}
                  </h2>
                  {/* Avatar and Name */}
                  <div className='flex items-center space-x-4'>
                    <img 
                      src={profileData.avatar} 
                      alt={t("employerProfile.avatarAlt")}
                      className='w-20 h-20 rounded-full object-cover border-4 border-blue-50' 
                    />
                    <div>
                      <h3 className='text-lg font-semibold text-gray-800'>
                        {profileData.name}
                      </h3>
                      <div className='flex items-center text:sm text-gray-600 mt-1'>
                        <Mail className='w-4 h-4 mr-2' />
                        <span>{profileData.email}</span>
                      </div>
                    </div>
                  </div>
                </div>
              {/*  Company Information */}
              <div className='space-y-6'>
                <h2 className='text-lg font-semibold text-gray-800 border-b border-gray-200 pb-2'>
                  {t("employerProfile.companyInfo")}
                </h2>
                {/* Company Logo and Name */}
                <div className='flex items-center space-x-4'>
                  <img 
                    src={profileData.companyLogo} 
                    alt={t("employerProfile.companyLogoAlt")}
                    className='w-20 h-20 rounded-lg object-cover border-4 border-blue-50'
                  />
                  <div className=''>
                    <h3 className='text-lg font-semibold text-gray-800'>
                      {profileData.companyName}
                    </h3>
                    <div className='flex items-center text-sm text-gray-600 mt-1'>
                      <Building2 className='w-4 h-4 mr-2' />
                      <span>{t("employerProfile.companyLabel")}</span>
                    </div>
                  </div>
                </div>
              </div>
              </div>
              {/* Company Description */}
              <div className='mt-8'>
                <h2 className='text-lg font-semibold text-gray-800 border-b border-gray-200 pb-2 mb-6'>
                  {t("employerProfile.aboutCompany")}
                </h2>
                <p className='text:sm text-gray-700 leading-relaxed bg-gray-50 p-6 rounded-lg'>
                  {profileData.companyDescription}
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </DashBoardLayout>
  )
}

export default EmployerProfilePage