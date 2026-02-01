import { Crown, CircleArrowUp } from "lucide-react";
import { useNavigate } from "react-router-dom";

function SubscriptionSection({ dashboardData }) {
  const navigate = useNavigate();
  const handlePlanUpgrade = () => {
    navigate("/upgrade-plan");
  };

  return (
    <section className="rounded-2xl border border-zinc-200 dark:border-zinc-800 p-6">
      <div className="flex items-center gap-2 mb-4">
        <Crown size={18} />
        <h2 className="text-lg font-medium">Subscription</h2>{" "}
      </div>
      <p className="text-sm text-gray-400 mb-6">Your current plan and limits</p>
      <div className="rounded-xl dark:bg-[#060606] bg-[#f2f2f5] dark:text-gray-400 border border-zinc-200 dark:border-zinc-800  p-4 flex justify-between items-center gap-4 mb-6">
        <div className="flex justify-center items-center gap-6">
          <div className="h-10 w-10 flex items-center justify-center rounded-full bg-orange-500">
            <Crown size={20} color="yellow" />
          </div>
          <div>
            <p className="font-medium">Current Plan</p>
            <span className="mt-1 inline-block rounded-full bg-red-500/20 px-3 py-1 text-xs text-red-400">
              {dashboardData?.user?.subscriptionType || "FREE"}
            </span>
          </div>
        </div>
        {dashboardData?.user?.subscriptionType !== "DIAMOND" &&
          (dashboardData?.user?.subscriptionType === "FREE" ||
            (dashboardData?.user?.subscriptionType === "GOLD" &&
              dashboardData?.user?.remainingGiveaways <= 0)) && (
            <button
              onClick={handlePlanUpgrade}
              className="flex gap-2 bg-[#FF3E30] px-4 p-2 text-white font-bold rounded-xl hover:scale-105 hover:bg-orange-700"
            >
              <CircleArrowUp />
              <p>Upgrade Plan</p>
            </button>
          )}
      </div>
      <div className="space-y-4 ">
        <Limit
          label="Max Comments Per Giveaway"
          value={dashboardData?.user?.maxComments}
        />
        <Limit
          label="Max Winners Per Giveaway"
          value={dashboardData?.user?.maxWinners}
        />
        <Limit
          label="Remaining Giveaways"
          value={
            dashboardData?.user?.subscriptionType === "DIAMOND"
              ? "Unlimited"
              : dashboardData?.user?.remainingGiveaways
          }
        />
      </div>
    </section>
  );
}

function Limit({ label, value }) {
  return (
    <div className="flex items-center justify-between rounded-xl dark:bg-[#060606] bg-[#f2f2f5] dark:text-gray-400 border border-zinc-200 dark:border-zinc-800  p-4">
      <p>{label}</p>
      <span className="rounded-full dark:bg-[#060606] bg-[#f2f2f5] dark:text-gray-400 px-3 py-1 text-sm">
        {value}
      </span>
    </div>
  );
}

export default SubscriptionSection;
