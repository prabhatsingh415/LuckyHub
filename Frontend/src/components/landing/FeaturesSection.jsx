import { Trophy, Play, Users, Zap } from "lucide-react";
import React from "react";
import { useSelector } from "react-redux";

const theme = localStorage.getItem("theme");

const featuresData = [
  {
    index: 1,
    icon: <Play color={theme === "dark" ? "#ff4d29" : "#de0202"} />,
    title: "Fetch YouTube Comments",
    description:
      "Easily import comments from any public YouTube video using just the video URL",
  },
  {
    index: 2,
    icon: <Trophy color={theme === "dark" ? "#ff4d29" : "#de0202"} />,
    title: "Smart Winner Selection",
    description:
      "Advanced algorithms to ensure fair and random winner selection with customizable filters",
  },
  {
    index: 3,
    icon: <Users color={theme === "dark" ? "#ff4d29" : "#de0202"} />,
    title: "Manage Participants",
    description:
      "View, filter, and manage all participants with detailed engagement metrics",
  },
  {
    index: 4,
    icon: <Zap color={theme === "dark" ? "#ff4d29" : "#de0202"} />,
    title: "Instant Results",
    description:
      "Get instant winner selection results with animated reveal and export capabilities",
  },
];

function FeaturesSection() {
  return (
    <div className="flex flex-col justify-center items-center p-8 gap-8 dark:text-white">
      <div className="flex flex-col items-center gap-4 mb-8 mt-16">
        <h2 className="text-4xl">Powerful Features for Fair Selection</h2>
        <p className="text-xl text-[#a1a1a1]">
          Everything you need to run transparent and fair YouTube comment
          giveaways
        </p>
      </div>

      <div className="flex flex-col md:flex-row justify-center items-center gap-8">
        {featuresData.map((feature, index) => (
          <div
            key={index}
            className="w-full md:w-3/4 flex flex-col justify-center items-start gap-4 border border-[#a1a1a1] p-8 rounded-xl transform-gpu hover:scale-105 transition-transform duration-500 ease-out"
          >
            <div
              className={`p-2 flex justify-center items-center rounded-lg
                ${
                  theme === "dark"
                    ? "bg-gradient-to-r from-[#ff3333]/10 to-[#ffeb3b]/10"
                    : "bg-[#ffedeb]"
                }`}
            >
              {feature.icon}
            </div>

            <h3>{feature.title}</h3>
            <div className="text-[#a1a1a1] text-md flex  items-center justify-center ">
              {feature.description}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default FeaturesSection;
