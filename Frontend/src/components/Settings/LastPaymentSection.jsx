import { CreditCard } from "lucide-react";
import { useGetLastPaymentQuery } from "../../Redux/slices/apiSlice";

function LastPaymentSection() {
  const { data: lastPaymentData } = useGetLastPaymentQuery();
  return (
    <section className="rounded-2xl border border-zinc-200 dark:border-zinc-800 p-6">
      <div className="flex items-center gap-2 mb-4">
        <CreditCard size={18} />
        <h2 className="text-lg font-medium">Payment Details</h2>
      </div>
      <p className="text-sm text-gray-400 mb-6">
        Your recent payment information
      </p>
      <div className="rounded-xl dark:bg-[#060606] bg-[#f2f2f5] dark:text-gray-400 border border-zinc-200 dark:border-zinc-800 p-6">
        {lastPaymentData ? (
          <div className="grid grid-cols-2 gap-6 text-sm ">
            <p className="font-medium mb-4">Last Payment</p>
            <div>
              <p className="text-gray-400">Payment ID</p>
              <p>{lastPaymentData.paymentId || "N/A"}</p>
            </div>
            <div>
              <p className="text-gray-400">Amount</p>
              <p>
                {lastPaymentData.amount ? `₹${lastPaymentData.amount}` : "N/A"}
              </p>
            </div>
            <div>
              <p className="text-gray-400">Subscription Type</p>
              <p>{lastPaymentData.subscriptionType || "N/A"}</p>
            </div>
            <div>
              <p className="text-gray-400">Period</p>
              <p>
                {lastPaymentData.periodStart && lastPaymentData.periodEnd
                  ? `${lastPaymentData.periodStart} - ${lastPaymentData.periodEnd}`
                  : "N/A"}
              </p>
            </div>
          </div>
        ) : (
          <div className="flex flex-col justify-center items-center">
            <div className="flex justify-center items-center h-12 w-12 md:h-24 md:w-24 bg-gray-600 rounded-full">
              <CreditCard size={24} className="text-gray-400 md:h-16 md:w-16" />
            </div>
            <div className="text-center mt-4">
              <p className="text-sm text-white">
                No payment records found. Your recent payments will appear here.
              </p>
              <p className="text-xs mt-4 text-gray-500">
                You are currently on the FREE plan
              </p>
            </div>
          </div>
        )}

        {lastPaymentData?.nextBillingDate && (
          <p className="mt-4 text-sm text-gray-400">
            Next billing date: {lastPaymentData.nextBillingDate}
          </p>
        )}
      </div>
    </section>
  );
}

export default LastPaymentSection;
