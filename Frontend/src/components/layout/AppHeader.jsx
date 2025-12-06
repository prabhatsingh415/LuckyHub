import { Menu, House, ChartColumn, Settings, X, Sun, Moon } from "lucide-react";
import { NavLink } from "react-router-dom";
import { logoDark, logoLight } from "../../"; // Ensure this path is correct
import { useSelector, useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";
import { toggleTheme } from "../../Redux/slices/themeSlice"; // Ensure path is correct
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

  const SidebarContent = (
    <div className="flex flex-col h-full">
      {/* Logo Section */}
      <div className="flex items-center justify-start px-6 py-6 mb-4">
        <img
          src={theme === "dark" ? logoDark : logoLight}
          alt="Logo"
          className="h-8 md:h-10 w-auto"
        />
      </div>

      {/* Navigation Links */}
      <nav className="flex flex-col gap-2 px-4 flex-1">
        {menuItems.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            onClick={() => setIsMenuOpen(false)} // Close menu on mobile click
            className={({ isActive }) =>
              `flex items-center gap-3 px-4 py-3 rounded-xl transition-all duration-200 font-medium ${
                isActive
                  ? "bg-[#ff3333] text-white shadow-lg shadow-red-500/20" // Active: Red bg, White text
                  : "text-gray-600 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-[#1f1f1f] hover:text-black dark:hover:text-white"
              }`
            }
          >
            {item.icon}
            <span>{item.label}</span>
          </NavLink>
        ))}
      </nav>

      {/* Bottom Section: Theme Toggle */}
      <div className="p-4 border-t border-gray-200 dark:border-[#1f1f1f]">
        <button
          onClick={() => dispatch(toggleTheme())}
          className="flex items-center justify-center w-full gap-2 p-3 rounded-xl bg-gray-100 dark:bg-[#1f1f1f] text-gray-800 dark:text-white hover:bg-gray-200 dark:hover:bg-[#2a2a2a] transition-colors"
        >
          {theme === "dark" ? <Sun size={20} /> : <Moon size={20} />}
          <span className="text-sm font-medium">
            {theme === "dark" ? "Light Mode" : "Dark Mode"}
          </span>
        </button>
      </div>
    </div>
  );

  return (
    <>
      {/* 1. Mobile Header */}
      <header className="lg:hidden w-full flex items-center justify-between border-b border-[#f7f7f7] dark:border-[#1f1f1f] px-4 py-3 bg-[#fafafa] dark:bg-[#0a0a0a]">
        <div className="flex items-center">
          <button onClick={() => setIsMenuOpen(true)}>
            <Menu color={theme === "dark" ? "white" : "black"} size={28} />
          </button>
          <img
            src={theme === "dark" ? logoDark : logoLight}
            alt="Logo"
            className="h-8 w-auto"
          />
        </div>
        <button
          onClick={() => dispatch(toggleTheme())}
          className="flex justify-center items-center rounded-xl h-12 w-12 cursor-pointer hover:bg-[#f7f7f7] hover:dark:bg-[#0f0f0f]"
        >
          {theme === "dark" ? <Sun color="#FFFFFF" /> : <Moon />}
        </button>
      </header>
      <aside
        className={`fixed top-0 left-0 z-50 h-screen w-64 bg-[#fafafa] dark:bg-[#0a0a0a] border-r border-[#f7f7f7] dark:border-[#1f1f1f] transition-transform duration-300 ease-in-out
          ${
            isMenuOpen ? "translate-x-0" : "-translate-x-full"
          } lg:translate-x-0 lg:static lg:block shrink-0`}
      >
        <div className="lg:hidden flex justify-end p-4">
          <button onClick={() => setIsMenuOpen(false)}>
            <X color={theme === "dark" ? "white" : "black"} size={28} />
          </button>
        </div>

        {SidebarContent}
      </aside>

      {isMenuOpen && (
        <div
          className="fixed inset-0 bg-black/50 z-40 lg:hidden"
          onClick={() => setIsMenuOpen(false)}
        />
      )}
    </>
  );
}

export default AppHeader;
