import { RouterProvider } from "react-router-dom";
import { useSelector } from "react-redux";
import AuthInitializer from "./app/AuthInitializer.jsx";
import { router } from "./app/router.jsx";
import { Loader } from "./components/Common";
import "./App.css";
import { useEffect } from "react";

function App() {
  const theme = useSelector((state) => state.theme.mode);
  const showGlobalLoader = useSelector((state) => state.loader.showLoader);

  useEffect(() => {
    document.documentElement.classList.toggle("dark", theme === "dark");
  }, [theme]);

  return (
    <div className="flex flex-col min-h-screen dark:bg-[var(--black)]">
      <AuthInitializer>
        <RouterProvider router={router} />
        {showGlobalLoader && <Loader />}
      </AuthInitializer>
    </div>
  );
}

export default App;
