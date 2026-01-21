import { useSelector } from "react-redux";
import { CircleCheckBig } from "lucide-react";

function PlanCard({ plan, onClick, isCurrent }) {
  const theme = useSelector((state) => state.theme.mode);

  return (
    <div
      className={`flex flex-col justify-between items-center border border-[#a1a1a1] rounded-xl p-8 gap-4 transition-all duration-500 
             w-80 md:w-50 lg:w-80 min-h-[500px] 
              ${
                plan.id === 2
                  ? "md:-mt-6 md:shadow-xl md:scale-105 md:z-10 md:bg-white md:dark:bg-[#1a1a1a]"
                  : "md:z-0 md:bg-white md:dark:bg-[#111]"
              }`}
    >
      <div
        className={`w-fit p-2 flex justify-center items-center rounded-lg ${
          theme === "dark"
            ? "bg-gradient-to-r from-[#ff3333]/10 to-[#ffeb3b]/10"
            : "bg-[#ffedeb]"
        }`}
      >
        {plan.icon}
      </div>

      <h1 className="text-3xl font-semibold">{plan.name}</h1>
      <p className="text-gray-500 dark:text-gray-300 text-center">
        {plan.description}
      </p>

      <div className="flex flex-col gap-2 w-full flex-1">
        {plan.features.map((feature, index) => (
          <div key={index} className="flex gap-2 items-start">
            <CircleCheckBig color="#ff4d29" size={18} />
            <p className="text-sm">{feature}</p>
          </div>
        ))}
      </div>

      <button
        onClick={() => onClick(plan)}
        disabled={isCurrent}
        className={`w-full p-3 rounded-lg font-semibold cursor-pointer transform-gpu hover:scale-105 transition-transform duration-500 ease-out
          ${
            plan.id === 2
              ? "bg-[#ff4d29] text-white"
              : "bg-[#3c4859] text-white"
          }`}
      >
        {isCurrent ? "Active Plan" : plan.cta}
      </button>
    </div>
  );
}
export default PlanCard;
