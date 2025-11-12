# Frontend_Test

A static Parallax-themed demo built with vanilla HTML, CSS, and JavaScript to showcase an identifier-first sign-in experience, account creation, password reset, and a simple license registration workflow. All state is persisted in the browser via `localStorage` so the flows can be explored without any backend services.

## Features

- **Identifier-first sign in** – Prompt for an email address or phone number before revealing the password step, closely mirroring the referenced Parallax layout.
- **Account creation** – Multi-field registration form that validates contact details, birth date, password strength, and captcha input before storing a new profile locally.
- **Password reset** – Guided reset screen that validates a captcha and simulates sending recovery instructions based on the provided identifier.
- **License management** – After authentication, users can register license plates, view saved entries, and remove them, with data scoped to the signed-in account.
- **Apple-inspired styling** – Responsive, glassmorphism-influenced UI with floating labels and Parallax branding as requested.

## Getting started

1. Open `index.html` in a browser or serve the repository with any static file server.
2. Create a new Parallax account or sign in with an existing entry saved in `localStorage`.
3. Register license plates once authenticated to see them listed in the management panel.

## Local data model

The demo uses three `localStorage` collections:

- `ft_users` – Stores registered user profiles.
- `ft_session` – Tracks the active username for auto sign-in after reloads.
- `ft_licenses` – Maps usernames to their saved license plates.

## API reference

The interface does not invoke any remote services. All interactions are handled on the client.

| Method | Endpoint | Purpose |
| ------ | -------- | ------- |
| — | — | All authentication, password reset, and license workflows execute entirely in the browser without calling external APIs. |

## Notes

Because the implementation is fully client-side, the captcha and messaging flows are illustrative only and should be backed by real services in production.
