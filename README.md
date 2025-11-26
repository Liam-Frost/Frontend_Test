# Parallax Web Application

A modern, single-page web application for managing vehicles, user accounts, and blacklist queries — inspired by Apple-style UI aesthetics.
All data is stored locally in the browser (localStorage), and the entire app operates without any backend.

---

## ✨ Features

### 1. Authentication

- Email/phone based login
- Multi-stage sign-in (identifier → password)
- Password reset flow with CAPTCHA
- Full account creation with region, birthday, phone, and email
- Secure client-side validation (regex, length checks, and required fields)

### 2. Vehicles Management

After login, users are automatically routed to **Vehicles Management**.
Features include:

- Register vehicles with:
  - License plate (with regex validation)
  - Manufacturer
  - Model
  - Year
- Manufacturer/model lists populated dynamically
- Full list of registered vehicles displayed in a responsive table
- Toggle blacklist status
- Delete vehicles individually
- Data persisted via localStorage

### 3. Account Management

Accessible via navigation bar after login.
Includes:

- Update email, phone, and country calling code
- Change password (old password validation, new password regex, CAPTCHA)
- Delete entire account and all stored data

### 4. Vehicle Query

A dedicated page for searching any license plate and retrieving its blacklist status.
Includes:

- License plate pattern validation
- Instant display of blacklist result
- Same UI style as Vehicles Management

### 5. Global Navigation

- Always-visible top navigation bar after login
- Sections: **My Account**, **My Vehicles**, **Query**, **Sign Out**
- SPA-style navigation (no page reloads)

### 6. Apple-Style UI

The project includes:

- Soft shadows and rounded cards
- Apple-inspired layout and typography
- Custom Parallax ring logo with generated conic-gradient background and canvas-mask dots
- Responsive layout across mobile and desktop
- Floating labels and animated input fields

### 7. Utility Features

- Refresh button located at the bottom-right corner (fixed position button)
- All dropdown lists (regions, phone codes, manufacturers, models, years) generated from centralized constants
- Full form validation with helpful error messages

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|------------|
| **UI / Layout** | HTML5, CSS3, custom Apple-style components |
| **Logic** | Vanilla JavaScript (ES6 modules) |
| **State Storage** | Browser `localStorage` |
| **Rendering** | DOM manipulation, canvas drawing for ring logo |
| **Build Tools** | None (pure static) |

The project is fully static and can be deployed on any static hosting service such as GitHub Pages, Netlify, or Vercel.

---

## 📁 Project Structure

```
project/
│
├── index.html # Main single-page HTML shell
├── styles.css # Apple-style UI styling + responsive layout
├── app.js # All app logic, navigation, data handling, validation
├── README.md # Documentation
└── assets/ # (Optional) Additional icons, images
```

`index.html` contains all UI sections:

- Authentication container
- Register form
- Reset form
- Vehicles Management
- Account Management
- Query page

`app.js` dynamically shows/hides these sections depending on login state.

---

## 🚀 Getting Started

### Requirements

No dependencies, no frameworks—just open the HTML file.

### Run Locally

#### Option 1:

Open index.html in any browser.

#### Option 2 (recommended for module-safe localStorage behavior):

npx serve then open the URL shown in the terminal

---

## 🔧 Customization

### Adding New Regions or Phone Codes

Edit the objects inside:

```
const REGION_OPTIONS = [ ... ];
```

### Adding Vehicle Makes and Models

Modify:

```
const VEHICLE_DATA = {
"Toyota": [ "Camry", "Corolla", ... ],
"Mercedes-Benz": [ "C-Class", ... ],
...
};
```
### Updating UI Theme

Edit color gradients, shadows, and card styles in styles.css.

## 🧪 Known Issues / TODOs

- No backend; authentication is client-side only
- No encryption of stored passwords (browser localStorage limitation)
- Manufacturer → model mapping can be extended further
- Future updates may include:
- i18n support
- Real API backend
- Dark mode

## 📜 License

This project is provided for educational and personal development use.
Feel free to fork and improve.

## 💡 Credits

Design inspired by Apple ID and Apple UI conventions.
All implementation done manually without external libraries.

If you have ideas or want additional documentation (API, architecture diagram, code comments, etc.), feel free to ask!

```

```
