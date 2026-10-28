import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { GitBranch, Loader2, Star } from 'lucide-react';
import { getToken } from '@/lib/tokens';

export default function Home() {
  const token = getToken();
  const [repos, setRepos] = useState([]);
  const [status, setStatus] = useState('idle');

  useEffect(() => {
    if (!token) {
      setStatus('missing');
      return;
    }
    let alive = true;
    setStatus('loading');
    fetch('https://api.github.com/user/repos?per_page=30&sort=updated', {
      headers: {
        Authorization: 'Bearer ' + token,
        Accept: 'application/vnd.github+json',
      },
    })
      .then(function (res) {
        if (!res.ok) throw new Error('GitHub said ' + res.status);
        return res.json();
      })
      .then(function (data) {
        if (!alive) return;
        setRepos(Array.isArray(data) ? data : []);
        setStatus('ready');
      })
      .catch(function (error) {
        if (alive) setStatus(error.message);
      });
    return function () {
      alive = false;
    };
  }, [token]);

  return (
    <div className="space-y-4">
      <section className="card p-5">
        <p className="text-[11px] uppercase tracking-[0.22em] text-lime-700">dashboard</p>
        <h1 className="mt-1 text-2xl font-semibold tracking-tight">Your repositories</h1>
        <p className="mt-1 text-sm text-slate-500">
          This starter runs entirely in the browser — no server needed.
        </p>
      </section>

      {status === 'missing' ? (
        <p className="card p-4 text-sm text-slate-600">
          Add your GitHub token in <Link to="/settings" className="underline">Settings</Link> to load your repositories.
        </p>
      ) : null}

      {status === 'loading' ? (
        <p className="flex items-center gap-2 p-2 text-sm text-slate-500">
          <Loader2 className="h-4 w-4 animate-spin text-lime-600" /> Loading…
        </p>
      ) : null}

      {typeof status === 'string' && status !== 'missing' && status !== 'loading' && status !== 'ready' && status !== 'idle' ? (
        <p className="card p-4 text-sm text-red-600">{status}</p>
      ) : null}

      <div className="space-y-2">
        {repos.map(function (repo) {
          return (
            <a
              key={repo.id}
              href={repo.html_url}
              target="_blank"
              rel="noreferrer"
              className="card flex items-center justify-between gap-3 p-4 transition hover:-translate-y-0.5"
            >
              <span className="min-w-0">
                <span className="block truncate text-sm font-semibold">{repo.full_name}</span>
                <span className="mt-0.5 flex items-center gap-3 text-[11px] text-slate-500">
                  <span className="inline-flex items-center gap-1">
                    <Star className="h-3 w-3" /> {repo.stargazers_count}
                  </span>
                  <span className="inline-flex items-center gap-1">
                    <GitBranch className="h-3 w-3" /> {repo.default_branch}
                  </span>
                </span>
              </span>
              <span className="rounded-lg bg-lime-300/35 px-2.5 py-1 text-[10px] font-semibold uppercase tracking-wide text-emerald-900">
                {repo.private ? 'private' : 'public'}
              </span>
            </a>
          );
        })}
      </div>
    </div>
  );
}
