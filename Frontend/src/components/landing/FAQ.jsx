import React, { useState } from "react";
import { ChevronDown } from "lucide-react";

const FAQ = () => {
  const [openIndex, setOpenIndex] = useState(null);

  const toggleAccordion = (index) => {
    setOpenIndex(openIndex === index ? null : index);
  };

  const faqData = [
    {
      question: "How does LuckyHub select giveaway winners?",
      answer:
        "LuckyHub fetches comments directly using the official YouTube Data API and selects winners programmatically based on your inputs (number of winners, videos provided, and optional keyword filters). The process is fully automated with no manual interference.",
    },
    {
      question: "Does LuckyHub require access to my YouTube password?",
      answer:
        "No. LuckyHub uses Google OAuth and official YouTube APIs. We never ask for your YouTube password and only request the minimum read-only permissions needed to fetch comments.",
    },
    {
      question: "Can I filter comments using keywords?",
      answer:
        "Yes. You can apply keyword-based filtering so only comments containing specific words are considered during winner selection.",
    },
    {
      question: "Can LuckyHub pick multiple winners?",
      answer:
        "Yes. You can choose how many winners you want according to your plan, and LuckyHub will generate a list of unique winners in a single selection process.",
    },
    {
      question: "Can I select winners from multiple YouTube videos?",
      answer:
        "Yes. LuckyHub allows you to add upto 3 multiple YouTube video links and selects winners by processing comments across all provided videos.",
    },
    {
      question: "Is there a limit on how many giveaways I can run?",
      answer:
        "Yes. Giveaway limits depend on your subscription plan. Free users have limited monthly usage, while higher-tier plans offer increased or unlimited winner selections.",
    },
    {
      question: "How does subscription tracking work?",
      answer:
        "LuckyHub tracks how many giveaways and winners you have selected during the current subscription cycle and updates your remaining quota in real time.",
    },
    {
      question: "Can I view my previous giveaway or payment details?",
      answer:
        "Yes. LuckyHub stores your giveaway activity and last payment details, which you can view anytime from your dashboard.",
    },
  ];

  return (
    <div className="min-h-screen bg-white text-zinc-900 dark:bg-black dark:text-white py-16 px-4 sm:px-6 lg:px-8 font-sans transition-colors duration-300">
      <div className="max-w-3xl mx-auto">
        {/* Header Section */}
        <div className="text-center mb-12">
          <h2 className="text-3xl md:text-4xl font-bold mb-4 tracking-tight">
            Frequently Asked Questions
          </h2>
          <p className="text-zinc-600 dark:text-zinc-400 text-sm md:text-base">
            Answers to common questions about LuckyHub
          </p>
        </div>

        {/* Accordion Section */}
        <div className="space-y-4">
          {faqData.map((faq, index) => (
            <div
              key={index}
              className="border border-zinc-200 dark:border-zinc-800 rounded-xl overflow-hidden transition-all duration-300 bg-zinc-50 dark:bg-[#0a0a0a]"
            >
              <button
                onClick={() => toggleAccordion(index)}
                className="w-full flex justify-between items-center p-5 text-left hover:bg-zinc-100 dark:hover:bg-zinc-900/50 transition-colors"
              >
                <span className="text-sm md:text-base font-semibold pr-4">
                  {faq.question}
                </span>
                <ChevronDown
                  className={`w-5 h-5 text-red-600 transition-transform duration-300 ${
                    openIndex === index ? "rotate-180" : ""
                  }`}
                />
              </button>

              <div
                className={`transition-all duration-300 ease-in-out overflow-hidden ${
                  openIndex === index
                    ? "max-h-60 opacity-100"
                    : "max-h-0 opacity-0"
                }`}
              >
                <div className="p-5 pt-0 text-zinc-600 dark:text-zinc-400 text-sm leading-relaxed border-t border-zinc-200 dark:border-zinc-800/50 mt-2">
                  {faq.answer}
                </div>
              </div>
            </div>
          ))}
        </div>

        {/* Footer Support Note */}
        <div className="mt-12 text-center text-zinc-500 text-sm">
          Still have questions?{" "}
          <a
            href="#contact"
            className="text-red-500 hover:text-red-600 font-medium hover:underline"
          >
            Contact our support team
          </a>
        </div>
      </div>
    </div>
  );
};

export default FAQ;
