import { createBrowserRouter } from "react-router-dom";

import {
  SignUp,
  SignIn,
  ForgotPassword,
  ResetPassword,
  AuthSuccess,
  VerifyUserPage,
} from "../pages/Auth";
import { AppLayout } from "../components/layout";
import { Home, Dashboard, Settings } from "../pages/User";
import ProtectedRoute from "./ProtectedRoute.jsx";
import PublicRoute from "./PublicRoute.jsx";
import { TermsOfService, PrivacyPolicy } from "../pages/Legal";
import { UpgradePlan, ReviewOrder } from "../pages/Payment";
import { Support, LandingPage } from "../pages/General";

export const router = createBrowserRouter([
  { path: "/", element: <PublicRoute element={<LandingPage />} /> },
  { path: "/signup", element: <PublicRoute element={<SignUp />} /> },
  { path: "/signin", element: <PublicRoute element={<SignIn />} /> },
  { path: "/verify_user", element: <VerifyUserPage /> },
  { path: "/terms-of-service", element: <TermsOfService /> },
  { path: "/privacy-policy", element: <PrivacyPolicy /> },
  { path: "/signIn/forgot-password", element: <ForgotPassword /> },
  { path: "/reset-password", element: <ResetPassword /> },
  { path: "/auth-success", element: <AuthSuccess /> },
  { path: "/support", element: <Support /> },
  {
    path: "/upgrade-plan",
    element: <ProtectedRoute element={<UpgradePlan />} />,
  },
  {
    path: "/review-order",
    element: <ProtectedRoute element={<ReviewOrder />} />,
  },
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
