import React from "react";
import { useSelector } from "react-redux";

function SocialIcon({ src, size = 24, className = "" }) {
  const theme = useSelector((state) => state.theme.mode);
  const isDark = theme === "dark";

  return (
    <div
      className={`cursor-pointer ${className} transition-transform duration-300 hover:scale-110`}
      style={{ width: size, height: size }}
    >
      <img
        src={src}
        alt="social-icon"
        width={size}
        height={size}
        style={{ filter: isDark ? "invert(1)" : "invert(0)" }}
      />
    </div>
  );
}

export default SocialIcon;
