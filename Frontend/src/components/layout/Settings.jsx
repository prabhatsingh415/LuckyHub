import React, { useState, useRef, useEffect } from "react";
import {
  User,
  Lock,
  Crown,
  CreditCard,
  Upload,
  Pencil,
  CircleArrowUp,
} from "lucide-react";
import {
  useChangeNameMutation,
  useDashboardAPIQuery,
  useChangeAvatarMutation,
  useChangePasswordMutation,
  useGetLastPaymentQuery,
} from "../../Redux/slices/apiSlice";
import InfoModal from "../../pages/InfoModal";
import Loader from "../../pages/Loader";
import { useNavigate } from "react-router-dom";

export default function Settings() {
  const navigate = useNavigate();
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [isEditing, setIsEditing] = useState(false);
  const [editSnapshot, setEditSnapshot] = useState({
    firstName: "",
    lastName: "",
  });
  const [currentPassword, setCurrentPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [modal, setModal] = useState({
    open: false,
    title: "",
    message: "",
    type: "info",
  });

  const [avatarUploading, setAvatarUploading] = useState(false);

  const fileInputRef = useRef();

  const {
    data: dashboardData,
    error: dashboardError,
    isLoading: dashboardLoading,
    refetch: refetchDashboard,
  } = useDashboardAPIQuery();

  const [changeName, { isLoading: isChangingName }] = useChangeNameMutation();
  const [changeAvatar] = useChangeAvatarMutation();
  const [
    changePassword,
    { isLoading: isChangingPassword, error: changePasswordError },
  ] = useChangePasswordMutation();
  const {
    data: lastPaymentData,
    isLoading: isLoadingLastPayment,
    error: lastPaymentError,
  } = useGetLastPaymentQuery();

  // Load user data once dashboardData is available
  useEffect(() => {
    if (dashboardData?.user) {
      setFirstName(dashboardData.user.firstName);
      setLastName(dashboardData.user.lastName);
    }
  }, [dashboardData]);

  // Handle name update
  const handleUpdateName = async () => {
    try {
      await changeName({ firstName, lastName }).unwrap();
      setIsEditing(false);
      setModal({
        open: true,
        type: "success",
        title: "Success",
        message: "Username updated successfully!",
      });
      refetchDashboard(); // Refresh dashboard data
    } catch (err) {
      setModal({
        open: true,
        type: "error",
        title: "Error",
        message:
          err?.data?.message || "Something went wrong, please try again!",
      });
    }
  };

  // Avatar upload
  const handleAvatarClick = () => {
    fileInputRef.current.click();
  };

  const handleFileChange = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    const formData = new FormData();
    formData.append("file", file);

    setAvatarUploading(true);
    setModal({
      open: true,
      type: "info",
      title: "Uploading",
      message: "Updating your profile picture...",
    });

    try {
      await changeAvatar(formData).unwrap();

      setModal((prev) => ({ ...prev, open: false }));

      await refetchDashboard();
      setModal({
        open: true,
        type: "success",
        title: "Success",
        message: "Profile picture updated successfully!",
      });
    } catch (err) {
      setModal((prev) => ({ ...prev, open: false }));
      setTimeout(() => {
        setModal({
          open: true,
          type: "error",
          title: "Upload Failed",
          message: err?.data?.message || "Failed to upload avatar. Try again.",
        });
      }, 0);
    } finally {
      setAvatarUploading(false);
      e.target.value = ""; // Reset file input
    }
  };

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

  const handleChangePassword = async () => {
    setModal({
      open: true,
      type: "info",
      title: "Updating Password",
      message: "Please wait while we update your password...",
    });

    try {
      await changePassword({
        currentPassword,
        newPassword,
        confirmNewPassword: confirmPassword,
      }).unwrap();

      setModal((prev) => ({ ...prev, open: false }));

      setTimeout(() => {
        setModal({
          open: true,
          type: "success",
          title: "Success",
          message: "Password changed successfully!",
        });

        setCurrentPassword("");
        setNewPassword("");
        setConfirmPassword("");
      }, 100);
    } catch (err) {
      setModal((prev) => ({ ...prev, open: false }));

      setTimeout(() => {
        setModal({
          open: true,
          type: "error",
          title: "Error",
          message:
            err?.data?.message || "Something went wrong, please try again!",
        });
      }, 100);
    }
  };

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
        <section className="rounded-2xl border border-zinc-200 dark:border-zinc-800 p-6">
          <div className="flex items-center gap-2 mb-4">
            <User size={18} />
            <h2 className="text-lg font-medium">Profile Information</h2>
          </div>

          <p className="text-sm text-gray-400 mb-6">
            Update your personal information and profile picture
          </p>

          {/* Avatar */}
          <div className="flex items-center gap-6 mb-6">
            <img
              src={dashboardData?.user?.avatarUrl}
              alt="avatar"
              className="h-20 w-20 rounded-full object-cover"
            />
            <button
              onClick={handleAvatarClick}
              className="flex items-center gap-2 rounded-lg border-zinc-200 dark:border-zinc-800 px-4 py-2 text-sm hover:bg-white/10"
            >
              <Upload size={16} />{" "}
              {avatarUploading ? "Uploading..." : "Change Picture"}
            </button>
            <input
              type="file"
              accept="image/png, image/jpeg, image/webp"
              ref={fileInputRef}
              onChange={handleFileChange}
              className="hidden"
            />
          </div>

          {/* NAME FIELDS */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label className="text-sm dark:text-gray-400 flex items-center justify-between">
                First Name
                <button
                  type="button"
                  onClick={() => {
                    setEditSnapshot({ firstName, lastName });
                    setIsEditing(true);
                  }}
                  className="dark:text-gray-400 hover:text-orange-600"
                >
                  <Pencil size={14} />
                </button>
              </label>

              <input
                type="text"
                value={firstName}
                disabled={!isEditing}
                onChange={(e) => setFirstName(e.target.value)}
                className={`mt-2 w-full rounded-lg border px-4 py-2 focus:outline-none ${
                  isEditing
                    ? "border-zinc-900 dark:border-amber-50 dark:bg-[#050505]"
                    : "dark:bg-[#111111] bg-[#f2f2f5] dark:text-gray-400 border-zinc-200 dark:border-zinc-800"
                }`}
              />
            </div>

            <div>
              <label className="text-sm dark:text-gray-400">Last Name</label>
              <input
                type="text"
                value={lastName}
                disabled={!isEditing}
                onChange={(e) => setLastName(e.target.value)}
                className={`mt-2 w-full rounded-lg border px-4 py-2 focus:outline-none ${
                  isEditing
                    ? "border-zinc-900 dark:border-amber-50 dark:bg-[#050505]"
                    : "dark:bg-[#111111] bg-[#f2f2f5] dark:text-gray-400 border-zinc-200 dark:border-zinc-800"
                }`}
              />
            </div>

            <div className="md:col-span-2">
              <label className="text-sm dark:text-gray-400">Email</label>
              <input
                type="email"
                value={dashboardData?.user?.email || ""}
                disabled
                className="mt-2 w-full rounded-lg dark:bg-[#111111] bg-[#f2f2f5] border border-zinc-200 dark:border-zinc-800 px-4 py-2 text-gray-500"
              />
              <p className="mt-1 text-xs text-gray-500">
                Email cannot be changed.
              </p>
            </div>
          </div>

          {/* ACTIONS */}
          <div className="flex justify-end gap-3 mt-6">
            {isEditing && (
              <button
                type="button"
                onClick={() => {
                  setFirstName(editSnapshot.firstName);
                  setLastName(editSnapshot.lastName);
                  setIsEditing(false);
                }}
                className="rounded-lg border border-zinc-200 dark:border-zinc-800 px-6 py-2 hover:bg-zinc-200 dark:hover:bg-white/10"
              >
                Cancel
              </button>
            )}

            <button
              type="button"
              disabled={
                !isEditing ||
                (firstName === editSnapshot.firstName &&
                  lastName === editSnapshot.lastName)
              }
              onClick={handleUpdateName}
              className={`rounded-lg px-6 py-2 font-medium ${
                !isEditing ||
                (firstName === editSnapshot.firstName &&
                  lastName === editSnapshot.lastName)
                  ? "bg-zinc-200 dark:bg-zinc-700 cursor-not-allowed"
                  : "bg-orange-500 hover:bg-orange-600"
              }`}
            >
              {isChangingName ? "Saving..." : "Save Changes"}
            </button>
          </div>
        </section>
        {/* Change Password */}
        <section className="rounded-2xl border border-zinc-200 dark:border-zinc-800 p-6">
          <div className="flex items-center gap-2 mb-4">
            <Lock size={18} />
            <h2 className="text-lg font-medium">Change Password</h2>
          </div>

          <p className="text-sm text-gray-400 mb-6">
            Update your account password
          </p>

          <div className="space-y-4 max-w-xl">
            <input
              type="password"
              placeholder="Current password"
              value={currentPassword}
              onChange={(e) => setCurrentPassword(e.target.value)}
              className="w-full rounded-lg dark:bg-[#060606] bg-[#f2f2f5] dark:text-gray-400 border border-zinc-200 dark:border-zinc-800 px-4 py-2"
            />

            <input
              type="password"
              placeholder="New password"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              className="w-full rounded-lg dark:bg-[#060606] bg-[#f2f2f5] dark:text-gray-400 border border-zinc-200 dark:border-zinc-800 px-4 py-2"
            />

            {/* Password rules */}
            {newPassword && (
              <ul className="text-xs text-gray-400 space-y-1">
                <li
                  className={
                    newPassword.length >= 8 ? "text-green-500" : "text-red-500"
                  }
                >
                  • At least 8 characters
                </li>
                <li
                  className={
                    /[A-Z]/.test(newPassword)
                      ? "text-green-500"
                      : "text-red-500"
                  }
                >
                  • One uppercase letter
                </li>
                <li
                  className={
                    /[a-z]/.test(newPassword)
                      ? "text-green-500"
                      : "text-red-500"
                  }
                >
                  • One lowercase letter
                </li>
                <li
                  className={
                    /[0-9]/.test(newPassword)
                      ? "text-green-500"
                      : "text-red-500"
                  }
                >
                  • One number
                </li>
                <li
                  className={
                    /[^A-Za-z0-9]/.test(newPassword)
                      ? "text-green-500"
                      : "text-red-500"
                  }
                >
                  • One special character
                </li>
              </ul>
            )}

            <input
              type="password"
              placeholder="Confirm new password"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              className="w-full rounded-lg dark:bg-[#060606] bg-[#f2f2f5] dark:text-gray-400 border border-zinc-200 dark:border-zinc-800 px-4 py-2"
            />

            {confirmPassword && newPassword !== confirmPassword && (
              <p className="text-sm text-red-500">
                New password and confirm password do not match
              </p>
            )}
          </div>

          <div className="flex justify-end mt-6">
            <button
              onClick={handleChangePassword}
              disabled={
                !currentPassword ||
                !newPassword ||
                !confirmPassword ||
                newPassword !== confirmPassword ||
                newPassword.length < 8 ||
                !/[A-Z]/.test(newPassword) ||
                !/[a-z]/.test(newPassword) ||
                !/[0-9]/.test(newPassword) ||
                !/[^A-Za-z0-9]/.test(newPassword)
              }
              className={`rounded-lg px-6 py-2 font-medium transition ${
                !currentPassword ||
                !newPassword ||
                !confirmPassword ||
                newPassword !== confirmPassword ||
                newPassword.length < 8 ||
                !/[A-Z]/.test(newPassword) ||
                !/[a-z]/.test(newPassword) ||
                !/[0-9]/.test(newPassword) ||
                !/[^A-Za-z0-9]/.test(newPassword)
                  ? "bg-zinc-200 dark:bg-zinc-700 cursor-not-allowed"
                  : "bg-orange-500 hover:bg-orange-600"
              }`}
            >
              Update Password
            </button>
          </div>
        </section>
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
                  {console.log(dashboardData?.user?.subscriptionType)}
                </span>
              </div>
            </div>
            {dashboardData?.user?.subscriptionType !== "DIAMOND" &&
              dashboardData?.user?.subscriptionType === "GOLD" &&
              dashboardData?.user?.remainingGiveaways <= 0 && (
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
          <div className="rounded-xl dark:bg-[#060606] bg-[#f2f2f5] dark:text-gray-400 border border-zinc-200 dark:border-zinc-800  p-6">
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
              <p className="text-sm text-gray-400">
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
