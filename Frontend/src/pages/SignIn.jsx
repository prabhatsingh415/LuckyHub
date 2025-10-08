import { logoDarkSvg, logoLightSvg } from "..";
import Form from "../components/Form";
import { useForm } from "react-hook-form";
import { Lock, Mail } from "lucide-react";
import { useSelector } from "react-redux";

function SignUp() {
  const theme = useSelector((state) => state.theme.mode);
  const {
    register,
    handleSubmit,
    watch,
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
    {
      label: "Password",
      type: "text",
      icon: (
        <Lock
          size={18}
          className="absolute top-1/2 transform -translate-y-1/2 text-gray-400"
        />
      ),
      placeholder: "Create a password",
      register: register("password", {
        required: "Password is required",
        message: "Invalid Password",
      }),
    },
  ];

  const heading = "Welcome Back!";
  const headingClassName = "text-3xl mb-4";

  return (
    <div className="w-full flex flex-col justify-center items-center dark:text-white">
      <div className="w-full flex flex-col justify-center items-center">
        <img
          src={theme === "dark" ? logoDarkSvg : logoLightSvg}
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
          submitBtnText="Create Account"
          btnClassName="w-full bg-[var(--orange)] rounded-lg p-2  text-black dark:text-white hover:scale-105 transition-transform "
          isContainsGoogleSignIn={true}
          isSignInPage={true}
        />
      </div>
    </div>
  );
}

export default SignUp;
