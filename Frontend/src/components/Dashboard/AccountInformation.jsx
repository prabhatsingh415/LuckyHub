import { Trophy, User, Mail, Award, MessageSquare } from "lucide-react";

function AccountInformation({ data }) {
  const remaining = data?.user?.remainingGiveaways || 0;
  const used = Number(data?.user?.winnersSelectedThisMonth) || 0;
  const total = Number(data?.user?.maxGiveaways) || 0;

  const percent =
    total > 0 ? Math.min(Math.round((used / total) * 100), 100) : 0;

  return (
    <div className="w-full flex flex-col md:flex-row gap-8">
      <div className="w-full lg:w-1/4 flex flex-col border-2 border-zinc-200 dark:border-zinc-800 rounded-xl gap-8 p-4">
        <div className="flex gap-2">
          <User color="#e02d2d" />
          <h1 className="font-semibold">Account Information</h1>
        </div>

        <div className="flex flex-row justify-start items-start gap-3">
          <img
            src={data?.user?.avatarUrl}
            alt="Avatar"
            className="w-12 h-12 md:w-16 md:h-16 rounded-full flex-shrink-0 object-cover border border-zinc-700"
          />

          <div className="flex-1 min-w-0">
            <h2 className="text-lg md:text-xl font-bold leading-tight break-words dark:text-white">
              {data?.user?.firstName?.charAt(0).toUpperCase() +
                data?.user?.firstName?.slice(1)}{" "}
              {data?.user?.lastName?.charAt(0).toUpperCase() +
                data?.user?.lastName?.slice(1)}
            </h2>

            <div className="flex gap-1.5 items-start mt-1.5">
              <Mail
                color="#e02d2d"
                size={14}
                className="flex-shrink-0 mt-0.5"
              />
              <p className="text-xs md:text-sm text-zinc-400 break-all leading-relaxed">
                {data?.user?.email}
              </p>
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
  );
}

export default AccountInformation;
