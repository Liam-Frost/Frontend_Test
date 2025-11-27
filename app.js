const STORAGE_KEYS = {
  users: "ft_users",
  licenses: "ft_licenses",
  session: "ft_session",
};

const API_BASE = window.APP_CONFIG?.API_BASE || "/api";

async function apiRequest(path, options = {}) {
  const base = (window.APP_CONFIG && window.APP_CONFIG.API_BASE) || "";
  const response = await fetch(base + path, {
    headers: {
      "Content-Type": "application/json",
      ...(options.headers || {}),
    },
    ...options,
  });
  return response;
}

async function api(path, options = {}) {
  const response = await apiRequest(path, options);

  const isJson = response.headers
    .get("Content-Type")
    ?.includes("application/json");

  const body = isJson ? await response.json() : null;

  if (!response.ok) {
    const message =
      body?.message || `Request failed with status ${response.status}`;
    throw new Error(message);
  }

  return body;
}

const loginForm = document.getElementById("login-form");
const loginIdentifierInput = document.getElementById("login-username");
const loginPasswordInput = document.getElementById("login-password");
const loginPrimaryButton = loginForm?.querySelector(".primary-button");
const registerForm = document.getElementById("register-form");
const resetForm = document.getElementById("reset-form");
const authMessage = document.getElementById("auth-message");
const formTitle = document.getElementById("form-title");
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
const PASSWORD_PATTERN = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[A-Za-z\d]{8,20}$/;
const REGION_OPTIONS = [
  { iso: "US", name: "United States", phoneCode: "+1" },
  { iso: "CA", name: "Canada", phoneCode: "+1" },
  { iso: "GB", name: "United Kingdom", phoneCode: "+44" },
  { iso: "AU", name: "Australia", phoneCode: "+61" },
  { iso: "NZ", name: "New Zealand", phoneCode: "+64" },

  { iso: "CN", name: "China", phoneCode: "+86" },
  { iso: "HK", name: "Hong Kong SAR", phoneCode: "+852" },
  { iso: "MO", name: "Macao SAR", phoneCode: "+853" },
  { iso: "TW", name: "Taiwan", phoneCode: "+886" },
  { iso: "JP", name: "Japan", phoneCode: "+81" },
  { iso: "KR", name: "South Korea", phoneCode: "+82" },

  { iso: "DE", name: "Germany", phoneCode: "+49" },
  { iso: "FR", name: "France", phoneCode: "+33" },
  { iso: "IT", name: "Italy", phoneCode: "+39" },
  { iso: "ES", name: "Spain", phoneCode: "+34" },
  { iso: "PT", name: "Portugal", phoneCode: "+351" },
  { iso: "NL", name: "Netherlands", phoneCode: "+31" },
  { iso: "BE", name: "Belgium", phoneCode: "+32" },
  { iso: "CH", name: "Switzerland", phoneCode: "+41" },
  { iso: "AT", name: "Austria", phoneCode: "+43" },
  { iso: "DK", name: "Denmark", phoneCode: "+45" },
  { iso: "NO", name: "Norway", phoneCode: "+47" },
  { iso: "SE", name: "Sweden", phoneCode: "+46" },
  { iso: "FI", name: "Finland", phoneCode: "+358" },
  { iso: "IS", name: "Iceland", phoneCode: "+354" },
  { iso: "IE", name: "Ireland", phoneCode: "+353" },
  { iso: "RU", name: "Russia", phoneCode: "+7" },
  { iso: "UA", name: "Ukraine", phoneCode: "+380" },
  { iso: "PL", name: "Poland", phoneCode: "+48" },
  { iso: "CZ", name: "Czech Republic", phoneCode: "+420" },
  { iso: "SK", name: "Slovakia", phoneCode: "+421" },
  { iso: "HU", name: "Hungary", phoneCode: "+36" },
  { iso: "RO", name: "Romania", phoneCode: "+40" },
  { iso: "BG", name: "Bulgaria", phoneCode: "+359" },
  { iso: "GR", name: "Greece", phoneCode: "+30" },
  { iso: "TR", name: "Turkey", phoneCode: "+90" },

  { iso: "IL", name: "Israel", phoneCode: "+972" },
  { iso: "SA", name: "Saudi Arabia", phoneCode: "+966" },
  { iso: "AE", name: "United Arab Emirates", phoneCode: "+971" },
  { iso: "QA", name: "Qatar", phoneCode: "+974" },
  { iso: "KW", name: "Kuwait", phoneCode: "+965" },
  { iso: "BH", name: "Bahrain", phoneCode: "+973" },
  { iso: "OM", name: "Oman", phoneCode: "+968" },

  { iso: "EG", name: "Egypt", phoneCode: "+20" },
  { iso: "ZA", name: "South Africa", phoneCode: "+27" },
  { iso: "NG", name: "Nigeria", phoneCode: "+234" },
  { iso: "KE", name: "Kenya", phoneCode: "+254" },

  { iso: "IN", name: "India", phoneCode: "+91" },
  { iso: "PK", name: "Pakistan", phoneCode: "+92" },
  { iso: "BD", name: "Bangladesh", phoneCode: "+880" },
  { iso: "LK", name: "Sri Lanka", phoneCode: "+94" },

  { iso: "ID", name: "Indonesia", phoneCode: "+62" },
  { iso: "MY", name: "Malaysia", phoneCode: "+60" },
  { iso: "SG", name: "Singapore", phoneCode: "+65" },
  { iso: "TH", name: "Thailand", phoneCode: "+66" },
  { iso: "VN", name: "Vietnam", phoneCode: "+84" },
  { iso: "PH", name: "Philippines", phoneCode: "+63" },

  { iso: "AR", name: "Argentina", phoneCode: "+54" },
  { iso: "BR", name: "Brazil", phoneCode: "+55" },
  { iso: "CL", name: "Chile", phoneCode: "+56" },
  { iso: "CO", name: "Colombia", phoneCode: "+57" },
  { iso: "PE", name: "Peru", phoneCode: "+51" },
  { iso: "UY", name: "Uruguay", phoneCode: "+598" },
  { iso: "VE", name: "Venezuela", phoneCode: "+58" },
  { iso: "MX", name: "Mexico", phoneCode: "+52" }
];
const VEHICLE_MODELS = {
  Acura: [
    "ILX", "TLX", "RLX",
    "RDX", "MDX"
  ],
  Audi: [
    "A3", "A4", "A5", "A6", "A7", "A8",
    "R3", "R4", "R5", "R6", "R7", "R8",
    "RS3", "RS4", "RS5", "RS6", "RS7",
    "Q3", "Q5", "Q7", "Q8",
    "e-tron", "Q4 e-tron"
  ],
  BMW: [
    "1 Series", "2 Series", "3 Series", "4 Series", "5 Series", "7 Series", "8 Series",
    "X1", "X2", "X3", "X4", "X5", "X6", "X7",
    "M1", "M2", "M3", "M4", "M5", "M6", "M7", "M8",
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
    "CPEN", "ELEC", "ENPH",
    "BMEG", "CVIL", "MECH",
    "MANU", "ENVL", "MTRL",
    "CHBE", "MINE", "IGEN"
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
  MBTI: [
    "INTJ", "INTP", "ENTJ", "ENTP",
    "INFJ", "INFP", "ENFJ", "ENFP",
    "ISTJ", "ISTJ", "ESTJ", "ESFJ",
    "ISTP", "ISFP", "ESTP", "ESFP",
    "REST"
  ],
  "Mercedes-Benz": [
    "A-Class", "B-Class", "C-Class", "E-Class", "S-Class",
    "CLA", "CLS",
    "GLA", "GLB", "GLC", "GLE", "GLS", "G-Class",
    "EQE", "EQS"
  ],
  "Mercedes-Maybach": [
    "S-Class",
    "GLS",
    "EQS SUV",
    "Pullman"
  ],
  "Mercedes-AMG": [
    "A-Class", "C-Class", "E-Class", "S-Class",
    "CLA",
    "GT",
    "SL",
    "G-Class", "GLA", "GLB", "GLC", "GLE", "GLS",
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
    "911", "918",
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
const ringCfg = {
  layers: 4,
  baseRadiusRatio: 0.26,
  gap: 13,
  baseDots: 25,
  dotsDelta: -0.11,
  dotSizeInner: 3.8,
  dotSizeOuter: 5.8,
  ringStartAngle: (-10 * Math.PI / 180),
};

function drawParallaxRingLogo() {
  const canvas = document.getElementById("parallax-ring-mask");
  if (!canvas) return;

  const DPR = Math.max(1, Math.min(2, window.devicePixelRatio || 1));
  const rect = canvas.getBoundingClientRect();
  const size = Math.min(rect.width, rect.height) || 80;

  canvas.width = Math.floor(size * DPR);
  canvas.height = Math.floor(size * DPR);

  const ctx = canvas.getContext("2d");
  ctx.setTransform(DPR, 0, 0, DPR, 0, 0);

  const cx = size / 2;
  const cy = size / 2;

  ctx.globalCompositeOperation = "source-over";
  ctx.clearRect(0, 0, size, size);
  ctx.fillStyle = "#fff";
  ctx.fillRect(0, 0, size, size);
  ctx.globalCompositeOperation = "destination-out";

  const baseR = size * ringCfg.baseRadiusRatio;

  const lerp = (a, b, t) => a + (b - a) * t;

  for (let layer = 0; layer < ringCfg.layers; layer += 1) {
    const t = ringCfg.layers === 1 ? 0 : layer / (ringCfg.layers - 1);
    const r = baseR + layer * ringCfg.gap;
    const count = Math.max(3, Math.round(ringCfg.baseDots + layer * ringCfg.dotsDelta));
    const step = (Math.PI * 2) / count;
    const sizeDot = lerp(ringCfg.dotSizeInner, ringCfg.dotSizeOuter, t);
    const a0 = ringCfg.ringStartAngle + (layer % 2 ? step * 0.5 : 0);

    for (let i = 0; i < count; i += 1) {
      const a = a0 + i * step;
      const x = cx + r * Math.cos(a);
      const y = cy + r * Math.sin(a);
      ctx.beginPath();
      ctx.arc(x, y, sizeDot, 0, Math.PI * 2);
      ctx.fill();
    }
  }

  ctx.globalCompositeOperation = "source-over";
}

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

async function fetchUserVehicles(username) {
  const res = await apiRequest(
    `/vehicles?username=${encodeURIComponent(username || "")}`
  );
  if (!res.ok) {
    throw new Error("Failed to fetch vehicles");
  }
  return res.json();
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
  const selectedMonth = parseInt(monthSelect ? monthSelect.value : "", 10);
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

async function handleRegister(event) {
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

  try {
    const res = await apiRequest("/auth/register", {
      method: "POST",
      body: JSON.stringify({
        email,
        password,
        firstName,
        lastName,
        country,
        birthMonth: month,
        birthDay: day,
        birthYear: year,
        phoneCountry,
        phone: phoneDigits,
        contactMethod,
      }),
    });

    if (res.status === 201) {
      const lowerEmail = (email || "").toLowerCase();
      const displayName =
        [firstName, lastName].filter(Boolean).join(" ") || lowerEmail;
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

      // Temporary local storage until full backend persistence is wired everywhere
      const users = readUsers();
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
      return;
    }

    if (res.status === 409) {
      const body = await res.json().catch(() => ({}));
      const code = body?.message || "";
      if (code === "EMAIL_EXISTS") {
        showMessage(
          authMessage,
          "That email is already registered. Please sign in instead.",
          "error"
        );
        return;
      }
      if (code === "PHONE_EXISTS") {
        showMessage(
          authMessage,
          "That phone number is already registered. Please sign in instead.",
          "error"
        );
        return;
      }
      showMessage(authMessage, "Registration already exists.", "error");
      return;
    }

    showMessage(
      authMessage,
      "Unable to register right now. Please try again soon.",
      "error"
    );
  } catch (error) {
    console.error(error);
    showMessage(
      authMessage,
      error?.message || "Unable to register right now. Please try again.",
      "error"
    );
  }
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

async function handleLogin(event) {
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

  try {
    const result = await api("/auth/login", {
      method: "POST",
      body: JSON.stringify({
        identifier,
        password,
      }),
    });

    if (!result?.success) {
      showMessage(
        authMessage,
        result?.message || "Incorrect username or password.",
        "error"
      );
      return;
    }

    // For now, keep session handling on the front-end side
    // Use the backend response to set currentUser + session
    const users = readUsers();
    const lower = (result.username || identifier).toLowerCase();
    let user = users.find((u) => u.username === lower || u.email === lower);

    if (!user) {
      // If this is purely backend-authenticated, we can create a shell user object
      user = {
        username: lower,
        email: lower,
        displayName: result.displayName || lower,
      };
      users.push(user);
      saveUsers(users);
    }

    currentUser = user;
    setSession(user);
    loginForm.reset();
    setLoginStage("identifier");
    enterLicenseMode();
  } catch (error) {
    console.error(error);
    showMessage(
      authMessage,
      error.message || "Unable to sign in right now.",
      "error"
    );
  }
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


async function handleLicenseSubmit(event) {
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

  try {
    const res = await apiRequest("/vehicles", {
      method: "POST",
      body: JSON.stringify({
        username: currentUser.username,
        licenseNumber,
        make: vehicleMake,
        model: vehicleModel,
        year: vehicleYear,
      }),
    });

    if (res.status === 201) {
      showMessage(licenseMessage, "License plate saved successfully!", "success");
      licenseForm.reset();
      populateVehicleSelects();
      await refreshLicenseList();
      return;
    }

    const body = await res.json().catch(() => ({}));
    const code = body?.message || "";
    if (res.status === 409 && code === "LICENSE_EXISTS") {
      showMessage(licenseMessage, "That license plate is already registered.", "error");
      return;
    }

    showMessage(
      licenseMessage,
      body?.message || "Unable to save license plate right now.",
      "error"
    );
  } catch (error) {
    console.error(error);
    showMessage(
      licenseMessage,
      error?.message || "Unable to save license plate right now.",
      "error"
    );
  }
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

async function refreshLicenseList() {
  if (!currentUser) return;
  if (!vehiclesTableBody) return;
  vehiclesTableBody.innerHTML = "";

  try {
    const vehicles = await fetchUserVehicles(currentUser.username);
    const licenses = readLicenses();
    licenses[currentUser.username] = vehicles;
    saveLicenses(licenses);

    renderVehicleRows(vehicles);
  } catch (error) {
    console.error("Failed to fetch vehicles from API", error);
    const fallbackLicenses = readLicenses();
    const userLicenses = fallbackLicenses[currentUser.username] || [];
    renderVehicleRows(userLicenses);
  }
}

function renderVehicleRows(vehicles) {
  if (!vehiclesTableBody) return;
  const sorted = [...vehicles].sort(
    (a, b) => new Date(b.createdAt || 0) - new Date(a.createdAt || 0)
  );

  if (sorted.length === 0) {
    const row = document.createElement("tr");
    const cell = document.createElement("td");
    cell.colSpan = 6;
    cell.textContent = "No vehicles registered yet.";
    row.appendChild(cell);
    vehiclesTableBody.appendChild(row);
    return;
  }

  sorted.forEach((item) => {
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

async function removeLicense(licenseNumber) {
  if (!currentUser) return;

  try {
    const res = await apiRequest("/vehicles", {
      method: "DELETE",
      body: JSON.stringify({
        username: currentUser.username,
        licenseNumber,
      }),
    });

    if (res.status === 204) {
      await refreshLicenseList();
      showMessage(licenseMessage, "License plate removed.", "success");
      return;
    }

    const body = await res.json().catch(() => ({}));
    const errorMsg = body?.message || "Unable to remove license plate.";
    showMessage(licenseMessage, errorMsg, "error");
  } catch (error) {
    console.error(error);
    showMessage(
      licenseMessage,
      error?.message || "Unable to remove license plate.",
      "error"
    );
  }
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

  drawParallaxRingLogo();
  window.addEventListener("resize", drawParallaxRingLogo);

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
