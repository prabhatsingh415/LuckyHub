import { ArrowRight } from "lucide-react";
import React from "react";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";

function CTASection() {
  const theme = useSelector((state) => state.theme.mode);
  const navigate = useNavigate();
  return (
    <div className="flex flex-col justify-center items-center p-6 px-8 md:p-12 md:px-16 gap-8 dark:text-white my-8">
      <div
        className={`flex flex-col justify-center items-center w-full max-w-sm md:max-w-full pt-12 md:pt-24 p-6 md:p-8 rounded-2xl border border-[#a1a1a1]   ${
          theme === "dark"
            ? "bg-gradient-to-r from-[#ff3333]/10 to-[#ffeb3b]/10"
            : "bg-[#ffedeb]"
        }`}
      >
        <div className=" w-full flex flex-col justify-center items-center gap-4 mb-6">
          <h2 className="text-2xl md:text-4xl font-bold text-center leading-tight">
            Ready to Start Picking Winners?
          </h2>

          <div className="w-full flex flex-col items-center justify-center">
            <p className="text-sm md:text-xl text-[#a1a1a1] text-center max-w-md">
              Join thousands of content creators who trust LuckyHub for fair and
            </p>
            <p className="text-sm md:text-xl text-[#a1a1a1] text-center max-w-md">
              transparent giveaways.
            </p>
          </div>
        </div>

        <div className="flex flex-col sm:flex-row justify-center items-center gap-4 w-full sm:w-1/2">
          <button
            onClick={() => navigate("/signUp")}
            className="h-10 md:h-12 w-full lg:w-2/5 bg-[var(--orange)] text-sm md:text-lg rounded-md font-bold text-black dark:text-white hover:scale-105 transition-transform cursor-pointer flex items-center justify-center"
          >
            Get Started
            <ArrowRight className="inline-block ml-2" size={16} />
          </button>

          <button
            onClick={() => navigate("/signIn")}
            className="h-10 md:h-12 w-full lg:w-1/5 bg-[#221d14] text-xs md:text-sm rounded-md font-bold border border-[#a1a1a1] text-white hover:scale-105 transition-transform cursor-pointer"
          >
            Sign In
          </button>
        </div>
      </div>
    </div>
  );
}

export default CTASection;
