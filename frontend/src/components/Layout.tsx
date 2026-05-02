import { useState } from 'react';
import { Outlet } from 'react-router-dom';
import Sidebar from './Sidebar';
import { Menu, Heart } from 'lucide-react';

export default function Layout() {
  const [open, setOpen] = useState(false);

  return (
    <div className="flex h-screen overflow-hidden bg-gray-50">

      {/* ── Desktop sidebar (always visible ≥ md) ── */}
      <div className="hidden md:flex md:shrink-0">
        <Sidebar />
      </div>

      {/* ── Mobile drawer overlay ── */}
      {open && (
        <div className="fixed inset-0 z-40 md:hidden">
          {/* Backdrop */}
          <div
            className="absolute inset-0 bg-black/50 backdrop-blur-sm"
            onClick={() => setOpen(false)}
          />
          {/* Drawer */}
          <div className="absolute left-0 top-0 h-full z-50">
            <Sidebar onClose={() => setOpen(false)} />
          </div>
        </div>
      )}

      {/* ── Content area ── */}
      <div className="flex flex-1 flex-col overflow-hidden min-w-0">

        {/* Mobile top bar */}
        <header className="md:hidden flex items-center justify-between px-4 py-3 bg-primary-900 text-white shrink-0 shadow-md">
          <button
            onClick={() => setOpen(true)}
            className="p-1.5 rounded-lg hover:bg-primary-800 transition-colors"
            aria-label="Open menu"
          >
            <Menu size={22} />
          </button>

          <div className="flex items-center gap-2">
            <div className="flex h-7 w-7 items-center justify-center rounded-lg bg-primary-500">
              <Heart size={14} />
            </div>
            <span className="text-sm font-bold tracking-tight">MediCare+</span>
          </div>

          {/* Spacer to keep title centred */}
          <div className="w-9" />
        </header>

        <main className="flex-1 overflow-y-auto">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
