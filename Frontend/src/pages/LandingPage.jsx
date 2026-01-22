import Header from "../components/Header";
import Footer from "../components/Footer";
import MainContent from "../components/landing/HeroSection";
import WorkingSection from "../components/landing/WorkingSection";
import FeaturesSection from "../components/landing/FeaturesSection";
import Subscription from "../components/landing/Subscription";
import CTASection from "../components/landing/CTASection";
import FAQ from "../components/landing/FAQ";

function LandingPage() {
  return (
    <>
      <Header />
      <MainContent />
      <WorkingSection />
      <div id="features">
        <FeaturesSection />
      </div>

      <div id="pricing">
        <Subscription />
      </div>

      <div id="faq">
        <FAQ />
      </div>
      <CTASection />
      <Footer />
    </>
  );
}

export default LandingPage;
