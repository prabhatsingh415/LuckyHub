import {
  History,
  Trophy,
  Crown,
  ClipboardCheck,
  User,
  Mail,
} from "lucide-react";
import { useDashboardAPIQuery } from "../../Redux/slices/apiSlice";
import Loader from "../../pages/Loader";
import InfoModal from "../../pages/InfoModal";

function Dashboard() {
  const { data, error, isLoading } = useDashboardAPIQuery();
  if (isLoading) {
    return <Loader />;
  }
  if (error) {
    return (
      <InfoModal
        isOpen={true}
        title="Error"
        message="Failed to load dashboard data. Please try again later."
        type="error"
        okText="OK"
        isContainsResendBtn={false}
      />
    );
  }
  return (
    <div className="w-full dark:bg-[#0a0a0a] flex flex-col justify-center items-center p-4 gap-8 dark:text-white">
      {/*Heading*/}
      <div className="w-full flex flex-col justify-start items-start">
        <h1 className="w-full text-xl md:text-3xl font-bold">
          LuckyHub Dashboard
        </h1>
        <p className="text-zinc-400 text-xs md:text-lg">
          Manage your giveaways and track performance
        </p>
      </div>
      {/*Quick Actions*/}
      <div className="w-full flex flex-col border-2 border-zinc-400 dark:border-zinc-800 rounded-xl gap-8 p-4">
        <div className="flex gap-4">
          <ClipboardCheck color="#e02d2d" />
          <h1>Quick Actions</h1>
        </div>
        <div className="flex flex-col md:flex-row gap-6">
          <button className="bg-[#ff4d29] w-full p-4 flex flex-col gap-2 justify-center items-center border-2 dark:border-zinc-800 rounded-xl">
            <Trophy />
            <h1>Start New </h1>
          </button>
          <button className="bg-[#121212] hover:bg-[#232222] w-full p-4 flex flex-col gap-2 justify-center items-center border-2 dark:border-zinc-800 rounded-xl">
            <History color="#2568ce" /> <h1>My History</h1>
          </button>
          <button className="bg-[#121212] hover:bg-[#232222] w-full p-4 flex flex-col gap-2 justify-center items-center border-2 dark:border-zinc-800 rounded-xl">
            <Crown color="#f8e439" /> <h1>Manage Subscription</h1>
          </button>
        </div>
      </div>
      {/* Account Information */}
      <div className="w-full flex flex-col border-2 border-zinc-400 dark:border-zinc-800 rounded-xl gap-8 p-4">
        <div className="flex gap-2">
          <User color="#e02d2d" />
          <h1>Account Information</h1>
        </div>

        <div className="flex flex-row justify-start items-center gap-2">
          {/*Avatar*/}
          <img
            src={data?.user?.avatarUrl}
            alt="Avatar"
            className="w-12 h-12 md:w-16 md:h-16 rounded-full"
          />
          {/* user name & email */}
          <div>
            <h2 className="text-lg md:text-xl font-bold mt-2">
              {data?.user?.firstName?.charAt(0).toUpperCase() +
                data.user.firstName?.slice(1)}{" "}
              {data?.user?.lastName?.charAt(0).toUpperCase() +
                data.user.lastName?.slice(1)}
            </h2>

            <div className="flex gap-0.5 md:gap-1.5 justify-center items-center">
              <Mail color="#e02d2d" size={12} />
              <p className="text-xs text-zinc-400">yourEmail@Mail.com</p>
            </div>
          </div>
        </div>
        {/* plan & memeber since*/}
        <div className="border-t-2 border-zinc-400 dark:border-zinc-800 pt-4">
          {/* Row 1 */}
          <div className="flex justify-between">
            <p className="text-gray-500 dark:text-gray-400 uppercase text-sm">
              Plan
            </p>
            <span
              className={`
    inline-flex items-center justify-center px-3 py-1 text-sm font-semibold rounded-lg uppercase tracking-wider
    ${
      data?.user?.subscriptionType === "Gold"
        ? "bg-[#ff3333] text-white"
        : data?.user?.subscriptionType === "Diamond"
        ? "bg-[#ffeb3b] text-gray-900"
        : "bg-gray-500 text-white"
    }
  `}
            >
              {data?.user?.subscriptionType}
            </span>
          </div>

          {/* Row 2 */}
          <div className="flex justify-between mt-2">
            <p className="text-gray-500 dark:text-gray-400 uppercase text-sm">
              Member Since
            </p>
            <p className="font-semibold text-gray-800 dark:text-gray-100 text-sm">
              {data?.user?.createdAt && !isNaN(new Date(data.user.createdAt))
                ? new Date(data.user.createdAt).toLocaleDateString("en-US", {
                    year: "numeric",
                    month: "long",
                    day: "numeric",
                  })
                : "N/A"}
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Dashboard;
