import Sidebar from "./components/Sidebar";
import { Outlet } from "react-router-dom";

export default function Layout() {
  return (
    <div className="flex min-h-screen">
      <Sidebar />
      <div className="flex-1">
        <main className="bg-background h-full w-full p-5">
          <Outlet />
        </main>
      </div>
    </div>
  );
}