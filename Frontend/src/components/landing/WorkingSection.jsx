import React from "react";

function WorkingSection() {
  const steps = [
    {
      step: "1",
      title: "Add Video Links",
      description: ["Paste up to 5 YouTube video URLs for your giveaway draw."],
    },
    {
      step: "2",
      title: "Fetch Comments",
      description: [
        "LuckyHub grabs all comments from your videos quickly and safely.",
      ],
    },
    {
      step: "3",
      title: "Add Keywords",
      description: [
        "Add keywords (optional) to focus on specific comments. Applied before deduplication.",
      ],
    },
    {
      step: "4",
      title: "Pick Winners",
      description: [
        "Deduplicate commenters, run fair random selection and get winners instantly.",
      ],
    },
  ];

  return (
    <div className="bg-[#111111] flex flex-col justify-center items-center p-8 gap-8 dark:text-white">
      <div className="flex flex-col items-center gap-4 mb-8">
        <h2 className="text-4xl">How It Works</h2>
        <p className="text-xl text-[#a1a1a1]">
          Pick winners from YouTube comments in just 4 simple steps
        </p>
      </div>

      <div className="flex justify-center items-center gap-4">
        {steps.map((step) => (
          <div
            key={step.step}
            className="flex flex-col justify-center items-center gap-4"
          >
            <div className="bg-gradient-to-r from-[#ff4500] via-[#e1780f] to-[#d37815] font-bold text-lg w-fit py-4 px-6 rounded-full">
              {step.step}
            </div>
            <h3 className="text-base">{step.title}</h3>
            <div className="w-3/5 text-[#a1a1a1] text-sm flex  items-center justify-center ">
              {step.description}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default WorkingSection;
