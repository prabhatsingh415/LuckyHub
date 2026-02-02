import { logoDark, logoLight } from "../..";
import { Form, Loader, InfoModal } from "../../components/Common";
import { useForm } from "react-hook-form";
import { useSelector } from "react-redux";
import { Lock, Mail, User } from "lucide-react";
import { useSignUpMutation } from "../../Redux/slices/apiSlice";
import { useEffect } from "react";

function SignUp() {
  const theme = useSelector((state) => state.theme.mode);
  const {
    register,
    handleSubmit,
    formState: { errors },
    watch,
  } = useForm();

  const password = watch("password", "");

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
        maxLength: { value: 50, message: "Max 50 characters allowed" },
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
        maxLength: { value: 50, message: "Max 50 characters allowed" },
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
        maxLength: { value: 100, message: "Email too long" },
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
        pattern: {
          value:
            /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\S+$).{8,50}$/,
          message: "Password does not meet requirements",
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
        {password && (
          <div className="w-full px-10 -mt-4 mb-4 text-left">
            <ul className="text-[10px] md:text-xs text-gray-400 space-y-1">
              <li
                className={
                  password.length >= 8 ? "text-green-500" : "text-red-500"
                }
              >
                • At least 8 characters
              </li>
              <li
                className={
                  /[A-Z]/.test(password) ? "text-green-500" : "text-red-500"
                }
              >
                • One uppercase letter
              </li>
              <li
                className={
                  /[a-z]/.test(password) ? "text-green-500" : "text-red-500"
                }
              >
                • One lowercase letter
              </li>
              <li
                className={
                  /[0-9]/.test(password) ? "text-green-500" : "text-red-500"
                }
              >
                • One number
              </li>
              <li
                className={
                  /[^A-Za-z0-9]/.test(password)
                    ? "text-green-500"
                    : "text-red-500"
                }
              >
                • One special character (@#$%^&+=)
              </li>
            </ul>
          </div>
        )}
      </div>
    </div>
  );
}

export default SignUp;
