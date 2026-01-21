import { useNavigate } from "react-router-dom";
import PlanCard from "../PlanCard";
import { CircleCheckBig, CircleStar, Gem, Gift } from "lucide-react";

const subscriptionPlan = [
  {
    id: 1,
    icon: <Gift />,
    name: "Free",
    price: "$0/forever",
    description: "Perfect for getting started with giveaways",
    features: [
      "3 giveaways per month",
      "Up to 300 comments",
      "2 winner per giveaway",
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
      "10 giveaways per month",
      "Up to 600 comments",
      "Up to 5 winners per giveaway",
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
      "1000 comments",
      "10 winners per giveaway",
    ],
    cta: "Go Diamond",
  },
];
function Subscription() {
  const navigate = useNavigate();

  const handleClick = () => {
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
          <PlanCard onClick={handleClick} plan={plan} />
        ))}
      </div>
    </div>
  );
}

export default Subscription;
