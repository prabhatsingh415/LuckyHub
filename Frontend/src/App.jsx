import { useEffect, useState } from "react";
import { useSelector, useDispatch } from "react-redux";
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
import ForgotPassword from "./pages/ForgotPassword";
import ResetPassword from "./pages/ResetPassword";
import AuthSuccess from "./pages/AuthSuccess";
import AppLayout from "./components/layout/AppLayout";
import Dashboard from "./components/layout/Dashboard";
import Settings from "./components/layout/Settings";
import Home from "./components/layout/Home";
import { useDashboardAPIQuery } from "./Redux/slices/apiSlice";
import { setAuth, logout } from "./Redux/slices/authSlice";

import "./App.css";

function App() {
  const dispatch = useDispatch();
  const theme = useSelector((state) => state.theme.mode);
  const loader = useSelector((state) => state.loader.showLoader);

  const { isAuthenticated, isCheckingAuth } = useSelector(
    (state) => state.userDetails
  );

  const {
    data: userData,
    isLoading: isApiLoading,
    isError,
  } = useDashboardAPIQuery();

  useEffect(() => {
    if (userData) {
      dispatch(setAuth({ isAuthenticated: true, user: userData }));
    } else if (isError) {
      dispatch(logout());
    }
  }, [userData, isError, dispatch]);

  // Theme toggle
  useEffect(() => {
    document.documentElement.classList.toggle("dark", theme === "dark");
  }, [theme]);

  if (isApiLoading || isCheckingAuth) {
    return <Loader />;
  }

  // Wrapper Components for clean route protection
  const ProtectedRoute = ({ element }) =>
    isAuthenticated ? element : <Navigate to="/" replace />;

  const PublicRoute = ({ element }) =>
    !isAuthenticated ? element : <Navigate to="/home" replace />;

  const router = createBrowserRouter([
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

  return (
    <div className="flex flex-col min-h-screen dark:bg-[var(--black)]">
      <RouterProvider router={router} />
      {loader && <Loader />}
    </div>
  );
}

export default App;
