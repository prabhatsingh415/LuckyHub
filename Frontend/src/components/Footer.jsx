import React from "react";
import { logoDark, logoLight } from "..";
import { useSelector } from "react-redux";
import { Link as ScrollLink } from "react-scroll";
import { useNavigate } from "react-router-dom";
import SocialIcon from "./SocialIcon";
import { XIcon, LinkedInIcon } from "..";

function Footer() {
  const theme = useSelector((state) => state.theme.mode);
  const navigate = useNavigate();

  return (
    <footer className="flex flex-col md:grid grid-cols-3 grid-rows-2 justify-center items-center  border-t-2  border-t-[#f7f7f7] dark:border-t-[#0f0f0f]">
      <div className="flex items-center ml-4 my-4 md:ml-12 mr-2 md:mr-8">
        <img
          src={theme === "dark" ? logoDark : logoLight}
          alt="Logo"
          className="h-16 md:h-12 lg:h-16 md:w-auto"
        />
      </div>

      <nav className="flex md:flex-col md:gap-4 md:mt-8 lg:flex-row justify-center items-center gap-8 text-lg font-semibold text-black dark:text-white flex-1 ">
        <ScrollLink
          to="pricing"
          smooth={true}
          duration={500}
          offset={-70}
          className="hover:text-[var(--orange)] cursor-pointer text-sm"
          onClick={() => navigate("Terms-of-Service")}
        >
          Privacy Policy
        </ScrollLink>
        <ScrollLink
          to="features"
          smooth={true}
          duration={500}
          offset={-70}
          className="hover:text-[var(--orange)] cursor-pointer text-sm"
          onClick={() => navigate("/Privacy-Policy")}
        >
          Terms of Service
        </ScrollLink>
        <ScrollLink
          to="faq"
          smooth={true}
          duration={500}
          offset={-70}
          className="hover:text-[var(--orange)] cursor-pointer text-sm"
          onClick={() => navigate("/Support")}
        >
          Support
        </ScrollLink>
      </nav>

      <hr className="md:hidden border-t-2  border-t-[#f7f7f7] dark:border-t-[#121212] w-[calc(100%-4rem)] md:w-[calc(100%-8rem)] mx-auto my-8" />

      <div className="flex justify-center items-center">
        <nav className="flex gap-8">
          <SocialIcon
            src={XIcon}
            size={28}
            onClick={() =>
              window.open("https://x.com/Prabhatsingh415", "_blank")
            }
          />
          <SocialIcon
            src={LinkedInIcon}
            size={28}
            onClick={() =>
              window.open(
                "https://www.linkedin.com/in/prabhat-singh-rj415/",
                "_blank"
              )
            }
          />
        </nav>
      </div>

      <hr className="md:col-span-3 border-t-2  border-t-[#f7f7f7] dark:border-t-[#121212] w-[calc(100%-4rem)] md:w-[calc(100%-8rem)] mx-auto my-8" />

      <div className="flex flex-col justify-center items-center md:col-span-3 md:row-start-3gap-4 dark:text-[#a1a1a1] text-sm mb-8">
        <p>© 2025 LuckyHub. All rights reserved. </p>
        <p>Developed By Prabhat Singh</p>
      </div>
    </footer>
  );
}

export default Footer;
