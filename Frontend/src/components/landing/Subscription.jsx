import { CircleCheckBig, CircleStar, Gem, Gift } from "lucide-react";
import { useNavigate } from "react-router-dom";

const theme = localStorage.getItem("theme");

const subscriptionPlan = [
  {
    id: 1,
    icon: <Gift />,
    name: "Free",
    price: "$0/forever",
    description: "Perfect for getting started with giveaways",
    features: [
      "5 giveaways per month",
      "Up to 1,000 comments",
      "1 winner per giveaway",
      "Basic analytics",
      "Email support",
    ],
    cta: "Start Free",
  },
  {
    id: 2,
    icon: <CircleStar />,
    name: "Gold",
    price: "$49/month",
    description: "Ideal for frequent organizers with advanced features",
    features: [
      "20 giveaways per month",
      "Up to 5,000 comments",
      "Up to 3 winners per giveaway",
      "Advanced analytics",
      "Priority email support",
    ],
    cta: "Go Gold",
  },
  {
    id: 3,
    icon: <Gem />,
    name: "Diamond",
    price: "$99/month",
    description: "Unlimited giveaways and top-tier features for pros",
    features: [
      "Unlimited giveaways",
      "Unlimited comments",
      "Unlimited winners per giveaway",
      "Full analytics & export",
      "Priority support & custom redirects",
    ],
    cta: "Go Diamond",
  },
];

function Subscription() {
  const navigate = useNavigate();

  const handleClick = () => {
    const redirect = "/subscription";
    localStorage.setItem("redirect", redirect);
    navigate("/signup");
  };

  return (
    <div className="flex flex-col justify-center items-center p-8 gap-12 dark:text-white my-8">
      <div className="w-full flex flex-col justify-center items-center gap-4 mb-8">
        <h2 className="text-3xl sm:text-4xl font-semibold text-center leading-tight">
          Choose Your Perfect Plan
        </h2>

        <div className="w-full flex flex-col items-center justify-center">
          <p className="text-sm sm:text-xl text-[#a1a1a1] text-center max-w-md">
            Start for free and upgrade as your giveaway needs grow. All plans
            include
          </p>
          <p className="text-sm sm:text-xl text-[#a1a1a1] text-center max-w-md">
            fair winner selection.
          </p>
        </div>
      </div>

      <div className="flex flex-wrap justify-center items-stretch gap-8 w-full max-w-6xl">
        {subscriptionPlan.map((plan) => (
          <div
            key={plan.id}
            className={`flex flex-col justify-between items-center border border-[#a1a1a1] rounded-xl p-8 gap-4 transition-all duration-500 
              w-80 min-h-[500px] 
              ${
                plan.id === 2
                  ? "md:-mt-6 md:shadow-xl md:scale-105 md:z-10 md:bg-white md:dark:bg-[#1a1a1a]"
                  : "md:z-0 md:bg-white md:dark:bg-[#111]"
              }`}
          >
            {/* Icon */}
            <div
              className={`w-fit p-2 flex justify-center items-center rounded-lg
                ${
                  theme === "dark"
                    ? "bg-gradient-to-r from-[#ff3333]/10 to-[#ffeb3b]/10"
                    : "bg-[#ffedeb]"
                }`}
            >
              {plan.icon}
            </div>

            {/* Title */}
            <h1 className="text-3xl font-semibold">{plan.name}</h1>
            <p className="text-gray-500 dark:text-gray-300 text-center">
              {plan.description}
            </p>

            {/* Features */}
            <div className="flex flex-col gap-2 w-full flex-1">
              {plan.features.map((feature, index) => (
                <div key={index} className="flex gap-2 items-start">
                  <CircleCheckBig color="#ff4d29" />
                  <p>{feature}</p>
                </div>
              ))}
            </div>

            {/* CTA Button */}
            <button
              onClick={handleClick}
              className={`w-full p-3 rounded-lg font-semibold cursor-pointer transform-gpu hover:scale-105 transition-transform duration-500 ease-out
                ${
                  plan.id === 2
                    ? "bg-[#ff4d29] text-white"
                    : "bg-[#3c4859] text-white"
                }`}
            >
              {plan.cta}
            </button>
          </div>
        ))}
      </div>
    </div>
  );
}

export default Subscription;
