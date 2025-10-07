import { useEffect } from "react";
import { useSelector } from "react-redux";
import "./App.css";
import { BrowserRouter } from "react-router-dom";
import LandingPage from "./pages/LandingPage";
import SignUp from "./pages/SignUp";

function App() {
  const theme = useSelector((state) => state.theme.mode);

  useEffect(() => {
    document.documentElement.classList.toggle("dark", theme === "dark");
  }, [theme]);

  return (
    <BrowserRouter>
      <div className=" flex flex-col min-h-screen  dark:bg-[var(--black)] ">
        {/* <LandingPage /> */}
        <SignUp />
      </div>
    </BrowserRouter>
  );
}

export default App;
