import {
  Header,
  Footer,
  HeroSection as MainContent,
  WorkingSection,
  FeaturesSection,
  SubscriptionSection as Subscription,
  CTA as CTASection,
  FAQSection as FAQ,
} from "../../components/landing";

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
