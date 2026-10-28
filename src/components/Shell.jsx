import { NavLink, Outlet } from 'react-router-dom';
import { Home, Settings as SettingsIcon, Sparkles } from 'lucide-react';

const link = ({ isActive }) =>
  'rounded-xl px-3 py-2 text-sm transition ' +
  (isActive ? 'bg-lime-300/40 font-semibold text-slate-900' : 'text-slate-500 hover:bg-white/70');

export default function Shell() {
  return (
    <div className="mx-auto w-full max-w-3xl px-4 py-6">
      <header className="mb-6 flex items-center justify-between gap-3">
        <div className="flex items-center gap-3">
          <span className="flex h-10 w-10 items-center justify-center rounded-2xl bg-gradient-to-br from-lime-400 to-emerald-300 shadow-lg shadow-lime-500/30">
            <Sparkles className="h-5 w-5 text-emerald-950" />
          </span>
          <div className="leading-tight">
            <p className="text-sm font-semibold">DevStyle Starter</p>
            <p className="text-[10px] uppercase tracking-[0.18em] text-slate-400">my app</p>
          </div>
        </div>
        <nav className="flex items-center gap-1">
          <NavLink to="/" end className={link}>
            <span className="inline-flex items-center gap-1.5">
              <Home className="h-3.5 w-3.5" /> Home
            </span>
          </NavLink>
          <NavLink to="/settings" className={link}>
            <span className="inline-flex items-center gap-1.5">
              <SettingsIcon className="h-3.5 w-3.5" /> Settings
            </span>
          </NavLink>
        </nav>
      </header>
      <Outlet />
    </div>
  );
}
