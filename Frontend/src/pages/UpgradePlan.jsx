import { useState } from "react";
import { Crown, Gift, CircleStar, Gem } from "lucide-react";
import PlanCard from "../components/PlanCard";
import InfoModal from "../pages/InfoModal";
import {
  useDashboardAPIQuery,
  useCreateOrderMutation,
  useVerifyPaymentMutation,
} from "../Redux/slices/apiSlice";
import { useNavigate } from "react-router-dom";

const subscriptionPlan = [
  {
    id: 1,
    icon: <Gift />,
    name: "Free",
    price: "₹0/forever",
    description: "Perfect for getting started with giveaways",
    features: [
      "3 giveaways per month",
      "Up to 300 comments",
      "2 winner per giveaway",
    ],
    cta: "Start Free",
  },
  {
    id: 2,
    icon: <CircleStar />,
    name: "Gold",
    price: "₹49/month",
    description: "Ideal for frequent organizers with advanced features",
    features: [
      "10 giveaways per month",
      "Up to 600 comments",
      "Up to 5 winners per giveaway",
    ],
    cta: "Go Gold",
  },
  {
    id: 3,
    icon: <Gem />,
    name: "Diamond",
    price: "₹79/month",
    description: "Unlimited giveaways and top-tier features for pros",
    features: [
      "Unlimited giveaways",
      "1000 comments",
      "10 winners per giveaway",
    ],
    cta: "Go Diamond",
  },
];

function UpgradePlan() {
  const { data: dashboardData } = useDashboardAPIQuery();
  const [createOrder] = useCreateOrderMutation();
  const [verifyPayment] = useVerifyPaymentMutation();
  const navigate = useNavigate();
  const [modal, setModal] = useState({
    open: false,
    title: "",
    message: "",
    type: "info",
    okText: "OK",
    isContainsResendBtn: false,
    onOk: () => setModal({ ...modal, open: false }),
  });

  const currentPlan = dashboardData?.user?.subscriptionType || "FREE";

  const handlePayment = async (plan) => {
    if (plan.name.toUpperCase() === "FREE") return;

    try {
      const orderData = await createOrder(plan.name.toUpperCase()).unwrap();

      const options = {
        key: import.meta.env.VITE_RAZORPAY_KEY_ID,
        amount: orderData.amount * 100,
        currency: "INR",
        name: "Lucky Hub",
        description: `Upgrade to ${plan.name} Plan`,
        order_id: orderData.orderId,
        handler: async function (response) {
          try {
            await verifyPayment({
              razorpay_order_id: response.razorpay_order_id,
              razorpay_payment_id: response.razorpay_payment_id,
              razorpay_signature: response.razorpay_signature,
            }).unwrap();
            setModal({
              open: true,
              title: "Payment Successful",
              message: `You have successfully upgraded to the ${plan.name} plan.`,
              type: "success",
              okText: "OK",
              isContainsResendBtn: false,
              onOk: () => {
                window.location.href = "/settings";
              },
            });
          } catch (err) {
            setModal({
              open: true,
              title: "Verification Failed",
              message:
                "Payment verification failed. Please contact support if amount was deducted.",
              type: "error",
              okText: "OK",
              onOk: () => setModal((prev) => ({ ...prev, open: false })),
            });
          }
        },
        prefill: {
          email: dashboardData?.user?.email,
        },
        theme: { color: "#ff4d29" },
      };

      const rzp = new window.Razorpay(options);
      rzp.open();
    } catch (err) {
      setModal({
        open: true,
        title: "Payment Failed",
        message: "An error occurred during payment.",
        type: "error",
        isContainsResendBtn: false,
        okText: "OK",
        onOk: () => setModal({ ...modal, open: false }),
      });
    }
  };

  return (
    <div className="min-h-screen dark:bg-[#0a0a0a] flex flex-col items-center p-8 gap-12 text-white">
      <div className="flex flex-col items-center gap-6 mt-10">
        <div className="inline-flex items-center gap-3 px-6 py-2 rounded-full border border-orange-500/30 bg-gradient-to-r from-orange-950/20 to-yellow-950/20 backdrop-blur-sm">
          <Crown className="text-yellow-500" size={24} />
          <h2 className="text-xl sm:text-2xl font-bold bg-gradient-to-r from-red-500 via-orange-500 to-yellow-500 bg-clip-text text-transparent">
            Choose Your Plan
          </h2>
        </div>

        <p className="text-[#a1a1a1] text-center max-w-2xl text-lg">
          Unlock the full potential of LuckyHub with our flexible pricing plans
          designed for creators of all sizes
        </p>
      </div>

      <div className="flex flex-wrap justify-center items-stretch gap-8 w-full max-w-7xl mb-20">
        {subscriptionPlan.map((plan) => (
          <PlanCard
            key={plan.id}
            plan={plan}
            isCurrent={currentPlan === plan.name.toUpperCase()}
            onClick={() => handlePayment(plan)}
          />
        ))}
      </div>
      {modal.open && (
        <InfoModal
          isOpen={modal.open}
          title={modal.title}
          message={modal.message}
          type={modal.type}
          okText={modal.okText}
          isContainsResendBtn={modal.isContainsResendBtn}
          onOk={modal.onOk}
        />
      )}
    </div>
  );
}

export default UpgradePlan;
