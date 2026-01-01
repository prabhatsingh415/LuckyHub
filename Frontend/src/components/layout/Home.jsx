import React, { useState, useEffect } from "react";
import {
  Trophy,
  Sparkles,
  Link as LinkIcon,
  Trash2,
  Plus,
  RefreshCw,
  ExternalLink,
  Gamepad2,
  ShoppingBag,
} from "lucide-react";
import {
  useGetSubscriptionQuery,
  useGetWinnersMutation,
} from "../../Redux/slices/apiSlice";
import { motion, AnimatePresence } from "framer-motion";
import confetti from "canvas-confetti";
import InfoModal from "../../pages/InfoModal";

// --- Reward Options ---
const REWARD_PLATFORMS = [
  {
    name: "Codashop",
    url: "https://www.codashop.com",
    icon: <Gamepad2 size={16} />,
    color: "bg-purple-600",
  },
  {
    name: "Amazon Gift",
    url: "https://www.amazon.com/gc",
    icon: <ShoppingBag size={16} />,
    color: "bg-yellow-600",
  },
  {
    name: "Razer Gold",
    url: "https://gold.razer.com",
    icon: <Trophy size={16} />,
    color: "bg-green-600",
  },
];

function Home() {
  // --- States ---
  const [urls, setUrls] = useState([""]);
  const [winnersCount, setWinnersCount] = useState("1");
  const [keyword, setKeyword] = useState("");

  // 'input' -> 'fetching' -> 'countdown' -> 'result'
  const [gameState, setGameState] = useState("input");
  const [countdown, setCountdown] = useState(3);

  const [finalWinners, setFinalWinners] = useState([]);

  const [getWinners, { isLoading }] = useGetWinnersMutation();

  const [modal, setModal] = useState({
    open: false,
    title: "",
    message: "",
    type: "info",
  });

  const addInput = () => {
    if (urls.length < 3) setUrls([...urls, ""]);
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

  const { data: subscriptionData, error: subError } = useGetSubscriptionQuery();

  if (subError) {
    setModal({
      open: true,
      type: "error",
      title: "Error",
      message: subError?.data?.message || "Something Went Wrong !",
    });
  }

  const getWinnerOptions = () => {
    const max = subscriptionData?.maxWinners;

    // if (!max || max === null || max === undefined) return [];

    // if (max <= 5) {
    //   return Array.from({ length: max }, (_, i) => i + 1);
    // }

    return [1, 2, 3, 5, 7, 10, 15, 20];
  };

  // --- Pick Winners ---
  const handlePickWinner = async () => {
    try {
      setGameState("fetching");

      // 1. Fetch Data
      const response = await getWinners({
        videoLinks: urls.filter((url) => url.trim() !== ""),
        keyword: keyword.trim(),
        numberOfWinners: parseInt(winnersCount),
      }).unwrap();

      if (response && response.winners && response.winners.length > 0) {
        setFinalWinners(response.winners);
        setCountdown(3);
        setGameState("countdown"); // Start animation
      } else {
        throw new Error("No eligible comments found.");
      }
    } catch (err) {
      setGameState("input");
      setModal({
        open: true,
        type: "error",
        title: "Error",
        message:
          err?.data?.message ||
          "Could not fetch comments. Check URL/Privacy settings.",
      });
    }
  };

  // --- Countdown Timer ---
  useEffect(() => {
    let timer;
    if (gameState === "countdown") {
      timer = setInterval(() => {
        setCountdown((prev) => {
          if (prev <= 1) {
            clearInterval(timer);
            setGameState("result"); // Trigger Result
            triggerConfetti();
            return 0;
          }
          return prev - 1;
        });
      }, 1000); // 1 second interval
    }
    return () => clearInterval(timer);
  }, [gameState]);

  // --- Confetti Effect ---
  const triggerConfetti = () => {
    const duration = 3000;
    const animationEnd = Date.now() + duration;
    const defaults = { startVelocity: 30, spread: 360, ticks: 60, zIndex: 100 };

    const randomInRange = (min, max) => Math.random() * (max - min) + min;

    const interval = setInterval(function () {
      const timeLeft = animationEnd - Date.now();
      if (timeLeft <= 0) {
        return clearInterval(interval);
      }
      const particleCount = 50 * (timeLeft / duration);
      confetti({
        ...defaults,
        particleCount,
        origin: { x: randomInRange(0.1, 0.3), y: Math.random() - 0.2 },
      });
      confetti({
        ...defaults,
        particleCount,
        origin: { x: randomInRange(0.7, 0.9), y: Math.random() - 0.2 },
      });
    }, 250);
  };

  // --- Reset ---
  const resetGiveaway = () => {
    setGameState("input");
    setCountdown(3);
    setUrls([""]);
    setKeyword("");
    setFinalWinners([]);
  };

  // 1. LOADING SCREEN
  if (gameState === "fetching") {
    return (
      <div className="w-full min-h-[60vh] flex flex-col items-center justify-center text-white space-y-6">
        <div className="w-20 h-20 border-4 border-orange-500 border-t-transparent rounded-full animate-spin" />
        <h2 className="text-xl font-bold animate-pulse text-zinc-300">
          Reading Comments...
        </h2>
      </div>
    );
  }

  // 2. COUNTDOWN
  if (gameState === "countdown") {
    return (
      <div className="w-full min-h-[60vh] flex flex-col items-center justify-center text-white relative overflow-hidden  dark:bg-[#0A0A0A]">
        <motion.div
          initial={{ scale: 0.8, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
          className="text-center z-10"
        >
          <h1 className="text-4xl md:text-6xl font-black text-orange-500 mb-8 tracking-tighter uppercase animate-bounce">
            Drum Roll Please!
          </h1>

          <AnimatePresence mode="wait">
            <motion.div
              key={countdown}
              initial={{ scale: 0.5, opacity: 0 }}
              animate={{ scale: 1.5, opacity: 1 }}
              exit={{ scale: 2, opacity: 0 }}
              transition={{ duration: 0.4 }}
              className="text-[10rem] leading-none font-black bg-gradient-to-b from-yellow-300 to-orange-600 bg-clip-text text-transparent drop-shadow-[0_0_35px_rgba(234,88,12,0.5)]"
            >
              {countdown}
            </motion.div>
          </AnimatePresence>
        </motion.div>
      </div>
    );
  }

  // 3. WINNER REVEAL
  if (gameState === "result" && finalWinners.length > 0) {
    return (
      <div className="w-full max-w-4xl mx-auto p-4 flex flex-col items-center animate-in fade-in zoom-in duration-500 pt-10">
        <div className="w-full flex justify-between items-center mb-6">
          <h2 className="text-2xl font-bold text-white flex items-center gap-2">
            <Trophy className="text-yellow-400" /> Winners Circle
          </h2>
          <button
            onClick={resetGiveaway}
            className="flex items-center gap-2 px-4 py-2 bg-zinc-800 text-zinc-300 rounded-lg hover:bg-zinc-700 transition-all text-sm font-medium border border-zinc-700"
          >
            <RefreshCw size={16} /> Start New Giveaway
          </button>
        </div>

        {/* Winner Cards Stack */}
        <div className="grid grid-cols-1 gap-8 w-full">
          {finalWinners.map((winner, index) => (
            <div key={index} className="relative group">
              <div className="absolute -inset-1 bg-gradient-to-r from-yellow-600 via-orange-500 to-red-600 rounded-2xl blur opacity-30 group-hover:opacity-60 transition duration-1000"></div>

              <div className="relative bg-zinc-900 border border-zinc-700 rounded-2xl p-6 md:p-10 flex flex-col md:flex-row gap-8 items-center md:items-start text-center md:text-left shadow-2xl">
                {/* Rank Badge */}
                <div className="absolute top-4 left-4 md:-left-6 md:-top-6 bg-yellow-400 text-black font-black text-sm px-4 py-1.5 rounded-lg shadow-[0_0_20px_rgba(250,204,21,0.4)] transform -rotate-6 md:rotate-[-6deg] z-20 border-2 border-white">
                  #{index + 1} GRAND PRIZE
                </div>

                {/* Avatar */}
                <div className="w-28 h-28 md:w-36 md:h-36 shrink-0 rounded-full border-4 border-zinc-800 shadow-2xl overflow-hidden bg-zinc-800 relative z-10">
                  <div className="w-full h-full flex items-center justify-center bg-gradient-to-br from-orange-500 to-red-700 text-5xl font-bold text-white">
                    {winner.authorName?.charAt(0).toUpperCase()}
                  </div>
                </div>

                {/* Content */}
                <div className="flex-1 space-y-4 w-full">
                  <div>
                    <h3 className="text-2xl md:text-4xl font-black text-white mb-2 tracking-tight">
                      {winner.authorName}
                    </h3>
                    <p className="text-zinc-400 text-sm flex items-center justify-center md:justify-start gap-2">
                      <span className="w-2 h-2 bg-green-500 rounded-full animate-pulse shadow-[0_0_10px_rgba(34,197,94,0.5)]"></span>
                      Verified Comment •{" "}
                      {new Date(winner.publishedAt).toLocaleDateString()}
                    </p>
                  </div>

                  <div className="bg-black/40 p-5 rounded-xl border border-zinc-800/50">
                    <p className="text-zinc-200 text-lg italic">
                      "{winner.message}"
                    </p>
                  </div>

                  {/* --- Reward Actions Section --- */}
                  <div className="pt-6 border-t border-zinc-800/60 mt-4">
                    <p className="text-xs font-bold text-zinc-500 uppercase tracking-widest mb-4">
                      Send Reward Via
                    </p>
                    <div className="flex flex-wrap justify-center md:justify-start gap-3">
                      {REWARD_PLATFORMS.map((platform) => (
                        <a
                          key={platform.name}
                          href={platform.url}
                          target="_blank"
                          rel="noreferrer"
                          className={`${platform.color} hover:brightness-110 text-white px-5 py-2.5 rounded-lg text-sm font-bold flex items-center gap-2 transition-transform hover:scale-105 active:scale-95 shadow-lg shadow-black/40`}
                        >
                          {platform.icon} {platform.name}
                          <ExternalLink size={14} className="opacity-60" />
                        </a>
                      ))}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    );
  }

  return (
    <div className="w-full dark:bg-[#0a0a0a] flex flex-col justify-center items-center p-8 gap-8 dark:text-white">
      {/* Header */}
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
        {/* Banner */}
        <div className="w-full flex flex-col justify-center items-center">
          <div className="flex flex-row justify-center items-center w-auto p-4 border-2 border-[#ff4d29] bg-gradient-to-r from-[#ff3333]/5 to-[#ffeb3b]/2 rounded-4xl gap-4 text-xs md:text-2xl font-extrabold">
            <Trophy
              color="#ff4d29"
              className="w-6 h-6 sm:w-8 sm:h-8 md:w-10 md:h-10 lg:w-12 lg:h-12"
            />
            Start a New Giveaway
          </div>
        </div>

        {/* Inputs */}
        <section className="w-full lg:w-3xl mx-auto space-y-6 p-4 sm:p-6 sm:space-y-8">
          <div className="space-y-3 sm:space-y-4">
            {urls.map((url, index) => (
              <div key={index} className="flex items-center gap-2 sm:gap-3">
                <div className="relative flex-1">
                  <LinkIcon
                    size={16}
                    className="absolute left-3.5 top-1/2 -translate-y-1/2 text-zinc-400"
                  />
                  <input
                    type="url"
                    value={url}
                    onChange={(e) => handleUrlChange(index, e.target.value)}
                    placeholder={`Video URL ${index + 1}`}
                    className="w-full rounded-xl dark:bg-zinc-900/60 border border-zinc-800 px-11 py-3.5 text-sm dark:text-white focus:ring-2 focus:ring-orange-500 transition-all"
                  />
                </div>
                {urls.length > 1 && (
                  <button
                    onClick={() => removeInput(index)}
                    className="p-3.5 rounded-xl border border-zinc-800 text-red-500 hover:bg-red-500/10"
                  >
                    <Trash2 size={18} />
                  </button>
                )}
              </div>
            ))}
            {urls.length < 3 && (
              <button
                onClick={addInput}
                className="w-full flex items-center justify-center gap-2 rounded-xl border border-dashed border-zinc-700 py-4 text-sm text-zinc-400 hover:text-zinc-300"
              >
                <Plus size={18} /> Add Another Video URL
              </button>
            )}
          </div>

          {/* Keyword Input */}
          <div className="space-y-3 px-1">
            <h3 className="text-sm font-medium dark:text-zinc-200">
              Required Keyword
              <span className="text-[10px] text-zinc-500 ml-2">(Optional)</span>
            </h3>
            <input
              type="text"
              value={keyword}
              onChange={(e) => setKeyword(e.target.value)}
              placeholder="e.g. Winner2025"
              className="w-full rounded-xl dark:bg-zinc-900/60 border border-zinc-800 px-4 py-3.5 text-sm dark:text-white"
            />
          </div>

          {/* Winner Count & Button */}
          <div className="space-y-4">
            <select
              value={winnersCount}
              onChange={(e) => setWinnersCount(Number(e.target.value))}
              className="w-full rounded-xl dark:bg-zinc-900/60 border border-zinc-800 px-4 py-3 text-sm dark:text-white"
            >
              {getWinnerOptions().map((count) => (
                <option key={count} value={count}>
                  {count} {count === 1 ? "Winner" : "Winners"}
                </option>
              ))}
            </select>

            <button
              disabled={isLoading || !urls[0]}
              onClick={handlePickWinner}
              className="w-full flex items-center justify-center gap-3 rounded-xl bg-orange-500 py-4.5 text-sm sm:text-base font-bold text-black hover:bg-orange-400 active:scale-[0.98] transition-all shadow-lg shadow-orange-500/20"
            >
              <Sparkles size={18} />
              {isLoading
                ? "Fetching..."
                : `Pick ${winnersCount} Winner from Video`}
            </button>
          </div>
        </section>
      </div>

      {/* Error Modal */}
      {modal.open && (
        <InfoModal
          isOpen={modal.open}
          title={modal.title}
          message={modal.message}
          type={modal.type}
          okText="OK"
          onOk={() => setModal({ ...modal, open: false })}
        />
      )}
    </div>
  );
}

export default Home;
