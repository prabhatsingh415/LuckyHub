import { useEffect } from "react";
import { useSelector } from "react-redux";
import "./App.css";
import Header from "./components/Header";
import { BrowserRouter, createBrowserRouter } from "react-router-dom";
import Footer from "./components/Footer";
import LandingPage from "./pages/LandingPage";

function App() {
  const theme = useSelector((state) => state.theme.mode);

  useEffect(() => {
    document.documentElement.classList.toggle("dark", theme === "dark");
  }, [theme]);

  return (
    <BrowserRouter>
      <div className=" flex flex-col min-h-screen  dark:bg-[var(--black)] ">
        <LandingPage />
      </div>
    </BrowserRouter>
  );
}

export default App;
