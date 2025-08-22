import {motion} from 'framer-motion';
import { Search, ArrowRight, Users, Building2,TrendingUp } from 'lucide-react';
    import { Form, useNavigate } from 'react-router-dom'

const Hero = () => {
    const isAuthenticated = true;
    const user = { fullName: "Alex", role: "employer" };

    const navigate = useNavigate();

    const stats = [
        { icon: Users, label: "Active Users", value :'2.4M+'},
        { icon: Building2, label: "Companies", value :'10K+'},
        { icon: TrendingUp, label: "Jobs Posted", value :'50K+'},

    ]
  return (
    <div>Hero</div>
  )
}

export default Hero