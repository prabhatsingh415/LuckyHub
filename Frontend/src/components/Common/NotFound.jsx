import React from "react";
import Lottie from "lottie-react";
import { Link } from "react-router-dom";
import animationData from "../../assets/Page Not Found 404.json";

const NotFound = () => {
  return (
    <div
      style={{
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center",
        height: "100vh",
        textAlign: "center",
        backgroundColor: "#f8f9fa",
        fontFamily: "Arial, sans-serif",
      }}
    >
      <div style={{ width: "400px", height: "400px" }}>
        <Lottie animationData={animationData} loop={true} autoplay={true} />
      </div>

      <h1 style={{ fontSize: "2.5rem", color: "#333", marginBottom: "10px" }}>
        Oops! Bhai, Bhatak Gaye Ho?
      </h1>
      <p style={{ fontSize: "1.2rem", color: "#666", marginBottom: "30px" }}>
        Ye rasta LuckyHub par nahi jata.
      </p>

      <Link
        to="/"
        style={{
          padding: "12px 24px",
          backgroundColor: "#f97316",
          color: "white",
          textDecoration: "none",
          borderRadius: "8px",
          fontWeight: "bold",
          boxShadow: "0 4px 6px rgba(0,0,0,0.1)",
        }}
      >
        Wapas Home Jao
      </Link>
    </div>
  );
};

export default NotFound;
