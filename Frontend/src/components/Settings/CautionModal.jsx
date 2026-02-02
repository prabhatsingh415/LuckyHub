import { AlertTriangle } from "lucide-react";

export default function CautionModal({ isOpen, onClose, onConfirm, data }) {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-[100] flex items-center justify-center bg-black/60 backdrop-blur-sm p-4">
      <div className="w-full max-w-md rounded-2xl border border-zinc-800 bg-[#0a0a0a] p-6 shadow-2xl">
        <div className="flex items-center gap-3 mb-4">
          <div className="rounded-full bg-red-500/10 p-2 text-red-500">
            <AlertTriangle size={24} />
          </div>
          <h2 className="text-xl font-semibold text-white">{data.title}</h2>
        </div>

        <p className="text-gray-400 text-sm mb-6 leading-relaxed">
          {data.message}
        </p>

        {data.warnings && (
          <div className="mb-6 rounded-xl bg-red-900/10 border border-red-900/20 p-4">
            <p className="text-red-500 text-xs font-bold uppercase tracking-wider mb-2">
              The following will be {data.actionType}:
            </p>
            <ul className="space-y-1">
              {data.warnings.map((w, i) => (
                <li
                  key={i}
                  className="text-gray-300 text-xs flex items-center gap-2"
                >
                  <span className="h-1 w-1 rounded-full bg-red-500" /> {w}
                </li>
              ))}
            </ul>
          </div>
        )}

        <div className="flex gap-3 justify-end mt-8">
          <button
            onClick={onClose}
            className="px-5 py-2 rounded-lg border border-zinc-800 text-white hover:bg-zinc-900 transition-colors"
          >
            Cancel
          </button>
          <button
            onClick={onConfirm}
            className={`px-5 py-2 rounded-lg font-medium text-white transition-all active:scale-95 ${
              data.isDangerous
                ? "bg-red-700 hover:bg-red-800"
                : "bg-orange-600 hover:bg-orange-700"
            }`}
          >
            {data.confirmText}
          </button>
        </div>
      </div>
    </div>
  );
}
