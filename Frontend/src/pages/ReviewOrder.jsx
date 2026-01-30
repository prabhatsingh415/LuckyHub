import React, { useState, useEffect } from "react";
import { Check, Crown, ArrowLeft, ShieldCheck } from "lucide-react";
import SUBSCRIPTION_PLANS from "../../config/subscriptionPlans";
import { useNavigate, useSearchParams } from "react-router-dom";
import { usePayment } from "../hook/usePayment";
import InfoModal from "./InfoModal";
import { useDashboardAPIQuery } from "../Redux/slices/apiSlice";
import Loader from "./Loader";

function ReviewOrder() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const { data: dashboardData, isLoading: isDashboardLoading } =
    useDashboardAPIQuery();

  const [modal, setModal] = useState({
    open: false,
    title: "",
    message: "",
    type: "info",
    okText: "OK",
    isContainsResendBtn: false,
    onOk: () => setModal((prev) => ({ ...prev, open: false })),
  });

  const planName = searchParams.get("plan")?.toUpperCase() || "GOLD";
  const isValidPlan =
    Object.keys(SUBSCRIPTION_PLANS).includes(planName) && planName !== "FREE";

  useEffect(() => {
    if (!isDashboardLoading && !isValidPlan) {
      navigate("/home", { replace: true });
    }
  }, [isValidPlan, navigate, isDashboardLoading]);

  if (isDashboardLoading) {
    return <Loader />;
  }
  if (!isValidPlan) return <Loader />;
  const planDetails = SUBSCRIPTION_PLANS[planName] || SUBSCRIPTION_PLANS.GOLD;
  const price = planDetails.price;

  const features = [
    planDetails.maxGiveaways === -1
      ? "Unlimited giveaways"
      : `${planDetails.maxGiveaways} giveaways per month`,
    `Up to ${planDetails.maxComments.toLocaleString()} comments`,
    `Up to ${planDetails.maxWinners} winners per giveaway`,
  ];

  const userEmail = dashboardData?.user?.email || "";
  const { handlePayment } = usePayment(userEmail, setModal);

  return (
    <div className="min-h-screen bg-[#0a0a0a] text-white flex flex-col font-sans">
      <nav className="relative z-50 w-full p-6 flex justify-between items-center max-w-7xl mx-auto">
        <button
          onClick={() => navigate("/home")}
          className="flex items-center gap-2 text-gray-400 hover:text-white transition-all group"
        >
          <ArrowLeft
            size={20}
            className="group-hover:-translate-x-1 transition-transform"
          />
          <span className="font-medium">Back</span>
        </button>

        <div className="flex items-center gap-2 text-green-500/80 text-xs font-medium bg-green-500/10 px-3 py-1.5 rounded-full border border-green-500/20">
          <ShieldCheck size={14} />
          Secure Checkout
        </div>
      </nav>

      <main className="flex-1 flex flex-col items-center justify-center p-4 md:-mt-12 lg:-mt-20">
        <div className="w-full max-w-md">
          {/* Header Section */}
          <div className="text-center mb-4">
            <h1 className="text-3xl font-bold tracking-tight">Review Order</h1>
            <p className="text-gray-400 mt-1 text-sm px-2">
              You're just one step away from unlocking premium features
            </p>
          </div>

          {/* Order Summary Card */}
          <div className="bg-[#161616] border border-gray-800 rounded-3xl p-6 shadow-2xl relative overflow-hidden">
            <div className="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-transparent via-orange-500 to-transparent opacity-50"></div>

            <h2 className="text-lg font-semibold mb-6">Order Summary</h2>

            {/* Plan Info */}
            <div className="bg-[#1f1512] border border-orange-900/30 rounded-2xl p-5 mb-8 flex items-start gap-4">
              <div className="bg-[#2d1a12] p-3 rounded-xl border border-orange-500/20">
                <Crown className="text-orange-500 w-6 h-6" />
              </div>
              <div>
                <h3 className="font-bold text-xl text-orange-50">
                  {planName.charAt(0).toUpperCase() +
                    planName.slice(1).toLowerCase()}{" "}
                  Plan
                </h3>
                <p className="text-gray-400 text-sm">Monthly subscription</p>
              </div>
            </div>

            {/* Features List */}
            <ul className="space-y-4 mb-8">
              {features.map((feature, index) => (
                <li
                  key={index}
                  className="flex items-center gap-3 text-[15px] text-gray-300"
                >
                  <div className="bg-orange-500/10 p-1 rounded-full">
                    <Check className="text-orange-500 w-3.5 h-3.5" />
                  </div>
                  {feature}
                </li>
              ))}
            </ul>

            {/* Pricing Details */}
            <div className="border-t border-gray-800/60 pt-6 space-y-4">
              <div className="flex justify-between text-gray-400 text-sm">
                <span>Subtotal</span>
                <span className="text-gray-200">₹{price.toFixed(2)}</span>
              </div>
              <div className="flex justify-between text-gray-400 text-sm">
                <span>Tax (0%)</span>
                <span className="text-gray-200">₹0.00</span>
              </div>
              <div className="flex justify-between items-center pt-2">
                <span className="text-lg font-bold">Total</span>
                <div className="text-right">
                  <span className="text-2xl font-extrabold text-orange-500">
                    ₹{price.toFixed(2)}
                  </span>
                  <span className="text-gray-500 text-xs block">/month</span>
                </div>
              </div>
            </div>

            <button
              onClick={() => handlePayment(planName)}
              className="w-full bg-orange-600 hover:bg-orange-500 text-white font-bold py-4 rounded-2xl mt-8 transition-all active:scale-[0.98] shadow-lg shadow-orange-900/20 flex items-center justify-center gap-2"
            >
              Confirm and Pay
            </button>

            <div className="text-center text-[11px] text-gray-500 mt-6 leading-relaxed flex flex-wrap justify-center gap-1 px-4">
              By confirming, you agree to our{" "}
              <span
                onClick={() => navigate("/terms-of-service")}
                className="underline cursor-pointer hover:text-gray-300"
              >
                Terms
              </span>
              &
              <span
                onClick={() => navigate("/privacy-policy")}
                className="underline cursor-pointer hover:text-gray-300"
              >
                Privacy Policy
              </span>
            </div>
          </div>
        </div>
      </main>

      {modal.open && (
        <InfoModal
          isOpen={modal.open}
          title={modal.title}
          message={modal.message}
          type={modal.type}
          okText={modal.okText}
          isContainsResendBtn={false}
          onOk={modal.onOk}
        />
      )}
    </div>
  );
}

export default ReviewOrder;
