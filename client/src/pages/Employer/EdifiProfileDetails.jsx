import DashBoardLayout from '../../components/layout/DashBoardLayout'
import {Save, X} from "lucide-react"
import { useTranslation } from 'react-i18next';

import React from 'react'

const EdifiProfileDetails = ({formData, handleImageChange, handleInputChange, handleSave, handleCancel, saving, uploading}) => {
  const { t } = useTranslation();
  return (
    <DashBoardLayout activeMenu="company-profile">
      {formData && <div className='min-h-screen bg-gray-50 py-8 px-4'>
        <div className='max-w-4xl mx-auto'>
          <div className='bg-white rounded-lg shadow-lg overflow-hidden'>
            {/* header */}
            <div className='bg-gradient-to-r from-blue-500 to-blue-500 px-8 py-6'>
              <h1 className='text-lg md:text-xl font-medium text-white'>{t("editProfileDetails.editProfile")}</h1>
            </div>
            {/* edit form */}
            <div className='p-8'>
              <div className='grid grid-cols-1 lg:grid-cols-2 gap-8'>
                {/* personal information */}
                <div className='space-y-6'>
                  <h2 className='text-lg font-medium text-gray-800 border-b pb-2'>
                    {t("editProfileDetails.personalInfo")}
                  </h2>
                  {/* Avatar upload */}
                  <div className='flex items-center space-x-4'>
                    <div className='relative'>
                      {formData?.avatar ? (
                        <img
                          src={formData?.avatar}
                          alt={t("editProfileDetails.avatarAlt")}
                          className="w-20 h-20 rounded-full object-cover border-4 border-gray-50" />
                      ) : (
                        <div className="w-20 h-20 rounded-full bg-blue-100 flex items-center justify-center border-4 border-blue-400">
                          <svg width="40" height="40" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path stroke="#2563eb" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" d="M15.75 9A3.75 3.75 0 1 1 8.25 9a3.75 3.75 0 0 1 7.5 0zM4.5 19.25a7.25 7.25 0 0 1 15 0v.25A2.25 2.25 0 0 1 17.25 21.75h-10.5A2.25 2.25 0 0 1 4.5 19.5v-.25z" />
                          </svg>
                        </div>
                      )}
                      {uploading?.avatar && (
                        <div className='absolute inset-0 flex items-center justify-center bg-black bg-opacity-50 rounded-full'>
                          <div className='w-6 h-6 border-4 border-t-transparent border-white rounded-full animate-spin'></div>
                        </div>
                      )}
                    </div>
                    <div>
                      <label className='block'>
                        <span className='sr-only'>{t("editProfileDetails.chooseAvatar")}</span>
                        <input
                          type='file'
                          accept="image/*"
                          onChange={(e) => handleImageChange(e, 'avatar')}
                          className='block w-full text-sm text-gray-500 file:mr-4 file:p-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100 transition-colors' />
                      </label>
                    </div>
                  </div>
                  {/* Name Input */}
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      {t("editProfileDetails.fullName")}
                    </label>
                    <input
                      type="text"
                      value={formData.name}
                      onChange={(e) => handleInputChange('name', e.target.value)}
                      className='w-full border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all'
                      placeholder={t("editProfileDetails.fullNamePlaceholder")}/>
                  </div>

                  {/* Email (Read-only) */}
                  <div>
                    <label className=" block text-sm font-medium text-gray-700 mb-2">
                      {t("editProfileDetails.emailAddress")}
                    </label>
                    <input
                      type="text"
                      value={formData.email}
                      disabled
                      className='w-full border border-gray-300 rounded-lg bg-gray-50 text-gray-500' />
                  </div>
                </div>

                {/* Company Information */}
                <div className='space-y-6'>
                  <h2 className='text-lg font-medium text-gray-800 border-b pb-2'>
                    {t("editProfileDetails.companyInfo")}
                  </h2>

                  {/* Company Logo Upload */}
                  <div className='flex items-center space-x-4'>
                    <div className='relative'>
                      {formData?.companyLogo ? (
                        <img
                          src={formData?.companyLogo}
                          alt={t("editProfileDetails.companyLogoAlt")}
                          className="w-20 h-20 rounded-lg object-cover border-4 border-gray-200" />
                      ) : (
                        <div className="w-20 h-20 rounded-lg bg-green-100 flex items-center justify-center border-4 border-green-400">
                          <svg width="40" height="40" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path stroke="#22c55e" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" d="M3 21V7a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2v14M16 3v4M8 3v4" />
                          </svg>
                        </div>
                      )}
                      {uploading.logo && (
                        <div className='absolute inset-0 flex items-center justify-center bg-black bg-opacity-50 rounded-lg'>
                          <div className='w-6 h-6 border-4 border-t-transparent border-white rounded-full animate-spin'></div>
                        </div>
                      )}
                    </div>
                    <div>
                      <label className="block ">
                        <span className="sr-only">{t("editProfileDetails.chooseCompanyLogo")}</span>
                        <input
                          type="file"
                          accept='image/*'
                          onChange={(e) => handleImageChange(e, 'logo')}
                          className="block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-green-50 file:text-green-700 hover:file:bg-green-100 transition-colors" />
                      </label>
                    </div>
                  </div>

                  {/* Company name */}
                  <div>
                    <label className='block text-sm font-medium text-gray-700 mb-2'>{t("editProfileDetails.companyName")}</label>
                    <input
                      type="text"
                      placeholder={t("editProfileDetails.companyNamePlaceholder")}
                      value={formData?.companyName}
                      onChange={(e) => handleInputChange('companyName', e.target.value)}
                      className='w-full border px-4 py-3 border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all' />
                  </div>

                  {/* Company Description */}
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">{t("editProfileDetails.companyDescription")}</label>
                    <textarea
                      placeholder={t("editProfileDetails.companyDescriptionPlaceholder")}
                      value={formData?.companyDescription}
                      onChange={(e) => handleInputChange('companyDescription', e.target.value)}
                      rows={4}
                      className='w-full border px-4 py-3 border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all resize-none' />
                  </div>
                </div>
              </div>

              {/* Action buttons */}
              <div className='flex justify-end space-x-4 mt-8 pt-6 border-t'>
                <button
                  type="button"
                  onClick={handleCancel}
                  className='px-6 py-3 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition-colors flex items-center space-x-2'
                >
                <X className='w-4 h-4' />
                <span className=''>{t("editProfileDetails.cancel")}</span> 
                </button>
                <button
                  onClick={handleSave} 
                  disabled={saving || uploading.avatar || uploading.logo}
                  className='px-6 py-3 bg-blue-600 hover:bg-blue-700 text-white rounded-lg flex items-center space-x-2 disabled:opacity-50 disabled:cursor-not-allowed transition-colors '
                >
                  {saving ? (
                    <div className='w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin'></div>
                  ):(
                    <Save className='w-4 h-4 ' />
                  )}
                  <span>{saving ? t("editProfileDetails.saving") : t("editProfileDetails.saveChanges")}</span>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>}
    </DashBoardLayout>
  );
}

export default EdifiProfileDetails