import { useState } from "react";
import { User, LogOut, Trash2 } from "lucide-react";
import { useDashboardAPIQuery } from "../../Redux/slices/apiSlice";
import InfoModal from "../../pages/InfoModal";
import Loader from "../../pages/Loader";
import { useNavigate } from "react-router-dom";
import ProfileSection from "../ProfileSection";
import PasswordSection from "../PasswordSection";
import SubscriptionSection from "../SubscriptionSection";
import LastPaymentSection from "../LastPaymentSection";

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
        <SubscriptionSection dashboardData={dashboardData} />

        {/* Payment Details */}
        <LastPaymentSection />

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
                onClick={console.log("clicked")}
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
                onClick={console.log("clicked")}
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
