import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import {
  createBrowserRouter,
  RouterProvider,
  Navigate,
} from "react-router-dom";

import LandingPage from "./pages/LandingPage";
import SignUp from "./pages/SignUp";
import SignIn from "./pages/SignIn";
import TermsOfService from "./pages/TermsOfService";
import PrivacyPolicy from "./pages/PrivacyPolicy";
import VerifyUserPage from "./pages/verfyUserPage";
import Loader from "./pages/Loader";
import Home from "./pages/Home";
import ForgotPassword from "./pages/ForgotPassword";
import ResetPassword from "./pages/ResetPassword";
import AuthSuccess from "./pages/AuthSuccess";

import "./App.css";

function App() {
  const theme = useSelector((state) => state.theme.mode);
  const loader = useSelector((state) => state.loader.showLoader);
  const [isSignIn, setIsSignIn] = useState(
    localStorage.getItem("isSignIn") === "true"
  );

  // Theme toggle
  useEffect(() => {
    document.documentElement.classList.toggle("dark", theme === "dark");
  }, [theme]);

  // Sync sign-in status across tabs
  useEffect(() => {
    const handleStorageChange = () => {
      setIsSignIn(localStorage.getItem("isSignIn") === "true");
      const cookies = document.cookie;
    };
    window.addEventListener("storage", handleStorageChange);
    return () => window.removeEventListener("storage", handleStorageChange);
  }, []);

  // Wrapper Components for clean route protection
  const ProtectedRoute = ({ element }) =>
    isSignIn ? element : <Navigate to="/" replace />;

  const PublicRoute = ({ element }) =>
    !isSignIn ? element : <Navigate to="/home" replace />;

  const router = createBrowserRouter([
    { path: "/", element: <PublicRoute element={<LandingPage />} /> },
    { path: "/signup", element: <PublicRoute element={<SignUp />} /> },
    { path: "/signin", element: <PublicRoute element={<SignIn />} /> },
    { path: "/home", element: <ProtectedRoute element={<Home />} /> },
    { path: "/verify_user", element: <VerifyUserPage /> },
    { path: "/terms-of-condition", element: <TermsOfService /> },
    { path: "/privacy-policy", element: <PrivacyPolicy /> },
    { path: "/signIn/forgot-password", element: <ForgotPassword /> },
    { path: "/reset-password", element: <ResetPassword /> },
    { path: "/auth-success", element: <AuthSuccess /> },
  ]);

  return (
    <div className="flex flex-col min-h-screen dark:bg-[var(--black)]">
      <RouterProvider router={router} />
      {loader && <Loader />}
    </div>
  );
}

export default App;
