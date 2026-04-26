import Sidebar from "./components/Sidebar";
import { Outlet } from "react-router-dom";
import { useOnlineStatus } from "./services/UserService";

export default function Layout() {
  useOnlineStatus(); 
  return (
    <div className="flex min-h-screen">
      <Sidebar />
      <div className="flex-1">
        <main className="bg-background h-full w-full min-w-[900px]">
          <Outlet />
        </main>
      </div>
    </div>
  );
}