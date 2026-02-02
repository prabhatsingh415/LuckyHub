import React from "react";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import {
  Shield,
  User,
  CreditCard,
  CheckCircle,
  AlertCircle,
  Terminal,
  Scale,
  Mail,
  ArrowLeft,
} from "lucide-react";

const TermsOfService = () => {
  const lastUpdated = "January 22, 2026";
  const navigate = useNavigate();

  const theme = useSelector((state) => state.theme.mode);

  const sections = [
    {
      icon: <User className="w-5 h-5 text-red-500" />,
      title: "User Accounts & Eligibility",
      content: [
        {
          label: "Account Access",
          desc: "You must sign in using Google OAuth to access LuckyHub. You are responsible for maintaining the security of your account and any activity performed under it.",
        },
        {
          label: "Age Requirement",
          desc: "You must be at least 13 years old to use LuckyHub. If you are under 18, you confirm that you have consent from a parent or legal guardian.",
        },
        {
          label: "Accurate Information",
          desc: "You agree to provide accurate and up-to-date information and not to impersonate another person or entity.",
        },
      ],
    },
    {
      icon: <CreditCard className="w-5 h-5 text-red-500" />,
      title: "Subscription Plans & Billing",
      content: [
        {
          label: "Plan Availability",
          desc: "LuckyHub offers Free and paid subscription plans with usage limits based on the number of giveaways and winners selected, as displayed in your dashboard.",
        },
        {
          label: "Billing & Payments",
          desc: "Paid subscriptions are billed in advance through third-party payment providers. LuckyHub does not store payment card information.",
        },
        {
          label: "Renewals & Cancellation",
          desc: "Subscriptions may auto-renew unless canceled before the next billing cycle. You can manage or cancel your subscription from your account settings.",
        },
      ],
    },
    {
      icon: <CheckCircle className="w-5 h-5 text-red-500" />,
      title: "Acceptable Use",
      content: [
        {
          label: "Permitted Purpose",
          desc: "LuckyHub may only be used to select winners from YouTube comments for legitimate and transparent giveaways.",
        },
        {
          label: "Compliance",
          desc: "You agree to comply with YouTube’s Terms of Service, community guidelines, and all applicable laws when using LuckyHub.",
        },
        {
          label: "Winner Responsibility",
          desc: "LuckyHub only selects winners. You are solely responsible for delivering prizes and honoring giveaway commitments.",
        },
      ],
    },
    {
      icon: <AlertCircle className="w-5 h-5 text-red-500" />,
      title: "Prohibited Activities",
      desc: "You may not misuse LuckyHub for illegal, misleading, or abusive purposes. This includes attempting to bypass usage limits, manipulating results, scraping the service, reverse-engineering the platform, or automating access beyond intended usage.",
    },
    {
      icon: <Terminal className="w-5 h-5 text-red-500" />,
      title: "YouTube API & Third-Party Services",
      content: [
        {
          label: "YouTube Data Usage",
          desc: "LuckyHub uses the YouTube Data API to fetch public comments from videos you provide. Comment data is processed temporarily and not permanently stored.",
        },
        {
          label: "Third-Party Services",
          desc: "Authentication, payments, and analytics may be provided by third-party services governed by their own terms and privacy policies.",
        },
      ],
    },
    {
      icon: <Scale className="w-5 h-5 text-red-500" />,
      title: "Disclaimer & Limitation of Liability",
      content: [
        {
          label: "Service Availability",
          desc: "LuckyHub is provided on an “as-is” and “as-available” basis. We do not guarantee uninterrupted or error-free operation.",
        },
        {
          label: "Limitation of Liability",
          desc: "To the maximum extent permitted by law, LuckyHub shall not be liable for indirect, incidental, or consequential damages. Our total liability shall not exceed the amount paid by you in the preceding 12 months.",
        },
      ],
    },
  ];

  return (
    <div className={theme === "dark" ? "dark" : ""}>
      <div className="min-h-screen bg-white text-zinc-900 dark:bg-black dark:text-white py-12 px-4 sm:px-6 transition-colors duration-300">
        <div className="max-w-4xl mx-auto">
          {/* Header */}
          <div className="flex justify-between items-center mb-10 border-b border-zinc-200 dark:border-zinc-800 pb-6">
            <div className="flex items-center gap-3">
              <button
                onClick={() => navigate(-1)}
                className="p-2 hover:bg-zinc-100 dark:hover:bg-zinc-900 rounded-lg transition-all text-zinc-500 dark:text-zinc-400 hover:text-red-500"
                title="Go Back"
              >
                <ArrowLeft size={20} />
              </button>
              <div className="bg-red-600/10 dark:bg-red-600/20 p-2 rounded-lg">
                <Shield className="w-6 h-6 text-red-500" />
              </div>
              <h1 className="text-2xl font-bold tracking-tight">
                Terms of Service
              </h1>
            </div>
            <span className="text-zinc-500 text-[10px] font-mono border border-zinc-200 dark:border-zinc-800 px-2 py-1 rounded bg-zinc-50 dark:bg-transparent">
              Updated: {lastUpdated}
            </span>
          </div>

          <div className="mb-8 p-6 rounded-xl border border-zinc-200 dark:border-red-900/30 bg-zinc-50 dark:bg-[#0a0a0a]">
            <h2 className="text-red-500 font-semibold mb-2">
              Welcome to LuckyHub
            </h2>
            <p className="text-zinc-600 dark:text-zinc-400 text-sm leading-relaxed">
              These Terms govern your access to and use of LuckyHub. By using
              the platform, you agree to comply with these Terms, our Privacy
              Policy, and all applicable third-party terms.
            </p>
          </div>

          <div className="space-y-4">
            {sections.map((section, idx) => (
              <div
                key={idx}
                className="p-6 rounded-xl border border-zinc-200 dark:border-zinc-900 bg-white dark:bg-[#050505] shadow-sm dark:shadow-none transition-hover hover:border-red-500/20"
              >
                <div className="flex items-center gap-3 mb-4">
                  {section.icon}
                  <h3 className="font-bold text-lg text-zinc-800 dark:text-white">
                    {section.title}
                  </h3>
                </div>
                {section.content ? (
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    {section.content.map((item, i) => (
                      <div key={i} className="space-y-1">
                        <h4 className="text-red-500 text-[11px] font-bold uppercase tracking-widest">
                          {item.label}
                        </h4>
                        <p className="text-zinc-600 dark:text-zinc-400 text-sm leading-snug">
                          {item.desc}
                        </p>
                      </div>
                    ))}
                  </div>
                ) : (
                  <p className="text-zinc-600 dark:text-zinc-400 text-sm leading-relaxed">
                    {section.desc}
                  </p>
                )}
              </div>
            ))}
          </div>

          {/* Footer */}
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

export default TermsOfService;
