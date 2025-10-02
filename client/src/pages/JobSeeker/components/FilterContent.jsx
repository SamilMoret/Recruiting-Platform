import {
  ChevronDown,
  ChevronUp,
  Filter
} from 'lucide-react'
import { useTranslation } from 'react-i18next';
import { CATEGORIES, JOB_TYPES } from '../../../utils/data'
import SalaryRangeSlider from "../../../components/Input/SalaryRangeSlider" 

const FilterSection = ({title, children, isExpanded, onToggle}) => {
  return (
    <div className='border-b border-gray-200 pb-4 mb-4 last:border-b-0'>
      <button
        onClick={onToggle}  
        className='flex items-center justify-between w-full text-left font-semibold text-gray-500 mb-3 hover:text-blue-500 transition-colors'
      >
        {title}
        {isExpanded ? <ChevronUp className='' /> : <ChevronDown  className=''/>}
      </button>
      {isExpanded && children}
    </div>
  );
}

const FilterContent = ({
    toggleSection,
    clearAllFilters,
    expandedSections,
    filters,
    handleFilterChange
}) => {
  const { t } = useTranslation();
  return <>
    <div className='flex items-center justify-between mb-6'>
      <button
        onClick={clearAllFilters} 
        className='text-blue-600 hover:text-blue-700 font-semibold text-sm'
      >
        {t('jobSeekerDashboard.clearAllFilters')}
      </button>
    </div> 

    <FilterSection
      title={t('jobSeekerDashboard.filterJobs')} 
      isExpanded={expandedSections?.jobType}
      onToggle={()=>toggleSection('jobType')}
    >
      <div className='space-y-3'>
        {JOB_TYPES.map((type) => (
          <label key={type.value} className='flex items-center cursor-pointer'>
            <input
              type='checkbox'
              checked={Array.isArray(filters.type) ? filters.type.includes(type.value) : false}
              onChange={(e) => {
                let newTypes = Array.isArray(filters.type) ? [...filters.type] : [];
                if (e.target.checked) {
                  newTypes.push(type.value);
                } else {
                  newTypes = newTypes.filter(val => val !== type.value);
                }
                handleFilterChange('type', newTypes);
              }}
              className='rounded border-gray-300 to-blue-600 shadow-sm focus:border-blue-300 focus:ring focus:ring-blue-200 focus:ring-opacity-50'
            />
            <span className='ml-3 text-gray-700 font-medium'>{t(`jobTypes.${type.value}`, type.label)}</span>
          </label>
        ))}
      </div>
    </FilterSection>
   

    <FilterSection
      title={t('filterContent.category')} 
      isExpanded={expandedSections?.category}
      onToggle={()=>toggleSection('category')}
    >
     <div className='space-y-3'>
      {
        CATEGORIES.map((type) => (
          <label key={type.value} className='flex items-center cursor-pointer'>
            <input
             type="checkbox"
             className='rounded border-gray-300 to-blue-600 shadow-sm focus:border-blue-300 focus:ring focus:ring-blue-200 focus:ring-opacity-50'
             checked={Array.isArray(filters.category) ? filters.category.includes(type.value) : false}
             onChange={(e) => {
               let newCategories = Array.isArray(filters.category) ? [...filters.category] : [];
               if (e.target.checked) {
                 newCategories.push(type.value);
               } else {
                 newCategories = newCategories.filter(val => val !== type.value);
               }
               handleFilterChange('category', newCategories);
             }}
           />
           <span className='ml-3 text-gray-700 font-medium'>{t(`filterContent.categories.${type.value}`, type.label)}</span>
          </label>
        ))
      }
    </div> 
    </FilterSection>
  </>
  
}

export default FilterContent