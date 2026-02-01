import { useState, useRef } from "react";

export default function OTPVerificationModal({
  isOpen,
  onClose,
  onVerify,
  isVerifying,
}) {
  const [otp, setOtp] = useState(new Array(6).fill(""));
  const inputRefs = useRef([]);

  const handleChange = (element, index) => {
    if (isNaN(element.value)) return;
    const newOtp = [...otp];
    newOtp[index] = element.value;
    setOtp(newOtp);

    if (element.value !== "" && index < 5) {
      inputRefs.current[index + 1].focus();
    }
  };

  const handleKeyDown = (e, index) => {
    if (e.key === "Backspace" && !otp[index] && index > 0) {
      inputRefs.current[index - 1].focus();
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-[110] flex items-center justify-center bg-black/80 backdrop-blur-md p-4">
      <div className="w-full max-w-sm rounded-2xl border border-zinc-800 bg-[#0a0a0a] p-8 text-center">
        <h2 className="text-2xl font-bold text-white mb-2">Verify Deletion</h2>
        <p className="text-gray-400 text-sm mb-8">
          Enter the 6-digit code sent to your email.
        </p>

        <div className="flex justify-center gap-2 mb-8">
          {otp.map((data, index) => (
            <input
              key={index}
              type="text"
              maxLength="1"
              ref={(el) => (inputRefs.current[index] = el)}
              value={data}
              onChange={(e) => handleChange(e.target, index)}
              onKeyDown={(e) => handleKeyDown(e, index)}
              className="w-10 h-12 md:w-12 md:h-14 text-center text-xl font-bold rounded-xl border border-zinc-800 bg-zinc-900 text-white focus:border-orange-500 focus:outline-none transition-all"
            />
          ))}
        </div>

        <button
          onClick={() => onVerify(otp.join(""))}
          disabled={otp.join("").length < 6 || isVerifying}
          className="w-full py-3 rounded-xl bg-red-700 hover:bg-red-800 text-white font-semibold disabled:bg-zinc-800 disabled:text-zinc-500 transition-all"
        >
          {isVerifying ? "Verifying..." : "Confirm & Delete Everything"}
        </button>
        <button
          onClick={onClose}
          className="mt-4 text-zinc-500 hover:text-white text-sm"
        >
          Cancel
        </button>
      </div>
    </div>
  );
}
