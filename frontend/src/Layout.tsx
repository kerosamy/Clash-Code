import Sidebar from "./components/Sidebar";
import { Outlet } from "react-router-dom";

export default function Layout() {
  return (
    <div className="flex">
      <Sidebar />
      <div className="flex-1 bg-gray-100 min-h-screen">
        <main className="p-6">
          <Outlet />
        </main>
      </div>
    </div>
  );
}