import { useEffect, useState } from "react";
import useTimeout from "../hook/useTimeout";
import { useLazyResendVerificationQuery } from "../Redux/slices/apiSlice";
import Loader from "./Loader";

export default function InfoModal({
  isOpen,
  title = "Information",
  message = "",
  type = "info",
  okText = "OK",
  cancelText = null,
  onOk,
  onCancel,
  redirectUrl = null,
  isContainsResendBtn = true,
  userEmail = "",
}) {
  if (!isOpen) return null;

  const colorMap = {
    success: "from-[#22c55e]/20 to-[#bbf7d0]/10 text-[#22c55e]",
    error: "from-[#ff3333]/20 to-[#ff9933]/10 text-[#ff3333]",
    info: "from-[#ff9933]/20 to-[#fff176]/10 text-[#ffcc33]",
  };

  const MAX_ATTEMPTS = 3;
  const COOL_DOWN_15M = 10; // 15 min timer
  const RESET_TIME_24H = 24 * 60 * 60 * 1000;

  const ATTEMPTS_KEY = `resendAttempts_${userEmail}`;
  const TIMESTAMP_KEY = `resendTimestamp_${userEmail}`;

  const [resendAttempts, setResendAttempts] = useState(
    Number(localStorage.getItem(ATTEMPTS_KEY) || 0)
  );

  const { secondsLeft, setSecondsLeft } = useTimeout(COOL_DOWN_15M);
  const [disableBtn, setDisableBtn] = useState(true);

  const [modalMsg, setModalMsg] = useState(message);
  const [modalType, setModalType] = useState(type);
  const [modalTitle, setModalTitle] = useState(title);

  const [resendVerification, { data, isError, isLoading, isSuccess, error }] =
    useLazyResendVerificationQuery();

  useEffect(() => {
    const storedTimestamp = localStorage.getItem(TIMESTAMP_KEY);
    const now = Date.now();

    if (storedTimestamp && now - Number(storedTimestamp) >= RESET_TIME_24H) {
      localStorage.setItem(ATTEMPTS_KEY, "0");
      localStorage.removeItem(TIMESTAMP_KEY);
      setResendAttempts(0);
    }
  }, [ATTEMPTS_KEY, TIMESTAMP_KEY]);

  // initial timer
  useEffect(() => {
    if (isContainsResendBtn) setSecondsLeft(COOL_DOWN_15M);
  }, [isContainsResendBtn]);

  useEffect(() => {
    if (secondsLeft === 0) setDisableBtn(false);
  }, [secondsLeft]);

  useEffect(() => {
    if (isLoading) {
      setModalTitle("Please Wait");
      setModalMsg("Resending verification email...");
      setModalType("info");
    }

    if (isSuccess) {
      setModalTitle("Success");
      setModalMsg("Verification email sent successfully!");
      setModalType("success");

      if (data?.token) {
        localStorage.setItem("SignUpToken", data.token);
      }
    }

    if (isError) {
      if (error?.status === 429) {
        setModalTitle("Limit Reached");
        setModalMsg(
          "You reached maximum limit of resending verification link. Try again tomorrow."
        );
        setModalType("error");
        setResendAttempts(MAX_ATTEMPTS);
        localStorage.setItem(ATTEMPTS_KEY, MAX_ATTEMPTS.toString());
        setDisableBtn(true);
        return;
      } else {
        setModalTitle("Error");
        setModalMsg(error?.data?.Error || "Failed to resend. Try again later.");
        setModalType("error");
      }
    }
  }, [isLoading, isSuccess, isError]);

  const handleResend = () => {
    // 3 Attempts per signup session limit
    if (resendAttempts >= MAX_ATTEMPTS) {
      setModalTitle("Limit Reached");
      setModalMsg(
        "You reached maximum limit of resend verification email. Try signing up again."
      );
      setModalType("error");
      return;
    }

    const token = localStorage.getItem("SignUpToken");
    resendVerification(token);

    const updated = resendAttempts + 1;
    setResendAttempts(updated);
    localStorage.setItem(ATTEMPTS_KEY, updated.toString());

    if (updated === 1) {
      localStorage.setItem(TIMESTAMP_KEY, Date.now().toString());
    }

    setDisableBtn(true);
    setSecondsLeft(COOL_DOWN_15M);
  };

  const handleOk = () => {
    if (redirectUrl) window.location.href = redirectUrl;
    else if (onOk) onOk();
  };

  const handleCancel = () => onCancel && onCancel();

  return (
    <div className="fixed inset-0 flex items-center justify-center bg-black/60 backdrop-blur-md z-[9999] animate-fadeIn">
      <div
        className={`relative w-[90%] max-w-md text-center text-white border border-[#2a2a2a] bg-gradient-to-br ${colorMap[modalType]} rounded-2xl p-8`}
      >
        <h2 className="text-2xl font-bold mb-3">{modalTitle}</h2>

        {isLoading ? (
          <Loader />
        ) : (
          <p className="text-gray-300 text-sm mb-6">{modalMsg}</p>
        )}

        <div className="flex justify-center gap-4 mt-4">
          {cancelText && (
            <button
              onClick={handleCancel}
              className="px-5 py-2 rounded-lg border bg-[#121212] hover:bg-[#1c1c1c]"
            >
              {cancelText}
            </button>
          )}
          <button
            onClick={handleOk}
            className="px-6 py-2 rounded-lg bg-gradient-to-r from-[#ff3333] via-[#ff6b0f] to-[#ff9933]"
          >
            {okText}
          </button>
        </div>

        {isContainsResendBtn && (
          <div className="mt-4">
            {resendAttempts < MAX_ATTEMPTS ? (
              <>
                <button
                  onClick={handleResend}
                  disabled={disableBtn}
                  className={`hover:text-zinc-500 ${
                    disableBtn ? "opacity-50 cursor-not-allowed" : ""
                  }`}
                >
                  Resend
                </button>

                {disableBtn && (
                  <div className="text-sm text-gray-400 mt-1">
                    {Math.floor(secondsLeft / 60)}m{" "}
                    {secondsLeft % 60 < 10
                      ? "0" + (secondsLeft % 60)
                      : secondsLeft % 60}
                    s remaining
                  </div>
                )}
              </>
            ) : (
              <p className="text-sm text-[#ff3333] font-medium bg-red-500/10 py-2 rounded-lg border border-red-500/20">
                Maximum limit reached for today.
              </p>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
