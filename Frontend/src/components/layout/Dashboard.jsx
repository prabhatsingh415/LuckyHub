import {
  History,
  Trophy,
  Crown,
  ClipboardCheck,
  User,
  Mail,
  Award,
  MessageSquare,
} from "lucide-react";
import {
  LineChart,
  Line,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  CartesianGrid,
  ResponsiveContainer,
} from "recharts";
import {
  useDashboardAPIQuery,
  useHistoryQuery,
} from "../../Redux/slices/apiSlice";
import Loader from "../../pages/Loader";
import InfoModal from "../../pages/InfoModal";
import { useSelector } from "react-redux";

function Dashboard() {
  const { data, error, isLoading } = useDashboardAPIQuery();

  const { accessToken } = useSelector((state) => state.auth);

  const { data: historyData, error: historyError } = useHistoryQuery(
    undefined,
    { skip: !accessToken }
  );

  const chartData = historyData
    ? historyData.history.map((item, index) => ({
        id: index + 1,
        winnersCount: item.winnersCount,
        commentCount: item.commentCount,
        date: new Date(item.createdAt).toLocaleDateString(),
      }))
    : [];

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

  const remaining = data?.user?.remainingGiveaways || 0;
  const used = Number(data?.user?.winnersSelectedThisMonth) || 0;
  const total = Number(data?.user?.maxGiveaways) || 0;

  const percent =
    total > 0 ? Math.min(Math.round((used / total) * 100), 100) : 0;

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

      {/* Account Information */}
      <div className="w-full flex flex-col md:flex-row gap-8">
        <div className="w-full lg:w-1/4 flex flex-col border-2 border-zinc-200 dark:border-zinc-800 rounded-xl gap-8 p-4">
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
          <div className="border-t-2 border-zinc-200 dark:border-zinc-800 pt-4">
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
        <div className="w-full lg:flex-1 flex flex-col border-2 border-zinc-200 dark:border-zinc-800 rounded-xl gap-8 p-4">
          <div className="flex flex-col gap-2">
            <div className="flex gap-2">
              <Award color="#f8e439" />
              <h1>Current Month Usage</h1>
            </div>
            <p className="text-zinc-500">
              Your quota usage for this billing period
            </p>
          </div>

          <div className="flex justify-between mb-1">
            <span className="text-sm font-medium text-gray-900 dark:text-white">
              Giveaways Used
            </span>
            <span className="text-sm font-semibold text-gray-900 dark:text-white">
              {used}/{total}
            </span>
          </div>

          {/* Progress Bar */}
          <div className="w-full h-2 bg-gray-300 dark:bg-gray-700 rounded-full overflow-hidden">
            <div
              className="h-2 bg-black dark:bg-white transition-all duration-300"
              style={{ width: `${percent}%` }}
            ></div>
          </div>

          <div className="flex justify-between mt-1">
            <span className="text-xs text-gray-700 dark:text-gray-400">
              {remaining} remaining
            </span>
            <span className="text-xs text-red-600 font-medium">
              {percent}% used
            </span>
          </div>
          <div className="w-full flex flex-col md:flex-row gap-4 border-t-2 border-zinc-200 dark:border-zinc-800 p-4">
            <div className="w-full flex flex-col justify-center items-start border-2 border-zinc-200 dark:border-zinc-800 p-4 rounded-2xl">
              <div className="flex flex-col gap-2">
                <MessageSquare color="#2568ce" />
                <h1>Max Comments per Giveaway</h1>
              </div>
              <h1 className="text-2xl font-bold">{data?.user?.maxComments}</h1>
              <p className="text-xs text-zinc-500">
                Comments you can fetch in a single giveaway
              </p>
            </div>
            <div className="w-full border-2 border-zinc-200 dark:border-zinc-800 p-4 rounded-2xl">
              <div className="flex flex-col gap-2">
                <Trophy color="#e02d2d" />
                <h1>Max Winners per Giveaway</h1>
              </div>
              <h1 className="text-2xl font-bold">{data?.user?.maxWinners}</h1>
              <p className="text-xs text-zinc-500">
                Winners you can select in a single giveaway
              </p>
            </div>
          </div>
        </div>
      </div>

      <div className="w-full lg:flex-1 flex flex-col border-2 border-zinc-200 dark:border-zinc-800 rounded-xl gap-8 p-4">
        {/* Heading */}
        <div className="flex items-center gap-2">
          <h2 className="text-lg font-semibold dark:text-white">
            Giveaway Performance Insights
          </h2>
        </div>

        {/* Line Chart – Winners trend */}
        <div className="flex-1 flex flex-col border border-zinc-300 dark:border-zinc-700 rounded-xl p-4">
          <h3 className="font-medium text-gray-900 dark:text-white text-base">
            Winners Picked Over Time
          </h3>
          <p className="text-sm text-gray-500 dark:text-gray-400 mb-4">
            Daily / Weekly breakdown
          </p>

          <div className="w-full h-64">
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={chartData}>
                <CartesianGrid strokeDasharray="3 3" stroke="#9ca3af" />
                <XAxis dataKey="date" stroke="#ff3333" allowDecimals={false} />
                <YAxis
                  stroke="#ff3333"
                  domain={[1, 10]}
                  allowDecimals={false}
                />
                <Tooltip
                  contentStyle={{
                    backgroundColor: "#1f2937",
                    borderRadius: "8px",
                    color: "#fff",
                    border: "none",
                  }}
                />
                <Line type="monotone" dataKey="winnersCount" stroke="#8884d8" />
              </LineChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Bar Chart – Comment Count */}
        <div className="flex-1 flex flex-col border border-zinc-300 dark:border-zinc-700 rounded-xl p-4">
          <h3 className="font-medium text-gray-900 dark:text-white text-base">
            Comments Analyzed per Giveaway
          </h3>
          <p className="text-sm text-gray-500 dark:text-gray-400 mb-4">
            How many comments you processed
          </p>

          <div className="w-full h-64">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={chartData}>
                <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
                <XAxis dataKey="date" stroke="#9ca3af" />
                <YAxis
                  stroke="#9ca3af"
                  domain={[1, 1000]}
                  allowDecimals={false}
                />
                <Tooltip
                  contentStyle={{
                    backgroundColor: "#1f2937",
                    borderRadius: "8px",
                    color: "#fff",
                    border: "none",
                  }}
                />
                <Bar
                  dataKey="commentCount"
                  fill="#facc15"
                  barSize={32}
                  radius={[4, 4, 0, 0]}
                />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Dashboard;
