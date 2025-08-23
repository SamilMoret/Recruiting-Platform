import{color, motion} from 'framer-motion'
import { TrendingUp, Users, Briefcase, Target } from 'lucide-react'

const Analytics = () => {
    const stats = [
        {
            icon: Users,
            label: "Active Users",
            value: "2.4M+",
            growth: "15%",
            color: "blue"
        },
        {
            icon: Briefcase,
            label: "Job Postings",
            value: "150K",
            growth: "22%",
            color: "purple"
        },
        {
            icon: Target,
            label: "Sucessfull Hires",
            value: "94K",
            growth: "8%",
            color: "orange",
        },
        {
            icon: TrendingUp,
            label: "Matched Rate",
            value: "94%",
            growth: "5%",
        }
    ];
  return (
    <section className="py-20 bg-white relative overflow-hidden">
        <div className="container mx-auto px-4">
            <motion.div
                initial={{ opacity: 0, y: 30 }}
                whileInView={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.8 }}
                viewport={{ once: true }}
                className="text-center mb-16"
                >
                    <h2 className="text-4xl md:text-5xl font-bold text-gray-900 mb-6">
                        Plataform 
                        <span className="bg-gradient-to-r from-blue-600 to-purple-600 bg-clip-text text-transparent">
                            Analitycs
                        </span>
                    </h2>
                    <p className="text-xl text-gray-600 max-w-3xl mx-auto">
                        Get insights into your platform's performance with our advanced analytics tools.
                    </p>
                    </motion.div>

                    {/*  Stats Cards */}
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-16">
                        {stats.map((stat, index) => (
                            <motion.div
                                key={index}
                                initial={{ opacity: 0, y: 30 }}
                                whileInView={{ opacity: 1, y: 0 }}
                                transition={{ delay: index * 0.1, duration: 0.6 }}
                                viewport={{ once: true }}
                                className="bg-white p-6 rounded-2xl shadow-lg border border-gray-100 hover:shadow-xl transition-all duration-300"
                            >
                                <div className="flex items-center justify-between mb-4">
                                    <div className={`w-12 h-12 bg-${stat.color}-100 rounded-xl flex items-center justify-center`}>
                                        <stat.icon className={`w-6 h-6 text-${stat.color}-600`} />
                                    </div>
                                    <span className="text-green-500 text-sm font-semibold bg-green-50 px-2 py-1 rounded">
                                        {stat.growth}</span>
                                </div>
                                <h3 className=''>{stat.value}</h3>
                                <p className=''>{stat.label}</p>
                            </motion.div>
                        ))}
                    </div>
                
        </div>
    </section>
)
}

export default Analytics