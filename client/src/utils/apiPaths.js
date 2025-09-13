export const BASE_URL = "http://localhost:8000";
// export const BASE_URL = "http://129.148.29.122:8000";
export const API_PATHS = {
  AUTH: {
    REGISTER: `/api/auth/register`, //signup (jobseeker or employer)
    LOGIN: `/api/auth/login`, //login
    GET_PROFILE: `/api/auth/profile`, //get user profile
    UPDATE_PROFILE: `/api/user/profile`, //update user profile
    DELETE_RESUME: `/api/auth/resume`, //delete user resume
  },
  DASHBOARD: {
    OVERVIEW: `/api/analytics/overview`, //get dashboard statistics
  },

  JOBS: {
    GET_ALL_JOBS: `/api/jobs`, //get all jobs
    GET_JOB_BY_ID: (id) => `/api/jobs/${id}`, //get job by id
    POST_JOB: `/api/jobs`, //post a new job
    GET_JOBS_EMPLOYER: `/api/jobs/get-jobs-employer`,
    UPDATE_JOB: (id) => `/api/jobs/${id}`, //update job by id
    TOGGLE_CLOSE: (id) => `/api/jobs/${id}/toggle-close`, //toggle job close status
    DELETE_JOB: (id) => `/api/jobs/${id}`, //delete job by id

    SAVE_JOB: (id) => `/api/saved-jobs/${id}`, //save job by id
    UNSAVE_JOB: (id) => `/api/saved-jobs/${id}`, //unsave job by id
    GET_SAVED_JOBS: `/api/saved-jobs/my`,
  },

  APPLICATIONS: {
    APPLY_TO_JOB: (id) => `/api/applications/${id}`, //apply to job
    GET_ALL_APPLICATIONS: (id) => `/api/applications/job/${id}`, //get all applications for a job
    UPDATE_STATUS: (id) => `/api/applications/${id}/status`, //update application status
  },
  UPLOAD_IMAGE: `/api/auth/upload-image`,
};
