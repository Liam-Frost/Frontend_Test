const STORAGE_KEYS = {
  users: "ft_users",
  licenses: "ft_licenses",
  session: "ft_session",
};

const loginForm = document.getElementById("login-form");
const loginIdentifierInput = document.getElementById("login-username");
const loginPasswordInput = document.getElementById("login-password");
const loginPrimaryButton = loginForm?.querySelector(".primary-button");
const registerForm = document.getElementById("register-form");
const resetForm = document.getElementById("reset-form");
const authMessage = document.getElementById("auth-message");
const formTitle = document.getElementById("form-title");
const formSubtitle = document.getElementById("form-subtitle");
const createAccountLinks = document.querySelectorAll(
  ".create-account-trigger"
);
const backToLoginLink = document.getElementById("back-to-login");
const forgotPasswordLink = document.getElementById("forgot-password");
const resetBackLink = document.getElementById("reset-back-link");
const signinCard = document.querySelector(".signin-card");
const authShell = document.getElementById("auth-shell");
const licenseSection = document.getElementById("license-section");
const licenseForm = document.getElementById("license-form");
const licenseMessage = document.getElementById("license-message");
const welcomeMessage = document.getElementById("welcome-message");
const licenseList = document.getElementById("license-list");
const logoutButton = document.getElementById("logout-button");
const monthSelect = document.getElementById("register-birth-month");
const daySelect = document.getElementById("register-birth-day");
const yearSelect = document.getElementById("register-birth-year");
const captchaInput = document.getElementById("register-captcha");
const captchaDisplay = document.getElementById("captcha-display");
const refreshCaptchaButton = document.getElementById("refresh-captcha");
const resetIdentifierInput = document.getElementById("reset-identifier");
const resetCaptchaInput = document.getElementById("reset-captcha-input");
const resetCaptchaDisplay = document.getElementById("reset-captcha-display");
const resetRefreshCaptchaButton = document.getElementById("reset-refresh-captcha");

let currentUser = null;
let loginStage = "identifier";
const captchaState = {
  register: "469P",
  reset: "469P",
};

function readUsers() {
  try {
    return JSON.parse(localStorage.getItem(STORAGE_KEYS.users)) || [];
  } catch (error) {
    console.warn("Failed to read user data", error);
    return [];
  }
}

function saveUsers(users) {
  localStorage.setItem(STORAGE_KEYS.users, JSON.stringify(users));
}

function readLicenses() {
  try {
    return JSON.parse(localStorage.getItem(STORAGE_KEYS.licenses)) || {};
  } catch (error) {
    console.warn("Failed to read license data", error);
    return {};
  }
}

function saveLicenses(licenses) {
  localStorage.setItem(STORAGE_KEYS.licenses, JSON.stringify(licenses));
}

function setSession(user) {
  if (user) {
    localStorage.setItem(
      STORAGE_KEYS.session,
      JSON.stringify({ username: user.username })
    );
  } else {
    localStorage.removeItem(STORAGE_KEYS.session);
  }
}

function getSession() {
  try {
    const data = JSON.parse(localStorage.getItem(STORAGE_KEYS.session));
    return data?.username || null;
  } catch (error) {
    return null;
  }
}

function showMessage(element, text, type = "") {
  element.textContent = text;
  element.classList.remove("success", "error");
  if (type) {
    element.classList.add(type);
  }
}

function updateBirthDayOptions(preserveSelection = true) {
  if (!daySelect) return;
  const previousValue = preserveSelection ? daySelect.value : "";
  const selectedMonth = parseInt(monthSelect?.value || "", 10);
  const selectedYear = parseInt(yearSelect?.value || "", 10) || new Date().getFullYear();
  let daysInMonth = 31;

  if (selectedMonth) {
    daysInMonth = new Date(selectedYear, selectedMonth, 0).getDate();
  }

  daySelect.innerHTML = "";
  const placeholder = document.createElement("option");
  placeholder.value = "";
  placeholder.textContent = "Day";
  placeholder.disabled = true;
  daySelect.appendChild(placeholder);

  for (let day = 1; day <= daysInMonth; day += 1) {
    const option = document.createElement("option");
    option.value = String(day).padStart(2, "0");
    option.textContent = String(day);
    daySelect.appendChild(option);
  }

  if (preserveSelection && previousValue) {
    daySelect.value = previousValue;
  }

  if (!daySelect.value) {
    placeholder.selected = true;
  }
}

function populateBirthSelects() {
  if (!monthSelect || !daySelect || !yearSelect) return;

  const months = [
    "January",
    "February",
    "March",
    "April",
    "May",
    "June",
    "July",
    "August",
    "September",
    "October",
    "November",
    "December",
  ];

  monthSelect.innerHTML = "";
  const monthPlaceholder = document.createElement("option");
  monthPlaceholder.value = "";
  monthPlaceholder.textContent = "Month";
  monthPlaceholder.disabled = true;
  monthPlaceholder.selected = true;
  monthSelect.appendChild(monthPlaceholder);

  months.forEach((name, index) => {
    const option = document.createElement("option");
    option.value = String(index + 1).padStart(2, "0");
    option.textContent = name;
    monthSelect.appendChild(option);
  });

  yearSelect.innerHTML = "";
  const yearPlaceholder = document.createElement("option");
  yearPlaceholder.value = "";
  yearPlaceholder.textContent = "Year";
  yearPlaceholder.disabled = true;
  yearPlaceholder.selected = true;
  yearSelect.appendChild(yearPlaceholder);

  const currentYear = new Date().getFullYear();
  for (let year = currentYear; year >= currentYear - 120; year -= 1) {
    const option = document.createElement("option");
    option.value = String(year);
    option.textContent = String(year);
    yearSelect.appendChild(option);
  }

  updateBirthDayOptions(false);

  monthSelect.addEventListener("change", () => updateBirthDayOptions());
  yearSelect.addEventListener("change", () => updateBirthDayOptions());
}

function resetRegisterForm() {
  if (registerForm) {
    registerForm.reset();
  }

  if (monthSelect) {
    monthSelect.selectedIndex = 0;
  }

  if (yearSelect) {
    yearSelect.selectedIndex = 0;
  }

  updateBirthDayOptions(false);
  generateCaptcha("register");
}

function updateCaptchaDisplay(context, code) {
  if (context === "register" && captchaDisplay) {
    captchaDisplay.textContent = code;
    return;
  }

  if (context === "reset" && resetCaptchaDisplay) {
    resetCaptchaDisplay.textContent = code;
  }
}

function clearCaptchaInput(context) {
  if (context === "register" && captchaInput) {
    captchaInput.value = "";
    return;
  }

  if (context === "reset" && resetCaptchaInput) {
    resetCaptchaInput.value = "";
  }
}

function generateCaptcha(context = "register") {
  const characters = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
  let code = "";
  for (let index = 0; index < 4; index += 1) {
    const randomIndex = Math.floor(Math.random() * characters.length);
    code += characters[randomIndex];
  }
  captchaState[context] = code;
  updateCaptchaDisplay(context, code);
  clearCaptchaInput(context);
}

function setupCaptchaControls() {
  const controls = [
    { refresh: refreshCaptchaButton, context: "register" },
    { refresh: resetRefreshCaptchaButton, context: "reset" },
  ];

  controls.forEach(({ refresh, context }) => {
    if (refresh) {
      refresh.addEventListener("click", () => {
        generateCaptcha(context);
      });
    }
  });
}

function setLoginStage(stage) {
  loginStage = stage;
  if (loginForm) {
    loginForm.dataset.stage = stage;
  }

  if (loginPrimaryButton) {
    loginPrimaryButton.textContent =
      stage === "identifier" ? "Continue" : "Sign in";
  }

  if (formSubtitle) {
    formSubtitle.textContent =
      stage === "identifier"
        ? "Enter your email or phone number to continue."
        : "Enter your password to continue.";
  }

  if (stage === "identifier") {
    if (loginPasswordInput) {
      loginPasswordInput.value = "";
    }
    loginIdentifierInput?.focus();
  } else {
    loginPasswordInput?.focus();
  }
}

function showLoginView() {
  if (signinCard) {
    signinCard.classList.remove("register-mode", "reset-mode");
  }
  formTitle.textContent = "Sign in to Parallax";
  if (loginForm) {
    loginForm.reset();
  }
  setLoginStage("identifier");
  loginForm.classList.add("active");
  registerForm.classList.remove("active");
  resetForm?.classList.remove("active");
  showMessage(authMessage, "");
}

function showRegisterView() {
  if (signinCard) {
    signinCard.classList.add("register-mode");
    signinCard.classList.remove("reset-mode");
  }
  formTitle.textContent = "";
  formSubtitle.textContent = "";
  registerForm.classList.add("active");
  loginForm.classList.remove("active");
  resetForm?.classList.remove("active");
  generateCaptcha("register");
  showMessage(authMessage, "");
}

function showResetView() {
  if (signinCard) {
    signinCard.classList.add("reset-mode");
    signinCard.classList.remove("register-mode");
  }
  formTitle.textContent = "Reset your password";
  formSubtitle.textContent =
    "Enter your email address or phone number that you use with your account to continue.";
  loginForm.classList.remove("active");
  registerForm.classList.remove("active");
  resetForm?.classList.add("active");
  generateCaptcha("reset");
  showMessage(authMessage, "");
  resetForm?.reset();
  if (resetIdentifierInput) {
    resetIdentifierInput.focus();
  }
}

function handleRegister(event) {
  event.preventDefault();

  const country = document.getElementById("register-country")?.value;
  const firstName = document
    .getElementById("register-first-name")
    ?.value.trim();
  const lastName = document
    .getElementById("register-last-name")
    ?.value.trim();
  const month = monthSelect?.value || "";
  const day = daySelect?.value || "";
  const year = yearSelect?.value || "";
  const email = document.getElementById("register-email")?.value.trim();
  const password = document
    .getElementById("register-password")
    ?.value.trim();
  const confirmPassword = document
    .getElementById("register-confirm-password")
    ?.value.trim();
  const phoneCountry = document
    .getElementById("register-phone-country")
    ?.value;
  const phoneRaw = document.getElementById("register-phone")?.value.trim();
  const contactMethod =
    registerForm.querySelector('input[name="contact-method"]:checked')?.value ||
    "text";
  const captchaValue = captchaInput?.value.trim() || "";

  if (!country || !firstName || !lastName) {
    showMessage(authMessage, "Please complete your name and country details.", "error");
    return;
  }

  if (!month || !day || !year) {
    showMessage(authMessage, "Please select your full birth date.", "error");
    return;
  }

  const emailPattern = /^[^@\s]+@[^@\s]+\.[^@\s]+$/;
  if (!emailPattern.test(email || "")) {
    showMessage(authMessage, "Enter a valid email address.", "error");
    return;
  }

  if (!password || password.length < 8) {
    showMessage(authMessage, "Password must be at least 8 characters.", "error");
    return;
  }

  if (password !== confirmPassword) {
    showMessage(authMessage, "Passwords do not match.", "error");
    return;
  }

  const phoneDigits = (phoneRaw || "").replace(/\D/g, "");
  if (!phoneCountry || phoneDigits.length < 5) {
    showMessage(authMessage, "Enter a valid phone number and country code.", "error");
    return;
  }

  if (!captchaValue) {
    showMessage(authMessage, "Enter the verification code.", "error");
    return;
  }

  if (captchaValue.toUpperCase() !== (captchaState.register || "")) {
    showMessage(authMessage, "Incorrect verification code. Please try again.", "error");
    generateCaptcha("register");
    return;
  }

  const users = readUsers();
  const lowerEmail = (email || "").toLowerCase();
  if (users.some((user) => user.email?.toLowerCase() === lowerEmail)) {
    showMessage(authMessage, "That email is already registered. Please sign in instead.", "error");
    return;
  }

  const phoneSignature = `${(phoneCountry || "").replace(/\D/g, "")}${
    phoneDigits || ""
  }`;
  if (
    phoneDigits &&
    users.some((user) => {
      const storedDigits = String(user.phone || "").replace(/\D/g, "");
      const storedCountry = String(user.phoneCountry || "").replace(/\D/g, "");
      return storedDigits && storedCountry + storedDigits === phoneSignature;
    })
  ) {
    showMessage(authMessage, "That phone number is already registered. Please sign in instead.", "error");
    return;
  }

  const displayName = [firstName, lastName].filter(Boolean).join(" ") || lowerEmail;
  const newUser = {
    username: lowerEmail,
    email: lowerEmail,
    password,
    firstName,
    lastName,
    displayName,
    country,
    birthDate: { month, day, year },
    phoneCountry,
    phone: phoneDigits,
    contactMethod,
    createdAt: new Date().toISOString(),
  };

  users.push(newUser);
  saveUsers(users);
  resetRegisterForm();
  showLoginView();
  showMessage(authMessage, "Registration successful! Please sign in to continue.", "success");
}

function handleReset(event) {
  event.preventDefault();

  const identifier = resetIdentifierInput?.value.trim();
  const captchaValue = resetCaptchaInput?.value.trim();

  if (!identifier) {
    showMessage(authMessage, "Enter the email address or phone number for your account.", "error");
    return;
  }

  if (!captchaValue) {
    showMessage(authMessage, "Enter the verification code.", "error");
    return;
  }

  if (captchaValue.toUpperCase() !== (captchaState.reset || "")) {
    showMessage(authMessage, "Incorrect verification code. Please try again.", "error");
    generateCaptcha("reset");
    return;
  }

  const users = readUsers();
  const normalizedIdentifier = (identifier || "").toLowerCase();
  const digits = (identifier || "").replace(/\D/g, "");

  const match = users.some((user) => {
    const emailMatch = user.email?.toLowerCase() === normalizedIdentifier;
    const usernameMatch = user.username?.toLowerCase() === normalizedIdentifier;
    const storedDigits = String(user.phone || "").replace(/\D/g, "");
    const storedCountry = String(user.phoneCountry || "").replace(/\D/g, "");
    const combined = storedCountry + storedDigits;
    const phoneMatch =
      digits &&
      ((storedDigits && storedDigits === digits) ||
        (combined && combined === digits));
    return emailMatch || usernameMatch || phoneMatch;
  });

  resetForm?.reset();
  generateCaptcha("reset");

  if (match) {
    showMessage(
      authMessage,
      "We've sent password reset instructions to the contact details on file.",
      "success"
    );
  } else {
    showMessage(
      authMessage,
      "If the information is linked to a Parallax account, we'll send reset instructions.",
      "success"
    );
  }
}

function handleLogin(event) {
  event.preventDefault();
  const identifier = loginIdentifierInput?.value.trim();

  if (loginStage === "identifier") {
    if (!identifier) {
      showMessage(authMessage, "Enter your email address or phone number.", "error");
      loginIdentifierInput?.focus();
      return;
    }

    showMessage(authMessage, "");
    setLoginStage("password");
    return;
  }

  const password = loginPasswordInput?.value.trim();
  if (!identifier) {
    showMessage(authMessage, "Enter your email address or phone number.", "error");
    setLoginStage("identifier");
    return;
  }

  if (!password) {
    showMessage(authMessage, "Enter your password.", "error");
    loginPasswordInput?.focus();
    return;
  }
  const users = readUsers();
  const normalizedIdentifier = (identifier || "").toLowerCase();
  const phoneDigits = (identifier || "").replace(/\D/g, "");

  const user = users.find((item) => {
    if (item.password !== password) {
      return false;
    }

    const usernameMatch =
      item.username && item.username.toLowerCase() === normalizedIdentifier;
    const emailMatch =
      item.email && item.email.toLowerCase() === normalizedIdentifier;
    const storedDigits = String(item.phone || "").replace(/\D/g, "");
    const storedCountry = String(item.phoneCountry || "").replace(/\D/g, "");
    const combinedPhone = storedCountry + storedDigits;
    const phoneMatch =
      phoneDigits &&
      ((storedDigits && storedDigits === phoneDigits) ||
        (combinedPhone && combinedPhone === phoneDigits));

    return usernameMatch || emailMatch || phoneMatch;
  });

  if (!user) {
    showMessage(authMessage, "Incorrect username or password. Please try again.", "error");
    return;
  }

  currentUser = user;
  setSession(user);
  loginForm.reset();
  setLoginStage("identifier");
  enterLicenseMode();
}

function enterLicenseMode() {
  if (!currentUser) return;
  authShell.classList.add("hidden");
  licenseSection.classList.remove("hidden");
  const name =
    currentUser.displayName ||
    [currentUser.firstName, currentUser.lastName].filter(Boolean).join(" ") ||
    currentUser.username;
  welcomeMessage.textContent = `Welcome, ${name}! Please register your vehicles.`;
  showMessage(licenseMessage, "");
  refreshLicenseList();
}

function exitLicenseMode() {
  currentUser = null;
  setSession(null);
  licenseSection.classList.add("hidden");
  authShell.classList.remove("hidden");
  showLoginView();
  showMessage(authMessage, "You have signed out.", "success");
}

function handleLicenseSubmit(event) {
  event.preventDefault();
  const licenseNumber = document
    .getElementById("license-number")
    .value.trim()
    .toUpperCase();
  const vehicleModel = document
    .getElementById("vehicle-model")
    .value.trim();

  if (!currentUser) {
    showMessage(licenseMessage, "Please sign in first.", "error");
    return;
  }

  const licenses = readLicenses();
  const userLicenses = licenses[currentUser.username] || [];

  if (userLicenses.some((item) => item.licenseNumber === licenseNumber)) {
    showMessage(licenseMessage, "That license plate is already registered.", "error");
    return;
  }

  const entry = {
    licenseNumber,
    vehicleModel,
    createdAt: new Date().toISOString(),
  };

  userLicenses.push(entry);
  licenses[currentUser.username] = userLicenses;
  saveLicenses(licenses);
  showMessage(licenseMessage, "License plate saved successfully!", "success");
  licenseForm.reset();
  refreshLicenseList();
}

function refreshLicenseList() {
  licenseList.innerHTML = "";
  const licenses = readLicenses();
  const userLicenses = licenses[currentUser.username] || [];

  if (userLicenses.length === 0) {
    licenseList.innerHTML = "<li>No license plates registered yet.</li>";
    return;
  }

  userLicenses
    .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
    .forEach((item) => {
      const listItem = document.createElement("li");
      const left = document.createElement("span");
      left.innerHTML = `<strong>${item.licenseNumber}</strong> - ${item.vehicleModel}`;

      const removeButton = document.createElement("button");
      removeButton.textContent = "Remove";
      removeButton.addEventListener("click", () => removeLicense(item.licenseNumber));

      listItem.append(left, removeButton);
      licenseList.appendChild(listItem);
    });
}

function removeLicense(licenseNumber) {
  const licenses = readLicenses();
  const userLicenses = licenses[currentUser.username] || [];
  const updated = userLicenses.filter((item) => item.licenseNumber !== licenseNumber);
  licenses[currentUser.username] = updated;
  saveLicenses(licenses);
  refreshLicenseList();
  showMessage(licenseMessage, "License plate removed.", "success");
}

createAccountLinks.forEach((link) => {
  link.addEventListener("click", (event) => {
    event.preventDefault();
    showRegisterView();
  });
});

backToLoginLink.addEventListener("click", (event) => {
  event.preventDefault();
  showLoginView();
});

if (forgotPasswordLink) {
  forgotPasswordLink.addEventListener("click", (event) => {
    event.preventDefault();
    showResetView();
  });
}

if (resetBackLink) {
  resetBackLink.addEventListener("click", (event) => {
    event.preventDefault();
    showLoginView();
  });
}

registerForm.addEventListener("submit", handleRegister);
loginForm.addEventListener("submit", handleLogin);
resetForm?.addEventListener("submit", handleReset);
licenseForm.addEventListener("submit", handleLicenseSubmit);
logoutButton.addEventListener("click", exitLicenseMode);

document.addEventListener("DOMContentLoaded", () => {
  populateBirthSelects();
  setupCaptchaControls();
  generateCaptcha("register");
  generateCaptcha("reset");
  showLoginView();

  const username = getSession();
  if (!username) {
    return;
  }

  const users = readUsers();
  const user = users.find((item) => item.username === username);
  if (user) {
    currentUser = user;
    enterLicenseMode();
  } else {
    setSession(null);
  }
});
