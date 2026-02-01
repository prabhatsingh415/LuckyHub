import { Upload, Pencil, User } from "lucide-react";
import { useState, useRef } from "react";
import { useProfileLogic } from "../hook/useProfileLogic";
import { useEffect } from "react";

function ProfileSection({ dashboardData, refetchDashboard, setModal }) {
  const fileInputRef = useRef();
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [isEditing, setIsEditing] = useState(false);
  const [avatarUploading, setAvatarUploading] = useState(false);
  const [editSnapshot, setEditSnapshot] = useState({
    firstName: "",
    lastName: "",
  });
  const {
    handleFileChange: handleFileChangeLogic,
    handleUpdateName,
    isChangingName,
  } = useProfileLogic(
    setModal,
    setAvatarUploading,
    refetchDashboard,
    firstName,
    lastName,
    setIsEditing
  );

  useEffect(() => {
    if (dashboardData?.user) {
      setFirstName(dashboardData.user.firstName);
      setLastName(dashboardData.user.lastName);
    }
  }, [dashboardData]);

  const handleAvatarClick = () => {
    fileInputRef.current.click();
  };

  return (
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
          onChange={handleFileChangeLogic}
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
          <p className="mt-1 text-xs text-gray-500">Email cannot be changed.</p>
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
  );
}

export default ProfileSection;
