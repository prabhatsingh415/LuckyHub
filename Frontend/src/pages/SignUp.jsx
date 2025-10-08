import React from "react";
import { logoDarkSvg, logoLightSvg } from "..";
import Form from "../components/Form";
import { useForm } from "react-hook-form";
import { Lock, Mail, User } from "lucide-react";
import { useSignUpMutation } from "../Redux/slices/apiSlice";

function SignUp() {
  const theme = localStorage.getItem("theme");
  const {
    register,
    handleSubmit,
    watch,
    formState: { errors },
  } = useForm();
  const formData = [
    {
      label: "First Name",
      type: "text",
      icon: (
        <User
          size={20}
          className="absolute  top-1/2 transform -translate-y-1/2 text-gray-400"
        />
      ),
      placeholder: "Enter your first name",
      register: register("first name", {
        required: "First name is required",
        minLength: { value: 2, message: "Too short" },
        maxLength: { value: 30, message: "Too long" },
        message: "Invalid name",
      }),
    },
    {
      label: "Last Name",
      type: "text",
      icon: (
        <User
          size={20}
          className="absolute  top-1/2 transform -translate-y-1/2 text-gray-400"
        />
      ),
      placeholder: "Enter your last name",
      register: register("last name", {
        required: "Last name is required",
        minLength: { value: 2, message: "Too short" },
        maxLength: { value: 30, message: "Too long" },
        message: "Invalid name",
      }),
    },
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
        pattern: {
          value: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/,
          message: "Invalid email address",
        },
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

  const heading = "Create account";
  const headingClassName = "text-3xl";
  const subHeading = "Start your journey as a content creator";
  const subHeadingClassName = "text-sm text-[#a1a1a1] mb-8";

  const [signUpData, { isLoading, isSuccess, error }] = useSignUpMutation();

  const handleSignup = (data) => {
    signUpData(data);
  };

  return (
    <div className="w-full flex flex-col justify-center items-center dark:text-white">
      {isLoading && <div className="text-[var(--orange)]">Loading...</div>}
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
          subHeadingData={{ subHeading, subHeadingClassName }}
          errors={errors}
          className="bg-[#f2f2f5] dark:bg-[#121212] w-full pl-10 p-2 border border-[#171717] rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500 "
          showCheckMark={true}
          submitBtnText="Create Account"
          btnClassName="w-full bg-[var(--orange)] rounded-lg p-2  text-black dark:text-white hover:scale-105 transition-transform "
          isContainsGoogleSignIn={true}
          onSubmit={handleSubmit(handleSignup)}
        />
      </div>
    </div>
  );
}

export default SignUp;
