import { Briefcase } from 'lucide-react'
import React from 'react'
import moment from 'moment'
import { useTranslation } from 'react-i18next';

const JobDashBoardCard = ({job}) => {
  const { t } = useTranslation();
  return (
    <div className='flex items-center justify-between p-4 rounded-xl border-gray-100 hover:border-gray-200 transition-colors'>
        <div className='flex items-center space-x-4'>
            <div className='h-10 w-10 bg-blue-100 rounded-xl flex items-center justify-center'>
                <Briefcase className="h-5 w-5 text-blue-600" />
            </div>
            <div>
                <h4 className='text-[15px] font-medium text-gray-900'>{job.title}</h4>
                <p className='text-xs text-gray-500'>
                    {job.location} • {moment(job.createAT).format("MMM D, YYYY")}
                </p>
            </div>
        </div>
        <div className='flex items-center space-x-3'>
            <span 
            className={`px-3 py-1 text-xl font-medium rounded-full ${
                !job.isClosed
                  ? "bg-green-100 text-green-700"
                  :"bg-gray-100 text-gray-600" 
                }`}
                >
                    {job.isClosed ? t('jobDashBoardCard.closed') : t('jobDashBoardCard.active')}
            </span>
        </div>
    </div>
  )
}

export default JobDashBoardCard