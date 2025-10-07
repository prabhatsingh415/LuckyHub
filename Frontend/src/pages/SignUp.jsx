import React from "react";
import { logoDarkSvg, logoLightSvg } from "..";
import Form from "../components/Form";
import { useForm } from "react-hook-form";
import { Lock, LockOpen, Mail, User } from "lucide-react";

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
      label: "Full Name",
      type: "text",
      icon: (
        <User
          size={20}
          className="absolute  top-1/2 transform -translate-y-1/2 text-gray-400"
        />
      ),
      placeholder: "Enter your full name",
      register: register("name", {
        required: "Name is required",
        max: 30,
        min: 2,
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
    {
      label: "Confirm Password",
      type: "text",
      icon: (
        <LockOpen
          size={18}
          className="absolute top-1/2 transform -translate-y-1/2 text-gray-400"
        />
      ),
      placeholder: "Confirm your password",
      register: register("confirmPassword", {
        required: "Confirm Password is required",
        message: "Invalid Confirm Password",
      }),
    },
  ];

  const heading = "Create account";
  const headingClassName = "text-3xl";
  const subHeading = "Start your journey as a content creator";
  const subHeadingClassName = "text-sm text-[#a1a1a1] mb-8";

  return (
    <div className="w-full flex flex-col justify-center items-center dark:text-white">
      <div className="w-full flex flex-col justify-center items-center">
        <img
          src={theme === "dark" ? logoDarkSvg : logoLightSvg}
          className="h-16 w-32 border-2"
        />
        <h1 className="text-md text-[#a1a1a1]">Join the creator community</h1>
      </div>
      <div className="w-full flex flex-col justify-center items-center p-2">
        <div className="w-full border-2 flex items-center justify-center border-[#111111] rounded-xl">
          <Form
            formData={formData}
            headingData={{ heading, headingClassName }}
            subHeadingData={{ subHeading, subHeadingClassName }}
            errors={errors}
            className="bg-[#121212]"
            showCheckMark={true}
            submitBtnText="Create Account"
            btnClassName="w-full bg-[var(--orange)] rounded-lg p-2  text-black dark:text-white hover:scale-105 transition-transform "
          />
        </div>
      </div>
    </div>
  );
}

export default SignUp;
