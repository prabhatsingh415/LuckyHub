import React from "react";
import { Trophy, Crown, ClipboardCheck, History } from "lucide-react";
function QuickActions() {
  return (
    <div className="w-full flex flex-col border-2 border-zinc-200 dark:border-zinc-800 rounded-xl gap-8 p-4">
      <div className="flex gap-4">
        <ClipboardCheck color="#e02d2d" />
        <h1>Quick Actions</h1>
      </div>
      <div className="flex flex-col md:flex-row gap-6">
        <button className="bg-[#ff4d29] w-full p-4 flex flex-col gap-2 justify-center items-center border-2 border-zinc-200 dark:border-zinc-800 rounded-xl">
          <Trophy color="white" />
          <h1 className="text-white">Start New </h1>
        </button>
        <button className="bg-white hover:bg-zinc-200 dark:bg-[#121212] dark:hover:bg-[#232222] w-full p-4 flex flex-col gap-2 justify-center items-center border-2 border-zinc-200 dark:border-zinc-800 rounded-xl">
          <History color="#2568ce" /> <h1>My History</h1>
        </button>
        <button className="bg-white hover:bg-zinc-200 dark:bg-[#121212] dark:hover:bg-[#232222] w-full p-4 flex flex-col gap-2 justify-center items-center border-2 border-zinc-200 dark:border-zinc-800 rounded-xl">
          <Crown color="#f8e439" /> <h1>Manage Subscription</h1>
        </button>
      </div>
    </div>
  );
}

export default QuickActions;
