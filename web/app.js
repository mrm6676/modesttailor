const STORAGE_KEYS = {
  profile: "modest_tailor_profile_v1",
  closet: "modest_tailor_closet_v1",
  feedback: "modest_tailor_feedback_v1"
};

const CATALOG = {
  audiences: ["Ladies", "Girls", "Teens", "Ladies & Girls"],
  necklines: [
    "High Crew / Mock Neck",
    "Boat / Collar Neckline",
    "Hijab-Friendly High Neck"
  ],
  sleeves: [
    "Full Wrist-Length",
    "Three-Quarter (3/4) Sleeve",
    "Layered Long Sleeve"
  ],
  hemlines: [
    "Maxi / Floor-Length",
    "Ankle-Length",
    "Midi Below-Knee"
  ],
  fits: [
    "Flowy & Relaxed Drape",
    "Structured Loose Tailoring",
    "A-Line Tiered Silhouette"
  ],
  trendTags: [
    "Quiet Luxury Linen",
    "Pleated Satin Movement",
    "Tiered Cottagecore",
    "Utility Longline Trench",
    "Monochrome Minimal",
    "Soft Pastel Co-ords",
    "Occasion Abaya & Kaftan",
    "Modest Campus Denim"
  ],
  colors: [
    { name: "Rosewood", hex: "#6e3b49" },
    { name: "Silk Ivory", hex: "#faf7f2" },
    { name: "Sage Olive", hex: "#4c6455" },
    { name: "Warm Camel", hex: "#c29b6f" },
    { name: "Dusty Mauve", hex: "#b88a95" },
    { name: "Chambray Blue", hex: "#6c87a3" },
    { name: "Espresso", hex: "#4a342e" },
    { name: "Champagne Gold", hex: "#dec395" }
  ],
  occasions: [
    "Everyday Chic",
    "Work & Studio",
    "School & Campus",
    "Festive & Celebration",
    "Weekend Garden Brunch"
  ],
  seasons: [
    "Crisp Autumn Layering",
    "Warm Sunlit Spring",
    "Breezy Summer Linen",
    "Cozy Winter Wool & Knit"
  ],
  ladiesSizes: ["XS", "S", "M", "L", "XL", "XXL", "Plus 1X-3X"],
  abayaLengths: ["50", "52", "54", "56", "58", "60"],
  girlsTeensSizes: [
    "Not Needed",
    "Girls 4-6",
    "Girls 7-8",
    "Girls 10-12",
    "Girls 14",
    "Teens XS",
    "Teens S",
    "Teens M",
    "Teens L"
  ],
  closetFilters: ["All", "Favorites", "Ladies", "Girls", "Teens"],
  quickStyleTags: [
    "Summer",
    "Formal",
    "Work",
    "Casual",
    "Festive",
    "Spring",
    "Autumn",
    "Winter",
    "Hijab-Friendly"
  ]
};

const STARTER_BOUTIQUE_ITEMS = [
  {
    id: "inv-1",
    name: "Rosewood Sunburst Pleated Satin Maxi Skirt",
    category: "Bottom",
    audience: "Ladies",
    boutiqueName: "Atelier Noor",
    priceUsd: 88,
    color: "Rosewood",
    sizeRange: "XS - XXL",
    coverageSummary: "Full ankle-length opaque satin lining, non-cling fluid pleats, high elastic back waist",
    trendTag: "Pleated Satin Movement",
    occasions: ["Work & Studio", "Festive & Celebration", "Weekend Garden Brunch"],
    seasons: ["Crisp Autumn Layering", "Warm Sunlit Spring"]
  },
  {
    id: "inv-2",
    name: "Silk Ivory Bishop-Sleeve Mock-Neck Blouse",
    category: "Top",
    audience: "Ladies",
    boutiqueName: "Atelier Noor",
    priceUsd: 74,
    color: "Silk Ivory",
    sizeRange: "XS - XL",
    coverageSummary: "High mock collar, full wrist-buttoned bishop sleeves, double-lined bodice",
    trendTag: "Quiet Luxury Linen",
    occasions: ["Everyday Chic", "Work & Studio", "Festive & Celebration"],
    seasons: ["Crisp Autumn Layering", "Warm Sunlit Spring", "Breezy Summer Linen"]
  },
  {
    id: "inv-3",
    name: "Sage Belted Longline Linen Trench Coat",
    category: "Outerwear",
    audience: "Ladies",
    boutiqueName: "Olive & Loom Boutique",
    priceUsd: 135,
    color: "Sage Olive",
    sizeRange: "S - XXL",
    coverageSummary: "Calf-skimming relaxed duster cut, full storm cuffs, breathable heavyweight flax linen",
    trendTag: "Utility Longline Trench",
    occasions: ["Work & Studio", "Everyday Chic", "Weekend Garden Brunch"],
    seasons: ["Crisp Autumn Layering", "Warm Sunlit Spring", "Breezy Summer Linen"]
  },
  {
    id: "inv-4",
    name: "Champagne Modal Crinkle Hijab & Shawl Set",
    category: "Headwear & Accessory",
    audience: "Ladies",
    boutiqueName: "Atelier Noor",
    priceUsd: 28,
    color: "Champagne Gold",
    sizeRange: "One Size (190x75cm)",
    coverageSummary: "Generous chest and shoulder drape, non-slip breathable modal weave",
    trendTag: "Monochrome Minimal",
    occasions: ["Everyday Chic", "Work & Studio", "Festive & Celebration", "Weekend Garden Brunch"],
    seasons: ["Crisp Autumn Layering", "Warm Sunlit Spring", "Breezy Summer Linen", "Cozy Winter Wool & Knit"]
  },
  {
    id: "inv-5",
    name: "Girls' Chambray Peter-Pan Collar Tiered Maxi Dress",
    category: "Dress & Abaya",
    audience: "Girls",
    boutiqueName: "Little Cedar Modest Kids",
    priceUsd: 54,
    color: "Chambray Blue",
    sizeRange: "Girls 6 - 12",
    coverageSummary: "High rounded Peter-Pan collar, elasticated full wrist puff sleeves, ankle-length cotton tiers",
    trendTag: "Tiered Cottagecore",
    occasions: ["Weekend Garden Brunch", "Everyday Chic", "School & Campus"],
    seasons: ["Warm Sunlit Spring", "Breezy Summer Linen"]
  },
  {
    id: "inv-6",
    name: "Girls' Dusty Rose Pointelle Long-Sleeve Cardigan",
    category: "Outerwear",
    audience: "Girls",
    boutiqueName: "Little Cedar Modest Kids",
    priceUsd: 38,
    color: "Dusty Mauve",
    sizeRange: "Girls 6 - 14",
    coverageSummary: "Soft combed cotton layer, hip-covering length, full rib-knit cuffs, pearl buttons",
    trendTag: "Soft Pastel Co-ords",
    occasions: ["School & Campus", "Weekend Garden Brunch", "Festive & Celebration"],
    seasons: ["Crisp Autumn Layering", "Warm Sunlit Spring", "Cozy Winter Wool & Knit"]
  },
  {
    id: "inv-7",
    name: "Teens' Relaxed Denim Long Tunic & Wide-Leg Co-ord",
    category: "Set & Co-ord",
    audience: "Teens",
    boutiqueName: "Olive & Loom Boutique",
    priceUsd: 92,
    color: "Chambray Blue",
    sizeRange: "Teens XS - L",
    coverageSummary: "Mid-thigh tunic shirt over loose full-length wide-leg trousers, high crew collar",
    trendTag: "Modest Campus Denim",
    occasions: ["School & Campus", "Everyday Chic"],
    seasons: ["Crisp Autumn Layering", "Warm Sunlit Spring", "Cozy Winter Wool & Knit"]
  },
  {
    id: "inv-8",
    name: "Espresso Satin-Trimmed Open Abaya & Slip Dress",
    category: "Dress & Abaya",
    audience: "Ladies",
    boutiqueName: "Dar Al-Harir",
    priceUsd: 148,
    color: "Espresso",
    sizeRange: "52 - 58 Length",
    coverageSummary: "Floor-skimming opaque crepe inner dress paired with flowing kimono-sleeve outer abaya",
    trendTag: "Occasion Abaya & Kaftan",
    occasions: ["Festive & Celebration", "Work & Studio", "Everyday Chic"],
    seasons: ["Crisp Autumn Layering", "Cozy Winter Wool & Knit", "Warm Sunlit Spring"]
  }
];

const DEFAULT_PROFILE = {
  audience: "Ladies & Girls",
  cityOrArea: "Oakbrook & Promenade",
  necklineRule: "High Crew / Mock Neck",
  sleeveRule: "Full Wrist-Length",
  hemlineRule: "Maxi / Ankle-Length",
  fitRule: "Flowy & Relaxed Drape",
  hijabFriendly: true,
  topSize: "M",
  bottomSize: "M",
  dressAbayaLength: "54",
  girlsSize: "Girls 10-12",
  favoriteColors: ["Rosewood", "Silk Ivory", "Sage Olive"],
  trendTags: ["Quiet Luxury Linen", "Pleated Satin Movement", "Tiered Cottagecore"]
};

const DEFAULT_SAVED_OUTFITS = [
  {
    id: "saved-starter-1",
    title: "Rosewood Pleated Satin & Ivory Studio Look",
    audience: "Ladies",
    occasion: "Work & Studio",
    trendHighlight: "Pleated Satin Movement",
    coverageVerification: "High mock neckline, wrist-buttoned bishop sleeves, and full ankle-length opaque satin lining.",
    stylingNotes: "Pair with soft champagne modal drape for structured boardroom confidence. #work #formal #hijab-friendly",
    matchedPieces: [
      "Rosewood Sunburst Pleated Satin Maxi Skirt",
      "Silk Ivory Bishop-Sleeve Mock-Neck Blouse",
      "Champagne Modal Crinkle Hijab & Shawl Set"
    ],
    boutiqueSources: ["Atelier Noor"],
    favorite: true
  },
  {
    id: "saved-starter-2",
    title: "Chambray Tiered Garden & Pointelle Layer",
    audience: "Girls",
    occasion: "Weekend Garden Brunch",
    trendHighlight: "Tiered Cottagecore",
    coverageVerification: "Rounded Peter-Pan collar, full-wrist puff cuffs, and opaque cotton tiers layered with pointelle knit.",
    stylingNotes: "Breathable modest cotton tiers ideal for family picnics and spring gatherings. #summer #spring #casual",
    matchedPieces: [
      "Girls' Chambray Peter-Pan Collar Tiered Maxi Dress",
      "Girls' Dusty Rose Pointelle Long-Sleeve Cardigan"
    ],
    boutiqueSources: ["Little Cedar Modest Kids"],
    favorite: false
  }
];

const LETTER_SIZE_RANK = {
  XXS: 0, XS: 1, S: 2, SM: 2, M: 3, MD: 3, L: 4, LG: 4,
  XL: 5, XXL: 6, "2XL": 6, XXXL: 7, "3XL": 7, "1X": 5, "2X": 6, "3X": 7
};

const TAG_KEYWORD_RULES = [
  ["Summer", ["summer", "linen", "breezy", "sunlit", "warm", "cotton", "chiffon", "resort"]],
  ["Formal", ["formal", "evening", "gala", "festive", "celebration", "abaya", "kaftan", "satin", "silk", "eid", "wedding", "occasion"]],
  ["Work", ["work", "office", "studio", "tailoring", "tailored", "blazer", "trench", "structured", "professional", "meeting"]],
  ["Casual", ["casual", "everyday", "weekend", "denim", "campus", "school", "brunch", "relaxed", "cottagecore"]],
  ["Festive", ["festive", "celebration", "eid", "party", "champagne", "embellished", "gold"]],
  ["Spring", ["spring", "floral", "pastel", "garden", "blossom"]],
  ["Autumn", ["autumn", "fall", "crisp", "layering", "camel", "espresso"]],
  ["Winter", ["winter", "wool", "knit", "cozy", "velvet", "sweater"]],
  ["Hijab-Friendly", ["hijab", "headscarf", "shawl", "scarf", "wrap"]]
];

const state = {
  activeView: "studio",
  profile: loadProfile(),
  closet: loadCloset(),
  feedback: loadFeedback(),
  dailyRotationSeed: 0,
  dailyEdit: null,
  selectedOccasion: CATALOG.occasions[0],
  selectedSeason: CATALOG.seasons[0],
  customNote: "",
  composerResult: null,
  closetFilter: "All",
  closetSearch: "",
  activeDetailOutfitId: null,
  editingOutfitId: null,
  draftColors: [],
  draftTrends: []
};

let toastTimer = null;

function loadProfile() {
  try {
    const raw = localStorage.getItem(STORAGE_KEYS.profile);
    if (!raw) return { ...DEFAULT_PROFILE };
    return { ...DEFAULT_PROFILE, ...JSON.parse(raw) };
  } catch {
    return { ...DEFAULT_PROFILE };
  }
}

function saveProfileToStorage(profile) {
  try {
    localStorage.setItem(STORAGE_KEYS.profile, JSON.stringify(profile));
  } catch {
    // ignore storage quota errors
  }
}

function loadCloset() {
  try {
    const raw = localStorage.getItem(STORAGE_KEYS.closet);
    if (raw === null) return DEFAULT_SAVED_OUTFITS.map((item) => ({ ...item }));
    const parsed = JSON.parse(raw);
    return Array.isArray(parsed) ? parsed : [];
  } catch {
    return DEFAULT_SAVED_OUTFITS.map((item) => ({ ...item }));
  }
}

function saveClosetToStorage(closet) {
  try {
    localStorage.setItem(STORAGE_KEYS.closet, JSON.stringify(closet));
  } catch {
    // ignore storage quota errors
  }
}

function loadFeedback() {
  try {
    const raw = localStorage.getItem(STORAGE_KEYS.feedback);
    if (!raw) return {};
    const parsed = JSON.parse(raw);
    return parsed && typeof parsed === "object" ? parsed : {};
  } catch {
    return {};
  }
}

function saveFeedbackToStorage(feedback) {
  try {
    localStorage.setItem(STORAGE_KEYS.feedback, JSON.stringify(feedback));
  } catch {
    // ignore
  }
}

/* Size Matching Helper (JS port of SizeMatchingHelper.kt) */
function resolveTargetRanks(savedSizeRaw) {
  const upper = String(savedSizeRaw || "").toUpperCase().trim();
  if (upper.includes("PLUS") || upper.includes("1X") || upper.includes("2X") || upper.includes("3X")) {
    return new Set([6, 7]);
  }
  if (LETTER_SIZE_RANK[upper] !== undefined) {
    return new Set([LETTER_SIZE_RANK[upper]]);
  }
  const match = upper.match(/([A-Z0-9]+)\s*(?:-|TO|–|—)\s*([A-Z0-9]+)/);
  if (match) {
    const s = LETTER_SIZE_RANK[match[1]];
    const e = LETTER_SIZE_RANK[match[2]];
    if (s !== undefined && e !== undefined) {
      const set = new Set();
      for (let i = Math.min(s, e); i <= Math.max(s, e); i += 1) set.add(i);
      return set;
    }
  }
  return new Set();
}

function matchesLadiesSize(rawRange, savedSizeRaw) {
  const targetRanks = resolveTargetRanks(savedSizeRaw);
  if (targetRanks.size === 0) return true;
  const cleaned = String(rawRange || "")
    .toUpperCase()
    .replace(/TEENS/g, "")
    .replace(/LADIES/g, "")
    .trim();
  const rangeMatch = cleaned.match(/([A-Z0-9]+)\s*(?:-|TO|–|—)\s*([A-Z0-9]+)/);
  if (rangeMatch) {
    const s = LETTER_SIZE_RANK[rangeMatch[1]];
    const e = LETTER_SIZE_RANK[rangeMatch[2]];
    if (s !== undefined && e !== undefined) {
      const low = Math.min(s, e);
      const high = Math.max(s, e);
      for (const r of targetRanks) {
        if (r >= low && r <= high) return true;
      }
      return false;
    }
  }
  const tokens = cleaned.match(/[A-Z0-9]+/g) || [];
  const itemRanks = tokens.map((t) => LETTER_SIZE_RANK[t]).filter((v) => v !== undefined);
  if (itemRanks.length > 0) {
    return itemRanks.some((r) => targetRanks.has(r));
  }
  return false;
}

function matchesYouthSize(rawRange, savedYouthSize) {
  const savedNums = (String(savedYouthSize || "").match(/\d+/g) || []).map(Number);
  const rangeMatch = String(rawRange || "").match(/(\d+)\s*(?:-|to|–|—)\s*(\d+)/i);
  if (savedNums.length > 0 && rangeMatch) {
    const itemMin = Math.min(Number(rangeMatch[1]), Number(rangeMatch[2]));
    const itemMax = Math.max(Number(rangeMatch[1]), Number(rangeMatch[2]));
    const savedMin = Math.min(...savedNums);
    const savedMax = Math.max(...savedNums);
    return savedMax >= itemMin && savedMin <= itemMax;
  }
  const itemNums = (String(rawRange || "").match(/\d+/g) || []).map(Number);
  if (savedNums.length > 0 && itemNums.length > 0) {
    return savedNums.some((n) => itemNums.includes(n));
  }
  return false;
}

function matchesItemSavedSizes(item, profile) {
  const rawRange = (item.sizeRange || "").trim();
  if (!rawRange) return true;
  const lower = rawRange.toLowerCase();
  if (lower.includes("one size") || lower.includes("free size") || lower.includes("all sizes") || lower === "os") {
    return true;
  }
  const itemAudience = (item.audience || "").trim();
  const profileAudience = (profile.audience || "").trim();
  const girlsPref = (profile.girlsSize || "").trim();
  const hasYouthPref = Boolean(girlsPref) && girlsPref.toLowerCase() !== "not needed";

  if (itemAudience.toLowerCase() === "girls") {
    if (!hasYouthPref) return false;
    if (girlsPref.toLowerCase().startsWith("teens") && lower.includes("girls")) return false;
    return matchesYouthSize(rawRange, girlsPref);
  }
  if (itemAudience.toLowerCase() === "teens") {
    if (girlsPref.toLowerCase().startsWith("teens")) {
      return matchesLadiesSize(rawRange, girlsPref.replace(/^teens/i, "").trim());
    }
    if (profileAudience.toLowerCase() === "teens") {
      return matchesLadiesSize(rawRange, profile.topSize) || matchesLadiesSize(rawRange, profile.bottomSize);
    }
    if (!hasYouthPref && profileAudience.toLowerCase() === "ladies") return false;
    return matchesYouthSize(rawRange, girlsPref) || matchesLadiesSize(rawRange, profile.topSize);
  }
  if (profileAudience.toLowerCase() === "girls") return false;

  if (lower.includes("length") || lower.includes('"')) {
    const targetLen = Number((profile.dressAbayaLength || "").match(/\d+/)?.[0] || 0);
    if (!targetLen) return true;
    const lenMatch = rawRange.match(/(\d+)\s*(?:-|to|–|—)\s*(\d+)/i);
    if (lenMatch) {
      const low = Math.min(Number(lenMatch[1]), Number(lenMatch[2]));
      const high = Math.max(Number(lenMatch[1]), Number(lenMatch[2]));
      return targetLen >= low && targetLen <= high;
    }
    return true;
  }
  if (item.category === "Bottom") return matchesLadiesSize(rawRange, profile.bottomSize);
  if (item.category === "Top" || item.category === "Outerwear") return matchesLadiesSize(rawRange, profile.topSize);
  if (item.category === "Set & Co-ord" || item.category === "Dress & Abaya") {
    return matchesLadiesSize(rawRange, profile.topSize) || matchesLadiesSize(rawRange, profile.bottomSize);
  }
  return true;
}

function matchesItemCoverageRequirements(item, profile) {
  const cat = (item.category || "").trim();
  if (cat.toLowerCase() === "headwear & accessory") {
    return Boolean(profile.hijabFriendly);
  }

  const summary = (item.coverageSummary || "").trim().toLowerCase();
  if (!summary) return true;

  const neckPref = (profile.necklineRule || "").toLowerCase();
  if (neckPref.includes("high") || neckPref.includes("mock") || neckPref.includes("crew")) {
    if (/(low neck|v-neck|plunge|strapless)/.test(summary)) return false;
  }

  const sleevePref = (profile.sleeveRule || "").toLowerCase();
  if (sleevePref.includes("full") || sleevePref.includes("wrist") || sleevePref.includes("long")) {
    if (/(sleeveless|short sleeve|cap sleeve)/.test(summary)) return false;
  }

  if (["Bottom", "Dress & Abaya", "Set & Co-ord", "Outerwear"].includes(cat)) {
    const hemPref = (profile.hemlineRule || "").toLowerCase();
    const isFloorOnly = hemPref.includes("floor") && !hemPref.includes("ankle");
    if (isFloorOnly) {
      if (/(midi|knee)/.test(summary)) return false;
      const meetsFloorOrMaxi = /(floor|maxi|full-length)/.test(summary);
      if (!meetsFloorOrMaxi && /(calf|mid-thigh)/.test(summary)) return false;
    } else if (hemPref.includes("ankle") || hemPref.includes("maxi")) {
      if (summary.includes("above knee") || summary.includes("mini ")) return false;
      if (summary.includes("midi") && !summary.includes("maxi") && !summary.includes("ankle")) return false;
    }
  }

  return true;
}

function getSizeMatchedInventory(profile) {
  return STARTER_BOUTIQUE_ITEMS.filter(
    (item) => matchesItemSavedSizes(item, profile) && matchesItemCoverageRequirements(item, profile)
  );
}

function formatSizeSummary(profile) {
  const parts = [
    `Top ${profile.topSize}`,
    `Bottom ${profile.bottomSize}`,
    `Length ${profile.dressAbayaLength}"`
  ];
  if (profile.girlsSize && profile.girlsSize !== "Not Needed") {
    parts.push(`Youth: ${profile.girlsSize}`);
  }
  return parts.join(" \u2022 ");
}

/* Outfit Personalization Engine (Respects Charter, Sizes, Occasion, Season, & Thumbs Feedback) */
function todayDateKey() {
  const d = new Date();
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, "0");
  const day = String(d.getDate()).padStart(2, "0");
  return `${y}-${m}-${day}`;
}

function todayDisplayLabel() {
  return new Date().toLocaleDateString(undefined, {
    weekday: "long",
    month: "short",
    day: "numeric"
  });
}

function scoreOutfitWithFeedbackAndProfile(outfit, profile, feedbackMap) {
  let score = 0;
  const corpus = `${outfit.title} ${outfit.audience} ${outfit.trendHighlight} ${outfit.matchedPieces.join(" ")}`.toLowerCase();

  for (const color of profile.favoriteColors || []) {
    if (corpus.includes(color.toLowerCase().split(" ")[0])) score += 3;
  }
  for (const trend of profile.trendTags || []) {
    if (corpus.includes(trend.toLowerCase().split(" ")[0])) score += 3;
  }
  if (profile.hijabFriendly && corpus.includes("hijab")) score += 2;

  Object.values(feedbackMap || {}).forEach((entry) => {
    if (!entry || !entry.vote) return;
    const sameTrend = entry.trendHighlight && outfit.trendHighlight === entry.trendHighlight;
    const sameAudience = entry.audience && outfit.audience === entry.audience;
    const sameTitle = entry.outfitTitle && entry.outfitTitle === outfit.title;
    if (entry.vote === "UP") {
      if (sameTitle) score += 4;
      if (sameTrend) score += 3;
      if (sameAudience) score += 1;
    } else if (entry.vote === "DOWN") {
      if (sameTitle) score -= 8;
      if (sameTrend) score -= 4;
    }
  });
  return score;
}

function buildCuratedCandidatePool(profile, matchedInventory, occasion, season, customNote) {
  const hasItem = (id) => matchedInventory.some((i) => i.id === id);
  const topSize = profile.topSize;
  const bottomSize = profile.bottomSize;
  const lengthIn = profile.dressAbayaLength;
  const youthSize = profile.girlsSize !== "Not Needed" ? profile.girlsSize : "Girls 10-12";
  const district = profile.cityOrArea || "Oakbrook & Promenade";
  const hijabPiece = profile.hijabFriendly ? "Champagne Modal Crinkle Hijab & Shawl Set" : null;
  const customTag = customNote && customNote.trim() ? ` Tailored note: "${customNote.trim()}".` : "";

  const candidates = [
    {
      title: "Rosewood Pleated Satin & Ivory Studio Look",
      audience: "Ladies",
      occasion: occasion || "Work & Studio",
      trendHighlight: "Pleated Satin Movement",
      coverageVerification: `${profile.necklineRule} & ${profile.sleeveRule} verified with opaque lined satin at ${profile.hemlineRule} (${bottomSize} skirt, ${topSize} blouse).`,
      stylingNotes: `Balances fluid movement with crisp tailoring for ${season || "Crisp Autumn Layering"} around ${district}.${customTag}`,
      matchedPieces: [
        hasItem("inv-1") ? `Rosewood Pleated Satin Maxi Skirt (${bottomSize})` : `Rosewood Pleated Satin Maxi Skirt`,
        hasItem("inv-2") ? `Silk Ivory Mock-Neck Blouse (${topSize})` : `Silk Ivory Mock-Neck Blouse`,
        ...(hijabPiece ? [hijabPiece] : [])
      ],
      boutiqueSources: ["Atelier Noor"]
    },
    {
      title: "Sage Linen Longline Trench & Ivory Layer",
      audience: "Ladies",
      occasion: occasion || "Everyday Chic",
      trendHighlight: "Utility Longline Trench",
      coverageVerification: `${profile.fitRule} duster coat with full storm cuffs and high collarbone ivory layering (${topSize}).`,
      stylingNotes: `Effortless quiet-luxury silhouette ideal for transitional weather and studio errands.${customTag}`,
      matchedPieces: [
        `Sage Belted Longline Linen Trench (${topSize})`,
        `Silk Ivory Bishop-Sleeve Mock-Neck Blouse (${topSize})`,
        ...(hijabPiece ? [hijabPiece] : [])
      ],
      boutiqueSources: ["Olive & Loom Boutique", "Atelier Noor"]
    },
    {
      title: "Espresso Satin-Trimmed Abaya Evening Edit",
      audience: "Ladies",
      occasion: occasion || "Festive & Celebration",
      trendHighlight: "Occasion Abaya & Kaftan",
      coverageVerification: `Floor-skimming ${lengthIn}" opaque crepe slip paired with flowing kimono-sleeve outer abaya (${profile.sleeveRule}).`,
      stylingNotes: `Celebration-ready drape tailored to your saved ${lengthIn}" length preference.${customTag}`,
      matchedPieces: [
        `Espresso Satin-Trimmed Open Abaya (${lengthIn}" Length)`,
        ...(hijabPiece ? [hijabPiece] : [])
      ],
      boutiqueSources: ["Dar Al-Harir"]
    },
    {
      title: "Chambray Tiered Garden & Pointelle Layer",
      audience: "Girls",
      occasion: occasion || "Weekend Garden Brunch",
      trendHighlight: "Tiered Cottagecore",
      coverageVerification: `High rounded Peter-Pan collar, full wrist puff cuffs, and ankle-length opaque tiers sized for ${youthSize}.`,
      stylingNotes: `Playful modest movement crafted in breathable cotton with soft rose knit layering.${customTag}`,
      matchedPieces: [
        `Girls' Chambray Tiered Maxi Dress (${youthSize})`,
        `Girls' Dusty Rose Pointelle Cardigan (${youthSize})`
      ],
      boutiqueSources: ["Little Cedar Modest Kids"]
    },
    {
      title: "Teens' Relaxed Chambray Tunic & Wide-Leg Co-ord",
      audience: "Teens",
      occasion: occasion || "School & Campus",
      trendHighlight: "Modest Campus Denim",
      coverageVerification: `Mid-thigh relaxed tunic over full-length wide-leg trousers with ${profile.necklineRule} finish.`,
      stylingNotes: `Campus-friendly drape balancing modern relaxed denim with full modest coverage.${customTag}`,
      matchedPieces: [
        `Teens' Relaxed Denim Tunic & Wide-Leg Co-ord`,
        ...(hijabPiece ? [hijabPiece] : [])
      ],
      boutiqueSources: ["Olive & Loom Boutique", "Atelier Noor"]
    }
  ];

  // Filter strictly by audience focus unless custom request widens it
  const audiencePref = (profile.audience || "Ladies & Girls").toLowerCase();
  const filtered = candidates.filter((c) => {
    if (audiencePref === "ladies") return c.audience === "Ladies";
    if (audiencePref === "girls") return c.audience === "Girls";
    if (audiencePref === "teens") return c.audience === "Teens" || c.audience === "Ladies";
    return true;
  });

  return filtered.length > 0 ? filtered : candidates;
}

function generateDailyEdit(profile, feedbackMap, rotationOffset = 0) {
  const matchedInventory = getSizeMatchedInventory(profile);
  const pool = buildCuratedCandidatePool(profile, matchedInventory, "Everyday Chic", "Crisp Autumn Layering", "");
  const scored = pool
    .map((outfit, idx) => ({
      outfit,
      score: scoreOutfitWithFeedbackAndProfile(outfit, profile, feedbackMap),
      idx
    }))
    .sort((a, b) => (b.score - a.score) || ((a.idx + rotationOffset) % pool.length) - ((b.idx + rotationOffset) % pool.length));

  // Rotate tie-breakers smoothly on Refresh Edit
  const rotated = rotationOffset > 0 && scored.length > 1
    ? [...scored.slice(rotationOffset % scored.length), ...scored.slice(0, rotationOffset % scored.length)]
    : scored;

  const picked = rotated.slice(0, 3).map((entry) => entry.outfit);
  const likedCount = Object.values(feedbackMap || {}).filter((f) => f && f.vote === "UP").length;
  const feedbackSuffix = likedCount > 0
    ? ` Adapted from your ${likedCount} liked look${likedCount > 1 ? "s" : ""}.`
    : "";

  return {
    dateKey: todayDateKey(),
    dateDisplay: todayDisplayLabel(),
    dayTheme: `${profile.audience} Atelier Edit`,
    editorialSummary: `Filtered across ${matchedInventory.length} size-matched boutique pieces (${formatSizeSummary(profile)}) honoring ${profile.necklineRule} & ${profile.hemlineRule}.${feedbackSuffix}`,
    outfits: picked
  };
}

function generateBespokeRecommendation(profile, feedbackMap, occasion, season, customNote) {
  const matchedInventory = getSizeMatchedInventory(profile);
  const pool = buildCuratedCandidatePool(profile, matchedInventory, occasion, season, customNote);
  const keywords = `${occasion} ${season} ${customNote}`.toLowerCase().split(/\s+/).filter(Boolean);

  const ranked = pool
    .map((outfit) => {
      let score = scoreOutfitWithFeedbackAndProfile(outfit, profile, feedbackMap);
      const text = `${outfit.title} ${outfit.trendHighlight} ${outfit.matchedPieces.join(" ")} ${outfit.stylingNotes}`.toLowerCase();
      keywords.forEach((kw) => {
        if (kw.length > 2 && text.includes(kw)) score += 4;
      });
      if (occasion === "Festive & Celebration" && outfit.trendHighlight.includes("Abaya")) score += 6;
      if (occasion === "Work & Studio" && (outfit.trendHighlight.includes("Trench") || outfit.trendHighlight.includes("Pleated"))) score += 6;
      if (occasion === "Weekend Garden Brunch" && outfit.trendHighlight.includes("Cottagecore")) score += 6;
      if (occasion === "School & Campus" && outfit.trendHighlight.includes("Campus")) score += 6;
      return { outfit, score };
    })
    .sort((a, b) => b.score - a.score)
    .slice(0, 2)
    .map((r) => ({ ...r.outfit, occasion }));

  return {
    editorialSummary: `Curated ${ranked.length} bespoke modest ensembles for ${occasion} (${season}) matched to ${formatSizeSummary(profile)}.`,
    outfits: ranked
  };
}

/* Closet Search Helper (JS port of ClosetSearchHelper.kt) */
function extractCustomHashtags(outfit) {
  const raw = `${outfit.title || ""} ${outfit.stylingNotes || ""}`;
  const matches = raw.match(/#[A-Za-z0-9_-]{2,32}/g) || [];
  const seen = new Set();
  const out = [];
  matches.forEach((m) => {
    const clean = m.slice(1).trim();
    if (clean && !seen.has(clean.toLowerCase())) {
      seen.add(clean.toLowerCase());
      out.push(clean);
    }
  });
  return out;
}

function deriveStyleTags(outfit) {
  const tags = [];
  const seen = new Set();
  const addUnique = (tag) => {
    const clean = String(tag || "").trim();
    if (clean && !seen.has(clean.toLowerCase())) {
      seen.add(clean.toLowerCase());
      tags.push(clean);
    }
  };
  extractCustomHashtags(outfit).forEach(addUnique);

  const searchable = [
    outfit.title,
    outfit.occasion,
    outfit.trendHighlight,
    outfit.coverageVerification,
    outfit.stylingNotes,
    (outfit.matchedPieces || []).join(" "),
    outfit.audience
  ].join(" ").toLowerCase();

  TAG_KEYWORD_RULES.forEach(([displayTag, keywords]) => {
    if (keywords.some((kw) => new RegExp(`\\b${kw.replace(/[.*+?^${}()|[\]\\]/g, "\\$&")}\\b`, "i").test(searchable))) {
      addUnique(displayTag);
    }
  });
  return tags;
}

function collectAvailableTags(outfits) {
  const res = [];
  const seen = new Set();
  const push = (t) => {
    if (!seen.has(t.toLowerCase())) {
      seen.add(t.toLowerCase());
      res.push(t);
    }
  };
  CATALOG.quickStyleTags.forEach(push);
  outfits.forEach((o) => deriveStyleTags(o).forEach(push));
  return res;
}

function parseQueryTokens(query) {
  return String(query || "")
    .split(/[\s,]+/)
    .map((s) => s.trim().replace(/^#/, "").toLowerCase())
    .filter(Boolean)
    .filter((v, i, arr) => arr.indexOf(v) === i);
}

function toggleTagInQuery(currentQuery, tag) {
  const norm = tag.trim().replace(/^#/, "").toLowerCase();
  const tokens = parseQueryTokens(currentQuery);
  const updated = tokens.includes(norm)
    ? tokens.filter((t) => t !== norm)
    : [...tokens, norm];
  return updated.join(" ");
}

function matchesClosetFilter(outfit, filterChip, searchQuery) {
  if (filterChip === "Favorites" && !outfit.favorite) return false;
  if (filterChip === "Ladies" && !(outfit.audience || "").toLowerCase().includes("ladies")) return false;
  if (filterChip === "Girls" && !(outfit.audience || "").toLowerCase().includes("girls")) return false;
  if (filterChip === "Teens" && !(outfit.audience || "").toLowerCase().includes("teens")) return false;

  const tokens = parseQueryTokens(searchQuery);
  if (tokens.length === 0) return true;

  const derivedLower = deriveStyleTags(outfit).map((t) => t.toLowerCase());
  const corpus = [
    outfit.title,
    outfit.audience,
    outfit.occasion,
    outfit.trendHighlight,
    outfit.coverageVerification,
    outfit.stylingNotes,
    (outfit.matchedPieces || []).join(" "),
    (outfit.boutiqueSources || []).join(" "),
    derivedLower.join(" ")
  ].join(" ").toLowerCase();

  return tokens.every((tok) => derivedLower.some((t) => t.includes(tok)) || corpus.includes(tok));
}

/* Safe DOM Helpers */
function el(tag, className, text) {
  const node = document.createElement(tag);
  if (className) node.className = className;
  if (text !== undefined && text !== null) node.textContent = text;
  return node;
}

function showToast(message) {
  const toast = document.getElementById("toast-banner");
  if (!toast) return;
  toast.textContent = message;
  toast.classList.remove("hidden");
  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => toast.classList.add("hidden"), 2800);
}

function normalizeTitleKey(title) {
  return String(title || "").trim().slice(0, 120);
}

function isOutfitSaved(title) {
  const key = normalizeTitleKey(title);
  return state.closet.some((o) => normalizeTitleKey(o.title) === key);
}

function toggleSaveOutfit(outfit) {
  const key = normalizeTitleKey(outfit.title);
  const existing = state.closet.find((o) => normalizeTitleKey(o.title) === key);
  if (existing) {
    state.closet = state.closet.filter((o) => o.id !== existing.id);
    showToast(`Removed "${outfit.title}" from My Closet.`);
  } else {
    const newEntry = {
      id: `saved-${Date.now()}-${Math.random().toString(36).slice(2, 6)}`,
      title: outfit.title,
      audience: outfit.audience || "Ladies",
      occasion: outfit.occasion || "Curated Look",
      trendHighlight: outfit.trendHighlight || "",
      coverageVerification: outfit.coverageVerification || "",
      stylingNotes: outfit.stylingNotes || "",
      matchedPieces: Array.isArray(outfit.matchedPieces) ? [...outfit.matchedPieces] : [],
      boutiqueSources: Array.isArray(outfit.boutiqueSources) ? [...outfit.boutiqueSources] : ["Local Atelier"],
      favorite: false
    };
    state.closet = [newEntry, ...state.closet];
    showToast(`Saved "${outfit.title}" to My Closet.`);
  }
  saveClosetToStorage(state.closet);
  renderAll();
}

function shareOutfit(outfit) {
  const params = new URLSearchParams({
    title: outfit.title || "",
    audience: outfit.audience || "Ladies",
    occasion: outfit.occasion || "Curated Look",
    coverage: outfit.coverageVerification || "",
    notes: outfit.stylingNotes || "",
    pieces: (outfit.matchedPieces || []).join("|")
  });
  const shareUrl = `${window.location.origin}${window.location.pathname}?${params.toString()}`;
  if (navigator.share) {
    navigator.share({
      title: `Modest Tailor — ${outfit.title}`,
      text: `${outfit.title} (${outfit.audience})`,
      url: shareUrl
    }).catch(() => {});
    return;
  }
  if (navigator.clipboard && navigator.clipboard.writeText) {
    navigator.clipboard.writeText(shareUrl)
      .then(() => showToast("Copied shareable Modest Look link."))
      .catch(() => showToast("Share link ready in your browser URL."));
  } else {
    showToast("Share link ready.");
  }
}

function feedbackKey(dateKey, title) {
  return `${dateKey}|${normalizeTitleKey(title)}`;
}

function submitDailyFeedback(outfit, vote, dateKey) {
  const key = feedbackKey(dateKey, outfit.title);
  const current = state.feedback[key];
  if (current && current.vote === vote) {
    delete state.feedback[key];
    showToast(`Cleared rating for "${outfit.title}".`);
  } else {
    state.feedback[key] = {
      outfitTitle: outfit.title,
      audience: outfit.audience,
      trendHighlight: outfit.trendHighlight,
      vote,
      dateKey
    };
    showToast(
      vote === "UP"
        ? "Saved Thumbs Up! Future curations will prioritize styles like this."
        : "Saved Thumbs Down! Future curations will steer in another direction."
    );
  }
  saveFeedbackToStorage(state.feedback);
  renderDailyEdit();
}

function resolveOutfitDesignImage(outfit) {
  const corpus = [
    outfit?.title || "",
    outfit?.audience || "",
    outfit?.occasion || "",
    outfit?.trendHighlight || "",
    (outfit?.matchedPieces || []).join(" ")
  ].join(" ").toLowerCase();

  if (/(abaya|kaftan|espresso|evening|gala|eid|festive|celebration)/.test(corpus)) {
    return "/assets/design-espresso-abaya.jpg";
  }
  if (/(trench|sage|olive|linen|utility|duster|khaki)/.test(corpus)) {
    return "/assets/design-sage-trench.jpg";
  }
  if (/(girl|peter-pan|cottagecore|pointelle|cardigan|garden|tiered)/.test(corpus)) {
    return "/assets/design-girls-garden.jpg";
  }
  if (/(teen|denim|campus|wide-leg|tunic|school)/.test(corpus)) {
    return "/assets/design-teens-denim.jpg";
  }
  if (/(rosewood|satin|pleated|bishop|silk|mock-neck|blouse)/.test(corpus)) {
    return "/assets/design-rosewood-satin.jpg";
  }
  const aud = (outfit?.audience || "").toLowerCase();
  if (aud.includes("girls")) return "/assets/design-girls-garden.jpg";
  if (aud.includes("teens")) return "/assets/design-teens-denim.jpg";
  return "/assets/design-rosewood-satin.jpg";
}

/* Reusable Curated Outfit Card Builder */
function buildOutfitCardElement(outfit, options = {}) {
  const { index = 0, total = 1, showFeedback = false, dateKey = "" } = options;
  const card = el("article", "outfit-card");

  const mediaWrap = el("div", "outfit-design-media");
  const img = el("img", "outfit-design-img");
  img.src = resolveOutfitDesignImage(outfit);
  img.alt = `${outfit.title || "Curated modest outfit"} design preview`;
  img.loading = "lazy";

  const scrim = el("div", "outfit-design-scrim");
  const head = el("div", "outfit-card-head");
  head.append(
    el("span", "pill pill-sage", outfit.audience || "Ladies"),
    el("span", "pill pill-brass", `Look 0${index + 1}${total > 1 ? ` / 0${total}` : ""}`)
  );
  mediaWrap.append(img, scrim, head);
  card.append(mediaWrap);

  const titleEl = el("h3", "serif-title outfit-title", outfit.title);
  card.append(titleEl);

  const cov = el("div", "coverage-pill", outfit.coverageVerification);
  card.append(cov);

  if (outfit.matchedPieces && outfit.matchedPieces.length > 0) {
    const piecesRow = el("div", "tag-row");
    outfit.matchedPieces.forEach((p) => piecesRow.append(el("span", "tag-chip", p)));
    card.append(piecesRow);
  }

  if (outfit.boutiqueSources && outfit.boutiqueSources.length > 0) {
    card.append(el("div", "boutique-source", `Boutiques: ${outfit.boutiqueSources.join(" \u2022 ")}`));
  }

  card.append(el("p", "outfit-notes", outfit.stylingNotes));

  if (showFeedback) {
    const key = feedbackKey(dateKey, outfit.title);
    const currentVote = state.feedback[key]?.vote || null;

    const fbRow = el("div", "feedback-row");
    const hintText = currentVote === "UP"
      ? "Liked \u2022 Guiding future edits"
      : currentVote === "DOWN"
        ? "Noted \u2022 Will adjust direction"
        : "Rate this daily look";
    const hintClass = currentVote === "UP"
      ? "feedback-hint liked"
      : currentVote === "DOWN"
        ? "feedback-hint disliked"
        : "feedback-hint";
    fbRow.append(el("span", hintClass, hintText));

    const btns = el("div", "feedback-btns");
    const upBtn = el("button", `thumb-btn ${currentVote === "UP" ? "active-up" : ""}`, "\uD83D\uDC4D");
    upBtn.type = "button";
    upBtn.setAttribute("aria-label", `Thumbs up for ${outfit.title}`);
    upBtn.addEventListener("click", () => submitDailyFeedback(outfit, "UP", dateKey));

    const downBtn = el("button", `thumb-btn ${currentVote === "DOWN" ? "active-down" : ""}`, "\uD83D\uDC4E");
    downBtn.type = "button";
    downBtn.setAttribute("aria-label", `Thumbs down for ${outfit.title}`);
    downBtn.addEventListener("click", () => submitDailyFeedback(outfit, "DOWN", dateKey));

    btns.append(upBtn, downBtn);
    fbRow.append(btns);
    card.append(fbRow);
  }

  const saved = isOutfitSaved(outfit.title);
  const actionsRow = el("div", "outfit-actions-row");

  const saveBtn = el(
    "button",
    `btn btn-sm ${saved ? "btn-tonal" : "btn-outline"}`,
    saved ? "\u2713 Saved in Closet" : "+ Save to Closet"
  );
  saveBtn.type = "button";
  saveBtn.addEventListener("click", () => toggleSaveOutfit(outfit));

  const shareBtn = el("button", "btn btn-sm btn-outline", "Share");
  shareBtn.type = "button";
  shareBtn.addEventListener("click", () => shareOutfit(outfit));

  actionsRow.append(saveBtn, shareBtn);
  card.append(actionsRow);

  if (saved) {
    const viewRow = el("div", "view-closet-link-row");
    const viewBtn = el("button", "text-link-btn", "View in My Closet \u2192");
    viewBtn.type = "button";
    viewBtn.addEventListener("click", () => switchView("closet"));
    viewRow.append(viewBtn);
    card.append(viewRow);
  }

  return card;
}

/* Renderers */
function switchView(viewName) {
  state.activeView = viewName;
  const studioEl = document.getElementById("view-studio");
  const closetEl = document.getElementById("view-closet");
  const tabStudio = document.getElementById("nav-tab-studio");
  const tabCloset = document.getElementById("nav-tab-closet");

  if (viewName === "closet") {
    studioEl.classList.add("hidden");
    closetEl.classList.remove("hidden");
    tabStudio.classList.remove("active");
    tabCloset.classList.add("active");
  } else {
    closetEl.classList.add("hidden");
    studioEl.classList.remove("hidden");
    tabCloset.classList.remove("active");
    tabStudio.classList.add("active");
  }
  window.scrollTo({ top: 0, behavior: "smooth" });
}

function renderMastheadAndCharter() {
  const matched = getSizeMatchedInventory(state.profile);
  const total = STARTER_BOUTIQUE_ITEMS.length;
  const matchPercent = total > 0 ? Math.round((matched.length / total) * 100) : 0;
  const savedCount = state.closet.length;

  document.getElementById("masthead-district").textContent = state.profile.cityOrArea || "Oakbrook & Promenade";
  document.getElementById("metric-size-match").textContent = `${matched.length}/${total} fit & coverage`;
  document.getElementById("metric-match-percent").textContent = `${matchPercent}%`;
  const progressFill = document.getElementById("metric-progress-fill");
  const progressTrack = document.getElementById("metric-progress-track");
  if (progressFill) {
    progressFill.style.width = `${matchPercent}%`;
  }
  if (progressTrack) {
    progressTrack.setAttribute("aria-valuenow", String(matchPercent));
  }
  document.getElementById("metric-closet-count").textContent = `${savedCount} curated`;
  document.getElementById("nav-closet-badge").textContent = String(savedCount);
  document.getElementById("qa-closet-sub").textContent = `${savedCount} saved look${savedCount === 1 ? "" : "s"}`;

  document.getElementById("hero-audience-pill").textContent = state.profile.audience;
  document.getElementById("hero-size-pill").textContent = `${matched.length}/${total} Size & Coverage Match (${matchPercent}%)`;
  document.getElementById("charter-rules-summary").textContent =
    `${state.profile.necklineRule} \u2022 ${state.profile.sleeveRule} \u2022 ${state.profile.hemlineRule} \u2022 ${state.profile.fitRule}`;
  document.getElementById("charter-sizes-summary").textContent =
    `Saved Sizes: ${formatSizeSummary(state.profile)}`;

  const swatchRow = document.getElementById("charter-color-swatches");
  const chips = (state.profile.favoriteColors || []).slice(0, 5).map((name) => {
    const chip = el("span", "swatch-chip");
    const colorDef = CATALOG.colors.find((c) => c.name === name);
    const dot = el("span", "swatch-dot");
    dot.style.backgroundColor = colorDef ? colorDef.hex : "#6e3b49";
    chip.append(dot, document.createTextNode(name));
    return chip;
  });
  swatchRow.replaceChildren(...chips);
}

function renderDailyEdit() {
  state.dailyEdit = generateDailyEdit(state.profile, state.feedback, state.dailyRotationSeed);
  const rec = state.dailyEdit;

  document.getElementById("daily-theme-title").textContent = `${rec.dateDisplay} \u00B7 ${rec.dayTheme}`;
  document.getElementById("daily-editorial-summary").textContent = rec.editorialSummary;

  const grid = document.getElementById("daily-outfits-grid");
  const cards = rec.outfits.map((outfit, idx) =>
    buildOutfitCardElement(outfit, {
      index: idx,
      total: rec.outfits.length,
      showFeedback: true,
      dateKey: rec.dateKey
    })
  );
  grid.replaceChildren(...cards);
}

function renderComposerControls() {
  const occasionRow = document.getElementById("composer-occasion-chips");
  occasionRow.replaceChildren(
    ...CATALOG.occasions.map((occ) => {
      const btn = el("button", `filter-chip ${state.selectedOccasion === occ ? "active" : ""}`, occ);
      btn.type = "button";
      btn.addEventListener("click", () => {
        state.selectedOccasion = occ;
        renderComposerControls();
      });
      return btn;
    })
  );

  const seasonRow = document.getElementById("composer-season-chips");
  seasonRow.replaceChildren(
    ...CATALOG.seasons.map((season) => {
      const btn = el("button", `filter-chip ${state.selectedSeason === season ? "active" : ""}`, season);
      btn.type = "button";
      btn.addEventListener("click", () => {
        state.selectedSeason = season;
        renderComposerControls();
      });
      return btn;
    })
  );

  const noteInput = document.getElementById("composer-note-input");
  if (noteInput.value !== state.customNote) {
    noteInput.value = state.customNote;
  }
}

function renderComposerResults() {
  const wrap = document.getElementById("composer-results-wrap");
  if (!state.composerResult) {
    wrap.classList.add("hidden");
    return;
  }
  wrap.classList.remove("hidden");
  document.getElementById("composer-editorial-note").textContent = state.composerResult.editorialSummary;

  const list = document.getElementById("composer-outfits-list");
  list.replaceChildren(
    ...state.composerResult.outfits.map((outfit, idx) =>
      buildOutfitCardElement(outfit, {
        index: idx,
        total: state.composerResult.outfits.length,
        showFeedback: false
      })
    )
  );
}

function renderCloset() {
  const outfits = state.closet;
  const favCount = outfits.filter((o) => o.favorite).length;
  document.getElementById("closet-stats-pill").textContent = `${outfits.length} Saved \u2022 ${favCount} Pinned`;

  const searchInput = document.getElementById("closet-search-input");
  if (searchInput.value !== state.closetSearch) {
    searchInput.value = state.closetSearch;
  }
  document.getElementById("closet-search-clear").classList.toggle("hidden", !state.closetSearch);

  // Style tags row
  const activeTokens = parseQueryTokens(state.closetSearch);
  const availableTags = collectAvailableTags(outfits);
  const tagsRow = document.getElementById("closet-tag-chips");
  tagsRow.replaceChildren(
    ...availableTags.map((tag) => {
      const active = activeTokens.includes(tag.toLowerCase());
      const btn = el("button", `filter-chip ${active ? "active" : ""}`, `#${tag}`);
      btn.type = "button";
      btn.addEventListener("click", () => {
        state.closetSearch = toggleTagInQuery(state.closetSearch, tag);
        renderCloset();
      });
      return btn;
    })
  );

  // Audience/favorites filter chips
  const audienceRow = document.getElementById("closet-audience-chips");
  audienceRow.replaceChildren(
    ...CATALOG.closetFilters.map((filter) => {
      const btn = el("button", `filter-chip ${state.closetFilter === filter ? "active" : ""}`, filter);
      btn.type = "button";
      btn.addEventListener("click", () => {
        state.closetFilter = filter;
        renderCloset();
      });
      return btn;
    })
  );

  const filtered = outfits.filter((o) => matchesClosetFilter(o, state.closetFilter, state.closetSearch));
  const hasFilters = state.closetFilter !== "All" || activeTokens.length > 0;
  const matchBar = document.getElementById("closet-match-bar");
  matchBar.classList.toggle("hidden", !(hasFilters && outfits.length > 0));
  if (hasFilters) {
    document.getElementById("closet-match-count").textContent =
      `Showing ${filtered.length} of ${outfits.length} saved looks`;
  }

  const grid = document.getElementById("closet-grid");
  const emptyEl = document.getElementById("closet-empty-state");
  const emptyMsg = document.getElementById("closet-empty-message");

  if (filtered.length === 0) {
    grid.replaceChildren();
    emptyEl.classList.remove("hidden");
    emptyMsg.textContent = outfits.length === 0
      ? "Bookmark curated ensembles from Modest Tailor Studio to keep your favorite modest looks and notes ready anytime."
      : `No saved outfits match "${state.closetSearch.trim() || state.closetFilter}".`;
    return;
  }

  emptyEl.classList.add("hidden");

  const cards = filtered.map((outfit) => {
    const card = el("article", "closet-card");

    const banner = el("div", "closet-card-banner");
    banner.addEventListener("click", (e) => {
      if (e.target.closest(".fav-heart-btn")) return;
      openClosetDetailModal(outfit.id);
    });

    const bannerImg = el("img", "closet-banner-img");
    bannerImg.src = resolveOutfitDesignImage(outfit);
    bannerImg.alt = `${outfit.title || "Saved modest outfit"} design preview`;
    bannerImg.loading = "lazy";

    const bannerScrim = el("div", "closet-banner-scrim");

    const bannerTop = el("div", "closet-banner-top");
    bannerTop.append(el("span", "closet-audience-tag", outfit.audience || "Ladies"));

    const favBtn = el(
      "button",
      `fav-heart-btn ${outfit.favorite ? "is-fav" : ""}`,
      outfit.favorite ? "\u2665" : "\u2661"
    );
    favBtn.type = "button";
    favBtn.setAttribute("aria-label", `Toggle favorite for ${outfit.title}`);
    favBtn.addEventListener("click", () => toggleClosetFavorite(outfit.id));
    bannerTop.append(favBtn);

    const occasionSpan = el("span", "small closet-banner-occasion", outfit.occasion || "Curated Look");
    banner.append(bannerImg, bannerScrim, bannerTop, occasionSpan);
    card.append(banner);

    const body = el("div", "closet-card-body");
    const titleEl = el("h3", "closet-card-title serif-title", outfit.title);
    titleEl.addEventListener("click", () => openClosetDetailModal(outfit.id));
    body.append(titleEl);

    const derived = deriveStyleTags(outfit).slice(0, 3);
    if (derived.length > 0) {
      const tagRow = el("div", "tag-row");
      derived.forEach((t) => tagRow.append(el("span", "tag-chip", `#${t.toLowerCase()}`)));
      body.append(tagRow);
    }

    body.append(
      el("div", "small muted", `${(outfit.matchedPieces || []).length} matched pieces`),
      el("div", "boutique-source", (outfit.boutiqueSources || ["Local Atelier"])[0])
    );

    const footer = el("div", "closet-card-footer");
    const viewBtn = el("button", "btn btn-sm btn-outline", "Details");
    viewBtn.type = "button";
    viewBtn.addEventListener("click", () => openClosetDetailModal(outfit.id));

    const editBtn = el("button", "btn btn-sm btn-outline", "Edit");
    editBtn.type = "button";
    editBtn.addEventListener("click", () => openClosetEditModal(outfit.id));

    const delBtn = el("button", "btn btn-sm btn-ghost", "Delete");
    delBtn.type = "button";
    delBtn.addEventListener("click", () => deleteClosetOutfit(outfit.id));

    footer.append(viewBtn, editBtn, delBtn);
    body.append(footer);
    card.append(body);
    return card;
  });

  grid.replaceChildren(...cards);
}

function renderAll() {
  renderMastheadAndCharter();
  renderDailyEdit();
  renderComposerControls();
  renderComposerResults();
  renderCloset();
}

/* Closet Actions */
function toggleClosetFavorite(outfitId) {
  state.closet = state.closet.map((o) =>
    o.id === outfitId ? { ...o, favorite: !o.favorite } : o
  );
  saveClosetToStorage(state.closet);
  renderAll();
  if (state.activeDetailOutfitId === outfitId) {
    openClosetDetailModal(outfitId);
  }
}

function deleteClosetOutfit(outfitId) {
  state.closet = state.closet.filter((o) => o.id !== outfitId);
  saveClosetToStorage(state.closet);
  if (state.activeDetailOutfitId === outfitId) {
    closeClosetDetailModal();
  }
  showToast("Removed ensemble from My Closet.");
  renderAll();
}

/* Modals */
function populateSelect(id, options, selectedValue) {
  const select = document.getElementById(id);
  select.replaceChildren(
    ...options.map((opt) => {
      const o = document.createElement("option");
      o.value = opt;
      o.textContent = opt;
      if (opt === selectedValue) o.selected = true;
      return o;
    })
  );
}

function renderDraftPreferenceChips() {
  const colorsWrap = document.getElementById("pref-colors-chips");
  colorsWrap.replaceChildren(
    ...CATALOG.colors.map((c) => {
      const active = state.draftColors.includes(c.name);
      const btn = el("button", `filter-chip ${active ? "active" : ""}`);
      btn.type = "button";
      const dot = el("span", "swatch-dot");
      dot.style.backgroundColor = c.hex;
      btn.append(dot, document.createTextNode(c.name));
      btn.addEventListener("click", () => {
        state.draftColors = active
          ? state.draftColors.filter((n) => n !== c.name)
          : [...state.draftColors, c.name];
        renderDraftPreferenceChips();
      });
      return btn;
    })
  );

  const trendsWrap = document.getElementById("pref-trends-chips");
  trendsWrap.replaceChildren(
    ...CATALOG.trendTags.map((t) => {
      const active = state.draftTrends.includes(t);
      const btn = el("button", `filter-chip ${active ? "active" : ""}`, t);
      btn.type = "button";
      btn.addEventListener("click", () => {
        state.draftTrends = active
          ? state.draftTrends.filter((n) => n !== t)
          : [...state.draftTrends, t];
        renderDraftPreferenceChips();
      });
      return btn;
    })
  );
}

function openPreferencesModal() {
  const p = state.profile;
  populateSelect("pref-audience", CATALOG.audiences, p.audience);
  document.getElementById("pref-district").value = p.cityOrArea || "";
  populateSelect("pref-neckline", CATALOG.necklines, p.necklineRule);
  populateSelect("pref-sleeve", CATALOG.sleeves, p.sleeveRule);
  populateSelect("pref-hemline", CATALOG.hemlines, p.hemlineRule);
  populateSelect("pref-fit", CATALOG.fits, p.fitRule);
  document.getElementById("pref-hijab").checked = Boolean(p.hijabFriendly);
  populateSelect("pref-top-size", CATALOG.ladiesSizes, p.topSize);
  populateSelect("pref-bottom-size", CATALOG.ladiesSizes, p.bottomSize);
  populateSelect("pref-abaya-length", CATALOG.abayaLengths, p.dressAbayaLength);
  populateSelect("pref-girls-size", CATALOG.girlsTeensSizes, p.girlsSize);

  state.draftColors = [...(p.favoriteColors || [])];
  state.draftTrends = [...(p.trendTags || [])];
  renderDraftPreferenceChips();

  document.getElementById("preferences-modal").classList.remove("hidden");
}

function closePreferencesModal() {
  document.getElementById("preferences-modal").classList.add("hidden");
}

function openClosetDetailModal(outfitId) {
  const outfit = state.closet.find((o) => o.id === outfitId);
  if (!outfit) return;
  state.activeDetailOutfitId = outfitId;

  document.getElementById("detail-audience-pill").textContent = outfit.audience || "Ladies";
  document.getElementById("detail-modal-title").textContent = outfit.title;
  document.getElementById("detail-occasion-line").textContent = outfit.occasion || "Curated Look";
  const detailImg = document.getElementById("detail-design-img");
  if (detailImg) {
    detailImg.src = resolveOutfitDesignImage(outfit);
    detailImg.alt = `${outfit.title || "Saved outfit"} design preview`;
  }
  document.getElementById("detail-coverage-box").textContent =
    outfit.coverageVerification || "Verified modest coverage";
  document.getElementById("detail-trend-text").textContent = outfit.trendHighlight || "Editorial capsule";
  document.getElementById("detail-boutiques-text").textContent =
    (outfit.boutiqueSources || ["Local Atelier"]).join(" \u2022 ");
  document.getElementById("detail-notes-text").textContent = outfit.stylingNotes || "";

  const piecesRow = document.getElementById("detail-pieces-row");
  piecesRow.replaceChildren(
    ...(outfit.matchedPieces || []).map((piece) => el("span", "tag-chip", piece))
  );

  document.getElementById("detail-fav-btn").textContent =
    outfit.favorite ? "\u2665 Favorited" : "\u2661 Favorite";
  document.getElementById("closet-detail-modal").classList.remove("hidden");
}

function closeClosetDetailModal() {
  state.activeDetailOutfitId = null;
  document.getElementById("closet-detail-modal").classList.add("hidden");
}

function openClosetEditModal(outfitId) {
  const outfit = state.closet.find((o) => o.id === outfitId);
  if (!outfit) return;
  state.editingOutfitId = outfitId;
  document.getElementById("edit-outfit-title").value = outfit.title || "";
  document.getElementById("edit-outfit-notes").value = outfit.stylingNotes || "";
  document.getElementById("closet-edit-modal").classList.remove("hidden");
}

function closeClosetEditModal() {
  state.editingOutfitId = null;
  document.getElementById("closet-edit-modal").classList.add("hidden");
}

/* Shared Deep-Link Banner */
function hydrateSharedDeepLink() {
  const params = new URLSearchParams(window.location.search);
  const title = params.get("title");
  if (!title) return;

  const banner = document.getElementById("shared-look-banner");
  const titleEl = document.getElementById("shared-title");
  const metaEl = document.getElementById("shared-meta");
  const covEl = document.getElementById("shared-coverage");
  const notesEl = document.getElementById("shared-notes");
  const piecesEl = document.getElementById("shared-pieces");
  const deepLinkEl = document.getElementById("shared-deep-link");
  const saveBtn = document.getElementById("save-shared-btn");

  const audience = params.get("audience") || "Ladies";
  const occasion = params.get("occasion") || "Curated Look";
  const coverage = params.get("coverage") || "Verified modest neckline, sleeve & hemline coverage";
  const notes = params.get("notes") || "";
  const pieces = (params.get("pieces") || "").split("|").map((s) => s.trim()).filter(Boolean);

  titleEl.textContent = title;
  metaEl.textContent = `${audience} \u2022 ${occasion}`;
  const sharedImg = document.getElementById("shared-design-img");
  if (sharedImg) {
    sharedImg.src = resolveOutfitDesignImage({ title, audience, occasion, matchedPieces: pieces });
    sharedImg.alt = `${title} design preview`;
    sharedImg.classList.remove("hidden");
  }
  covEl.textContent = coverage;
  notesEl.textContent = notes;
  piecesEl.replaceChildren(...pieces.map((p) => el("span", "tag-chip", p)));

  const query = params.toString();
  deepLinkEl.setAttribute("href", query ? `modesttailor://outfit?${query}` : "modesttailor://outfit");

  saveBtn.onclick = () => {
    toggleSaveOutfit({
      title,
      audience,
      occasion,
      coverageVerification: coverage,
      stylingNotes: notes,
      matchedPieces: pieces,
      boutiqueSources: ["Shared Atelier Look"]
    });
  };
  document.getElementById("dismiss-shared").onclick = () => banner.classList.add("hidden");
  banner.classList.remove("hidden");
}

/* Event Wiring */
document.addEventListener("DOMContentLoaded", () => {
  renderAll();
  hydrateSharedDeepLink();

  document.getElementById("nav-tab-studio").addEventListener("click", () => switchView("studio"));
  document.getElementById("nav-tab-closet").addEventListener("click", () => switchView("closet"));
  document.getElementById("brand-home-link").addEventListener("click", (e) => {
    e.preventDefault();
    switchView("studio");
  });
  document.getElementById("nav-open-preferences").addEventListener("click", openPreferencesModal);

  document.getElementById("metric-stock-btn").addEventListener("click", openPreferencesModal);
  document.getElementById("metric-closet-btn").addEventListener("click", () => switchView("closet"));
  document.getElementById("charter-summary-btn").addEventListener("click", openPreferencesModal);

  document.getElementById("qa-daily-btn").addEventListener("click", () => {
    document.getElementById("daily-edit-section").scrollIntoView({ behavior: "smooth" });
  });
  document.getElementById("qa-closet-btn").addEventListener("click", () => switchView("closet"));
  document.getElementById("qa-preferences-btn").addEventListener("click", openPreferencesModal);

  document.getElementById("refresh-daily-btn").addEventListener("click", () => {
    state.dailyRotationSeed += 1;
    renderDailyEdit();
    showToast("Refreshed today's daily personalized edit.");
  });

  document.getElementById("starter-ladies-btn").addEventListener("click", () => {
    state.selectedOccasion = "Work & Studio";
    state.selectedSeason = "Crisp Autumn Layering";
    state.customNote = "Refined Ladies silhouette pairing pleated satin drape with tailored linen outerwear";
    renderComposerControls();
    document.getElementById("composer-section").scrollIntoView({ behavior: "smooth" });
  });

  document.getElementById("starter-girls-btn").addEventListener("click", () => {
    state.selectedOccasion = "Weekend Garden Brunch";
    state.selectedSeason = "Warm Sunlit Spring";
    state.customNote = "Playful, breathable Girls & Teens modest capsule with full-sleeve comfort";
    renderComposerControls();
    document.getElementById("composer-section").scrollIntoView({ behavior: "smooth" });
  });

  document.getElementById("composer-note-input").addEventListener("input", (e) => {
    state.customNote = e.target.value;
  });

  document.getElementById("generate-outfits-btn").addEventListener("click", () => {
    state.composerResult = generateBespokeRecommendation(
      state.profile,
      state.feedback,
      state.selectedOccasion,
      state.selectedSeason,
      state.customNote
    );
    renderComposerResults();
    document.getElementById("composer-results-wrap").scrollIntoView({ behavior: "smooth", block: "nearest" });
  });

  // Closet controls
  document.getElementById("closet-back-studio-btn").addEventListener("click", () => switchView("studio"));
  document.getElementById("closet-empty-cta").addEventListener("click", () => switchView("studio"));
  document.getElementById("closet-search-input").addEventListener("input", (e) => {
    state.closetSearch = e.target.value;
    renderCloset();
  });
  document.getElementById("closet-search-clear").addEventListener("click", () => {
    state.closetSearch = "";
    renderCloset();
  });
  document.getElementById("closet-clear-filters-btn").addEventListener("click", () => {
    state.closetFilter = "All";
    state.closetSearch = "";
    renderCloset();
  });

  // Preferences Modal
  document.getElementById("close-preferences-btn").addEventListener("click", closePreferencesModal);
  document.getElementById("cancel-preferences-btn").addEventListener("click", closePreferencesModal);
  document.getElementById("preferences-form").addEventListener("submit", (e) => {
    e.preventDefault();
    state.profile = {
      audience: document.getElementById("pref-audience").value,
      cityOrArea: document.getElementById("pref-district").value.trim() || "Oakbrook & Promenade",
      necklineRule: document.getElementById("pref-neckline").value,
      sleeveRule: document.getElementById("pref-sleeve").value,
      hemlineRule: document.getElementById("pref-hemline").value,
      fitRule: document.getElementById("pref-fit").value,
      hijabFriendly: document.getElementById("pref-hijab").checked,
      topSize: document.getElementById("pref-top-size").value,
      bottomSize: document.getElementById("pref-bottom-size").value,
      dressAbayaLength: document.getElementById("pref-abaya-length").value,
      girlsSize: document.getElementById("pref-girls-size").value,
      favoriteColors: state.draftColors.length > 0 ? [...state.draftColors] : ["Rosewood"],
      trendTags: state.draftTrends.length > 0 ? [...state.draftTrends] : ["Quiet Luxury Linen"]
    };
    saveProfileToStorage(state.profile);
    if (state.composerResult) {
      state.composerResult = generateBespokeRecommendation(
        state.profile,
        state.feedback,
        state.selectedOccasion,
        state.selectedSeason,
        state.customNote
      );
    }
    closePreferencesModal();
    renderAll();
    showToast("Saved modesty charter, clothing sizes & refreshed recommendations.");
  });

  // Closet Detail & Edit Modals
  document.getElementById("close-detail-btn").addEventListener("click", closeClosetDetailModal);
  document.getElementById("detail-fav-btn").addEventListener("click", () => {
    if (state.activeDetailOutfitId) toggleClosetFavorite(state.activeDetailOutfitId);
  });
  document.getElementById("detail-share-btn").addEventListener("click", () => {
    const outfit = state.closet.find((o) => o.id === state.activeDetailOutfitId);
    if (outfit) shareOutfit(outfit);
  });
  document.getElementById("detail-edit-btn").addEventListener("click", () => {
    const id = state.activeDetailOutfitId;
    closeClosetDetailModal();
    if (id) openClosetEditModal(id);
  });
  document.getElementById("detail-delete-btn").addEventListener("click", () => {
    if (state.activeDetailOutfitId) deleteClosetOutfit(state.activeDetailOutfitId);
  });

  document.getElementById("close-edit-btn").addEventListener("click", closeClosetEditModal);
  document.getElementById("cancel-edit-btn").addEventListener("click", closeClosetEditModal);
  document.getElementById("closet-edit-form").addEventListener("submit", (e) => {
    e.preventDefault();
    const id = state.editingOutfitId;
    if (!id) return;
    const newTitle = document.getElementById("edit-outfit-title").value.trim();
    const newNotes = document.getElementById("edit-outfit-notes").value.trim();
    if (!newTitle) return;

    state.closet = state.closet.map((o) =>
      o.id === id ? { ...o, title: newTitle, stylingNotes: newNotes } : o
    );
    saveClosetToStorage(state.closet);
    closeClosetEditModal();
    renderAll();
    showToast("Updated closet look notes.");
  });
});
