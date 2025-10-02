import {
  Search,
  Users,
  FileText,
  MessageSquare,
  BarChart3,
  Shield,
  Clock,
  Award,
  Briefcase,
  Building2,
  LayoutDashboard,
  Plus,
} from "lucide-react";

export const jobSeekerFeatures = [
  {
    icon: Search,
    title: "Smart Job Matching",
    description:
      "Get matched with the right job opportunities based on your skills and preferences.",
  },
  {
    icon: FileText,
    title: "Resume Builder",
    description: "Create a professional resume with our easy-to-use builder.",
  },
  {
    icon: MessageSquare,
    title: "Interview Preparation",
    description:
      "Get ready for your interviews with our expert tips and resources.",
  },
  {
    icon: Award,
    title: "Skill Development",
    description: "Access courses and resources to improve your skills.",
  },
];
export const employerFeatures = [
  {
    icon: Users,
    title: "Profile Optimization",
    description:
      "Enhance your profile to attract more recruiters and job offers.",
  },
  {
    icon: BarChart3,
    title: "Salary Insights",
    description: "Know your worth with our salary comparison tool.",
  },
  {
    icon: Shield,
    title: "Privacy Protection",
    description: "Keep your data safe with our advanced privacy features.",
  },
  {
    icon: Clock,
    title: "Application Tracking",
    description: "Stay organized with our application tracking system.",
  },
];

// Navigation items configuration
export const NAVIGATION_MENU = [
  {
    id: "employer-dashboard",
    nameKey: "navigationMenu.dashboard",
    icon: LayoutDashboard,
  },
  { id: "post-job", nameKey: "navigationMenu.postJob", icon: Plus },
  { id: "manage-jobs", nameKey: "navigationMenu.manageJobs", icon: Briefcase },
  {
    id: "company-profile",
    nameKey: "navigationMenu.companyProfile",
    icon: Building2,
  },
];

// categories and job types
export const CATEGORIES = [
  { value: "Engineering", labelKey: "categories.Engineering" },
  { value: "Marketing", labelKey: "categories.Marketing" },
  { value: "Sales", labelKey: "categories.Sales" },
  { value: "Design", labelKey: "categories.Design" },
  { value: "Finance", labelKey: "categories.Finance" },
  { value: "IT", labelKey: "categories.ITSoftware" },
  { value: "Customer-service", labelKey: "categories.CustomerService" },
  { value: "Product", labelKey: "categories.Product" },
  { value: "Operations", labelKey: "categories.Operations" },
  { value: "HR", labelKey: "categories.HR" },
  { value: "Other", labelKey: "categories.Other" },
];

export const JOB_TYPES = [
  { value: "Full-Time", labelKey: "jobTypes.FullTime" },
  { value: "Part-Time", labelKey: "jobTypes.PartTime" },
  { value: "Contract", labelKey: "jobTypes.Contract" },
  { value: "Remote", labelKey: "jobTypes.Remote" },
  { value: "Internship", labelKey: "jobTypes.Internship" },
];

export const SALARY_RANGES = [
  "Less than $1000",
  "$1000 - $15,000",
  "More than $15,000",
];
