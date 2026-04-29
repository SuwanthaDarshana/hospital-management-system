import { NavLink, useNavigate } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';
import { logout as logoutApi } from '../api/auth';
import {
  LayoutDashboard, Users, UserRound, CalendarDays, Stethoscope,
  LogOut, Hospital, UserCog,
} from 'lucide-react';
import clsx from 'clsx';

interface NavItem { to: string; label: string; icon: React.ReactNode; roles: string[] }

const navItems: NavItem[] = [
  { to: '/dashboard',    label: 'Dashboard',    icon: <LayoutDashboard size={18} />, roles: ['ADMIN','DOCTOR','PATIENT','STAFF'] },
  { to: '/doctors',      label: 'Doctors',      icon: <Stethoscope size={18} />,     roles: ['ADMIN','STAFF','PATIENT'] },
  { to: '/patients',     label: 'Patients',     icon: <UserRound size={18} />,       roles: ['ADMIN','STAFF','DOCTOR'] },
  { to: '/appointments', label: 'Appointments', icon: <CalendarDays size={18} />,    roles: ['ADMIN','STAFF','DOCTOR','PATIENT'] },
  { to: '/staff',        label: 'Staff',        icon: <Users size={18} />,           roles: ['ADMIN'] },
  { to: '/profile',      label: 'My Profile',   icon: <UserCog size={18} />,         roles: ['ADMIN','DOCTOR','PATIENT','STAFF'] },
];

export default function Sidebar() {
  const { user, tokens, logout } = useAuthStore();
  const navigate = useNavigate();

  const handleLogout = async () => {
    if (tokens?.refreshToken) {
      try { await logoutApi(tokens.refreshToken); } catch { /* ignore */ }
    }
    logout();
    navigate('/login');
  };

  const filtered = navItems.filter(item => user?.role && item.roles.includes(user.role));

  return (
    <aside className="flex h-full w-64 flex-col bg-primary-900 text-white">
      {/* Logo */}
      <div className="flex items-center gap-3 px-6 py-5 border-b border-primary-800">
        <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-primary-500">
          <Hospital size={20} />
        </div>
        <div>
          <p className="text-sm font-bold leading-none">MediCare</p>
          <p className="text-xs text-primary-300 mt-0.5">Hospital System</p>
        </div>
      </div>

      {/* User badge */}
      <div className="px-4 py-3 mx-3 mt-3 rounded-lg bg-primary-800/60">
        <p className="text-xs text-primary-300 truncate">{user?.email}</p>
        <span className="mt-1 inline-block rounded-full bg-primary-500/30 px-2 py-0.5 text-xs font-semibold text-primary-200">
          {user?.role}
        </span>
      </div>

      {/* Nav */}
      <nav className="flex-1 overflow-y-auto px-3 py-4 space-y-1">
        {filtered.map(item => (
          <NavLink
            key={item.to}
            to={item.to}
            className={({ isActive }) =>
              clsx('flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium transition-colors',
                isActive
                  ? 'bg-primary-600 text-white'
                  : 'text-primary-200 hover:bg-primary-800 hover:text-white')
            }
          >
            {item.icon}
            {item.label}
          </NavLink>
        ))}
      </nav>

      {/* Logout */}
      <div className="p-3 border-t border-primary-800">
        <button
          onClick={handleLogout}
          className="flex w-full items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium text-primary-200 hover:bg-primary-800 hover:text-white transition-colors"
        >
          <LogOut size={18} />
          Sign out
        </button>
      </div>
    </aside>
  );
}
