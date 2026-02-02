import {
  useCreateOrderMutation,
  useVerifyPaymentMutation,
} from "../../../Redux/slices/apiSlice";

export const usePayment = (userEmail, setModal) => {
  const [createOrder] = useCreateOrderMutation();
  const [verifyPayment] = useVerifyPaymentMutation();

  const handlePayment = async (planName) => {
    if (planName.toUpperCase() === "FREE") return;

    try {
      const orderData = await createOrder(planName.toUpperCase()).unwrap();

      const options = {
        key: import.meta.env.VITE_RAZORPAY_KEY_ID,
        amount: orderData.amount * 100,
        currency: "INR",
        name: "Lucky Hub",
        description: `Upgrade to ${planName} Plan`,
        order_id: orderData.orderId,
        handler: async function (response) {
          try {
            const apiResponse = await verifyPayment({
              razorpay_order_id: response.razorpay_order_id,
              razorpay_payment_id: response.razorpay_payment_id,
              razorpay_signature: response.razorpay_signature,
            }).unwrap();

            setModal({
              open: true,
              title: "Payment Successful",
              message:
                apiResponse.message ||
                `Upgraded to ${planName} plan successfully.`,
              type: "success",
              onOk: () => (window.location.href = "/settings"),
            });
          } catch (err) {
            setModal({
              open: true,
              title: "Verification Failed",
              message:
                err.data?.message || "Verification failed. Contact support.",
              type: "error",
            });
          }
        },
        prefill: { email: userEmail },
        theme: { color: "#ff4d29" },
      };

      const rzp = new window.Razorpay(options);
      rzp.open();
    } catch (err) {
      setModal({
        open: true,
        title: "Payment Failed",
        message: "Could not initialize payment.",
        type: "error",
      });
    }
  };

  return { handlePayment };
};
