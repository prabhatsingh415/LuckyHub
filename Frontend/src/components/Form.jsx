import { useState, useRef } from "react";
import Input from "./Input";
import { Eye, EyeOff } from "lucide-react";
import { useNavigate } from "react-router-dom";
import InfoModal from "../pages/InfoModal";

export default function Form({
  formData = [],
  headingData = {},
  subHeadingData = {},
  onSubmit,
  errors = {},
  className = "",
  btnClassName = "",
  submitBtnText = "Submit",
  showCheckMark = false,
  isContainsGoogleSignIn = false,
  isSignInPage = false,
  isAuthenticationForm = false,
}) {
  const navigate = useNavigate();
  const [visiblePasswords, setVisiblePasswords] = useState({});
  const [isChecked, setIsChecked] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const formRef = useRef(null);

  const handleFormSubmit = (e) => {
    e.preventDefault();

    if (showCheckMark && !isChecked) {
      setModalOpen(true);
      return;
    }

    if (onSubmit) {
      onSubmit(e);
    }
  };

  const togglePassword = (id) => {
    setVisiblePasswords((prev) => ({
      ...prev,
      [id]: !prev[id],
    }));
  };

  // Google login redirect
  const handleAuthWithGoogle = () => {
    const clientId = import.meta.env.VITE_GOOGLE_CLIENT_ID;
    const redirectUri = import.meta.env.VITE_GOOGLE_REDIRECT_URI;
    const scope = "openid email profile";
    const responseType = "code";

    window.location.href = `https://accounts.google.com/o/oauth2/v2/auth?client_id=${clientId}&redirect_uri=${redirectUri}&response_type=${responseType}&scope=${scope}`;
  };

  return (
    <form
      ref={formRef}
      className={`w-full p-4 ${className}`}
      onSubmit={handleFormSubmit}
      noValidate
    >
      {/* Headings */}
      {(headingData.heading || subHeadingData.subHeading) && (
        <div className="flex flex-col items-center gap-1 mb-4">
          {headingData.heading && (
            <h1
              className={
                headingData.headingClassName || "text-2xl font-semibold"
              }
            >
              {headingData.heading}
            </h1>
          )}
          {subHeadingData.subHeading && (
            <p
              className={
                subHeadingData.subHeadingClassName ||
                "text-gray-500 text-sm text-center"
              }
            >
              {subHeadingData.subHeading}
            </p>
          )}
        </div>
      )}

      {/* Inputs */}
      {formData.map((input, index) => {
        const isPassword = input.type === "password";
        const inputId = input.id || `input-${index}`;

        return (
          <div key={inputId} className="flex flex-col mb-4 relative">
            {input.label && (
              <label
                htmlFor={inputId}
                className="mb-1 text-sm text-gray-600 dark:text-gray-300"
              >
                {input.label}
              </label>
            )}

            <div className="relative w-full">
              {/* Left icon */}
              {input.icon && (
                <div className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400">
                  {input.icon}
                </div>
              )}

              <Input
                id={inputId}
                type={
                  isPassword
                    ? visiblePasswords[inputId]
                      ? "text"
                      : "password"
                    : input.type
                }
                placeholder={input.placeholder}
                {...(input.register || {})}
                errors={errors}
                className={`w-full ${input.icon ? "pl-10" : ""}`}
              />

              {/* Password toggle */}
              {isPassword && (
                <button
                  type="button"
                  onClick={() => togglePassword(inputId)}
                  className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400"
                >
                  {visiblePasswords[inputId] ? (
                    <EyeOff size={18} />
                  ) : (
                    <Eye size={18} />
                  )}
                </button>
              )}
            </div>

            {/* Inline error */}
            {errors[input.register?.name] && (
              <span className="text-xs text-red-500 mt-1">
                {errors[input.register?.name]?.message}
              </span>
            )}
          </div>
        );
      })}

      {/* Forgot password link (signin only) */}
      {isSignInPage && (
        <div className="mt-2 text-right">
          <button
            type="button"
            onClick={() => navigate("/forgot-password")}
            className="text-[#717182] dark:text-gray-400 hover:text-zinc-950 hover:dark:text-gray-200 hover:underline text-sm"
          >
            Forgot password?
          </button>
        </div>
      )}

      {/* Terms checkbox */}
      {showCheckMark && (
        <div className="flex items-start gap-2 mt-4 text-sm">
          <input
            id="terms"
            type="checkbox"
            className="accent-orange-500 mt-1 cursor-pointer"
            checked={isChecked}
            onChange={(e) => setIsChecked(e.target.checked)}
          />
          <label htmlFor="terms" className="text-[#a1a1a1] leading-snug">
            I agree to the{" "}
            <button
              type="button"
              onClick={() => navigate("/terms-of-condition")}
              className="text-[var(--orange)] hover:underline"
            >
              Terms of Service
            </button>{" "}
            and{" "}
            <button
              type="button"
              onClick={() => navigate("/privacy-policy")}
              className="text-[var(--orange)] hover:underline"
            >
              Privacy Policy
            </button>
          </label>
        </div>
      )}

      {/* Submit button */}
      <button type="submit" className={`w-full mt-5 ${btnClassName}`}>
        {submitBtnText}
      </button>

      {/* Google Sign-in */}
      {isContainsGoogleSignIn && (
        <>
          <div className="flex items-center justify-center gap-2 w-full mt-6">
            <hr className="flex-grow border-gray-500" />
            <span className="text-gray-400 text-sm">or</span>
            <hr className="flex-grow border-gray-500" />
          </div>

          <button
            type="button"
            onClick={handleAuthWithGoogle}
            className="w-full flex items-center justify-center gap-2 border px-4 py-2 rounded-md hover:bg-gray-100 mt-4 dark:border-gray-700"
          >
            <svg className="w-5 h-5" viewBox="0 0 24 24">
              <path
                fill="#4285F4"
                d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
              />
              <path
                fill="#34A853"
                d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
              />
              <path
                fill="#FBBC05"
                d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"
              />
              <path
                fill="#EA4335"
                d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"
              />
            </svg>
            <span>Continue with Google</span>
          </button>
        </>
      )}

      {/* Auth switch links */}
      {isAuthenticationForm && (
        <p className="w-full text-center mt-4">
          {isSignInPage ? "Don't have an account?" : "Already have an account?"}{" "}
          <button
            type="button"
            onClick={() => navigate(isSignInPage ? "/signup" : "/signin")}
            className="text-[var(--orange)] hover:underline"
          >
            {isSignInPage ? "Sign Up" : "Sign In"}
          </button>
        </p>
      )}

      <InfoModal
        isOpen={modalOpen}
        type="info"
        title="Action Required ⚠️"
        message="Please agree to the Terms and Privacy Policy before continuing."
        okText="Got it"
        isContainsResendBtn={false}
        onOk={() => setModalOpen(false)}
      />
    </form>
  );
}
