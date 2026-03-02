import React, { useState } from "react";
import { useSelector } from "react-redux";
import { useHistoryQuery } from "../../Redux/slices/apiSlice";
import {
  Search,
  Trophy,
  Users,
  MessageSquare,
  Download,
  Calendar,
  CheckCircle,
  Hash,
  Eye,
  ExternalLink,
  X,
  Youtube,
  Tag,
} from "lucide-react";
import { Loader } from "../../components/Common";

function History() {
  const { accessToken } = useSelector((state) => state.auth);
  const theme = useSelector((state) => state.theme.mode);
  const { data, isLoading } = useHistoryQuery(undefined, {
    skip: !accessToken,
  });
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedItem, setSelectedItem] = useState(null);

  if (isLoading) return <Loader />;

  const history = Array.isArray(data) ? data : [];
  const isDark = theme === "dark";

  const filteredHistory = history.filter(
    (item) =>
      item.winners.some((w) =>
        w.toLowerCase().includes(searchTerm.toLowerCase())
      ) ||
      item.videoDetails.some((v) =>
        v.title.toLowerCase().includes(searchTerm.toLowerCase())
      ) ||
      item.id.toString().includes(searchTerm)
  );

  const totalWinners = filteredHistory.reduce(
    (acc, curr) => acc + curr.winnersCount,
    0
  );
  const totalComments = filteredHistory.reduce(
    (acc, curr) => acc + curr.commentCount,
    0
  );

  const handleExport = () => {
    if (filteredHistory.length === 0) return;
    const headers = ["Date", "ID", "Comments", "Winners", "Keyword", "Loyalty"];
    const csvRows = filteredHistory.map((item) => [
      new Date(item.createdAt).toLocaleDateString(),
      item.id,
      item.commentCount,
      item.winners.join(" | "),
      item.keywordUsed || "N/A",
      item.loyaltyFilterApplied ? "Yes" : "No",
    ]);
    const csvContent = [headers, ...csvRows].map((e) => e.join(",")).join("\n");
    const blob = new Blob([csvContent], { type: "text/csv" });
    const url = URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.download = `LuckyHub_History.csv`;
    link.click();
  };

  return (
    <div
      className={`w-full min-h-screen p-4 md:p-8 space-y-6 transition-colors ${
        isDark ? "bg-[#050505] text-white" : "bg-[#f8f9fa] text-zinc-900"
      }`}
    >
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold">Giveaway History</h1>
          <p
            className={`text-sm ${isDark ? "text-zinc-500" : "text-zinc-400"}`}
          >
            Review and export your past winners
          </p>
        </div>
        <button
          onClick={handleExport}
          className={`flex items-center gap-2 px-4 py-2 rounded-xl text-sm font-semibold border transition-all ${
            isDark
              ? "bg-zinc-900 border-zinc-800 hover:bg-zinc-800 text-white"
              : "bg-white border-zinc-200 hover:bg-zinc-50 shadow-sm"
          }`}
        >
          <Download size={16} /> Export CSV
        </button>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <StatsCard
          isDark={isDark}
          icon={<Trophy className="text-orange-500" />}
          label="Total Runs"
          value={filteredHistory.length}
        />
        <StatsCard
          isDark={isDark}
          icon={<Users className="text-blue-500" />}
          label="Total Winners"
          value={totalWinners}
        />
        <StatsCard
          isDark={isDark}
          icon={<MessageSquare className="text-green-500" />}
          label="Comments"
          value={totalComments}
        />
      </div>

      <div
        className={`flex items-center px-4 py-2 rounded-2xl border ${
          isDark
            ? "bg-zinc-900/50 border-zinc-800"
            : "bg-white border-zinc-200 shadow-sm"
        }`}
      >
        <Search className="text-zinc-500 mr-3" size={20} />
        <input
          type="text"
          placeholder="Search by winner handle, video title or ID..."
          className="w-full bg-transparent border-none focus:ring-0 text-sm py-2"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
      </div>

      <div className="space-y-4">
        {filteredHistory.length > 0 ? (
          filteredHistory.map((item) => (
            <HistoryItem
              key={item.id}
              item={item}
              isDark={isDark}
              onView={() => setSelectedItem(item)}
            />
          ))
        ) : (
          <div className="py-20 text-center rounded-3xl border-2 border-dashed border-zinc-800 text-zinc-500">
            No records found.
          </div>
        )}
      </div>

      {selectedItem && (
        <DetailModal
          item={selectedItem}
          isDark={isDark}
          onClose={() => setSelectedItem(null)}
        />
      )}
    </div>
  );
}

const StatsCard = ({ icon, label, value, isDark }) => (
  <div
    className={`p-4 rounded-2xl border flex items-center gap-4 ${
      isDark
        ? "bg-zinc-900/40 border-zinc-800"
        : "bg-white border-zinc-200 shadow-sm"
    }`}
  >
    <div className={`p-3 rounded-xl ${isDark ? "bg-zinc-950" : "bg-zinc-50"}`}>
      {icon}
    </div>
    <div>
      <p className="text-[10px] uppercase font-bold tracking-widest text-zinc-500">
        {label}
      </p>
      <h4 className="text-xl font-black">{value}</h4>
    </div>
  </div>
);

const HistoryItem = ({ item, isDark, onView }) => (
  <div
    onClick={onView}
    className={`group p-4 md:p-5 rounded-3xl border transition-all cursor-pointer ${
      isDark
        ? "bg-[#0c0c0c] border-zinc-800 hover:border-zinc-600"
        : "bg-white border-zinc-100 shadow-sm hover:border-orange-200"
    }`}
  >
    <div className="flex flex-col lg:flex-row gap-5">
      <div className="relative flex-shrink-0">
        <div className="flex -space-x-10 overflow-hidden">
          {item.videoDetails.slice(0, 3).map((video, idx) => (
            <img
              key={idx}
              src={video.thumbnail}
              alt="thumb"
              className={`w-28 h-16 md:w-32 md:h-20 object-cover rounded-xl border-2 ${
                isDark ? "border-zinc-900" : "border-white"
              } shadow-lg transform group-hover:translate-x-1 transition-transform`}
            />
          ))}
          {item.videoDetails.length > 3 && (
            <div
              className={`w-32 h-20 rounded-xl flex items-center justify-center text-xs font-bold border-2 ${
                isDark
                  ? "bg-zinc-800 border-zinc-900"
                  : "bg-zinc-100 border-white"
              }`}
            >
              +{item.videoDetails.length - 3} More
            </div>
          )}
        </div>
      </div>

      <div className="flex-1 space-y-3 min-w-0">
        <div className="flex justify-between items-start gap-2">
          <div className="truncate">
            <h4 className="font-bold text-base md:text-lg truncate group-hover:text-orange-500 transition-colors">
              {item.videoDetails[0]?.title || "Giveaway Session"}
            </h4>
            <div className="flex flex-wrap items-center gap-3 mt-1">
              <span className="flex items-center gap-1 text-[11px] text-zinc-500">
                <Calendar size={12} />
                {new Date(item.createdAt).toLocaleDateString()}
              </span>
              <span className="flex items-center gap-1 text-[11px] text-zinc-500">
                <MessageSquare size={12} /> {item.commentCount} Comments
              </span>
              <span className="flex items-center gap-1 text-[11px] text-zinc-500">
                <Hash size={12} /> ID: #{item.id}
              </span>
            </div>
          </div>
          <button className="p-2 rounded-full hover:bg-orange-500/10 text-zinc-400 hover:text-orange-500 transition-colors">
            <Eye size={20} />
          </button>
        </div>

        <div className="flex flex-wrap gap-2">
          {item.winners.slice(0, 3).map((handle, idx) => (
            <span
              key={idx}
              className="text-[10px] md:text-xs bg-orange-500/10 text-orange-600 px-3 py-1 rounded-full border border-orange-500/20 font-bold"
            >
              {handle}
            </span>
          ))}
          {item.winners.length > 3 && (
            <span className="text-[10px] text-zinc-500 mt-1 font-medium">
              +{item.winners.length - 3} more
            </span>
          )}
        </div>
      </div>
    </div>
  </div>
);

const DetailModal = ({ item, onClose, isDark }) => (
  <div className="fixed inset-0 z-[100] flex items-center justify-center p-4 bg-black/80 backdrop-blur-sm animate-in fade-in duration-200">
    <div
      className={`relative w-full max-w-2xl max-h-[90vh] overflow-y-auto rounded-3xl border shadow-2xl ${
        isDark ? "bg-[#0c0c0c] border-zinc-800" : "bg-white border-zinc-200"
      }`}
    >
      <div className="sticky top-0 z-10 flex items-center justify-between p-6 border-b border-zinc-800/50 bg-inherit/80 backdrop-blur-md">
        <div className="flex items-center gap-3">
          <div className="p-2 bg-orange-500/20 rounded-lg">
            <Trophy size={20} className="text-orange-500" />
          </div>
          <h2 className="text-xl font-bold">Giveaway Details</h2>
        </div>
        <button
          onClick={onClose}
          className="p-2 rounded-full hover:bg-zinc-800 transition-colors"
        >
          <X size={20} />
        </button>
      </div>

      <div className="p-6 space-y-8">
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
          <DetailStat
            label="Giveaway ID"
            value={`#${item.id}`}
            icon={<Hash size={14} />}
          />
          <DetailStat
            label="Date"
            value={new Date(item.createdAt).toLocaleDateString()}
            icon={<Calendar size={14} />}
          />
          <DetailStat
            label="Comments"
            value={item.commentCount}
            icon={<MessageSquare size={14} />}
          />
          <DetailStat
            label="Winners"
            value={item.winnersCount}
            icon={<Users size={14} />}
          />
        </div>

        <div className="space-y-4">
          <h3 className="text-sm font-bold uppercase tracking-widest text-zinc-500 flex items-center gap-2">
            <Youtube size={16} /> Videos Involved
          </h3>
          <div className="space-y-3">
            {item.videoDetails.map((video, idx) => (
              <div
                key={idx}
                className={`flex gap-4 p-3 rounded-2xl border ${
                  isDark
                    ? "bg-black/40 border-zinc-800"
                    : "bg-zinc-50 border-zinc-100"
                }`}
              >
                <img
                  src={video.thumbnail}
                  className="w-24 h-14 object-cover rounded-lg shadow-sm"
                  alt=""
                />
                <div className="flex-1 min-w-0">
                  <h4 className="text-sm font-bold truncate">{video.title}</h4>
                  <a
                    href={`https://youtube.com/watch?v=${video.videoId}`}
                    target="_blank"
                    rel="noreferrer"
                    className="text-xs text-orange-500 flex items-center gap-1 mt-1 font-semibold"
                  >
                    View on YouTube <ExternalLink size={10} />
                  </a>
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="space-y-4">
          <h3 className="text-sm font-bold uppercase tracking-widest text-zinc-500 flex items-center gap-2">
            <CheckCircle size={16} /> Filter Settings
          </h3>
          <div className="flex flex-wrap gap-3">
            <FilterBadge
              label="Keyword"
              value={item.keywordUsed || "None"}
              icon={<Tag size={12} />}
            />
            <FilterBadge
              label="Loyalty Filter"
              value={item.loyaltyFilterApplied ? "Enabled" : "Disabled"}
              color={
                item.loyaltyFilterApplied ? "text-blue-500" : "text-zinc-500"
              }
              icon={<CheckCircle size={12} />}
            />
          </div>
        </div>

        <div className="space-y-4">
          <h3 className="text-sm font-bold uppercase tracking-widest text-zinc-500 flex items-center gap-2">
            <Trophy size={16} /> Selected Winners
          </h3>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
            {item.winners.map((handle, idx) => (
              <div
                key={idx}
                className="flex items-center gap-3 p-3 rounded-xl bg-orange-500/5 border border-orange-500/10"
              >
                <div className="w-8 h-8 rounded-full bg-orange-500 flex items-center justify-center text-black font-bold text-xs">
                  {idx + 1}
                </div>
                <span className="font-bold text-orange-500">{handle}</span>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  </div>
);

const DetailStat = ({ label, value, icon }) => (
  <div className="space-y-1">
    <p className="text-[10px] text-zinc-500 uppercase font-bold flex items-center gap-1">
      {icon} {label}
    </p>
    <p className="text-sm font-black">{value}</p>
  </div>
);

const FilterBadge = ({ label, value, color = "text-orange-500", icon }) => (
  <div className="flex flex-col bg-zinc-900/50 border border-zinc-800 p-3 rounded-xl min-w-[120px]">
    <span className="text-[9px] uppercase font-bold text-zinc-500 flex items-center gap-1">
      {icon} {label}
    </span>
    <span className={`text-xs font-bold mt-1 ${color}`}>{value}</span>
  </div>
);

export default History;
