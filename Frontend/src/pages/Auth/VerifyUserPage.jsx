import {
  useVerifyUserQuery,
  useLazyResendVerificationQuery,
} from "../../Redux/slices/apiSlice";
import { Loader } from "../../components/Common";
import { useSearchParams, useNavigate } from "react-router-dom";

function VerifyUserPage() {
  const [searchParams] = useSearchParams();
  const token = searchParams.get("token");
  const navigate = useNavigate();

  const { data, isLoading, isError, error, isSuccess } =
    useVerifyUserQuery(token);

  const [triggerResend, { isLoading: isResending, isSuccess: resendSuccess }] =
    useLazyResendVerificationQuery();

  const handleResend = async () => {
    await triggerResend(token);
  };

  const handleContinue = () => {
    const userEmail = data?.email;

    const redirectEndpoint =
      localStorage.getItem("redirectEndpoint") || "/home";

    if (userEmail) {
      localStorage.removeItem(`resendAttempts_${userEmail}`);
      localStorage.removeItem(`resendTimestamp_${userEmail}`);
    }
    localStorage.removeItem("SignUpToken");
    localStorage.removeItem("redirectEndpoint");
    window.location.href = redirectEndpoint;
  };

  if (isLoading) return <Loader />;

  let errorMessage = "Something went wrong. Please try again later.";

  if (error?.data?.error === "Token must not be empty") {
    errorMessage = "Verification link is missing or invalid.";
  } else if (error?.data?.Error === "Invalid Old Token , Try Sign up again !") {
    errorMessage =
      "Your verification link has expired or is no longer valid. Please sign up again.";
  } else if (error?.status === "FETCH_ERROR") {
    errorMessage = "Network error. Please check your internet connection.";
  } else if (error?.status >= 500) {
    errorMessage = "Internal server error. Please try again after some time.";
  } else if (error?.data?.message) {
    errorMessage = error.data.message;
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-[#0a0a0a] text-white px-4">
      <div className="w-full max-w-md text-center bg-gradient-to-br from-[#1a1a1a] to-[#111111] rounded-2xl border border-[#2a2a2a] shadow-[0_0_30px_rgba(255,255,255,0.05)] p-8 animate-scaleIn">
        {isSuccess && (
          <>
            <div className="text-6xl mb-4">🎉</div>
            <h2 className="text-2xl font-bold mb-3 text-[#22c55e]">
              {data?.message || "Email Verified Successfully!"}
            </h2>
            <p className="text-gray-400 mb-8">
              Your account has been successfully verified. You can now continue
              to explore LuckyHub.
            </p>
            <button
              onClick={handleContinue}
              className="w-full bg-gradient-to-r from-[#ff3333] via-[#ff6b0f] to-[#ff9933] py-2.5 rounded-lg font-semibold hover:opacity-90 transition-all"
            >
              Continue
            </button>
          </>
        )}

        {isError && (
          <>
            <div className="text-6xl mb-4">⚠️</div>
            <h2 className="text-2xl font-bold mb-3 text-[#ff3333]">
              Verification Failed
            </h2>
            <p className="text-gray-400 mb-6">{errorMessage}</p>

            <div className="flex flex-col gap-3">
              <button
                onClick={handleResend}
                disabled={isResending}
                className="w-full bg-gradient-to-r from-[#ff6b0f] to-[#ff9933] py-2.5 rounded-lg font-semibold hover:opacity-90 transition-all disabled:opacity-50"
              >
                {isResending
                  ? "Resending..."
                  : resendSuccess
                  ? "Email Sent ✅"
                  : "Resend Verification Email"}
              </button>

              <button
                onClick={() => navigate("/")}
                className="w-full py-2 rounded-lg border border-[#2a2a2a] hover:bg-[#1c1c1c] transition-all text-gray-300"
              >
                Back
              </button>
            </div>
          </>
        )}
      </div>

      <style>
        {`
          @keyframes scaleIn {
            from { opacity: 0; transform: scale(0.95); }
            to { opacity: 1; transform: scale(1); }
          }
          .animate-scaleIn {
            animation: scaleIn 0.3s ease-out;
          }
        `}
      </style>
    </div>
  );
}

export default VerifyUserPage;
