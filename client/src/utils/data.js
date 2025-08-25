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
  { id: "employer-dashboard", name: "Dashboard", icon: LayoutDashboard },
  { id: "post-job", name: "Post a Job", icon: Plus },
  { id: "manage-jobs", name: "Manage Jobs", icon: Briefcase },
  { id: "company-profile", name: "Company Profile", icon: Building2 },
];

// categories and job types
export const CATEGORIES = [
  { value: "Engineering", label: "Engineering" },
  { value: "Marketing", label: "Marketing" },
  { value: "Sales", label: "Sales" },
  { value: "Design", label: "Design" },
  { value: "Finance", label: "Finance" },
  { value: "IT & Software", label: "IT & Software" },
  { value: "Customer-service", label: "Customer Service" },
  { value: "Product", label: "Product" },
  { value: "Operations", label: "Operations" },
  { value: "HR", label: "Human Resources" },
  { value: "Other", label: "Other" },
];

export const JOB_TYPES = [
  { value: "Full-time", label: "Full-time" },
  { value: "Part-time", label: "Part-time" },
  { value: "Contract", label: "Contract" },
  { value: "Temporary", label: "Temporary" },
  { value: "Internship", label: "Internship" },
];

export const SALARY_RANGES = [
  "Less than $1000",
  "$1000 - $15,000",
  "More than $15,000",
];
