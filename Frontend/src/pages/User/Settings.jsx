import { useState, useEffect } from "react";
import { useDashboardAPIQuery } from "../../Redux/slices/apiSlice";
import { InfoModal, Loader } from "../../components/Common";
import {
  ProfileSection,
  PasswordSection,
  SubscriptionSection,
  LastPaymentSection,
  AccountActionsSection,
} from "../../components/Settings";
import { useLocation } from "react-router-dom";

export default function Settings() {
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

  const location = useLocation();

  useEffect(() => {
    if (location.hash === "#sub") {
      const timer = setTimeout(() => {
        const element = document.getElementById("sub");
        if (element) {
          element.scrollIntoView({ behavior: "smooth", block: "start" });
        }
      }, 100);

      return () => clearTimeout(timer);
    }
  }, [location, dashboardLoading]);

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
        <div id="sub">
          <SubscriptionSection dashboardData={dashboardData} />
        </div>

        {/* Payment Details */}
        <LastPaymentSection />

        {/* Account Actions Section */}
        <AccountActionsSection setModal={setModal} />
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
