import React from "react";
import Lottie from "lottie-react";
import { Link } from "react-router-dom";
import { useSelector } from "react-redux";
// Ensure the path is correct relative to your folder structure
import animationData from "../../assets/Page Not Found 404.json";

const NotFound = () => {
  const theme = useSelector((state) => state.theme.mode);
  const isDark = theme === "dark";

  return (
    <div
      style={{
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center",
        minHeight: "100vh",
        width: "100%",
        textAlign: "center",
        backgroundColor: isDark ? "#121212" : "#ffffff", // Dynamic theme background
        color: isDark ? "#f7fafc" : "#1a202c", // Dynamic text color
        fontFamily: "'Inter', sans-serif",
        padding: "20px",
        overflow: "hidden",
        transition: "background-color 0.3s ease",
      }}
    >
      {/* Immersive Lottie Animation - Scaled for high impact */}
      <div
        style={{
          width: "100%",
          maxWidth: "650px",
          height: "auto",
          marginBottom: "-10px",
        }}
      >
        <Lottie
          animationData={animationData}
          loop={true}
          autoplay={true}
          style={{ width: "100%", height: "100%" }}
        />
      </div>

      {/* Professional Copywriting */}
      <h1
        style={{
          fontSize: "clamp(2.5rem, 6vw, 3.5rem)",
          fontWeight: "800",
          marginBottom: "16px",
          letterSpacing: "-0.025em",
        }}
      >
        Page Not Found
      </h1>

      <p
        style={{
          fontSize: "1.125rem",
          color: isDark ? "#a0aec0" : "#4a5568",
          marginBottom: "40px",
          maxWidth: "550px",
          lineHeight: "1.6",
        }}
      >
        The resource you are looking for might have been removed, had its name
        changed, or is temporarily unavailable. Please verify the URL or return
        to the homepage.
      </p>

      {/* Primary Action Button */}
      <Link
        to="/"
        style={{
          padding: "16px 40px",
          backgroundColor: "#f97316", // LuckyHub Brand Orange
          color: "#ffffff",
          textDecoration: "none",
          borderRadius: "12px",
          fontSize: "1rem",
          fontWeight: "600",
          transition: "all 0.3s ease",
          boxShadow: isDark
            ? "0 10px 15px -3px rgba(0, 0, 0, 0.5)"
            : "0 10px 15px -3px rgba(249, 115, 22, 0.3)",
          display: "inline-flex",
          alignItems: "center",
        }}
        onMouseEnter={(e) => {
          e.target.style.transform = "scale(1.05)";
          e.target.style.backgroundColor = "#ea580c";
        }}
        onMouseLeave={(e) => {
          e.target.style.transform = "scale(1)";
          e.target.style.backgroundColor = "#f97316";
        }}
      >
        Return to Homepage
      </Link>
    </div>
  );
};

export default NotFound;
