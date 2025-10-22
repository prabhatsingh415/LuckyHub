import { logoDarkSvg, logoLightSvg } from "..";
import Form from "../components/Form";
import { useForm } from "react-hook-form";
import { useSelector } from "react-redux";
import { ArrowLeft, Mail } from "lucide-react";
import Button from "../components/Button";
import { useNavigate } from "react-router-dom";
import { useForgotPasswordMutation } from "../Redux/slices/apiSlice";

function ForgotPassword() {
  const theme = useSelector((state) => state.theme.mode);
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm();

  const formData = [
    {
      label: "Email",
      type: "email",
      icon: (
        <Mail
          size={18}
          className="absolute top-1/2 transform -translate-y-1/2 text-gray-400"
        />
      ),
      placeholder: "Enter your email",
      register: register("email", {
        required: "Email is required",
        pattern: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$/,
        message: "Invalid email address",
      }),
    },
  ];

  const heading = "Fogot Your Password ?";
  const headingClassName = "text-3xl mb-4";
  const navigate = useNavigate();
  const [forgotPasswordData, { isLoading, isSuccess, error }] =
    useForgotPasswordMutation();
  const handleForgotPassword = (data) => {
    forgotPasswordData(data);
  };
  return (
    <div className="w-full flex flex-col justify-center items-center mt-36 dark:text-white">
      <div className="w-full flex flex-col justify-center items-center">
        <img
          src={theme === "dark" ? logoDarkSvg : logoLightSvg}
          className="h-16 w-32"
        />
        <h1 className="text-md text-[#a1a1a1]">Reset your password</h1>
      </div>

      <div className="w-full md:w-md md:p-8 mt-4 border-2 flex flex-col items-center justify-center border-[#111111] rounded-xl">
        <Form
          formData={formData}
          headingData={{ heading, headingClassName }}
          errors={errors}
          className="bg-[#f2f2f5] dark:bg-[#121212] w-full pl-10 p-2 border border-[#171717] rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500 "
          submitBtnText="Create Account"
          btnClassName="w-full bg-[var(--orange)] rounded-lg p-2  text-black dark:text-white hover:scale-105 transition-transform"
          isContainsGoogleSignIn={false}
          isSignInPage={false}
          onSubmit={handleSubmit(handleForgotPassword)}
          isAuthenticationForm={false}
        />
        <div className="w-full p-4">
          {" "}
          <button
            className="w-full rounded-lg py-2 text-black dark:text-white hover:bg-zinc-800 transition-transform flex items-center justify-center"
            onClick={() => navigate("/signin")}
            type="button"
          >
            <ArrowLeft /> Back to login
          </button>
        </div>
      </div>
    </div>
  );
}

export default ForgotPassword;
