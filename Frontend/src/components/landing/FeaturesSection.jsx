import { Trophy, Play, Users, Zap } from "lucide-react";
import { useSelector } from "react-redux";

const featuresData = [
  {
    index: 1,
    Icon: Play,
    title: "Fetch YouTube Comments",
    description:
      "Easily import comments from any public YouTube video using just the video URL",
  },
  {
    index: 2,
    Icon: Trophy,
    title: "Smart Winner Selection",
    description:
      "Advanced algorithms to ensure fair and random winner selection with customizable filters",
  },
  {
    index: 3,
    Icon: Users,
    title: "Manage Participants",
    description:
      "View, filter, and manage all participants with detailed engagement metrics",
  },
  {
    index: 4,
    Icon: Zap,
    title: "Instant Results",
    description:
      "Get instant winner selection results with animated reveal and export capabilities",
  },
];

function FeaturesSection() {
  const theme = useSelector((state) => state.theme.mode);

  const iconColor = theme === "dark" ? "#ff4d29" : "#de0202";

  return (
    <div className="flex flex-col justify-center items-center p-8 gap-8 dark:text-white">
      <div className="flex flex-col items-center text-center gap-4 mb-8 mt-16">
        <h2 className="text-3xl md:text-4xl font-semibold">
          Powerful Features for Fair Selection
        </h2>
        <p className="text-lg md:text-xl text-gray-400 max-w-2xl">
          Everything you need to run transparent and fair YouTube comment
          giveaways
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:flex lg:flex-row justify-center items-stretch gap-8">
        {featuresData.map((feature) => (
          <div
            key={feature.index}
            className="w-full md:w-[300px] lg:w-1/4 flex flex-col justify-start items-start gap-4 border border-zinc-200 dark:border-zinc-800 p-8 rounded-2xl transform-gpu hover:scale-105 transition-all duration-500 ease-out dark:bg-zinc-900/20"
          >
            {/* Icon Container */}
            <div
              className={`p-3 flex justify-center items-center rounded-xl ${
                theme === "dark" ? "bg-red-500/10" : "bg-red-50"
              }`}
            >
              <feature.Icon color={iconColor} size={24} />
            </div>

            <h3 className="text-xl font-medium">{feature.title}</h3>
            <p className="text-gray-500 dark:text-gray-400 text-sm leading-relaxed">
              {feature.description}
            </p>
          </div>
        ))}
      </div>
    </div>
  );
}

export default FeaturesSection;
