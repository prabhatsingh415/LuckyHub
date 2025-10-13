import { useState } from "react";
import Input from "./Input";
import { Eye, EyeOff } from "lucide-react";
import { useNavigate } from "react-router-dom";

export default function Form({
  formData = [],
  className = "",
  headingData = "",
  subHeadingData = "",
  onSubmit,
  errors,
  showCheckMark = false,
  submitBtnText = "Submit",
  btnClassName = "",
  isContainsGoogleSignIn = true,
  isSignInPage = false,
}) {
  const [visiblePasswords, setVisiblePasswords] = useState({});

  const togglePassword = (index) => {
    setVisiblePasswords((prev) => ({
      ...prev,
      [index]: !prev[index],
    }));
  };

  const handleAuthWithGoogle = () => {
    const clientId = import.meta.env.VITE_GOOGLE_CLIENT_ID;
    const redirectUri = "http://localhost:8080/auth/google/callback";
    const scope = "openid email profile";
    const responseType = "code";

    window.location.href = `https://accounts.google.com/o/oauth2/v2/auth?client_id=${clientId}&redirect_uri=${redirectUri}&response_type=${responseType}&scope=${scope}`;
  };

  const navigate = useNavigate();

  return (
    <form className="w-full p-4" onSubmit={onSubmit}>
      <div className="w-full my-4 flex flex-col justify-center gap-2">
        <div className="flex flex-col justify-center items-center gap-2">
          {headingData && (
            <h1 className={headingData.headingClassName}>
              {headingData.heading}
            </h1>
          )}
          {subHeadingData && (
            <p className={subHeadingData.subHeadingClassName}>
              {subHeadingData.subHeading}
            </p>
          )}
        </div>

        {formData.map((input, index) => {
          const isPassword = input.label.toLowerCase().includes("password");

          return (
            <div key={index} className="flex flex-col gap-1 relative w-full">
              <label>{input.label}</label>

              <div className="relative w-full">
                {/* Left icon */}
                {input.icon && (
                  <div className="absolute left-3 top-1/2 transform -translate-y-1/2">
                    {input.icon}
                  </div>
                )}

                {/* Input */}
                <Input
                  type={
                    isPassword
                      ? visiblePasswords[index]
                        ? "text"
                        : "password"
                      : input.type
                  }
                  placeholder={input.placeholder}
                  {...(input.register || {})}
                  errors={errors}
                  className={className}
                />

                {/* Right eye icon (per password field) */}
                {isPassword && (
                  <div
                    onClick={() => togglePassword(index)}
                    className="absolute right-3 top-1/2 transform -translate-y-1/2 cursor-pointer text-gray-400"
                  >
                    {visiblePasswords[index] ? (
                      <EyeOff size={18} />
                    ) : (
                      <Eye size={18} />
                    )}
                  </div>
                )}
              </div>
            </div>
          );
        })}

        {isSignInPage ? (
          <div className="mt-2">
            <button
              type="button"
              onClick={() => {}}
              className="text-[#717182] dark:text-gray-400 hover:text-zinc-950 hover:dark:text-gray-200 hover:underline"
            >
              Forgot password?
            </button>
          </div>
        ) : (
          ""
        )}

        {showCheckMark && (
          <div className="flex items-center gap-2 mt-4">
            <input
              type="checkbox"
              className="accent-orange-500 cursor-pointer"
            />
            <span className="text-sm text-[#a1a1a1]">
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
            </span>
          </div>
        )}
      </div>

      <button className={btnClassName}>{submitBtnText}</button>

      <div className="flex items-center justify-center gap-2 w-full mt-6">
        <hr className="flex-grow border-gray-500" />
        <span className="text-gray-400 text-sm">or</span>
        <hr className="flex-grow border-gray-500" />
      </div>

      {isContainsGoogleSignIn && (
        <button
          onClick={handleAuthWithGoogle}
          className="w-full flex items-center justify-center gap-2 border px-4 py-2 rounded-md hover:bg-gray-100 mt-4"
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
      )}

      <p className="w-full text-center mt-4">
        {isSignInPage ? "Don't have an account?" : "Already have an account?"}{" "}
        <button
          type="button"
          onClick={() => {
            isSignInPage ? navigate("/signup") : navigate("/signin");
          }}
          className="text-[var(--orange)] hover:underline"
        >
          {isSignInPage ? "Sign Up" : "Sign In"}
        </button>
      </p>
    </form>
  );
}
