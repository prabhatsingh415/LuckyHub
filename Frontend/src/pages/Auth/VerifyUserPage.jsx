import React, { useEffect } from "react";
import { useDispatch } from "react-redux";
import { useSearchParams, useNavigate } from "react-router-dom";
import {
  useVerifyUserQuery,
  useLazyResendVerificationQuery,
} from "../../Redux/slices/apiSlice";
import { setCredentials } from "../../Redux/slices/authSlice"; // Adjust path to your auth slice
import { Loader } from "../../components/Common";

function VerifyUserPage() {
  const [searchParams] = useSearchParams();
  const token = searchParams.get("token");
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const { data, isLoading, isError, error, isSuccess } =
    useVerifyUserQuery(token);

  const [triggerResend, { isLoading: isResending, isSuccess: resendSuccess }] =
    useLazyResendVerificationQuery();

  // ✨ AUTO-LOGIN LOGIC: Dispatch credentials as soon as verification succeeds
  useEffect(() => {
    if (isSuccess && data?.token && data?.user) {
      dispatch(
        setCredentials({
          user: data.user,
          token: data.token,
        })
      );

      // Optional: Small delay so the user can see the "Verified" celebration
      const timer = setTimeout(() => {
        handleContinue();
      }, 2000);

      return () => clearTimeout(timer);
    }
  }, [isSuccess, data, dispatch]);

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

    // Use navigate for a smooth SPA transition without page reload
    navigate(redirectEndpoint);
  };

  if (isLoading) return <Loader />;

  let errorMessage = "Something went wrong. Please try again later.";
  if (error?.data?.error === "Token must not be empty") {
    errorMessage = "Verification link is missing or invalid.";
  } else if (error?.data?.Error === "Invalid Old Token , Try Sign up again !") {
    errorMessage = "Your verification link has expired. Please sign up again.";
  } else if (error?.status === "FETCH_ERROR") {
    errorMessage = "Network error. Please check your internet connection.";
  } else if (error?.data?.message) {
    errorMessage = error.data.message;
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-[#0a0a0a] text-white px-4">
      <div className="w-full max-w-md text-center bg-gradient-to-br from-[#1a1a1a] to-[#111111] rounded-2xl border border-[#2a2a2a] shadow-[0_0_30px_rgba(255,255,255,0.05)] p-8 animate-scaleIn">
        {isSuccess ? (
          <>
            <div className="text-6xl mb-4">🎉</div>
            <h2 className="text-2xl font-bold mb-3 text-[#22c55e]">
              {data?.message || "Email Verified Successfully!"}
            </h2>
            <p className="text-gray-400 mb-8">
              Welcome to LuckyHub! You are being redirected to your dashboard...
            </p>
            <button
              onClick={handleContinue}
              className="w-full bg-gradient-to-r from-[#ff3333] via-[#ff6b0f] to-[#ff9933] py-2.5 rounded-lg font-semibold hover:opacity-90 transition-all"
            >
              Continue Now
            </button>
          </>
        ) : isError ? (
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
                onClick={() => navigate("/signup")}
                className="w-full py-2 rounded-lg border border-[#2a2a2a] hover:bg-[#1c1c1c] transition-all text-gray-300"
              >
                Back to Sign Up
              </button>
            </div>
          </>
        ) : null}
      </div>
    </div>
  );
}

export default VerifyUserPage;
