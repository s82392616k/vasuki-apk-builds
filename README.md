# Vasuki APK Converter

Turn an **HTML file**, a **ZIP website** or a **web link** into a real Android **APK**, with your own icon, splash screen and colours. It runs entirely in your browser: open the page, fill in the form, tap **Convert to APK**.

Made in India. Contact: [vasukihomepage.netlify.app](https://vasukihomepage.netlify.app)

---

## Features

- **Three sources:** a single `.html` file, a `.zip` site (must contain an `index.html`, any folder depth), or a URL.
- **App details:** custom app name and package name (e.g. `com.vasuki.myapp`).
- **Permissions you choose:** Files and downloads, Camera, Microphone, Location, Notifications. Internet access is always added. The app asks the phone only for what you tick, on first launch.
- **Branding:** custom app icon, splash screen, status bar colour, splash background colour and icon background colour.
- **Venjix AI Logo Maker:** describe a logo and pick a style (Glossy 3D, Neon, Modern gradient, Mascot, Luxury gold, Minimal flat). Pictures only, with no text or watermarks. Use it for the icon and splash in one tap.
- **Built-in image editor:** crop, rotate, add text, erase objects (photo fill, clear to transparent, or optional AI rebuild with your own Gemini key), undo.
- **Codex:** convert C++ or Python code (pasted or from a file) into a single HTML file, or wrap HTML code, then use it directly as your app source.
- Works on phones: light and dark mode, touch-friendly editor, camera capture for icon and splash.

## How it works

1. Your page, icon, splash and settings are packed into an Android Gradle project by plain JavaScript running in the browser. Nothing to install, no Python download.
2. The project is pushed to a GitHub repository.
3. A GitHub Actions workflow (JDK 17, Gradle 8.7, `compileSdk`/`targetSdk` 34, `minSdk` 24) compiles a **debug APK** and attaches it to a GitHub Release.
4. The page polls for the release (about 3 to 5 minutes) and shows a **Download APK** button.

The generated app is a thin Android WebView shell that loads your bundled site (or URL), with file picking, downloads, camera and the permissions you selected wired in.

## Use it online (for everyone)

Open the hosted page: **https://YOUR-SITE-LINK-HERE** (add your Netlify or GitHub Pages link). No account on this site and nothing to install.

To build an APK you need your own free GitHub account, because the APK is compiled in *your* repository:

1. Sign in at [github.com](https://github.com) (or create a free account).
2. Create a token: **Settings → Developer settings → Personal access tokens → Tokens (classic) → Generate new token**. Tick **repo** and **workflow**, then copy it.
3. Paste the token into **Build account** in the converter, and keep the default repository name (or choose one only used for this tool).
4. Follow the steps in **Usage** below.

Never share your token with anyone. You can delete it from GitHub at any time.

## Usage

1. Open `Vasuki-APK-Converter.html` in a modern browser (Chrome recommended), or host it on any static host (Netlify, GitHub Pages, etc.).
2. **Source:** choose HTML file, ZIP or URL.
3. **App details:** set the app name and package name.
4. **Permissions:** tick what your app needs.
5. **Icon, splash and colours:** upload, take a photo, or generate a logo.
6. Tap **Convert to APK**, wait for the build, then **Download APK**.
7. Install the APK on your Android phone (allow "Install unknown apps" when asked).

### Package name rules

Lowercase letters, numbers and underscores, at least two dot-separated parts, each starting with a letter: `com.yourname.appname`.

## Requirements

- A modern browser with internet access (GitHub, the ZIP reader and the logo service are loaded online).
- A GitHub account for the build step (free). The tool creates and overwrites files in one repository, so use a repository only for this tool.
- Android 7.0 (API 24) or newer to run the resulting APK.

## Privacy and data

- Your files are processed in your browser and uploaded only to the GitHub repository used for the build. That repository is created **public** by default, so anything inside your app will be public. Do not convert apps containing secrets.
- Logo prompts are sent to [Pollinations](https://pollinations.ai). Codex conversions are also sent to Pollinations.
- If you add an optional Gemini key for AI erase, it is stored only in your browser's local storage and sent only to Google's API.

## Limitations

- Output is a **debug-signed** APK, suitable for sideloading and testing. It is not ready for Google Play (no release keystore, versionCode fixed at 1).
- Apps that need a backend must point to a reachable server. Local files only work for bundled HTML, CSS and JS.
- AI features depend on third-party services and may be slow or busy at times.
- Builds depend on GitHub Actions availability and free-tier limits.

## Self-hosting

It is a single static HTML file. To host your own copy, upload it to any static host. No server or build step is needed.

## Troubleshooting

| Problem | Fix |
|---|---|
| "Package name must look like com.vasuki.myapp" | Use lowercase, dot-separated names. |
| "GitHub rejected the token" | The token needs `repo` and `workflow` permissions. |
| "The build failed. See the log" | Open the link shown; most failures come from a broken ZIP or invalid HTML source. |
| "Build is taking too long" | Check the **Actions** tab of your build repository. |
| Logo maker is busy | Wait a moment and try again. |
| Phone blocks install | Enable "Install unknown apps" for your browser or file manager. |

## Contributing

Issues and pull requests are welcome. Please keep the tool a single self-contained HTML file.

## License

Add your preferred license here (for example MIT) before publishing, and place the full text in a `LICENSE` file.

---

*Vasuki APK Converter, made in India.*
