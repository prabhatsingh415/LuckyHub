import React from "react";
import Header from "../components/Header";
import Footer from "../components/Footer";
import MainContent from "../components/landing/HeroSection";
import WorkingSection from "../components/landing/WorkingSection";
import FeaturesSection from "../components/landing/FeaturesSection";
import Subscription from "../components/landing/Subscription";
import CTASection from "../components/landing/CTASection";

function LandingPage() {
  return (
    <>
      <Header />
      <MainContent />
      <WorkingSection />
      <FeaturesSection />
      <Subscription />
      <CTASection />
      <Footer />
    </>
  );
}

export default LandingPage;
