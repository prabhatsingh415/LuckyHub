import { Play } from "lucide-react";

function MainContent() {
  const theme = localStorage.getItem("theme");

  return (
    <div className="flex flex-col justify-center items-center gap-8 dark:text-white p-8 my-16">
      <span
        className={`h-full w-fit px-4 text-[#ff3333] border border-[#ff3333]/20 rounded-lg
                 ${
                   theme === "dark"
                     ? "bg-gradient-to-r from-[#ff3333]/10 to-[#ffeb3b]/10"
                     : "bg-[#ffedeb]"
                 }`}
      >
        🎉 The #1 YouTube Comment Picker
      </span>
      <h1 className="text-6xl md:text-4xl lg:text-6xl font-bold leading-tight text-center">
        Pick Winners from{" "}
        <span className="bg-gradient-to-r from-[#ff4500] via-[#e1780f] to-[#d37815] bg-clip-text text-transparent">
          YouTube
        </span>
        <br />
        <span className="md:ml-8 bg-gradient-to-r from-[#ff9933] via-[#c68709] to-[#e2cd09] bg-clip-text text-transparent">
          Comments
        </span>{" "}
        Fairly & Easily
      </h1>

      <p className="text-[#a1a1a1] text-sm md:text-xl text-center max-w-md">
        The most trusted platform for content creators to run fair giveaways and
        <br />
        contests by randomly selecting winners from YouTube video comments.
      </p>
      <div className="flex justify-center items-center gap-4">
        <button className="px-6 py-2 bg-gradient-to-r from-[#f62a2a] to-[#f7490f] hover:from-[#f62a2a] hover:to-[#e73b02] text-white font-bold rounded-lg ">
          Start Picking Winners &rarr;
        </button>

        <button className="bg-[#121212] hover:bg-[#141313] border-2 border-[#1a1a1a] text-white rounded-lg px-6 py-2 flex items-center gap-2">
          Watch Demo
          <Play size={16} />
        </button>
      </div>

      <p className="text-md">Your Next Giveaway, Made Simple</p>
    </div>
  );
}

export default MainContent;
