import { useState } from "react";
import { User, LogOut, Trash2 } from "lucide-react";
import { useAccountActions } from "../hook/useAccountActions";
import CautionModal from "./CautionModal";
import OTPVerificationModal from "./OtpVerificationModal";

function AccountActionsSection({ setModal }) {
  const {
    handleLogout,
    triggerDelete,
    handleCautionConfirm,
    handleVerifyOTP,
    closeModals,
    showCaution,
    showOtpModal,
    isVerifying,
  } = useAccountActions(setModal);

  const btnDetails = [
    {
      title: "Logout",
      description: "Sign out from your account on this device",
      icon: <LogOut size={16} />,
      onClick: triggerDelete,
    },
    {
      title: "Delete Account",
      description: "Permanently delete your account and all associated data",
      icon: <Trash2 size={16} />,
      onClick: triggerDelete,
    },
  ];
  const handleCloseCaution = () => {
    setCautionModal((prev) => ({ ...prev, isOpen: false }));
  };

  const [cautionModal, setCautionModal] = useState({
    isOpen: false,
    onClose: {},
    onConfirm: {},
    data: {},
  });

  const handleBtnClick = (title) => {
    if (title === "Logout") {
      setCautionModal({
        isOpen: true,
        onClose: closeModals,
        onConfirm: handleLogout,
        data: {
          title: "Confirm Logout",
          message: "Are you sure you want to logout from your account?",
          confirmText: "Logout",
          isDangerous: false,
        },
      });
    } else if (title === "Delete Account") {
      setCautionModal({
        isOpen: true,
        onClose: closeModals,
        onConfirm: handleCautionConfirm,
        data: {
          title: "Are you absolutely sure?",
          message:
            "This action cannot be undone. All your data will be removed.",
          confirmText: "Yes, Delete My Account",
          isDangerous: true,
          actionType: "deleted",
          warnings: ["Giveaway history", "Profile data", "Subscriptions"],
        },
      });
    }
  };

  return (
    <>
      <section className="rounded-2xl border border-zinc-200 dark:border-zinc-800 p-6">
        <div className="flex items-center gap-2 mb-4">
          <User size={18} />
          <h2 className="text-lg font-medium">Account Actions</h2>
        </div>
        <p className="text-sm text-gray-400 mb-6">
          Irreversible actions for your account
        </p>

        <div className="flex flex-col gap-6">
          {btnDetails.map((btn, index) => (
            <div
              key={index}
              className="flex items-center justify-between rounded-xl dark:bg-[#060606] bg-[#f2f2f5] dark:text-gray-400 border border-zinc-200 dark:border-zinc-800 p-4 md:p-6 gap-4"
            >
              <div className="flex-1">
                <h2 className="text-white text-sm md:text-base font-medium">
                  {btn.title}
                </h2>
                <p className="text-[11px] md:text-sm text-gray-500 leading-tight">
                  {btn.description}
                </p>
              </div>

              <button
                onClick={() => handleBtnClick(btn.title)}
                className={`h-10 md:h-12 w-fit rounded-lg px-4 py-2 text-white flex-shrink-0 ${
                  btn.title === "Delete Account"
                    ? "bg-red-900 hover:bg-red-950 border-red-800"
                    : "bg-zinc-950 hover:bg-zinc-900 border-zinc-800"
                } border`}
              >
                <div className="flex items-center justify-center gap-2">
                  {btn.icon}
                  <span className="text-xs md:text-sm whitespace-nowrap">
                    {btn.title}
                  </span>
                </div>
              </button>
            </div>
          ))}
        </div>
      </section>

      <CautionModal
        isOpen={cautionModal.isOpen}
        onClose={handleCloseCaution}
        onConfirm={() => {
          handleCloseCaution();
          cautionModal.onConfirm();
        }}
        data={cautionModal.data}
      />
      <OTPVerificationModal
        isOpen={showOtpModal}
        onClose={closeModals}
        onVerify={handleVerifyOTP}
        isVerifying={isVerifying}
      />
    </>
  );
}

export default AccountActionsSection;
