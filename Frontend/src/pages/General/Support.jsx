import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";
import {
  BookOpen,
  ChevronDown,
  ArrowLeft,
  ExternalLink,
  Github,
  Mail,
} from "lucide-react";

const Support = () => {
  const navigate = useNavigate();
  const [openFaq, setOpenFaq] = useState(null);

  const theme = useSelector((state) => state.theme.mode);

  const supportCards = [
    {
      title: "How it Works",
      desc: "Learn about the YouTube API and winner selection logic.",
      icon: <BookOpen className="text-red-500" />,
      action: "View Docs",
    },
    {
      title: "Source Code",
      desc: "Check out the project repository and contribution guidelines.",
      icon: (
        <Github
          className={theme === "dark" ? "text-zinc-400" : "text-zinc-600"}
        />
      ),
      action: "GitHub Repo",
    },
  ];

  const faqs = [
    {
      q: "How do I run a giveaway on LuckyHub?",
      a: "Simply paste your YouTube video link, set your preferred filters (like keywords or duplicate handling), and click 'Pick Winner'. The app will fetch comments in real-time.",
    },
    {
      q: "Is the winner selection truly random?",
      a: "Yes. LuckyHub uses a programmatic randomization algorithm to ensure every eligible comment has an equal chance of winning.",
    },
    {
      q: "How many videos can I process at once?",
      a: "Currently, you can add up to 3 video links to fetch and merge comments for a single large giveaway.",
    },
  ];

  return (
    <div className={theme === "dark" ? "dark" : ""}>
      <div className="min-h-screen bg-white text-zinc-900 dark:bg-black dark:text-white font-sans selection:bg-red-500/30 transition-colors duration-300">
        <div className="border-b border-zinc-200 dark:border-zinc-900 bg-white/80 dark:bg-black/80 backdrop-blur-md sticky top-0 z-50">
          <div className="max-w-5xl mx-auto px-6 py-4 flex justify-between items-center">
            <div className="flex items-center gap-4">
              <button
                onClick={() => navigate(-1)}
                className="p-2 hover:bg-zinc-100 dark:hover:bg-zinc-900 rounded-lg transition-all text-zinc-500 dark:text-zinc-400 hover:text-red-500"
                title="Go Back"
              >
                <ArrowLeft size={20} />
              </button>
              <h1 className="text-lg font-bold tracking-tight">
                Support Center
              </h1>
            </div>
          </div>
        </div>

        <div className="max-w-4xl mx-auto px-6 py-12">
          <div className="text-center mb-16">
            <h2 className="text-3xl md:text-4xl font-extrabold mb-4 tracking-tight">
              How can we help?
            </h2>
            <p className="text-zinc-600 dark:text-zinc-500 text-sm md:text-base max-w-lg mx-auto leading-relaxed">
              Find answers to common questions or explore the technical side of
              LuckyHub.
            </p>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 mb-16">
            {supportCards.map((card, idx) => (
              <div
                key={idx}
                className="bg-zinc-50 dark:bg-[#080808] border border-zinc-200 dark:border-zinc-800/50 p-6 rounded-2xl hover:border-red-500/50 transition-all group"
              >
                <div className="mb-4 bg-white dark:bg-zinc-900 w-12 h-12 rounded-xl flex items-center justify-center shadow-sm group-hover:bg-red-500/10 transition-colors border border-zinc-100 dark:border-transparent">
                  {card.icon}
                </div>
                <h3 className="font-bold mb-1 text-zinc-800 dark:text-white">
                  {card.title}
                </h3>
                <p className="text-zinc-500 dark:text-zinc-500 text-xs mb-4 leading-relaxed">
                  {card.desc}
                </p>
                <button className="text-red-500 text-xs font-bold flex items-center gap-1 hover:gap-2 transition-all">
                  {card.action} <ExternalLink size={12} />
                </button>
              </div>
            ))}
          </div>

          <div className="mb-20">
            <h3 className="text-xl font-bold mb-8 flex items-center gap-2">
              <span className="w-1.5 h-6 bg-red-600 rounded-full"></span>
              Frequently Asked Questions
            </h3>
            <div className="space-y-3">
              {faqs.map((item, idx) => (
                <div
                  key={idx}
                  className="border border-zinc-200 dark:border-zinc-900 rounded-xl overflow-hidden bg-white dark:bg-[#050505] shadow-sm dark:shadow-none"
                >
                  <button
                    onClick={() => setOpenFaq(openFaq === idx ? null : idx)}
                    className="w-full p-4 text-left flex justify-between items-center hover:bg-zinc-50 dark:hover:bg-zinc-900/50 transition-colors"
                  >
                    <span className="text-sm font-medium text-zinc-700 dark:text-zinc-300">
                      {item.q}
                    </span>
                    <ChevronDown
                      className={`transition-transform duration-300 ${
                        openFaq === idx
                          ? "rotate-180 text-red-600"
                          : "text-zinc-400 dark:text-zinc-600"
                      }`}
                      size={16}
                    />
                  </button>

                  {openFaq === idx && (
                    <div className="p-4 pt-0 text-zinc-600 dark:text-zinc-500 text-sm border-t border-zinc-100 dark:border-zinc-900/50 leading-relaxed mt-2 animate-in fade-in slide-in-from-top-1">
                      {item.a}
                    </div>
                  )}
                </div>
              ))}
            </div>
          </div>

          <div className="bg-gradient-to-b from-zinc-100 to-white dark:from-zinc-900/50 dark:to-black border border-zinc-200 dark:border-zinc-800 rounded-3xl p-8 text-center shadow-sm dark:shadow-none">
            <div className="bg-red-600/10 w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-6">
              <Mail className="text-red-500" size={28} />
            </div>
            <h2 className="text-2xl font-bold mb-2">Still have questions?</h2>
            <p className="text-zinc-600 dark:text-zinc-500 text-sm mb-8 max-w-sm mx-auto">
              Since this is a personal project, feel free to reach out to me via
              email for feedback or tech talk!
            </p>
            <a
              href="mailto:your-email@example.com"
              className="inline-flex items-center gap-2 bg-zinc-900 text-white dark:bg-white dark:text-black px-8 py-3 rounded-full font-bold text-sm hover:bg-black dark:hover:bg-zinc-200 transition-all transform hover:scale-105"
            >
              Send an Email
            </a>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Support;
