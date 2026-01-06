import { logoDark, logoLight } from "..";
import Form from "../components/Form";
import { useForm } from "react-hook-form";
import { useEffect } from "react";
import { Lock, Mail } from "lucide-react";
import { useSelector } from "react-redux";
import { useSignInMutation } from "../Redux/slices/apiSlice";
import Loader from "./Loader";
import InfoModal from "./InfoModal";

function SignIn() {
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
      placeholder: "Enter your password",
      register: register("password", {
        required: "Password is required",
        // minLength: {
        //   value: 6,
        //   message: "Password must be at least 6 characters",
        // },
        // maxLength: {
        //   value: 30,
        //   message: "Password must be less than 30 characters",
        // },
        // pattern: {
        //   value: /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d@$!%*#?&]{6,}$/,
        //   message: "Password must contain letters and numbers",
        // },
      }),
    },
  ];

  const heading = "Welcome Back!";
  const headingClassName = "text-3xl mb-4";

  const [signIn, { data, isLoading, isSuccess, isError, error, reset }] =
    useSignInMutation();

  const handleSignIn = (formValues) => {
    signIn(formValues);
  };

  useEffect(() => {
    if (isSuccess) {
      localStorage.setItem("isSignIn", "true");
    }
  }, [isSuccess, data]);
  return (
    <div className="w-full flex flex-col justify-center items-center dark:text-white">
      {isLoading && <Loader />}

      {isSuccess && (
        <InfoModal
          isOpen={true}
          type="success"
          title="Sign In successfull"
          message="Signed in successfully !"
          isContainsResendBtn={false}
          okText="ok"
          redirectUrl={"http://localhost:5173/"}
          onOk={() => reset()}
        />
      )}

      {isError && (
        <InfoModal
          isOpen={true}
          type="error"
          title="SignIn Failed"
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
          errors={errors}
          className="bg-[#f2f2f5] dark:bg-[#121212] w-full mx-6 my-6 md:my-0 pl-4 p-2 border border-[#171717] rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500 "
          submitBtnText="Sign In"
          btnClassName="w-full bg-[var(--orange)] rounded-lg p-2  text-black dark:text-white hover:scale-105 transition-transform "
          isContainsGoogleSignIn={true}
          isSignInPage={true}
          onSubmit={handleSubmit(handleSignIn)}
        />
      </div>
    </div>
  );
}

export default SignIn;
