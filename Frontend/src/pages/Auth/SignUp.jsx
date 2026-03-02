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
          className="absolute top-1/2 transform -translate-y-1/2 text-gray-400"
        />
      ),
      placeholder: "Enter your first name",
      register: register("firstName", {
        required: "First name is required",
        minLength: { value: 2, message: "Too short" },
        maxLength: { value: 50, message: "Max 50 characters allowed" },
      }),
    },
    {
      label: "Last Name",
      type: "text",
      icon: (
        <User
          size={20}
          className="absolute top-1/2 transform -translate-y-1/2 text-gray-400"
        />
      ),
      placeholder: "Enter your last name",
      register: register("lastName", {
        required: "Last name is required",
        minLength: { value: 2, message: "Too short" },
        maxLength: { value: 50, message: "Max 50 characters allowed" },
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
            /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9])(?=\S+$).{8,50}$/,
          message: "Password does not meet requirements",
        },
      }),
    },
  ];

  const heading = "Create account";
  const headingClassName = "text-2xl font-bold mb-1";
  const subHeading = "Start your journey as a content creator";
  const subHeadingClassName = "text-xs text-[#a1a1a1] mb-6";

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
    <div className="w-full min-h-screen flex flex-col items-center dark:bg-black dark:text-white pt-10 pb-20 px-4 overflow-y-auto">
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
          message={error?.data?.message || "Something went wrong."}
          okText="Try Again"
          onOk={() => reset()}
        />
      )}

      <div className="w-full max-w-md flex flex-col items-center">
        <div className="mb-2 flex flex-col items-center">
          <img
            src={theme === "dark" ? logoDark : logoLight}
            className="h-16 w-auto mb-2"
          />
          <h1 className="text-sm text-[#a1a1a1]">Join the creator community</h1>
        </div>

        <div className="w-full bg-[#f2f2f5] dark:bg-[#121212] border border-gray-200 dark:border-[#171717] rounded-2xl shadow-xl">
          <div className="p-6 md:p-8">
            <Form
              formData={formData}
              headingData={{ heading, headingClassName }}
              subHeadingData={{ subHeading, subHeadingClassName }}
              errors={errors}
              className="w-full space-y-3 focus:outline-none"
              showCheckMark={true}
              submitBtnText="Create Account"
              btnClassName="w-full bg-[var(--orange)] rounded-lg py-3 text-white font-semibold hover:opacity-90 transition-all mt-4"
              isContainsGoogleSignIn={true}
              onSubmit={handleSubmit(handleSignup)}
            />

            {password.length > 0 && (
              <div className="mt-6 pt-4 border-t border-gray-200 dark:border-zinc-800">
                <p className="text-[10px] uppercase tracking-wider text-gray-500 font-bold mb-2 italic">
                  Password Requirements:
                </p>
                <ul className="grid grid-cols-1 sm:grid-cols-2 gap-x-4 gap-y-1.5">
                  <li
                    className={`flex items-center gap-1.5 text-[11px] ${
                      password.length >= 8 ? "text-green-500" : "text-red-500"
                    }`}
                  >
                    <span className="text-sm">•</span> Min 8 chars
                  </li>
                  <li
                    className={`flex items-center gap-1.5 text-[11px] ${
                      /[A-Z]/.test(password) ? "text-green-500" : "text-red-500"
                    }`}
                  >
                    <span className="text-sm">•</span> Uppercase
                  </li>
                  <li
                    className={`flex items-center gap-1.5 text-[11px] ${
                      /[a-z]/.test(password) ? "text-green-500" : "text-red-500"
                    }`}
                  >
                    <span className="text-sm">•</span> Lowercase
                  </li>
                  <li
                    className={`flex items-center gap-1.5 text-[11px] ${
                      /[0-9]/.test(password) ? "text-green-500" : "text-red-500"
                    }`}
                  >
                    <span className="text-sm">•</span> One Number
                  </li>
                  <li
                    className={`flex items-center gap-1.5 text-[11px] ${
                      /[^A-Za-z0-9]/.test(password)
                        ? "text-green-500"
                        : "text-red-500"
                    }`}
                  >
                    <span className="text-sm">•</span> Special char
                  </li>
                </ul>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

export default SignUp;
