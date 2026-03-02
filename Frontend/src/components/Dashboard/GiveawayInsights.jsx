import { useSelector } from "react-redux";
import { useHistoryQuery } from "../../Redux/slices/apiSlice";
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

function GiveawayInsights() {
  const { accessToken } = useSelector((state) => state.auth);

  const { data: historyData } = useHistoryQuery(undefined, {
    skip: !accessToken,
    refetchOnMountOrArgChange: true,
  });

  const chartData = historyData
    ? historyData.map((item, index) => ({
        id: index + 1,
        winnersCount: item.winnersCount,
        commentCount: item.commentCount,
        date: new Date(item.createdAt).toLocaleDateString(),
      }))
    : [];

  const brandOrange = "#ff4d29";

  return (
    <div className="w-full lg:flex-1 flex flex-col border-2 border-zinc-200 dark:border-zinc-800 rounded-xl gap-8 p-4">
      {/* Heading */}
      <div className="flex items-center gap-2">
        <h2 className="text-lg font-semibold dark:text-white">
          Giveaway Performance Insights
        </h2>
      </div>

      <div className="flex-1 flex flex-col border border-zinc-300 dark:border-zinc-700 rounded-xl p-4">
        <h3 className="font-medium text-gray-900 dark:text-white text-base">
          Winners Picked Over Time
        </h3>
        <p className="text-sm text-gray-500 dark:text-gray-400 mb-4">
          Daily / Weekly breakdown
        </p>

        {chartData.length !== 0 ? (
          <div className="w-full h-64">
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={chartData}>
                <CartesianGrid
                  strokeDasharray="3 3"
                  stroke="#374151"
                  vertical={false}
                />
                <XAxis
                  dataKey="date"
                  stroke={brandOrange}
                  fontSize={12}
                  tickLine={false}
                  axisLine={false}
                />
                <YAxis
                  stroke={brandOrange}
                  domain={[0, "auto"]}
                  fontSize={12}
                  tickLine={false}
                  axisLine={false}
                  allowDecimals={false}
                />
                <Tooltip
                  contentStyle={{
                    backgroundColor: "#111827",
                    borderRadius: "12px",
                    color: "#fff",
                    border: "1px solid #374151",
                  }}
                />
                <Line
                  type="monotone"
                  dataKey="winnersCount"
                  stroke={brandOrange}
                  strokeWidth={3}
                  dot={{ r: 4, fill: brandOrange, strokeWidth: 2 }}
                  activeDot={{ r: 6, strokeWidth: 0 }}
                />
              </LineChart>
            </ResponsiveContainer>
          </div>
        ) : (
          <div className="w-full h-64 flex items-center justify-center">
            <p className="text-gray-500 dark:text-gray-400 text-sm">
              No giveaway history found. Run a giveaway to see insights here.
            </p>
          </div>
        )}
      </div>

      <div className="flex-1 flex flex-col border border-zinc-300 dark:border-zinc-700 rounded-xl p-4">
        <h3 className="font-medium text-gray-900 dark:text-white text-base">
          Comments Analyzed per Giveaway
        </h3>
        <p className="text-sm text-gray-500 dark:text-gray-400 mb-4">
          How many comments you processed
        </p>
        {chartData.length !== 0 ? (
          <div className="w-full h-64">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={chartData}>
                <CartesianGrid
                  strokeDasharray="3 3"
                  stroke="#374151"
                  vertical={false}
                />
                <XAxis
                  dataKey="date"
                  stroke={brandOrange}
                  fontSize={12}
                  tickLine={false}
                  axisLine={false}
                />
                <YAxis
                  stroke={brandOrange}
                  fontSize={12}
                  tickLine={false}
                  axisLine={false}
                  allowDecimals={false}
                />
                <Tooltip
                  cursor={{ fill: "rgba(255, 77, 41, 0.1)" }}
                  contentStyle={{
                    backgroundColor: "#111827",
                    borderRadius: "12px",
                    color: "#fff",
                    border: "1px solid #374151",
                  }}
                />
                <Bar
                  dataKey="commentCount"
                  fill={brandOrange}
                  barSize={32}
                  radius={[6, 6, 0, 0]}
                />
              </BarChart>
            </ResponsiveContainer>
          </div>
        ) : (
          <div className="w-full h-64 flex items-center justify-center">
            <p className="text-gray-500 dark:text-gray-400 text-sm">
              No giveaway history found. Run a giveaway to see insights here.
            </p>
          </div>
        )}
      </div>
    </div>
  );
}

export default GiveawayInsights;
