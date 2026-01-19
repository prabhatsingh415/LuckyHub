import { createBrowserRouter, Navigate } from "react-router-dom";

import LandingPage from "../pages/LandingPage";
import SignUp from "../pages/SignUp";
import SignIn from "../pages/SignIn";
import AppLayout from "../components/layout/AppLayout";
import Home from "../components/layout/Home";
import Dashboard from "../components/layout/Dashboard";
import Settings from "../components/layout/Settings";
import ProtectedRoute from "./ProtectedRoute.jsx";
import PublicRoute from "./PublicRoute.jsx";
import VerifyUserPage from "../pages/verifyUserPage.jsx";
import TermsOfService from "../pages/TermsOfService";
import PrivacyPolicy from "../pages/PrivacyPolicy";
import ForgotPassword from "../pages/ForgotPassword";
import ResetPassword from "../pages/ResetPassword";
import AuthSuccess from "../pages/AuthSuccess";

export const router = createBrowserRouter([
  { path: "/", element: <PublicRoute element={<LandingPage />} /> },
  { path: "/signup", element: <PublicRoute element={<SignUp />} /> },
  { path: "/signin", element: <PublicRoute element={<SignIn />} /> },
  { path: "/verify_user", element: <VerifyUserPage /> },
  { path: "/terms-of-condition", element: <TermsOfService /> },
  { path: "/privacy-policy", element: <PrivacyPolicy /> },
  { path: "/signIn/forgot-password", element: <ForgotPassword /> },
  { path: "/reset-password", element: <ResetPassword /> },
  { path: "/auth-success", element: <AuthSuccess /> },
  {
    element: <AppLayout />,
    children: [
      { path: "/home", element: <ProtectedRoute element={<Home />} /> },
      {
        path: "/dashboard",
        element: <ProtectedRoute element={<Dashboard />} />,
      },
      {
        path: "/settings",
        element: <ProtectedRoute element={<Settings />} />,
      },
    ],
  },
]);
