import React from 'react'
import { useState } from 'react'
import { useTranslation } from 'react-i18next';

const SalaryRangeSlider = ({filters, handleFilterChange}) => {
  const { t } = useTranslation();
  const [minSalary, setMinSalary] = useState(filters?.minSalary ?? "");
  const [maxSalary, setMaxSalary] = useState(filters?.maxSalary ?? "");
  return (
    <div className='space-y-4'>
      <div className='grid grid-cols-2 gap-4'>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            {t('salaryRangeSlider.minSalaryLabel')}
          </label>
          <input
            type='number'
            placeholder={t('salaryRangeSlider.minSalaryPlaceholder')}
            min='0'
            step='1000'
            className='w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:border-blue-300 focus:ring focus:ring-blue-500 focus:ring-opacity-50'
            value={minSalary || ""}
            onChange={({target})=>setMinSalary(target.value)}
            onBlur={()=>
              handleFilterChange(
                "minSalary",
                minSalary ? parseInt(minSalary) : ""
              )
            }
          />
        </div>
        <div>
          <label className='block text-sm font-medium text-gray-700 mb-2'>
            {t('salaryRangeSlider.maxSalaryLabel')}
          </label>
          <input 
            type='number'
            placeholder={t('salaryRangeSlider.maxSalaryPlaceholder')}
            min='0'
            step='1000'
            className='w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:border-blue-300 focus:ring focus:ring-blue-500 focus:ring-opacity-50'
            value={maxSalary || ""}
            onChange={({target})=>setMaxSalary(target.value)}
            onBlur={()=>
              handleFilterChange(
                "maxSalary",
                maxSalary ? parseInt(maxSalary) : ""
              )
            }
          />
        </div>
      </div>
      {/* display current range */}
      {(minSalary !== "" || maxSalary !== "") && (
        <div className='text-sm text-gray-600 bg-gray-50 px3 py-2 rounded-2xl'>
          {t('salaryRangeSlider.rangeLabel')}: {minSalary !== "" ? `${Number(minSalary).toLocaleString()}` : t('salaryRangeSlider.minNotSet')} - {maxSalary !== "" ? `${Number(maxSalary).toLocaleString()}` : t('salaryRangeSlider.noLimit')}
        </div>
      )}
    </div>
  )
}

export default SalaryRangeSlider