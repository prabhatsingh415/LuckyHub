import {
  useChangeAvatarMutation,
  useChangeNameMutation,
} from "../Redux/slices/apiSlice";

export const useProfileLogic = (
  setModal,
  setAvatarUploading,
  refetchDashboard,
  firstName,
  lastName,
  setIsEditing
) => {
  const [changeAvatar] = useChangeAvatarMutation();
  const [changeName, { isLoading: isChangingName }] = useChangeNameMutation();

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
  return { handleFileChange, handleUpdateName, isChangingName };
};
