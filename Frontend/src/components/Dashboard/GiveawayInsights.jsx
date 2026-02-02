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
  });

  const chartData = historyData
    ? historyData.history.map((item, index) => ({
        id: index + 1,
        winnersCount: item.winnersCount,
        commentCount: item.commentCount,
        date: new Date(item.createdAt).toLocaleDateString(),
      }))
    : [];
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

        <div className="w-full h-64">
          <ResponsiveContainer width="100%" height="100%">
            <LineChart data={chartData}>
              <CartesianGrid strokeDasharray="3 3" stroke="#9ca3af" />
              <XAxis dataKey="date" stroke="#ff3333" allowDecimals={false} />
              <YAxis stroke="#ff3333" domain={[1, 10]} allowDecimals={false} />
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
  );
}

export default GiveawayInsights;
