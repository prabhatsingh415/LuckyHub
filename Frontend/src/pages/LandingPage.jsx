import React from "react";
import Header from "../components/Header";
import Footer from "../components/Footer";
import MainContent from "../components/landing/HeroSection";
import WorkingSection from "../components/landing/WorkingSection";

function LandingPage() {
  return (
    <>
      <Header />
      <MainContent />
      <WorkingSection />
      <Footer />
    </>
  );
}

export default LandingPage;
