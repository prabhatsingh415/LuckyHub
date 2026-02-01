import { useChangePasswordMutation } from "../Redux/slices/apiSlice";
import { useState } from "react";

export const usePasswordLogic = (setModal) => {
  const [currentPassword, setCurrentPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [changePassword] = useChangePasswordMutation();

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

  return {
    currentPassword,
    setCurrentPassword,
    newPassword,
    setNewPassword,
    confirmPassword,
    setConfirmPassword,
    handleChangePassword,
  };
};
