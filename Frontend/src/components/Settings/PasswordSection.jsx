import { Lock } from "lucide-react";
import { usePasswordLogic } from "./hooks/usePasswordLogic";
import { Loader } from "../Common";

function PasswordSection({ setModal }) {
  const {
    currentPassword,
    setCurrentPassword,
    newPassword,
    setNewPassword,
    confirmPassword,
    setConfirmPassword,
    handleChangePassword,
  } = usePasswordLogic(setModal);

  return (
    <section className="rounded-2xl border border-zinc-200 dark:border-zinc-800 p-6">
      <div className="flex items-center gap-2 mb-4">
        <Lock size={18} />
        <h2 className="text-lg font-medium">Change Password</h2>
      </div>

      <p className="text-sm text-gray-400 mb-6">Update your account password</p>

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
                /[A-Z]/.test(newPassword) ? "text-green-500" : "text-red-500"
              }
            >
              • One uppercase letter
            </li>
            <li
              className={
                /[a-z]/.test(newPassword) ? "text-green-500" : "text-red-500"
              }
            >
              • One lowercase letter
            </li>
            <li
              className={
                /[0-9]/.test(newPassword) ? "text-green-500" : "text-red-500"
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
  );
}

export default PasswordSection;
