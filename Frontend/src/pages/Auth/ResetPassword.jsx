import { useSearchParams } from "react-router-dom";
import { useSavePasswordMutation } from "../../Redux/slices/apiSlice";
import { useForm } from "react-hook-form";
import { logoDark, logoLight } from "../..";
import { Lock } from "lucide-react";
import { Form, Loader } from "../../components/Common";
import { useSelector } from "react-redux";

function ResetPassword() {
  const [searchParams] = useSearchParams();
  const token = searchParams.get("token");

  const theme = useSelector((state) => state.theme.mode);

  const {
    register,
    handleSubmit,
    getValues,
    formState: { errors },
  } = useForm();

  const formData = [
    {
      label: "Password",
      type: "password",
      icon: (
        <Lock
          size={18}
          className="absolute top-1/2 transform -translate-y-1/2 text-gray-400"
        />
      ),
      placeholder: "Enter your password",
      register: register("password", {
        required: "Password is required",
        minLength: {
          value: 6,
          message: "Password must be at least 6 characters",
        },
        maxLength: {
          value: 30,
          message: "Password must be less than 30 characters",
        },
        pattern: {
          value: /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d@$!%*#?&]{6,}$/,
          message: "Password must contain letters and numbers",
        },
      }),
    },
    {
      label: "Confirm Password",
      type: "password",
      icon: (
        <Lock
          size={18}
          className="absolute top-1/2 transform -translate-y-1/2 text-gray-400"
        />
      ),
      placeholder: "Confirm your password",
      register: register("confirmPassword", {
        required: "Please confirm your password",
        validate: (value) =>
          value === getValues("password") || "Passwords do not match",
      }),
    },
  ];

  const heading = "Reset Password";
  const headingClassName = "text-3xl";

  const [savePassword, { isLoading, isSuccess, error }] =
    useSavePasswordMutation();

  const handleForgotPassword = (data) => {
    savePassword({ password: data.password, token });
  };

  return (
    <div className="w-full flex flex-col justify-center items-center dark:text-white">
      {isLoading && <Loader />}
      <div className="w-full flex flex-col justify-center items-center">
        <img
          src={theme === "dark" ? logoDark : logoLight}
          className="h-16 w-32"
        />
        <h1 className="text-md text-[#a1a1a1]">Join the creator community</h1>
      </div>

      <div className="w-full md:w-md md:px-4 mt-4 border-2 flex items-center justify-center border-[#111111] rounded-xl">
        <Form
          formData={formData}
          headingData={{ heading, headingClassName }}
          errors={errors}
          className="bg-[#f2f2f5] dark:bg-[#121212] w-full pl-10 p-2 border border-[#171717] rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500 "
          submitBtnText="Change Password"
          btnClassName="w-full bg-[var(--orange)] rounded-lg p-2  text-black dark:text-white hover:scale-105 transition-transform "
          onSubmit={handleSubmit(handleForgotPassword)}
          isContainsGoogleSignIn={false}
          isAuthenticationForm={false}
        />
      </div>
    </div>
  );
}

export default ResetPassword;
