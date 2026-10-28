# DevStyle Starter

A React + Vite + Tailwind starter that runs entirely in the browser — no server required.

## Run it

```bash
npm install
npm run dev
```

Open the printed local URL. Add your GitHub token in **Settings** to load your repositories.

## Go live

Push to `main` — the included GitHub Actions workflow builds the site and publishes it to
GitHub Pages. In the repository open **Settings → Pages** once and choose **GitHub Actions**
as the source.

## Structure

- `src/pages/Home.jsx` — repository dashboard
- `src/pages/Settings.jsx` — token stored in the browser
- `src/components/Shell.jsx` — layout and navigation
