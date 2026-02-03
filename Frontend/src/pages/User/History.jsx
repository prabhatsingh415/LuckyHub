import React, { useState } from "react";
import { useSelector } from "react-redux";
import { useHistoryQuery } from "../../Redux/slices/apiSlice";
import { Search, Trophy, Users, MessageSquare, Download, ExternalLink, Eye } from "lucide-react";
import { Loader } from "../../components/Common";

function History() {
  const { accessToken } = useSelector((state) => state.auth);
  const theme = useSelector((state) => state.theme.mode); // Redux se theme uthayi
  
  // Conditional fetching: API sirf tab chalegi jab accessToken ho
  const { data, isLoading } = useHistoryQuery(undefined, { skip: !accessToken });
  const [searchTerm, setSearchTerm] = useState("");

  if (isLoading) return <Loader />;

  const history = data?.history || [];
  const isDark = theme === "dark";

  // --- 1. SEARCH LOGIC ---
  // Winner handle ya Giveaway ID se search kar sakte ho
  const filteredHistory = history.filter((item) => 
    item.winners.some(winner => winner.toLowerCase().includes(searchTerm.toLowerCase())) ||
    item.id.toString().includes(searchTerm)
  );

  // Stats Calculation based on filtered data
  const totalWinners = filteredHistory.reduce((acc, curr) => acc + curr.winnersCount, 0);
  const totalComments = filteredHistory.reduce((acc, curr) => acc + curr.commentCount, 0);

  // --- 2. CSV EXPORT LOGIC ---
  const handleExport = () => {
    if (filteredHistory.length === 0) return;

    const headers = ["Date", "Giveaway_ID", "Comments_Analyzed", "Winners_Count", "Winners_List"];
    const csvRows = filteredHistory.map(item => [
      new Date(item.createdAt).toLocaleDateString(),
      `#${item.id}`,
      item.commentCount,
      item.winnersCount,
      `"${item.winners.join(", ")}"` // Quotes handles with commas
    ]);

    const csvContent = [headers, ...csvRows].map(e => e.join(",")).join("\n");
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.setAttribute("href", url);
    link.setAttribute("download", `LuckyHub_History_${new Date().toISOString().split('T')[0]}.csv`);
    link.click();
  };

  return (
    <div className={`w-full min-h-screen p-4 md:p-8 space-y-8 transition-colors duration-300 ${isDark ? "bg-[#0a0a0a] text-white" : "bg-[#f9fafb] text-zinc-900"}`}>
      
      {/* Header Section */}
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Winner History</h1>
          <p className={`${isDark ? "text-zinc-400" : "text-zinc-500"} text-sm mt-1`}>Manage and export your giveaway records</p>
        </div>
        <button 
          onClick={handleExport}
          className={`flex items-center gap-2 px-4 py-2 rounded-lg text-sm font-medium transition-all border ${
            isDark 
              ? "bg-zinc-900 border-zinc-800 hover:bg-zinc-800 text-white" 
              : "bg-white border-zinc-200 shadow-sm hover:bg-zinc-50 text-zinc-700"
          }`}
        >
          <Download size={16} /> Export CSV
        </button>
      </div>

      {/* Search Bar */}
      <div className={`flex flex-col md:flex-row gap-4 items-center p-2 rounded-xl border ${isDark ? "bg-zinc-900/50 border-zinc-800" : "bg-white border-zinc-200 shadow-sm"}`}>
        <div className="relative flex-1 w-full">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-zinc-500" size={18} />
          <input 
            type="text" 
            placeholder="Search by winner handle or ID..."
            className="w-full bg-transparent border-none focus:ring-0 text-sm pl-10"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-2 lg:grid-cols-3 gap-4">
        <StatsCard isDark={isDark} icon={<Trophy className="text-orange-500" />} label="Total Records" value={filteredHistory.length} />
        <StatsCard isDark={isDark} icon={<Users className="text-green-500" />} label="Total Winners" value={totalWinners} />
        <StatsCard isDark={isDark} icon={<MessageSquare className="text-blue-500" />} label="Total Comments" value={totalComments} />
      </div>

      {/* History List */}
      <div className="space-y-4 mt-8">
        <h3 className="text-lg font-semibold flex items-center gap-2">
          Giveaway History <span className={`text-xs font-normal ${isDark ? "text-zinc-500" : "text-zinc-400"}`}>Detailed view</span>
        </h3>
        
        {filteredHistory.length > 0 ? (
          filteredHistory.map((item) => (
            <HistoryItem key={item.id} item={item} isDark={isDark} />
          ))
        ) : (
          <div className={`text-center py-20 rounded-2xl border-2 border-dashed ${isDark ? "border-zinc-800 text-zinc-600" : "border-zinc-200 text-zinc-400"}`}>
            No records found matching your search.
          </div>
        )}
      </div>
    </div>
  );
}

// --- Sub-Components ---

const StatsCard = ({ icon, label, value, isDark }) => (
  <div className={`p-5 rounded-2xl flex items-center gap-4 border ${isDark ? "bg-zinc-900 border-zinc-800" : "bg-white border-zinc-200 shadow-sm"}`}>
    <div className={`p-3 rounded-xl ${isDark ? "bg-zinc-950" : "bg-zinc-50"}`}>{icon}</div>
    <div>
      <p className={`${isDark ? "text-zinc-500" : "text-zinc-400"} text-xs uppercase tracking-wider`}>{label}</p>
      <h4 className="text-xl font-bold">{value}</h4>
    </div>
  </div>
);

const HistoryItem = ({ item, isDark }) => (
  <div className={`group p-6 rounded-2xl transition-all border relative overflow-hidden ${
    isDark ? "bg-zinc-900/40 border-zinc-800 hover:border-zinc-700" : "bg-white border-zinc-200 shadow-sm hover:border-orange-200"
  }`}>
    <div className="flex flex-col md:flex-row gap-6 items-start">
      <div className={`w-16 h-16 rounded-xl flex items-center justify-center shrink-0 ${isDark ? "bg-zinc-800" : "bg-zinc-100"}`}>
        <Trophy size={24} className={isDark ? "text-zinc-600" : "text-zinc-400"} />
      </div>

      <div className="flex-1 min-w-0 space-y-3">
        <div className="flex justify-between items-start">
          <div className="min-w-0 flex-1">
            <h4 className={`text-lg font-bold transition-colors truncate ${isDark ? "group-hover:text-orange-500" : "group-hover:text-orange-600"}`}>
              YouTube Giveaway #{item.id}
            </h4>
            <div className="flex gap-3 text-xs text-zinc-500 mt-1">
              <span>{new Date(item.createdAt).toLocaleDateString()}</span>
              <span>• {item.commentCount} comments</span>
              <span className={`px-2 rounded-md text-[10px] uppercase font-bold ${isDark ? "bg-zinc-800 text-green-400" : "bg-green-50 text-green-600"}`}>Completed</span>
            </div>
          </div>
          <div className="flex gap-2 ml-4">
            <button className={`p-2 rounded-lg transition-all ${isDark ? "hover:bg-zinc-800 text-zinc-500 hover:text-white" : "hover:bg-zinc-50 text-zinc-400 hover:text-zinc-900"}`}><Eye size={18} /></button>
            <button className={`p-2 rounded-lg transition-all ${isDark ? "hover:bg-zinc-800 text-zinc-500 hover:text-white" : "hover:bg-zinc-50 text-zinc-400 hover:text-zinc-900"}`}><ExternalLink size={18} /></button>
          </div>
        </div>

        {/* Winner Badges: Backend 'winners' array ko map kar raha hai */}
        <div className={`p-3 rounded-xl border flex flex-wrap gap-2 ${isDark ? "bg-black/30 border-zinc-800/50" : "bg-zinc-50 border-zinc-100"}`}>
          {item.winners.map((handle, i) => (
            <span key={i} className="text-xs bg-orange-500/10 text-orange-600 px-3 py-1 rounded-full border border-orange-500/20 break-all">
              {handle} <span className="text-[10px] ml-1 opacity-70 font-semibold uppercase">Winner</span>
            </span>
          ))}
        </div>
      </div>
    </div>
  </div>
);

export default History;