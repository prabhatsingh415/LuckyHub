import { logoDark, logoLight } from "..";
import Form from "../components/Form";
import { useForm } from "react-hook-form";
import { useSelector } from "react-redux";
import { Lock, Mail, User } from "lucide-react";
import { useSignUpMutation } from "../Redux/slices/apiSlice";
import Loader from "./Loader";
import InfoModal from "./InfoModal";
import { useEffect } from "react";

function SignUp() {
  const theme = useSelector((state) => state.theme.mode);
  const {
    register,
    handleSubmit,
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
      register: register("firstName", {
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
      register: register("lastName", {
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
      type: "password",
      icon: (
        <Lock
          size={18}
          className="absolute top-1/2 transform -translate-y-1/2 text-gray-400"
        />
      ),
      placeholder: "Create a password",
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
  ];

  const heading = "Create account";
  const headingClassName = "text-3xl";
  const subHeading = "Start your journey as a content creator";
  const subHeadingClassName = "text-sm text-[#a1a1a1] mb-8";

  const [signUpData, { data, isLoading, isSuccess, isError, error, reset }] =
    useSignUpMutation();

  const handleSignup = (formValues) => {
    signUpData(formValues);
  };

  useEffect(() => {
    if (isSuccess && data?.token) {
      localStorage.setItem("SignUpToken", data.token);
    }
  }, [isSuccess, data]);
  return (
    <div className="w-full flex flex-col  md:mt-32 md:ml-5 lg:mt-0 lg:ml-0 justify-center items-center dark:text-white">
      {/* Loader */}
      {isLoading && <Loader />}

      {isSuccess && (
        <InfoModal
          isOpen={true}
          type="success"
          title="Account Created 🎉"
          message="Signed Up successfully !"
          okText="Go to Gmail"
          redirectUrl="https://mail.google.com/"
          onOk={() => reset()}
          userEmail={data?.email}
          isContainsResendBtn={true}
        />
      )}

      {isError && (
        <InfoModal
          isOpen={true}
          type="error"
          title="Signup Failed"
          isContainsResendBtn={false}
          message={
            error?.data?.message || "Something went wrong, please try again."
          }
          okText="Try Again"
          onOk={() => reset()}
        />
      )}
      <div className="w-full flex flex-col justify-center items-center">
        <img
          src={theme === "dark" ? logoDark : logoLight}
          className="h-24 w-auto my-4"
        />
        <h1 className="text-md text-[#a1a1a1]">Join the creator community</h1>
      </div>

      <div className="w-full md:w-md md:px-4 mt-4 flex items-center justify-center rounded-xl">
        <Form
          formData={formData}
          headingData={{ heading, headingClassName }}
          subHeadingData={{ subHeading, subHeadingClassName }}
          errors={errors}
          className="bg-[#f2f2f5] dark:bg-[#121212] w-full mx-6 my-6 md:my-0 pl-10 p-2 border border-[#171717] rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500 "
          showCheckMark={true}
          submitBtnText="Create Account"
          btnClassName="w-full bg-[var(--orange)] rounded-lg p-2 text-black dark:text-white hover:scale-105 transition-transform "
          isContainsGoogleSignIn={true}
          onSubmit={handleSubmit(handleSignup)}
        />
      </div>
    </div>
  );
}

export default SignUp;
