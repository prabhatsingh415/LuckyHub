import { Outlet } from "react-router-dom";
import AppHeader from "./AppHeader";

function AppLayout() {
  return (
    <div className="bg-[#fafafa] flex flex-col justify-center items-center p-8 gap-8 dark:text-white dark:bg-[var(--black)]">
      <AppHeader />
      <main className="p-6">
        <Outlet />
      </main>
    </div>
  );
}

export default AppLayout;
