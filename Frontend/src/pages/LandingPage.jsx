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
      <div id="features">
        <FeaturesSection />
      </div>

      {/* 2. Add id="pricing" to match Header ScrollLink */}
      <div id="pricing">
        <Subscription />
      </div>

      {/* 3. If you have an FAQ section inside CTA or Footer, wrap it too */}
      <div id="faq">
        <CTASection />
      </div>
      <Footer />
    </>
  );
}

export default LandingPage;
