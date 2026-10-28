# Portfolio + Android app

One repository, two outputs:

- **Website** — plain HTML/CSS/JS in the repo root. No npm install needed, so GitHub Pages and Vercel build it on every push.
- **Android app** — a native Java app in `android/`. GitHub Actions builds the APK and publishes it as a release.

## Where things are

| Path | What it is |
| --- | --- |
| `index.html`, `assets/` | The public site |
| `content.json` | Your name, bio, email, socials, projects and app link |
| `android/` | Native Android project (bottom nav, inbox, mail auto-reply) |
| `.github/workflows/site.yml` | Deploys the website to GitHub Pages |
| `.github/workflows/android.yml` | Builds the APK, uploads it and creates a release |

## How to use it

1. Edit `content.json` — name, role, about, email, phone, photo, socials, projects.
2. `git push` — the site deploys and the APK builds automatically.
3. On the site, **Download Android app** points at `/releases/latest`, so the newest build is one tap away.
4. Open the app → **Settings** → enter your mail address and app password to turn on the inbox and HTML auto-reply.

No login or sign-up screens anywhere: the site is public, the app stores your mail settings on the device.

## Build the app yourself

```bash
cd android
gradle assembleDebug
```

The APK lands in `android/app/build/outputs/apk/debug/`.

The studio's **APK Studio** tab does all of this for you from a ZIP or an HTML file.
