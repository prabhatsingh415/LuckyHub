import {
  Trophy,
  Sparkles,
  CheckSquare,
  ShieldCheck,
  Link,
  Trash2,
  Plus,
} from "lucide-react";
import React from "react";
import { useState } from "react";
import { useGetWinnersMutation } from "../../Redux/slices/apiSlice";
import InfoModal from "../../pages/InfoModal";

function Home() {
  const steps = [
    {
      step: "1",
      title: "Paste Video URLs",
      desc: "Add up to 3 YouTube video URLs for your giveaway",
    },
    {
      step: "2",
      title: "Set Winners Count",
      desc: "Choose how many winners you want to select",
    },
    {
      step: "3",
      title: "Pick Winners",
      desc: "Click the button and winners are instantly selected",
    },
    {
      step: "4",
      title: "Announce Winners",
      desc: "Share the results with your audience",
    },
  ];
  const [urls, setUrls] = useState([""]);
  const [winners, setWinners] = useState("1");
  const [keyword, setKeyword] = useState("");

  const [getWinners, { isLoading, isSuccess, data, isError, error }] =
    useGetWinnersMutation();

  const [modal, setModal] = useState({
    open: false,
    title: "",
    message: "",
    type: "info",
  });

  const addInput = () => {
    if (urls.length < 3) {
      setUrls([...urls, ""]);
    }
  };

  const removeInput = (index) => {
    if (urls.length > 1) {
      const newUrls = urls.filter((_, i) => i !== index);
      setUrls(newUrls);
    }
  };

  const handleUrlChange = (index, value) => {
    const newUrls = [...urls];
    newUrls[index] = value;
    setUrls(newUrls);
  };

  const selectWinners = async () => {
    try {
      await getWinners({
        videoLinks: urls.filter((url) => url.trim() !== ""),
        keyword: keyword.trim(),
        numberOfWinners: parseInt(winners),
      }).unwrap();

      setModal({
        open: true,
        type: "success",
        title: "Success",
        message: "Winners have been selected! Scroll down to see the results.",
      });
    } catch (err) {
      setModal({
        open: true,
        type: "error",
        title: "Error",
        message:
          err?.data?.message || "Something went wrong, please try again!",
      });
    }
  };
  return (
    <div className="w-full dark:bg-[#0a0a0a] flex flex-col justify-center items-center p-8 gap-8 dark:text-white">
      <div className="w-full flex flex-col justify-start items-start">
        <h1 className="w-full text-xl md:text-3xl font-bold">
          YouTube Comment Picker
        </h1>
        <p className="text-zinc-400 text-xs md:text-lg">
          Fetch comments from up to 3 YouTube videos and instantly select
          winners
        </p>
      </div>
      <div className="w-full flex flex-col border-2 border-zinc-200 dark:border-zinc-800 bg-gradient-to-r from-[#ff3333]/10 to-[#ffeb3b]/10 rounded-xl gap-8 p-4">
        <div className="w-full flex flex-col justify-center items-center">
          <div
            className="flex flex-row justify-center items-center w-auto p-4 border-2 border-[#ff4d29] bg-gradient-to-r from-[#ff3333]/5 to-[#ffeb3b]/2 rounded-4xl gap-4 
                    text-xs md:text-2xl font-extrabold"
          >
            <Trophy
              color="#ff4d29"
              className="w-6 h-6 sm:w-8 sm:h-8 md:w-10 md:h-10 lg:w-12 lg:h-12"
            />
            Start a New Giveaway
          </div>
          <p className="text-center text-xs md:text-sm mt-4">
            <span className="block sm:inline">
              Paste up to 3 YouTube video URLs and
            </span>
            <span className="block sm:inline sm:ml-1">
              select how many winners you want
            </span>
          </p>
        </div>
        <section className="w-full lg:w-3xl mx-auto space-y-6 p-4 sm:p-6 sm:space-y-8">
          <div className="space-y-3 sm:space-y-4">
            {urls.map((url, index) => (
              <div
                key={index}
                className="flex items-center gap-2 sm:gap-3 animate-in fade-in slide-in-from-top-2 duration-200"
              >
                <div className="relative flex-1">
                  <Link
                    size={16}
                    className="absolute left-3.5 top-1/2 -translate-y-1/2 text-zinc-400"
                  />
                  <input
                    type="url"
                    value={url}
                    onChange={(e) => handleUrlChange(index, e.target.value)}
                    placeholder={`Video URL ${index + 1}`}
                    className="w-full rounded-xl bg-zinc-900/60 border border-zinc-800 px-11 py-3.5 text-sm text-white placeholder-zinc-500 focus:outline-none focus:ring-2 focus:ring-orange-500 transition-all"
                  />
                </div>

                {/* Trash icon - Better touch size */}
                {urls.length > 1 && (
                  <button
                    onClick={() => removeInput(index)}
                    className="p-3.5 rounded-xl border border-zinc-800 text-red-500 hover:bg-red-500/10 active:scale-95 transition-all"
                    aria-label="Delete URL"
                  >
                    <Trash2 size={18} />
                  </button>
                )}
              </div>
            ))}

            {urls.length < 3 && (
              <button
                onClick={addInput}
                className="w-full flex items-center justify-center gap-2 rounded-xl border border-dashed border-zinc-700 py-4 text-sm text-zinc-400 hover:border-zinc-500 hover:text-zinc-300 active:bg-zinc-800/50 transition-all"
              >
                <Plus size={18} />
                Add Another Video URL ({urls.length}/3)
              </button>
            )}
          </div>
          <div className="space-y-3 px-1">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <h3 className="text-sm font-medium text-zinc-200">
                  Required Keyword
                </h3>
                <span className="text-[10px] text-zinc-500 bg-zinc-800 px-2 py-0.5 rounded border border-zinc-700">
                  Optional
                </span>
              </div>
            </div>

            <div className="relative">
              <span className="absolute left-4 top-1/2 -translate-y-1/2 text-zinc-500 text-sm font-bold">
                #
              </span>
              <input
                type="text"
                value={keyword}
                onChange={(e) => setKeyword(e.target.value)}
                placeholder="e.g. Winner2025"
                className="w-full rounded-xl bg-zinc-900/60 border border-zinc-800 px-10 py-3.5 text-sm text-white placeholder-zinc-600 focus:outline-none focus:ring-2 focus:ring-orange-500 transition-all"
              />
            </div>
            <p className="text-[11px] text-zinc-500 leading-relaxed">
              Server will prioritize comments containing this keyword. If none
              are found, a winner will be picked from all comments.
            </p>
          </div>
          <div className="space-y-2">
            <div className="flex items-center justify-between">
              <h3 className="text-sm font-medium text-white">
                Number of Winners
              </h3>
              <span className="text-xs bg-red-500/15 text-red-400 px-3 py-1 rounded-full border border-red-500/20">
                Max 2 per giveaway (GOLD)
              </span>
            </div>

            <select
              value={winners}
              onChange={(e) => setWinners(e.target.value)}
              className="w-full rounded-xl bg-zinc-900/60 border border-zinc-800 px-4 py-3 text-sm text-white focus:outline-none focus:ring-2 focus:ring-orange-500"
            >
              <option value="1">1 Winner</option>
              <option value="2">2 Winners</option>
            </select>

            <p className="text-xs text-zinc-500">
              Winners will be randomly selected from all eligible comments
            </p>
          </div>
          <button
            disabled={isLoading || !urls[0] || !winners}
            onClick={selectWinners}
            className="w-full flex items-center justify-center gap-3 rounded-xl bg-orange-500 py-4.5 text-sm sm:text-base font-bold text-black hover:bg-orange-400 active:scale-[0.98] transition-all shadow-lg shadow-orange-500/20"
          >
            <Sparkles size={18} />
            Pick {winners} {winners === "1" ? "Winner" : "Winners"} from{" "}
            {urls.length} {urls.length === 1 ? "Video" : "Videos"}{" "}
          </button>
          <div className="flex flex-wrap items-center justify-center gap-6 text-xs text-zinc-400">
            <div className="flex items-center gap-2">
              <CheckSquare size={14} /> Auto-fetch comments
            </div>
            <div className="flex items-center gap-2">
              <ShieldCheck size={14} /> Fair random selection
            </div>
            <div className="flex items-center gap-2">
              <Trophy size={14} /> Instant results
            </div>
          </div>
        </section>

        {data && (
          <div className="w-full space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-500">
            {/* 1. Status Header */}
            <div className="flex flex-col items-center justify-center text-center space-y-2">
              <div className="bg-zinc-900/50 border border-orange-500/30 rounded-2xl p-6 shadow-2xl shadow-orange-500/10">
                <h2 className="text-2xl md:text-3xl font-black text-white flex items-center gap-3">
                  <Sparkles className="text-orange-500" />
                  Winners Selected!
                  <Sparkles className="text-orange-500" />
                </h2>
                <p className="text-zinc-400 text-sm mt-2 font-medium">
                  Total Winners:{" "}
                  <span className="text-orange-500">{winners}</span> winners
                  selected by you
                </p>
              </div>
            </div>

            {/* 2. Giveaway Info Card */}
            <div className="w-full bg-zinc-900/40 border border-zinc-800 rounded-2xl p-6 space-y-4">
              <h3 className="text-lg md:text-xl font-bold text-white leading-tight">
                iPhone 15 Pro Max Giveaway - Subscribe & Comment to Win!
              </h3>
              <div className="flex flex-wrap gap-4 text-xs md:text-sm text-zinc-500 font-medium">
                <div className="flex items-center gap-1.5">
                  <CheckSquare size={16} className="text-orange-500/70" /> 1.5K
                  comments
                </div>
                <div className="flex items-center gap-1.5">
                  <Trophy size={16} className="text-orange-500/70" /> {winners}{" "}
                  winner(s)
                </div>
                <div className="flex items-center gap-1.5">
                  <ShieldCheck size={16} className="text-orange-500/70" />{" "}
                  {new Date().toLocaleString()}
                </div>
              </div>
            </div>

            {/* 3. Winners List (Mapping from API Data) */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {data.winners?.map((winner, index) => (
                <div key={index} className="relative group">
                  {/* Winner Badge */}
                  <div className="absolute -top-3 -right-3 z-10 bg-yellow-400 text-black text-[10px] font-black px-3 py-1 rounded-full shadow-lg transform rotate-12 group-hover:rotate-0 transition-transform">
                    #{index + 1} WINNER
                  </div>

                  <div className="h-full bg-zinc-900/60 border border-zinc-800 rounded-2xl p-5 hover:border-orange-500/50 transition-all">
                    <div className="flex items-start gap-4">
                      {/* Avatar Simulation */}
                      <div className="w-12 h-12 rounded-full bg-gradient-to-br from-orange-500 to-red-600 flex items-center justify-center text-white font-bold text-xl shrink-0">
                        {winner.authorName?.charAt(0).toUpperCase()}
                      </div>

                      <div className="space-y-1">
                        <h4 className="text-white font-bold text-base">
                          {winner.authorName}
                        </h4>
                        <p className="text-[10px] text-zinc-500">
                          Published:{" "}
                          {new Date(winner.publishedAt).toLocaleDateString()} •
                          Video ID: {winner.videoId}
                        </p>
                      </div>
                    </div>

                    {/* Comment Message */}
                    <div className="mt-4 bg-zinc-950/50 border border-zinc-800/50 rounded-xl p-4">
                      <p className="text-sm text-zinc-300 italic leading-relaxed">
                        "{winner.message}"
                      </p>
                    </div>

                    {/* Extra Stats Table/List */}
                    <div className="mt-4 flex items-center justify-between border-t border-zinc-800 pt-4">
                      <div className="flex gap-3">
                        <span
                          className={`text-[10px] font-bold px-2 py-1 rounded-md ${
                            winner.containsKeyword
                              ? "bg-green-500/10 text-green-400 border border-green-500/20"
                              : "bg-zinc-800 text-zinc-500"
                          }`}
                        >
                          {winner.containsKeyword
                            ? "KEYWORD FOUND"
                            : "NO KEYWORD"}
                        </span>
                        <span className="text-[10px] font-bold bg-blue-500/10 text-blue-400 border border-blue-500/20 px-2 py-1 rounded-md">
                          FREQ: {winner.frequency}
                        </span>
                      </div>
                      <div className="flex items-center gap-1 text-[10px] text-zinc-500 font-bold uppercase tracking-widest">
                        <div className="w-2 h-2 rounded-full bg-green-500 animate-pulse" />
                        Verified
                      </div>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}
        <section className="w-full mx-auto py-12 px-4">
          <div className="bg-zinc-900/40 border border-zinc-800 rounded-2xl p-6 sm:p-10">
            {/* Heading */}
            <div className="mb-10 text-center sm:text-left">
              <h2 className="text-xl font-bold text-white mb-2">
                How to Use LuckyHub
              </h2>
              <p className="text-zinc-400 text-sm">
                Follow these simple steps to run your giveaway
              </p>
            </div>

            <div className="grid grid-cols-1 gap-y-10 sm:grid-cols-2 lg:grid-cols-4 gap-x-6">
              {steps.map((item, index) => (
                <div
                  key={index}
                  className="flex flex-col items-center text-center space-y-4 group"
                >
                  {/* Circle Number with Gradient */}
                  <div className="relative">
                    <div className="w-12 h-12 rounded-full bg-gradient-to-br from-orange-400 to-red-600 flex items-center justify-center text-black font-bold text-lg shadow-lg shadow-orange-500/20 group-hover:scale-110 transition-transform">
                      {item.step}
                    </div>
                    {/* Connecting line for desktop (Except last item) */}
                    {index < 3 && (
                      <div className="hidden lg:block absolute top-1/2 -right-full w-full h-[1px] bg-zinc-800 -z-10" />
                    )}
                  </div>

                  <div className="space-y-1.5 px-2">
                    <h3 className="text-sm font-semibold text-white tracking-wide">
                      {item.title}
                    </h3>
                    <p className="text-xs text-zinc-500 leading-relaxed max-w-[160px] mx-auto">
                      {item.desc}
                    </p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </section>
      </div>
      {modal.open && (
        <InfoModal
          isOpen={modal.open}
          title={modal.title}
          message={modal.message}
          type={modal.type}
          okText="OK"
          isContainsResendBtn={false}
          onOk={() => setModal({ ...modal, open: false })}
        />
      )}
    </div>
  );
}

export default Home;
