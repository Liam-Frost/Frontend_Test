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
const accountSection = document.getElementById("account-section");
const licenseForm = document.getElementById("license-form");
const licenseMessage = document.getElementById("license-message");
const welcomeMessage = document.getElementById("welcome-message");
const vehicleMakeSelect = document.getElementById("vehicle-make");
const vehicleModelSelect = document.getElementById("vehicle-model");
const vehicleYearSelect = document.getElementById("vehicle-year");
const vehiclesTableBody = document.getElementById("vehicles-table-body");
const navVehiclesLink = document.getElementById("nav-vehicles");
const navSignoutItem = document.getElementById("nav-signout-item");
const navSignoutLink = document.getElementById("nav-signout");
const monthSelect = document.getElementById("register-birth-month");
const daySelect = document.getElementById("register-birth-day");
const yearSelect = document.getElementById("register-birth-year");
const registerCountrySelect = document.getElementById("register-country");
const registerPhoneCountrySelect = document.getElementById("register-phone-country");
const accountPhoneCountrySelect = document.getElementById("account-phone-country");
const captchaInput = document.getElementById("register-captcha");
const captchaDisplay = document.getElementById("captcha-display");
const refreshCaptchaButton = document.getElementById("refresh-captcha");
const resetIdentifierInput = document.getElementById("reset-identifier");
const resetCaptchaInput = document.getElementById("reset-captcha-input");
const resetCaptchaDisplay = document.getElementById("reset-captcha-display");
const resetRefreshCaptchaButton = document.getElementById("reset-refresh-captcha");
const accountEmailInput = document.getElementById("account-email");
const accountPhoneCountrySelect = document.getElementById("account-phone-country");
const accountPhoneInput = document.getElementById("account-phone");
const accountCurrentPasswordInput = document.getElementById(
  "account-current-password-for-contact"
);
const accountContactForm = document.getElementById("account-contact-form");
const accountContactMessage = document.getElementById("account-contact-message");
const accountOldPasswordInput = document.getElementById("account-old-password");
const accountNewPasswordInput = document.getElementById("account-new-password");
const accountConfirmPasswordInput = document.getElementById(
  "account-confirm-password"
);
const accountPasswordForm = document.getElementById("account-password-form");
const accountPasswordMessage = document.getElementById("account-password-message");
const accountCaptchaDisplay = document.getElementById("account-captcha-display");
const accountCaptchaInput = document.getElementById("account-captcha-input");
const accountRefreshCaptchaButton = document.getElementById(
  "account-refresh-captcha"
);
const accountDeleteButton = document.getElementById("account-delete");
const navAccountLink = document.getElementById("nav-account");
const navQueryLink = document.getElementById("nav-query");
const querySection = document.getElementById("query-section");
const queryForm = document.getElementById("query-form");
const queryLicenseInput = document.getElementById("query-license-number");
const queryMessage = document.getElementById("query-message");

let currentUser = null;
let loginStage = "identifier";
const captchaState = {
  register: "469P",
  reset: "469P",
  account: "1234",
};

const LICENSE_PATTERN = /^[A-Z0-9-]{1,7}$/;
const EMAIL_PATTERN = /^[^@\s]+@[^@\s]+\.[^@\s]+$/;
const PASSWORD_PATTERN = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[A-Za-z\d]{8,}$/;
const REGION_OPTIONS = [
  { iso: "US", name: "United States", phoneCode: "+1" },
  { iso: "CA", name: "Canada",        phoneCode: "+1" },
  { iso: "MX", name: "Mexico",        phoneCode: "+52" },
  { iso: "BR", name: "Brazil",        phoneCode: "+55" },
  { iso: "GB", name: "United Kingdom", phoneCode: "+44" },
  { iso: "DE", name: "Germany",        phoneCode: "+49" },
  { iso: "FR", name: "France",         phoneCode: "+33" },
  { iso: "IT", name: "Italy",          phoneCode: "+39" },
  { iso: "ES", name: "Spain",          phoneCode: "+34" },
  { iso: "NL", name: "Netherlands",    phoneCode: "+31" },
  { iso: "SE", name: "Sweden",         phoneCode: "+46" },
  { iso: "CN", name: "China",          phoneCode: "+86" },
  { iso: "HK", name: "Hong Kong",      phoneCode: "+852" },
  { iso: "TW", name: "Taiwan",         phoneCode: "+886" },
  { iso: "JP", name: "Japan",          phoneCode: "+81" },
  { iso: "KR", name: "South Korea",    phoneCode: "+82" },
  { iso: "SG", name: "Singapore",      phoneCode: "+65" },
];
const VEHICLE_MODELS = {
  Acura: [
    "ILX", "TLX", "RLX",
    "RDX", "MDX"
  ],
  Audi: [
    "A3", "A4", "A5", "A6", "A7", "A8",
    "Q3", "Q5", "Q7", "Q8",
    "e-tron", "Q4 e-tron"
  ],
  BMW: [
    "1 Series", "2 Series", "3 Series", "4 Series", "5 Series", "7 Series",
    "X1", "X3", "X4", "X5", "X6", "X7",
    "i3", "i4", "iX", "iX3"
  ],
  Buick: [
    "Encore", "Encore GX", "Envision", "Enclave", "Regal"
  ],
  Cadillac: [
    "CT4", "CT5", "CT6",
    "XT4", "XT5", "XT6",
    "Escalade"
  ],
  Chevrolet: [
    "Spark", "Cruze", "Malibu", "Impala",
    "Camaro", "Corvette",
    "Trax", "Equinox", "Blazer", "Traverse",
    "Tahoe", "Suburban",
    "Colorado", "Silverado 1500", "Silverado 2500"
  ],
  Chrysler: [
    "300", "Pacifica", "Voyager"
  ],
  Dodge: [
    "Charger", "Challenger",
    "Durango", "Journey",
    "Grand Caravan"
  ],
  Engneering: [
    "CPEN", "EE", "EMPH",
    "BMEG", "CVIL", "MECH"
  ],
  Fiat: [
    "500", "500X", "500L"
  ],
  Ford: [
    "Fiesta", "Focus", "Fusion",
    "Mustang",
    "EcoSport", "Escape", "Edge", "Explorer", "Expedition",
    "Bronco", "Bronco Sport",
    "Ranger", "F-150", "F-250 Super Duty"
  ],
  Genesis: [
    "G70", "G80", "G90",
    "GV60", "GV70", "GV80"
  ],
  GMC: [
    "Terrain", "Acadia", "Yukon",
    "Canyon", "Sierra 1500", "Sierra 2500"
  ],
  Honda: [
    "Fit", "Civic", "Accord",
    "Insight",
    "HR-V", "CR-V", "Passport", "Pilot",
    "Odyssey",
    "Ridgeline"
  ],
  Hyundai: [
    "Accent", "Elantra", "Sonata", "Ioniq", "Veloster",
    "Kona", "Tucson", "Santa Fe", "Palisade",
    "Ioniq 5", "Ioniq 6"
  ],
  Infiniti: [
    "Q50", "Q60",
    "QX50", "QX55", "QX60", "QX80"
  ],
  Jaguar: [
    "XE", "XF", "XJ",
    "F-Type",
    "E-PACE", "F-PACE", "I-PACE"
  ],
  Jeep: [
    "Renegade", "Compass", "Cherokee", "Grand Cherokee",
    "Wrangler", "Gladiator"
  ],
  Kia: [
    "Rio", "Forte", "K5", "Stinger",
    "Soul", "Seltos", "Sportage", "Sorento", "Telluride",
    "Niro", "EV6"
  ],
  "Land Rover": [
    "Range Rover", "Range Rover Sport", "Range Rover Evoque",
    "Discovery", "Discovery Sport",
    "Defender"
  ],
  Lexus: [
    "IS", "ES", "GS", "LS",
    "UX", "NX", "RX", "GX", "LX",
    "RC", "LC"
  ],
  Lincoln: [
    "Corsair", "Nautilus", "Aviator", "Navigator"
  ],
  Maserati: [
    "Ghibli", "Quattroporte", "Levante", "Grecale", "MC20"
  ],
  Mazda: [
    "Mazda3", "Mazda6",
    "CX-3", "CX-30", "CX-5", "CX-50", "CX-9", "CX-90",
    "MX-5 Miata"
  ],
  "Mercedes-Benz": [
    "A-Class", "C-Class", "E-Class", "S-Class",
    "CLA", "CLS",
    "GLA", "GLB", "GLC", "GLE", "GLS", "G-Class",
    "EQE", "EQS"
  ],
  Mini: [
    "Cooper 3 Door", "Cooper 5 Door", "Clubman", "Countryman"
  ],
  Mitsubishi: [
    "Mirage", "Lancer",
    "Eclipse Cross", "Outlander", "Outlander PHEV",
    "RVR / ASX"
  ],
  Nissan: [
    "Versa", "Sentra", "Altima", "Maxima",
    "Leaf",
    "Kicks", "Rogue", "Pathfinder", "Murano", "Armada",
    "Frontier", "Titan"
  ],
  Porsche: [
    "718 Boxster", "718 Cayman",
    "911",
    "Panamera", "Taycan",
    "Macan", "Cayenne"
  ],
  Ram: [
    "1500", "2500", "3500", "ProMaster"
  ],
  "Rolls-Royce": [
    "Ghost", "Phantom", "Wraith", "Dawn", "Cullinan"
  ],
  StarWars: [
    "DeathStar", "MF", "X-Wing",
    "R2D2", "C3PO", "TIE Fighter"
  ],
  Subaru: [
    "Impreza", "Legacy",
    "Crosstrek", "Forester", "Outback", "Ascent",
    "BRZ"
  ],
  Tesla: [
    "Model 3", "Model Y", "Model S", "Model X", "Cybertruck"
  ],
  Toyota: [
    "Yaris", "Corolla", "Camry", "Avalon",
    "Prius",
    "C-HR", "RAV4", "Highlander", "4Runner", "Sequoia",
    "Sienna",
    "Tacoma", "Tundra",
    "GR86", "Supra"
  ],
  Volkswagen: [
    "Golf", "Jetta", "Passat", "Arteon",
    "Tiguan", "Atlas", "Atlas Cross Sport",
    "ID.4"
  ],
  Volvo: [
    "S60", "S90",
    "V60", "V90",
    "XC40", "XC60", "XC90",
    "EX30", "EX90"
  ]
};

function populateCountrySelect(selectEl) {
  selectEl.innerHTML = "";
  const placeholder = document.createElement("option");
  placeholder.value = "";
  placeholder.textContent = "Select country/region";
  placeholder.disabled = true;
  placeholder.selected = true;
  selectEl.appendChild(placeholder);

  REGION_OPTIONS.forEach((item) => {
    const option = document.createElement("option");
    option.value = item.iso;
    option.textContent = item.name;
    selectEl.appendChild(option);
  });
}

function populatePhoneCodeSelect(selectEl) {
  selectEl.innerHTML = "";
  const placeholder = document.createElement("option");
  placeholder.value = "";
  placeholder.textContent = "Country code";
  placeholder.disabled = true;
  placeholder.selected = true;
  selectEl.appendChild(placeholder);

  REGION_OPTIONS.forEach((item) => {
    const option = document.createElement("option");
    option.value = item.phoneCode;
    option.textContent = `${item.phoneCode} (${item.name})`;
    selectEl.appendChild(option);
  });
}

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
  const selectedMonth = parseInt(?.value || "", 10);
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

function populateVehicleSelects() {
  if (!vehicleMakeSelect || !vehicleModelSelect || !vehicleYearSelect) return;

  vehicleMakeSelect.innerHTML = "";
  const makePlaceholder = document.createElement("option");
  makePlaceholder.value = "";
  makePlaceholder.textContent = "Select manufacturer";
  makePlaceholder.disabled = true;
  makePlaceholder.selected = true;
  vehicleMakeSelect.appendChild(makePlaceholder);

  Object.keys(VEHICLE_MODELS).forEach((make) => {
    const option = document.createElement("option");
    option.value = make;
    option.textContent = make;
    vehicleMakeSelect.appendChild(option);
  });

  const currentYear = new Date().getFullYear();
  const startYear = 1980;
  vehicleYearSelect.innerHTML = "";
  const yearPlaceholder = document.createElement("option");
  yearPlaceholder.value = "";
  yearPlaceholder.textContent = "Select year";
  yearPlaceholder.disabled = true;
  yearPlaceholder.selected = true;
  vehicleYearSelect.appendChild(yearPlaceholder);

  for (let year = currentYear; year >= startYear; year -= 1) {
    const option = document.createElement("option");
    option.value = String(year);
    option.textContent = String(year);
    vehicleYearSelect.appendChild(option);
  }

  const resetModelOptions = (selectedMake) => {
    vehicleModelSelect.innerHTML = "";
    const modelPlaceholder = document.createElement("option");
    modelPlaceholder.value = "";
    modelPlaceholder.textContent = "Select model";
    modelPlaceholder.disabled = true;
    modelPlaceholder.selected = true;
    vehicleModelSelect.appendChild(modelPlaceholder);

    const models = VEHICLE_MODELS[selectedMake] || [];
    models.forEach((model) => {
      const option = document.createElement("option");
      option.value = model;
      option.textContent = model;
      vehicleModelSelect.appendChild(option);
    });
  };

  resetModelOptions("");

  vehicleMakeSelect.addEventListener("change", (event) => {
    resetModelOptions(event.target.value);
  });
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
    return;
  }

  if (context === "account" && accountCaptchaDisplay) {
    accountCaptchaDisplay.textContent = code;
  }
}

function clearCaptchaInput(context) {
  if (context === "register" && captchaInput) {
    captchaInput.value = "";
    return;
  }

  if (context === "reset" && resetCaptchaInput) {
    resetCaptchaInput.value = "";
    return;
  }

  if (context === "account" && accountCaptchaInput) {
    accountCaptchaInput.value = "";
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

function setNavSignoutVisibility(isVisible) {
  if (navSignoutItem) {
    navSignoutItem.classList.toggle("hidden", !isVisible);
  }
}

function setupCaptchaControls() {
  const controls = [
    { refresh: refreshCaptchaButton, context: "register" },
    { refresh: resetRefreshCaptchaButton, context: "reset" },
    { refresh: accountRefreshCaptchaButton, context: "account" },
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

  if (!EMAIL_PATTERN.test(email || "")) {
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
  currentUser = newUser;
  setSession(newUser);
  showMessage(authMessage, "Registration successful!", "success");
  enterLicenseMode();
  showMessage(
    licenseMessage,
    "Registration successful! You can start adding vehicles now.",
    "success"
  );
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
  accountSection?.classList.add("hidden");
  querySection?.classList.add("hidden");
  setNavSignoutVisibility(true);
  licenseForm?.reset();
  populateVehicleSelects();
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
  accountSection?.classList.add("hidden");
  querySection?.classList.add("hidden");
  authShell.classList.remove("hidden");
  setNavSignoutVisibility(false);
  showLoginView();
  showMessage(authMessage, "You have signed out.", "success");
}

function populateAccountFields() {
  if (!currentUser) return;
  if (accountEmailInput) {
    accountEmailInput.value = currentUser.email || "";
  }
  if (accountPhoneCountrySelect) {
    accountPhoneCountrySelect.value = currentUser.phoneCountry || "";
  }
  if (accountPhoneInput) {
    accountPhoneInput.value = currentUser.phone || "";
  }
  if (accountCurrentPasswordInput) {
    accountCurrentPasswordInput.value = "";
  }
  if (accountOldPasswordInput) {
    accountOldPasswordInput.value = "";
  }
  if (accountNewPasswordInput) {
    accountNewPasswordInput.value = "";
  }
  if (accountConfirmPasswordInput) {
    accountConfirmPasswordInput.value = "";
  }
  if (accountCaptchaInput) {
    accountCaptchaInput.value = "";
  }
}

function enterAccountMode() {
  if (!currentUser) {
    setNavSignoutVisibility(false);
    licenseSection?.classList.add("hidden");
    accountSection?.classList.add("hidden");
    authShell?.classList.remove("hidden");
    showLoginView();
    return;
  }

  authShell?.classList.add("hidden");
  licenseSection?.classList.add("hidden");
  accountSection?.classList.remove("hidden");
  querySection?.classList.add("hidden");
  setNavSignoutVisibility(true);
  populateAccountFields();
  generateCaptcha("account");
  showMessage(accountContactMessage, "");
  showMessage(accountPasswordMessage, "");
}

function enterQueryMode() {
  if (!currentUser) {
    setNavSignoutVisibility(false);
    licenseSection?.classList.add("hidden");
    accountSection?.classList.add("hidden");
    querySection?.classList.add("hidden");
    authShell?.classList.remove("hidden");
    showLoginView();
    return;
  }

  authShell?.classList.add("hidden");
  licenseSection?.classList.add("hidden");
  accountSection?.classList.add("hidden");
  querySection?.classList.remove("hidden");
  setNavSignoutVisibility(true);
  showMessage(queryMessage, "");
  queryForm?.reset();
  if (queryLicenseInput) {
    queryLicenseInput.focus();
  }
}


function handleLicenseSubmit(event) {
  event.preventDefault();
  const licenseNumber = document
    .getElementById("license-number")
    .value.trim()
    .toUpperCase();
  const vehicleMake = vehicleMakeSelect?.value || "";
  const vehicleModel = vehicleModelSelect?.value || "";
  const vehicleYear = vehicleYearSelect?.value || "";

  if (!currentUser) {
    showMessage(licenseMessage, "Please sign in first.", "error");
    return;
  }

  if (!LICENSE_PATTERN.test(licenseNumber)) {
    showMessage(
      licenseMessage,
      "License plate must be 1-7 characters, A-Z, 0-9, or hyphen.",
      "error"
    );
    return;
  }

  if (!vehicleMake || !vehicleModel || !vehicleYear) {
    showMessage(licenseMessage, "Please select make, model, and year.", "error");
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
    make: vehicleMake,
    model: vehicleModel,
    year: vehicleYear,
    blacklisted: false,
    createdAt: new Date().toISOString(),
  };

  userLicenses.push(entry);
  licenses[currentUser.username] = userLicenses;
  saveLicenses(licenses);
  showMessage(licenseMessage, "License plate saved successfully!", "success");
  licenseForm.reset();
  populateVehicleSelects();
  refreshLicenseList();
}

function handleQuerySubmit(event) {
  event.preventDefault();

  if (!currentUser) {
    showLoginView();
    return;
  }

  const raw = (queryLicenseInput?.value || "").trim().toUpperCase();
  if (!LICENSE_PATTERN.test(raw)) {
    showMessage(
      queryMessage,
      "Enter a valid license plate (1–7 chars, A–Z, 0–9, or hyphen).",
      "error"
    );
    return;
  }

  const licenses = readLicenses();
  let found = false;
  let blacklisted = false;

  Object.values(licenses).forEach((userLicenses) => {
    (userLicenses || []).forEach((entry) => {
      if (entry.licenseNumber === raw) {
        found = true;
        if (entry.blacklisted) {
          blacklisted = true;
        }
      }
    });
  });

  if (!found) {
    showMessage(
      queryMessage,
      `License ${raw} was not found in the system.`,
      "success"
    );
  } else if (blacklisted) {
    showMessage(
      queryMessage,
      `License ${raw} is currently blacklisted.`,
      "success"
    );
  } else {
    showMessage(
      queryMessage,
      `License ${raw} is not blacklisted.`,
      "success"
    );
  }
}

function handleAccountContactSubmit(event) {
  event.preventDefault();
  if (!currentUser) {
    showLoginView();
    return;
  }

  const email = accountEmailInput?.value.trim();
  const lowerEmail = (email || "").toLowerCase();
  const phoneCountry = accountPhoneCountrySelect?.value || "";
  const phoneDigits = (accountPhoneInput?.value || "").replace(/\D/g, "");
  const currentPassword = accountCurrentPasswordInput?.value || "";

  if (currentPassword !== currentUser.password) {
    showMessage(accountContactMessage, "Incorrect current password.", "error");
    return;
  }

  if (!EMAIL_PATTERN.test(lowerEmail)) {
    showMessage(accountContactMessage, "Enter a valid email address.", "error");
    return;
  }

  const users = readUsers();
  const emailTaken = users.some(
    (user) => user.username !== currentUser.username && user.email?.toLowerCase() === lowerEmail
  );

  if (emailTaken) {
    showMessage(accountContactMessage, "That email is already in use.", "error");
    return;
  }

  if (!phoneCountry || phoneDigits.length < 5) {
    showMessage(accountContactMessage, "Enter a valid phone number and country code.", "error");
    return;
  }

  const phoneSignature = `${phoneCountry.replace(/\D/g, "")}${phoneDigits}`;
  const phoneTaken = users.some((user) => {
    if (user.username === currentUser.username) return false;
    const storedDigits = String(user.phone || "").replace(/\D/g, "");
    const storedCountry = String(user.phoneCountry || "").replace(/\D/g, "");
    return storedDigits && storedCountry + storedDigits === phoneSignature;
  });

  if (phoneTaken) {
    showMessage(accountContactMessage, "That phone number is already in use.", "error");
    return;
  }

  const oldUsername = currentUser.username;
  currentUser.email = lowerEmail;
  currentUser.username = lowerEmail;
  currentUser.phoneCountry = phoneCountry;
  currentUser.phone = phoneDigits;

  const updatedUsers = users.map((user) =>
    user.username === oldUsername ? { ...user, ...currentUser } : user
  );
  saveUsers(updatedUsers);

  const licenses = readLicenses();
  if (lowerEmail !== oldUsername && licenses[oldUsername]) {
    licenses[lowerEmail] = licenses[oldUsername];
    delete licenses[oldUsername];
    saveLicenses(licenses);
  } else if (lowerEmail === oldUsername) {
    saveLicenses(licenses);
  }

  setSession(currentUser);
  populateAccountFields();
  showMessage(accountContactMessage, "Contact information updated successfully.", "success");
}

function handleAccountPasswordSubmit(event) {
  event.preventDefault();
  if (!currentUser) {
    showLoginView();
    return;
  }

  const oldPassword = accountOldPasswordInput?.value || "";
  const newPassword = accountNewPasswordInput?.value || "";
  const confirmPassword = accountConfirmPasswordInput?.value || "";
  const captchaValue = accountCaptchaInput?.value || "";

  if (oldPassword !== currentUser.password) {
    showMessage(accountPasswordMessage, "Incorrect current password.", "error");
    return;
  }

  if (!PASSWORD_PATTERN.test(newPassword)) {
    showMessage(
      accountPasswordMessage,
      "Password must be at least 8 characters with upper, lower, and a number.",
      "error"
    );
    return;
  }

  if (newPassword !== confirmPassword) {
    showMessage(accountPasswordMessage, "New passwords do not match.", "error");
    return;
  }

  if (captchaValue.trim().toUpperCase() !== (captchaState.account || "")) {
    showMessage(accountPasswordMessage, "Incorrect verification code.", "error");
    generateCaptcha("account");
    return;
  }

  currentUser.password = newPassword;
  const users = readUsers().map((user) =>
    user.username === currentUser.username ? { ...user, password: newPassword } : user
  );
  saveUsers(users);
  generateCaptcha("account");
  populateAccountFields();
  showMessage(accountPasswordMessage, "Password changed successfully.", "success");
}

function handleAccountDelete() {
  if (!currentUser) {
    showLoginView();
    return;
  }

  const confirmation = window.confirm(
    "Deleting your account will remove all data in this browser. This cannot be undone. Continue?"
  );

  if (!confirmation) return;

  const users = readUsers().filter((user) => user.username !== currentUser.username);
  saveUsers(users);

  const licenses = readLicenses();
  delete licenses[currentUser.username];
  saveLicenses(licenses);

  setSession(null);
  currentUser = null;
  licenseSection?.classList.add("hidden");
  accountSection?.classList.add("hidden");
  querySection?.classList.add("hidden");
  authShell?.classList.remove("hidden");
  setNavSignoutVisibility(false);
  showLoginView();
  showMessage(authMessage, "Your account has been deleted.", "success");
}

function refreshLicenseList() {
  if (!currentUser) return;
  if (!vehiclesTableBody) return;
  vehiclesTableBody.innerHTML = "";
  const licenses = readLicenses();
  const userLicenses = licenses[currentUser.username] || [];

  if (userLicenses.length === 0) {
    const row = document.createElement("tr");
    const cell = document.createElement("td");
    cell.colSpan = 6;
    cell.textContent = "No vehicles registered yet.";
    row.appendChild(cell);
    vehiclesTableBody.appendChild(row);
    return;
  }

  userLicenses
    .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
    .forEach((item) => {
      const row = document.createElement("tr");
      const status = item.blacklisted ? "Blacklisted" : "Not blacklisted";

      const cells = [
        item.licenseNumber,
        item.make || "",
        item.model || "",
        item.year || "",
        status,
      ];

      cells.forEach((value) => {
        const cell = document.createElement("td");
        cell.textContent = value;
        row.appendChild(cell);
      });

      const actionCell = document.createElement("td");
      const removeButton = document.createElement("button");
      removeButton.textContent = "Remove";
      removeButton.addEventListener("click", () => removeLicense(item.licenseNumber));
      actionCell.appendChild(removeButton);
      row.appendChild(actionCell);

      vehiclesTableBody.appendChild(row);
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

function handleNavVehicles(event) {
  event.preventDefault();
  if (currentUser) {
    enterLicenseMode();
    return;
  }

  setNavSignoutVisibility(false);
  if (licenseSection) {
    licenseSection.classList.add("hidden");
  }
  if (accountSection) {
    accountSection.classList.add("hidden");
  }
  if (querySection) {
    querySection.classList.add("hidden");
  }
  if (authShell) {
    authShell.classList.remove("hidden");
  }
  showLoginView();
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

// My Vehicles
navVehiclesLink?.addEventListener("click", (event) => {
  event.preventDefault();
  handleNavVehicles(event);
});

// My Account
navAccountLink?.addEventListener("click", (event) => {
  event.preventDefault();
  enterAccountMode();
});

// Query
navQueryLink?.addEventListener("click", (event) => {
  event.preventDefault();
  enterQueryMode();
});

// Sign out
navSignoutLink?.addEventListener("click", (event) => {
  event.preventDefault();
  exitLicenseMode();
});

// Account forms
accountContactForm?.addEventListener("submit", handleAccountContactSubmit);
accountPasswordForm?.addEventListener("submit", handleAccountPasswordSubmit);
accountDeleteButton?.addEventListener("click", (event) => {
  event.preventDefault();
  handleAccountDelete();
});

// Query form
queryForm?.addEventListener("submit", handleQuerySubmit);

navAccountLink?.addEventListener("click", (event) => {
  event.preventDefault();
  enterAccountMode();
});
navQueryLink?.addEventListener("click", (event) => {
  event.preventDefault();
  enterQueryMode();
});
navSignoutLink?.addEventListener("click", (event) => {
  event.preventDefault();
  exitLicenseMode();
});
accountContactForm?.addEventListener("submit", handleAccountContactSubmit);
accountPasswordForm?.addEventListener("submit", handleAccountPasswordSubmit);
accountDeleteButton?.addEventListener("click", (event) => {
  event.preventDefault();
  handleAccountDelete();
});

queryForm?.addEventListener("submit", handleQuerySubmit);

document.addEventListener("DOMContentLoaded", () => {

  if (registerCountrySelect) {
    populateCountrySelect(registerCountrySelect);
  }
  if (registerPhoneCountrySelect) {
    populatePhoneCodeSelect(registerPhoneCountrySelect);
  }
  if (accountPhoneCountrySelect) {
    populatePhoneCodeSelect(accountPhoneCountrySelect);
  }
  
  populateBirthSelects();
  populateVehicleSelects();
  setupCaptchaControls();
  generateCaptcha("register");
  generateCaptcha("reset");
  generateCaptcha("account");
  showLoginView();
  setNavSignoutVisibility(false);

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
