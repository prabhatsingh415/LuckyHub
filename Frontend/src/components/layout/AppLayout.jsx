import { Outlet } from "react-router-dom";
import AppHeader from "./AppHeader";

function AppLayout() {
  return (
    <div className="flex flex-col lg:flex-row h-screen w-screen overflow-hidden bg-[#fafafa] dark:bg-[var(--black)] dark:text-white">
      <AppHeader />

      <main className="flex-1 h-full overflow-y-auto p-4 md:p-8">
        <div className="w-full max-w-7xl mx-auto">
          <Outlet />
        </div>
      </main>
    </div>
  );
}

export default AppLayout;
