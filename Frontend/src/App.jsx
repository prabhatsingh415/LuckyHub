import { useEffect } from "react";
import { useSelector } from "react-redux";
import "./App.css";
import Header from "./components/Header";
import { BrowserRouter } from "react-router-dom";
import Footer from "./components/Footer";

function App() {
  const theme = useSelector((state) => state.theme.mode);

  useEffect(() => {
    document.documentElement.classList.toggle("dark", theme === "dark");
  }, [theme]);

  return (
    <BrowserRouter>
      <div className=" flex flex-col gap-[300px] min-h-screen  dark:bg-[var(--black)] ">
        <Header />
        <Footer />
      </div>
    </BrowserRouter>
  );
}

export default App;
