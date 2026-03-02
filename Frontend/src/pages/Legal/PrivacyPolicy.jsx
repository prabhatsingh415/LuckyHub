import React from "react";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import {
  ShieldCheck,
  Info,
  Lock,
  Eye,
  UserCheck,
  Database,
  Mail,
  ArrowLeft,
} from "lucide-react";

const PrivacyPolicy = () => {
  const lastUpdated = "January 22, 2026";
  const navigate = useNavigate();

  const theme = useSelector((state) => state.theme.mode);

  const sections = [
    {
      icon: <Info className="w-5 h-5 text-red-500" />,
      title: "Information We Collect",
      content: [
        {
          label: "Account Information",
          desc: "When you sign in using Google OAuth, we receive your name, email address, and profile image as provided by Google.",
        },
        {
          label: "Giveaway & Usage Data",
          desc: "We store giveaway-related data such as video URLs, number of winners selected, keywords used, and selection history to provide dashboard insights.",
        },
        {
          label: "YouTube Comment Data",
          desc: "Public comments are fetched temporarily via the YouTube Data API for winner selection. LuckyHub does not permanently store comment content.",
        },
      ],
    },
    {
      icon: <ShieldCheck className="w-5 h-5 text-red-500" />,
      title: "How We Use Your Information",
      content: [
        {
          label: "Core Functionality",
          desc: "To fetch YouTube comments, apply filters, select winners, and display giveaway results accurately.",
        },
        {
          label: "Account Management",
          desc: "To manage authentication, track subscription limits, and show remaining giveaway eligibility on your dashboard.",
        },
        {
          label: "Essential Communication",
          desc: "To send account-related updates and essential notifications regarding your usage of the platform.",
        },
      ],
    },
    {
      icon: <Lock className="w-5 h-5 text-red-500" />,
      title: "Data Protection & Security",
      content: [
        {
          label: "Secure Transmission",
          desc: "All data exchanged between your browser and LuckyHub servers is protected using HTTPS and industry-standard TLS encryption.",
        },
        {
          label: "Restricted Access",
          desc: "Access to user data is strictly limited to authorized systems required to operate and maintain the service.",
        },
        {
          label: "Session Security",
          desc: "LuckyHub uses secure tokens to maintain sessions, ensuring your credentials are never exposed.",
        },
      ],
    },
    {
      icon: <Eye className="w-5 h-5 text-red-500" />,
      title: "Third-Party Services",
      content: [
        {
          label: "Google OAuth",
          desc: "Used for secure authentication. LuckyHub only accesses the basic profile details you explicitly approve.",
        },
        {
          label: "YouTube Data API",
          desc: "Used to retrieve public comments. LuckyHub complies with all YouTube API Services Terms of Service.",
        },
        {
          label: "No Data Selling",
          desc: "We do not sell, rent, or trade your personal information. Your data is used strictly for providing the service.",
        },
      ],
    },
    {
      icon: <UserCheck className="w-5 h-5 text-red-500" />,
      title: "Your Rights & Controls",
      content: [
        {
          label: "Account Access",
          desc: "You can view and update your profile and giveaway history directly from your account dashboard.",
        },
        {
          label: "Data Deletion",
          desc: "You may request account deletion at any time, which will remove your personal data from our active systems.",
        },
        {
          label: "Usage Tracking",
          desc: "LuckyHub provides full transparency regarding your usage quotas and subscription status.",
        },
      ],
    },
  ];

  React.useEffect(() => {
    window.scrollTo(0, 0);
  }, []);
  return (
    <div className={theme === "dark" ? "dark" : ""}>
      <div className="min-h-screen bg-white text-zinc-900 dark:bg-black dark:text-white py-12 px-4 sm:px-6 transition-colors duration-300">
        <div className="max-w-4xl mx-auto">
          {/* Header */}
          <div className="flex flex-col md:flex-row justify-between items-center mb-10 border-b border-zinc-200 dark:border-zinc-800 pb-6 gap-4">
            <div className="flex items-center gap-3 self-start md:self-auto">
              <button
                onClick={() => navigate(-1)}
                className="p-2 hover:bg-zinc-100 dark:hover:bg-zinc-900 rounded-lg transition-all text-zinc-500 dark:text-zinc-400 hover:text-red-500"
                title="Go Back"
              >
                <ArrowLeft size={20} />
              </button>
              <div className="bg-red-600/10 dark:bg-red-600/20 p-2 rounded-lg">
                <ShieldCheck className="w-6 h-6 text-red-500" />
              </div>
              <div>
                <h1 className="text-2xl font-bold tracking-tight">
                  Privacy Policy
                </h1>
                <p className="text-zinc-500 text-xs hidden sm:block">
                  Transparency about how LuckyHub handles your data
                </p>
              </div>
            </div>
            <div className="text-zinc-500 text-xs font-mono bg-zinc-50 dark:bg-zinc-900 px-3 py-1.5 rounded-full border border-zinc-200 dark:border-zinc-800">
              Updated: {lastUpdated}
            </div>
          </div>

          <div className="mb-8 p-6 rounded-xl border border-zinc-200 dark:border-red-700 bg-zinc-50 dark:bg-[#0a0a0a] dark:from-red-950/10 dark:to-transparent">
            <div className="flex items-center gap-2 mb-3 text-red-500">
              <Database className="w-5 h-5" />
              <h2 className="font-semibold text-lg">Introduction</h2>
            </div>
            <p className="text-zinc-600 dark:text-zinc-400 text-sm leading-relaxed">
              LuckyHub is built to help creators run fair YouTube giveaways. We
              collect only the information required to operate the platform and
              select winners reliably. For any concerns, reach out to us at{" "}
              <span className="text-red-500 font-medium">
                privacy@luckyhub.com
              </span>
              .
            </p>
          </div>

          <div className="space-y-6">
            {sections.map((section, idx) => (
              <div
                key={idx}
                className="p-6 rounded-xl border border-zinc-200 dark:border-zinc-800 bg-white dark:bg-[#0a0a0a] shadow-sm dark:shadow-none transition-all hover:border-red-500/20"
              >
                <div className="flex items-center gap-3 mb-5 border-b border-zinc-100 dark:border-zinc-900 pb-3">
                  {section.icon}
                  <h3 className="font-bold text-lg text-zinc-800 dark:text-white">
                    {section.title}
                  </h3>
                </div>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  {section.content.map((item, i) => (
                    <div key={i} className="space-y-1">
                      <h4 className="text-red-500 text-xs font-bold uppercase tracking-wider">
                        {item.label}
                      </h4>
                      <p className="text-zinc-600 dark:text-zinc-400 text-sm leading-snug">
                        {item.desc}
                      </p>
                    </div>
                  ))}
                </div>
              </div>
            ))}
          </div>

          <div className="mt-12 p-8 rounded-xl border border-zinc-200 dark:border-zinc-800 bg-zinc-50 dark:bg-[#0a0a0a] text-center shadow-sm dark:shadow-none">
            <Mail className="w-6 h-6 text-red-500 mx-auto mb-3" />
            <h3 className="font-bold mb-2 text-zinc-800 dark:text-white">
              Questions about these Terms?
            </h3>
            <p className="text-zinc-500 text-sm mb-4 font-mono">
              support@luckyhub.com
            </p>
            <button
              onClick={() => navigate(-1)}
              className="text-zinc-500 dark:text-zinc-400 hover:text-red-500 flex items-center gap-2 mx-auto text-sm transition-colors font-medium"
            >
              <ArrowLeft className="w-4 h-4" /> Back to Previous Page
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default PrivacyPolicy;
