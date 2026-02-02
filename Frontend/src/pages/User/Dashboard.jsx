import { Loader, InfoModal } from "../../components/Common";
import { useDashboardAPIQuery } from "../../Redux/slices/apiSlice";
import {
  AccountInformation,
  GiveawayInsights,
  QuickActions,
} from "../../components/Dashboard";

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
    <div className="w-full dark:bg-[#0a0a0a] flex flex-col justify-center p-4 gap-8 dark:text-white">
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
      <QuickActions />

      {/* Account Information */}
      <AccountInformation data={data} />

      {/*Giveaway Insights*/}
      <GiveawayInsights />
    </div>
  );
}

export default Dashboard;
