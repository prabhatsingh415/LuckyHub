import React, { useState, useRef, useEffect } from "react";
import {
  User,
  Crown,
  CreditCard,
  CircleArrowUp,
  LogOut,
  Trash2,
} from "lucide-react";
import {
  useDashboardAPIQuery,
  useChangePasswordMutation,
  useGetLastPaymentQuery,
} from "../../Redux/slices/apiSlice";
import InfoModal from "../../pages/InfoModal";
import Loader from "../../pages/Loader";
import { useNavigate } from "react-router-dom";
import ProfileSection from "../ProfileSection";
import PasswordSection from "../PasswordSection";

export default function Settings() {
  const navigate = useNavigate();
  const [modal, setModal] = useState({
    open: false,
    title: "",
    message: "",
    type: "info",
  });

  const {
    data: dashboardData,
    error: dashboardError,
    isLoading: dashboardLoading,
    refetch: refetchDashboard,
  } = useDashboardAPIQuery();

  const { data: lastPaymentData } = useGetLastPaymentQuery();

  // Loading/Error UI
  if (dashboardLoading) return <Loader />;

  if (dashboardError) {
    return (
      <InfoModal
        isOpen={true}
        title="Error"
        message="Something went wrong, please try again later!"
        type="error"
        okText="OK"
        isContainsResendBtn={false}
        onOk={() => setModal({ ...modal, open: false })}
      />
    );
  }

  const handlePlanUpgrade = () => {
    navigate("/upgrade-plan");
  };

  return (
    <div className="w-full dark:bg-[#0a0a0a] flex flex-col justify-center p-4 gap-8 dark:text-white">
      {/* Header */}
      <div className="mb-8">
        <h1 className="text-3xl font-semibold">Settings</h1>
        <p className="text-gray-400">
          Manage your account settings and preferences
        </p>
      </div>

      <div className="space-y-8 max-w-5xl">
        {/* Profile Information */}
        <ProfileSection
          dashboardData={dashboardData}
          refetchDashboard={refetchDashboard}
          setModal={setModal}
        />
        {/* Change Password */}
        <PasswordSection setModal={setModal} />

        {/* Subscription */}
        <section className="rounded-2xl border border-zinc-200 dark:border-zinc-800 p-6">
          <div className="flex items-center gap-2 mb-4">
            <Crown size={18} />
            <h2 className="text-lg font-medium">Subscription</h2>{" "}
          </div>
          <p className="text-sm text-gray-400 mb-6">
            Your current plan and limits
          </p>
          <div className="rounded-xl dark:bg-[#060606] bg-[#f2f2f5] dark:text-gray-400 border border-zinc-200 dark:border-zinc-800  p-4 flex justify-between items-center gap-4 mb-6">
            <div className="flex justify-center items-center gap-6">
              <div className="h-10 w-10 flex items-center justify-center rounded-full bg-orange-500">
                <Crown size={20} color="yellow" />
              </div>
              <div>
                <p className="font-medium">Current Plan</p>
                <span className="mt-1 inline-block rounded-full bg-red-500/20 px-3 py-1 text-xs text-red-400">
                  {dashboardData?.user?.subscriptionType || "FREE"}
                </span>
              </div>
            </div>
            {dashboardData?.user?.subscriptionType !== "DIAMOND" &&
              (dashboardData?.user?.subscriptionType === "FREE" ||
                (dashboardData?.user?.subscriptionType === "GOLD" &&
                  dashboardData?.user?.remainingGiveaways <= 0)) && (
                <button
                  onClick={handlePlanUpgrade}
                  className="flex gap-2 bg-[#FF3E30] px-4 p-2 text-white font-bold rounded-xl hover:scale-105 hover:bg-orange-700"
                >
                  <CircleArrowUp />
                  <p>Upgrade Plan</p>
                </button>
              )}
          </div>
          <div className="space-y-4 ">
            <Limit
              label="Max Comments Per Giveaway"
              value={dashboardData?.user?.maxComments}
            />
            <Limit
              label="Max Winners Per Giveaway"
              value={dashboardData?.user?.maxWinners}
            />
            <Limit
              label="Remaining Giveaways"
              value={
                dashboardData?.user?.subscriptionType === "DIAMOND"
                  ? "Unlimited"
                  : dashboardData?.user?.remainingGiveaways
              }
            />
          </div>
        </section>

        {/* Payment Details */}
        <section className="rounded-2xl border border-zinc-200 dark:border-zinc-800 p-6">
          <div className="flex items-center gap-2 mb-4">
            <CreditCard size={18} />
            <h2 className="text-lg font-medium">Payment Details</h2>
          </div>
          <p className="text-sm text-gray-400 mb-6">
            Your recent payment information
          </p>
          <div className="rounded-xl dark:bg-[#060606] bg-[#f2f2f5] dark:text-gray-400 border border-zinc-200 dark:border-zinc-800 p-6">
            <p className="font-medium mb-4">Last Payment</p>

            {lastPaymentData ? (
              <div className="grid grid-cols-2 gap-6 text-sm ">
                <div>
                  <p className="text-gray-400">Payment ID</p>
                  <p>{lastPaymentData.paymentId || "N/A"}</p>
                </div>
                <div>
                  <p className="text-gray-400">Amount</p>
                  <p>
                    {lastPaymentData.amount
                      ? `₹${lastPaymentData.amount}`
                      : "N/A"}
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
              <p className="text-sm text-gray-500">
                No payment data available.
              </p>
            )}

            {lastPaymentData?.nextBillingDate && (
              <p className="mt-4 text-sm text-gray-400">
                Next billing date: {lastPaymentData.nextBillingDate}
              </p>
            )}
          </div>
        </section>

        {/* Delete Account and logout*/}
        <section className="rounded-2xl border border-zinc-200 dark:border-zinc-800 p-6">
          <div className="flex items-center gap-2 mb-4">
            <User size={18} />
            <h2 className="text-lg font-medium">Account Actions</h2>
          </div>
          <p className="text-sm text-gray-400 mb-6">
            Irreversible actions for your account
          </p>

          <div className="flex flex-col gap-6">
            <div className="flex justify-between rounded-xl dark:bg-[#060606] bg-[#f2f2f5] dark:text-gray-400 border border-zinc-200 dark:border-zinc-800 p-6">
              <div className="">
                <h2 className="text-white">Logout</h2>
                <p className="text-sm text-gray-500">
                  Sign out from your account on this device
                </p>
              </div>

              <button
                onClick={console.log("eeee Ellvish bhai !!")}
                className="w-fit rounded-lg bg-zinc-950 border border-zinc-800  px-4 py-2 text-white hover:bg-zinc-900 mt-4"
              >
                <div className="flex items-center justify-center gap-2">
                  <LogOut size={16} />
                  <span>Logout</span>
                </div>
              </button>
            </div>

            <div className="flex justify-between rounded-xl dark:bg-[#060606] bg-[#f2f2f5] dark:text-gray-400 border border-zinc-200 dark:border-zinc-800 p-6">
              <div className="">
                <h2 className="text-white">Delete Account</h2>
                <p className="text-sm text-gray-500">
                  Permanently delete your account and all associated data
                </p>
              </div>

              <button
                onClick={console.log("eeee Ellvish bhai !!")}
                className="w-fit rounded-lg bg-red-900 px-4 py-2 text-white hover:bg-red-950"
              >
                <div className="flex items-center justify-center gap-2">
                  <Trash2 size={16} />
                  <span>Delete Account</span>
                </div>
              </button>
            </div>
          </div>
        </section>
      </div>

      {modal.open && (
        <InfoModal
          isOpen={modal.open}
          title={modal.title}
          message={modal.message}
          type={modal.type}
          okText="OK"
          isContainsResendBtn={false}
          onOk={() => setModal({ ...modal, open: false })}
        />
      )}
    </div>
  );
}

function Limit({ label, value }) {
  return (
    <div className="flex items-center justify-between rounded-xl dark:bg-[#060606] bg-[#f2f2f5] dark:tex t-gray-400 border border-zinc-200 dark:border-zinc-800  p-4">
      <p>{label}</p>
      <span className="rounded-full dark:bg-[#060606] bg-[#f2f2f5] dark:text-gray-400 px-3 py-1 text-sm">
        {value}
      </span>
    </div>
  );
}
