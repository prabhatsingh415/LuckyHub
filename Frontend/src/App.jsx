import { useEffect } from "react";
import { useSelector } from "react-redux";
import { RouterProvider, createBrowserRouter } from "react-router-dom";
import LandingPage from "./pages/LandingPage";
import SignUp from "./pages/SignUp";
import SignIn from "./pages/SignIn";
import TermsOfService from "./pages/TermsOfService";
import PrivacyPolicy from "./pages/PrivacyPolicy";
import VerifyUserPage from "./pages/verfyUserPage";
import Home from "./pages/Home";
import "./App.css";

function App() {
  const theme = useSelector((state) => state.theme.mode);
  const isSignIn = localStorage.getItem("isSignIn") === "true";

  useEffect(() => {
    document.documentElement.classList.toggle("dark", theme === "dark");
  }, [theme]);

  const router = createBrowserRouter([
    { path: "/", element: isSignIn ? <Home /> : <LandingPage /> },
    { path: "/signup", element: <SignUp /> },
    { path: "/signin", element: <SignIn /> },
    { path: "/home", element: <Home /> },
    { path: "/terms-of-condition", element: <TermsOfService /> },
    { path: "/privacy-policy", element: <PrivacyPolicy /> },
    { path: "/verify_user", element: <VerifyUserPage /> },
  ]);

  return (
    <div className="flex flex-col min-h-screen dark:bg-[var(--black)]">
      <RouterProvider router={router} />
    </div>
  );
}

export default App;
