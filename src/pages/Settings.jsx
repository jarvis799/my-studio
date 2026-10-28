import { useState } from 'react';
import { CheckCircle2, KeyRound } from 'lucide-react';
import { getToken, setToken } from '@/lib/tokens';

export default function Settings() {
  const [value, setValue] = useState(getToken());
  const [saved, setSaved] = useState(false);

  const save = () => {
    setToken(value);
    setSaved(true);
    setTimeout(function () {
      setSaved(false);
    }, 2000);
  };

  return (
    <div className="card space-y-3 p-5">
      <div className="flex items-center gap-2">
        <KeyRound className="h-4 w-4 text-lime-600" />
        <h1 className="text-lg font-semibold">GitHub token</h1>
      </div>
      <p className="text-[11px] text-slate-500">
        Stored only in this browser. A classic token with the repo scope is enough.
      </p>
      <input
        type="password"
        value={value}
        onChange={function (event) {
          setValue(event.target.value);
        }}
        placeholder="ghp_…"
        className="w-full rounded-xl border border-slate-200 bg-white/85 px-3 py-2 font-mono text-xs outline-none focus:border-lime-500"
      />
      <button
        type="button"
        onClick={save}
        className="w-full rounded-xl bg-gradient-to-r from-lime-400 to-emerald-300 py-2.5 text-sm font-semibold text-emerald-950 shadow-lg shadow-lime-500/25"
      >
        Save token
      </button>
      {saved ? (
        <p className="flex items-center gap-1.5 text-[11px] text-emerald-700">
          <CheckCircle2 className="h-3.5 w-3.5" /> Saved in this browser
        </p>
      ) : null}
    </div>
  );
}
