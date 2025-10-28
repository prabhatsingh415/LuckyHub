export default function InfoModal({
  isOpen,
  title = "Information",
  message = "",
  type = "info", // "success" | "error" | "info"
  okText = "OK",
  cancelText = null,
  onOk,
  onCancel,
  redirectUrl = null,
}) {
  if (!isOpen) return null;

  const colorMap = {
    success: "from-[#22c55e]/20 to-[#bbf7d0]/10 text-[#22c55e]",
    error: "from-[#ff3333]/20 to-[#ff9933]/10 text-[#ff3333]",
    info: "from-[#ff9933]/20 to-[#fff176]/10 text-[#ffcc33]",
  };

  const handleOk = () => {
    if (redirectUrl) window.location.href = redirectUrl;
    else if (onOk) onOk();
  };

  const handleCancel = () => {
    if (onCancel) onCancel();
  };

  return (
    <div className="fixed inset-0 flex items-center justify-center bg-black/60 backdrop-blur-md z-[9999] animate-fadeIn">
      <div
        className={`relative w-[90%] max-w-md text-center text-white border border-[#2a2a2a] bg-gradient-to-br ${colorMap[type]} rounded-2xl p-8 shadow-[0_0_30px_rgba(255,255,255,0.05)] animate-scaleIn`}
      >
        {/* Title */}
        <h2 className="text-2xl font-bold mb-3 tracking-wide">{title}</h2>

        {/* Message */}
        <p className="text-gray-300 text-sm leading-relaxed mb-6">{message}</p>

        {/* Buttons */}
        <div className="flex justify-center gap-4 mt-4">
          {cancelText && (
            <button
              onClick={handleCancel}
              className="px-5 py-2 rounded-lg border border-[#2f2f2f] bg-[#121212] hover:bg-[#1c1c1c] transition-all text-gray-300"
            >
              {cancelText}
            </button>
          )}
          <button
            onClick={handleOk}
            className="px-6 py-2 font-semibold text-white rounded-lg bg-gradient-to-r from-[#ff3333] via-[#ff6b0f] to-[#ff9933] hover:opacity-90 transition-all"
          >
            {okText}
          </button>
        </div>

        <div className="absolute inset-0 rounded-2xl border border-[#ff3333]/10 pointer-events-none"></div>
      </div>

      <style>
        {`
          @keyframes fadeIn {
            from { opacity: 0; }
            to { opacity: 1; }
          }
          @keyframes scaleIn {
            from { opacity: 0; transform: scale(0.95); }
            to { opacity: 1; transform: scale(1); }
          }
          .animate-fadeIn { animation: fadeIn 0.2s ease-out; }
          .animate-scaleIn { animation: scaleIn 0.25s ease-out; }
        `}
      </style>
    </div>
  );
}
