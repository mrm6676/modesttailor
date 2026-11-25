const CAPSULES = [
  {
    title: "Sage Linen Trench & Silk Ivory Mock-Neck",
    audience: "Ladies",
    occasion: "Work & Studio",
    tags: ["ladies", "work"],
    sizes: "Tops XS–XXL • Length 54–58\"",
    coverage: "High collarbone mock neck, full wrist cuffs, ankle-grazing coat drape",
    pieces: ["Sage Belted Linen Trench", "Silk Ivory Mock-Neck Blouse", "Champagne Modal Hijab"]
  },
  {
    title: "Rosewood Sunburst Satin Evening Edit",
    audience: "Ladies",
    occasion: "Evening Celebration",
    tags: ["ladies", "formal"],
    sizes: "Skirts XS–XXL • Abaya 52–58\"",
    coverage: "Double-lined opaque satin maxi skirt paired with espresso satin-trimmed over-layer",
    pieces: ["Rosewood Pleated Satin Skirt", "Espresso Open Abaya", "Silk Ivory Wrap"]
  },
  {
    title: "Girls Chambray Tiered Garden Maxi",
    audience: "Girls",
    occasion: "Weekend Brunch",
    tags: ["girls", "summer"],
    sizes: "Girls 6–14",
    coverage: "Full-sleeve breathable cotton chambray with Peter-Pan collar and opaque cotton lining",
    pieces: ["Chambray Tiered Maxi Dress", "Dusty Rose Pointelle Cardigan", "Cotton Jersey Scarf"]
  }
];

function createTagChip(label) {
  const chip = document.createElement("span");
  chip.className = "tag-chip";
  chip.textContent = label;
  return chip;
}

function renderCapsules(filterKey) {
  const grid = document.getElementById("capsule-grid");
  if (!grid) return;

  const filtered = filterKey === "all"
    ? CAPSULES
    : CAPSULES.filter((item) => item.tags.includes(filterKey));

  const cards = filtered.map((item) => {
    const card = document.createElement("article");
    card.className = "capsule-card";

    const headerWrap = document.createElement("div");

    const metaRow = document.createElement("div");
    metaRow.className = "capsule-meta";

    const audienceSpan = document.createElement("span");
    audienceSpan.textContent = `${item.audience} \u2022 ${item.occasion}`;

    const sizesSpan = document.createElement("span");
    sizesSpan.textContent = item.sizes;

    metaRow.append(audienceSpan, sizesSpan);

    const heading = document.createElement("h3");
    heading.className = "serif-title";
    heading.style.fontSize = "1.35rem";
    heading.style.margin = "8px 0";
    heading.textContent = item.title;

    const coverageDiv = document.createElement("div");
    coverageDiv.className = "coverage-pill";
    coverageDiv.textContent = item.coverage;

    headerWrap.append(metaRow, heading, coverageDiv);

    const tagRow = document.createElement("div");
    tagRow.className = "tag-row";
    tagRow.append(...item.pieces.map(createTagChip));

    card.append(headerWrap, tagRow);
    return card;
  });

  grid.replaceChildren(...cards);
}

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
  const dismissBtn = document.getElementById("dismiss-shared");

  if (!banner || !titleEl) return;
  titleEl.textContent = title;
  if (metaEl) {
    metaEl.textContent = [params.get("audience") || "Ladies", params.get("occasion") || "Curated Look"]
      .join(" \u2022 ");
  }
  if (covEl) {
    covEl.textContent = params.get("coverage") || "Verified modest neckline, sleeve & hemline coverage";
  }
  if (notesEl) {
    notesEl.textContent = params.get("notes") || "";
  }

  if (piecesEl) {
    const piecesRaw = params.get("pieces") || "";
    const pieces = piecesRaw.split("|").map((s) => s.trim()).filter(Boolean);
    piecesEl.replaceChildren(...pieces.map(createTagChip));
  }

  if (deepLinkEl) {
    const query = params.toString();
    deepLinkEl.setAttribute("href", query ? `modesttailor://outfit?${query}` : "modesttailor://outfit");
  }
  if (dismissBtn) {
    dismissBtn.addEventListener("click", () => banner.classList.add("hidden"));
  }
  banner.classList.remove("hidden");
}

document.addEventListener("DOMContentLoaded", () => {
  renderCapsules("all");
  hydrateSharedDeepLink();

  document.querySelectorAll(".filter-btn").forEach((btn) => {
    btn.addEventListener("click", () => {
      document.querySelectorAll(".filter-btn").forEach((b) => b.classList.remove("active"));
      btn.classList.add("active");
      renderCapsules(btn.dataset.filter || "all");
    });
  });
});
