import { useSelector, useDispatch } from "react-redux";
import { logoDarkSvg, logoLightSvg, logoDark, logoLight } from "..";
import { Link as ScrollLink } from "react-scroll";
import { Sun, Moon } from "lucide-react";
import { toggleTheme } from "../Redux/slices/themeSlice";
import { useNavigate } from "react-router-dom";

function Header() {
  const theme = useSelector((state) => state.theme.mode);
  const dispatch = useDispatch();
  const navigate = useNavigate();

  return (
    <header className="w-full flex items-center justify-between border-b-2 border-b-[#f7f7f7] dark:border-b-[#0f0f0f] px-4 md:px-8">
      <div className="flex items-center mr-2 md:mr-[14rem] ">
        <img
          src={theme === "dark" ? logoDark : logoLight}
          alt="Logo"
          className="h-8 md:h-12 lg:h-16 md:w-auto"
        />
      </div>

      <nav className="hidden lg:flex justify-center items-center gap-16 text-lg font-semibold text-black dark:text-white flex-1">
        <ScrollLink
          to="pricing"
          smooth={true}
          duration={500}
          offset={-70}
          className="hover:text-[var(--orange)] cursor-pointer"
        >
          Pricing
        </ScrollLink>
        <ScrollLink
          to="features"
          smooth={true}
          duration={500}
          offset={-70}
          className="hover:text-[var(--orange)] cursor-pointer"
        >
          Features
        </ScrollLink>
        <ScrollLink
          to="faq"
          smooth={true}
          duration={500}
          offset={-70}
          className="hover:text-[var(--orange)] cursor-pointer"
        >
          FAQ's
        </ScrollLink>
      </nav>

      <div className="flex gap-2 md:gap-6 items-center w-auto">
        <button
          onClick={() => dispatch(toggleTheme())}
          className="flex justify-center items-center rounded-xl h-12 w-12 cursor-pointer hover:bg-[#f7f7f7] hover:dark:bg-[#0f0f0f]"
        >
          {theme === "dark" ? <Sun color="#FFFFFF" /> : <Moon />}
        </button>

        <button
          onClick={() => navigate("/signIn")}
          className="font-bold dark:text-white text-black text-xs md:text-lg cursor-pointer hover:text-[var(--orange)]"
        >
          Sign In
        </button>

        <button
          onClick={() => navigate("/signUp")}
          className="h-6 w-24 ml-2 md:h-8 md:pb-8 md:w-32 lg:pb-2 lg:h-12 bg-[var(--orange)] text-xs md:text-lg md:py-2 rounded-lg md:rounded-xl font-bold text-black dark:text-white hover:scale-90 hover:bg-orange-800 transition-transform cursor-pointer"
        >
          Get Started
        </button>
      </div>
    </header>
  );
}

export default Header;
