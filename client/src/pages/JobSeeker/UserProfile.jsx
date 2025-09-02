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

 const handleInputChange = (field, value) => {
    setProfileData(prev => ({ ...prev, [field]: value }));
  }
  
  const handleImageUpload = async (file, type) => {
    setUploading(prev => ({...prev, [type]: true}));

    try {
        const imgUploadRes = await uploadImage(file);
        const avatarUrl = imgUploadRes.imageUrl || "";
        
        //update form data with new image URL
        handleInputChange(type, avatarUrl);
    } catch (error) {
      console.error('image upload failed', error);
    } finally {
      setUploading(prev => ({...prev, [type]: false}));
    }
  }

  const handleImageChange  = async(e, type) => {
    const file = e.target.files[0];
    if(file){
      const previewUrl = URL.createObjectURL(file);
      handleImageChange(type, previewUrl)

      //upload image
      handleImageUpload(file, type)
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
        API_PATHS.AUTH.DELETE_RESUME,
        {
          resumeUrl: user.resume || ""
        }
      );
      toast.success('Resume deleted successfully');
      setProfileData(prev => ({ ...prev, resume: '' }));
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
    }

    setProfileData({...userData});
    setFormData({...userData});
    return () => {};

  }, [user]);

  return (
    <div className=''>
      <Navbar />

      <div className=''>
        <div className=''>
          <div className=''>
            {/* Header */}
            <div className=''>
              <h1 className=''>Profile</h1>
            </div>

            <div>
              <div>
                <div>
                  <div>
                    <img
                     src={formData?.avatar}
                     alt="Avatar"
                     className='' 
                    />
                    {uploading?.avatar &&(
                     <div className=''>
                      <div className=''></div>
                     </div>
                    )}
                  </div>

                  <div>
                    <label className="">
                      <span className=''>Choose avatar</span>
                      <input 
                        type="file"
                        accept="image/*"
                        onChange={(e) => handleImageChange(e, 'avatar')}
                        className=""
                      />
                    </label>
                  </div>
                </div>


              </div>

              {/* Action Buttons */}
              <div>
                <Link
                  onClick={handleImageChange} 
                  to='/find-jobs'
                  className=''
                >
                  <X className='' />
                  <span className=''>Cancel</span>
                </Link>
                <button
                  onClick={handleSave} 
                  disabled={saving || uploading.avatar || uploading.logo}
                  className=''
                >
                 {saving ?(
                  <div className=''></div>
                 ) : (
                  <Save className='' />
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