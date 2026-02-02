import { Outlet } from "react-router-dom";
import { AppHeader } from "./";

function AppLayout() {
  return (
    <div className="flex flex-col lg:flex-row h-screen w-screen overflow-hidden bg-[#fafafa] dark:bg-[var(--black)] dark:text-white">
      <AppHeader />

      <main className="flex-1 h-full overflow-y-auto">
        <div className="w-full">
          <Outlet />
        </div>
      </main>
    </div>
  );
}

export default AppLayout;
