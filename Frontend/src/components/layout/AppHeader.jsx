import { Menu, House, ChartColumn, Settings, X, Sun, Moon } from "lucide-react";
import { NavLink } from "react-router-dom";
import { logoDark, logoLight } from "../../";
import { useSelector, useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";
import { toggleTheme } from "../../Redux/slices/themeSlice";
import { useState } from "react";

function AppHeader() {
  const theme = useSelector((state) => state.theme.mode);
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const menuItems = [
    { label: "Home", to: "/home", icon: <House size={20} /> },
    { label: "Dashboard", to: "/dashboard", icon: <ChartColumn size={20} /> },
    { label: "Settings", to: "/settings", icon: <Settings size={20} /> },
  ];

  return (
    <header className="w-full flex items-center justify-between border-b-2 border-b-[#f7f7f7] dark:border-b-[#0f0f0f] px-4 md:px-8">
      <Menu
        color="white"
        className="mt-2 lg:hidden"
        size={30}
        onClick={() => setIsMenuOpen(true)}
      />
      <div className="flex items-center mr-2 md:mr-[14rem] ">
        <img
          src={theme === "dark" ? logoDark : logoLight}
          alt="Logo"
          className="h-8 md:h-12 lg:h-16 md:w-auto"
        />
      </div>
      {isMenuOpen && (
        <div className="fixed top-0 left-0 h-screen w-3/5 bg-[#0a0a0a] border-r-2 border-zinc-800 z-50 shadow-lg transform transition-transform duration-300">
          <button className="p-2" onClick={() => setIsMenuOpen(false)}>
            <X color="white" size={30} />
          </button>
          <nav className="flex flex-col gap-4 mt-8 px-4">
            {menuItems.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                className={({ isActive }) =>
                  isActive
                    ? "text-[#ff3333] font-bold flex items-center gap-2"
                    : "text-[#fafafa] flex items-center gap-2"
                }
                onClick={() => setIsMenuOpen(false)}
              >
                {item.icon}
                {item.label}
              </NavLink>
            ))}
          </nav>
        </div>
      )}
      <nav className="hidden lg:flex gap-4">
        <NavLink
          to="/home"
          className={({ isActive }) =>
            isActive ? "text-[#ff3333] font-bold" : "text-[#fafafa]"
          }
        >
          Home
        </NavLink>

        <NavLink
          to="/dashboard"
          className={({ isActive }) =>
            isActive ? "text-[#ff3333] font-bold" : "text-[#fafafa]"
          }
        >
          Dashboard
        </NavLink>

        <NavLink
          to="/settings"
          className={({ isActive }) =>
            isActive ? "text-[#ff3333] font-bold" : "text-[#fafafa]"
          }
        >
          Settings
        </NavLink>
      </nav>
      <button
        onClick={() => dispatch(toggleTheme())}
        className="flex justify-center items-center rounded-xl h-12 w-12 cursor-pointer hover:bg-[#f7f7f7] hover:dark:bg-[#0f0f0f]"
      >
        {theme === "dark" ? <Sun color="#FFFFFF" /> : <Moon />}
      </button>
    </header>
  );
}

export default AppHeader;
