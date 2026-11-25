const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

function validProfile(uid, ts) {
  return {
    userId: uid,
    displayName: "Amira",
    audience: "Ladies & Girls",
    necklineRule: "High Crew / Collar",
    sleeveRule: "Full Wrist-Length",
    hemlineRule: "Maxi / Ankle-Length",
    fitRule: "Flowy & Relaxed",
    hijabFriendly: true,
    cityOrArea: "Oakbrook District",
    trendTags: ["Quiet Luxury Linen", "Pleated Satin"],
    favoriteColors: ["Rosewood", "Silk Ivory"],
    topSize: "M",
    bottomSize: "M",
    dressAbayaLength: "54",
    girlsSize: "Girls 10-12",
    createdAt: ts,
    updatedAt: ts,
  };
}

function validInventory(uid, ts) {
  return {
    userId: uid,
    name: "Rosewood Pleated Satin Maxi Skirt",
    category: "Bottom",
    audience: "Ladies",
    boutiqueName: "Atelier Noor",
    neighborhood: "Downtown Promenade",
    priceUsd: 88.0,
    color: "Rosewood",
    sizeRange: "XS - XL",
    coverageSummary: "Opaque ankle-length pleated drape with high comfort waist",
    trendTag: "Pleated Satin Movement",
    inStock: true,
    createdAt: ts,
    updatedAt: ts,
  };
}

function validOutfit(uid, ts) {
  return {
    userId: uid,
    title: "Autumn Limestone Atelier Ensemble",
    audience: "Ladies",
    occasion: "Weekend Brunch & Gallery",
    trendHighlight: "Pairs structured linen tailoring with fluid pleated satin.",
    coverageVerification: "Full wrist sleeves, high mock neckline, ankle-skimming opaque skirt.",
    stylingNotes: "Tuck the bishop-sleeve blouse loosely and drape the chiffon shawl over one shoulder.",
    matchedPieces: ["Rosewood Pleated Satin Maxi Skirt", "Ivory Silk Bishop Blouse"],
    boutiqueSources: ["Atelier Noor"],
    favorite: true,
    createdAt: ts,
    updatedAt: ts,
  };
}

function validFeedback(uid, ts) {
  return {
    userId: uid,
    outfitTitle: "Monday Sage Linen Trench Edit",
    vote: "UP",
    dateKey: "2026-11-23",
    audience: "Ladies",
    occasion: "Monday Daily Edit",
    trendHighlight: "Utility Longline Trench",
    matchedPieces: ["Sage Belted Longline Linen Trench Coat", "Silk Ivory Blouse"],
    createdAt: ts,
    updatedAt: ts,
  };
}

test("1. Unauthenticated user cannot read or list profile or inventory", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("inventory").get()
  );
});

test("2. Authenticated owner can create and read StyleProfile", async () => {
  const ctx = testEnv.authenticatedContext(ALICE_UID);
  const db = ctx.firestore();
  const now = new Date(Date.now() - 1000);
  await assertSucceeds(
    db.collection("users").doc(ALICE_UID).set(validProfile(ALICE_UID, now))
  );
  await assertSucceeds(db.collection("users").doc(ALICE_UID).get());
});

test("3. Cross-user access to StyleProfile and InventoryItem is rejected", async () => {
  const now = new Date(Date.now() - 1000);
  await testEnv.withSecurityRulesDisabled(async (admin) => {
    const db = admin.firestore();
    await db.collection("users").doc(ALICE_UID).set(validProfile(ALICE_UID, now));
    await db
      .collection("users")
      .doc(ALICE_UID)
      .collection("inventory")
      .doc("skirt_01")
      .set(validInventory(ALICE_UID, now));
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
  await assertFails(
    bobDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("inventory")
      .doc("skirt_01")
      .get()
  );
});

test("4. Shadow/Ghost field injection is rejected via hasOnly()", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date(Date.now() - 1000);
  await assertFails(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .set({ ...validProfile(ALICE_UID, now), isVerifiedAdmin: true })
  );
});

test("5. Identity spoofing (mismatched userId) is rejected on create", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date(Date.now() - 1000);
  await assertFails(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("inventory")
      .doc("item_spoof")
      .set({ ...validInventory(ALICE_UID, now), userId: BOB_UID })
  );
});

test("6. Client query alignment: owner can list inventory and saved outfits with userId filter", async () => {
  const now = new Date(Date.now() - 1000);
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("inventory")
      .doc("item_01")
      .set(validInventory(ALICE_UID, now))
  );
  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("saved_outfits")
      .doc("look_01")
      .set(validOutfit(ALICE_UID, now))
  );

  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("inventory")
      .where("userId", "==", ALICE_UID)
      .get()
  );
  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("saved_outfits")
      .where("userId", "==", ALICE_UID)
      .get()
  );
});

test("7. Immutable createdAt cannot be mutated on update", async () => {
  const initialTime = new Date(Date.now() - 5000);
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("inventory")
      .doc("item_01")
      .set(validInventory(ALICE_UID, initialTime))
  );

  // Valid stock toggle update succeeds
  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("inventory")
      .doc("item_01")
      .update({
        inStock: false,
        updatedAt: new Date(Date.now() - 1000),
      })
  );

  // Tampering with createdAt fails
  await assertFails(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("inventory")
      .doc("item_01")
      .update({
        inStock: true,
        createdAt: new Date(Date.now() - 1000),
        updatedAt: new Date(Date.now() - 1000),
      })
  );
});

test("8. Invalid enum values or negative prices are rejected", async () => {
  const now = new Date(Date.now() - 1000);
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("inventory")
      .doc("item_bad_enum")
      .set({ ...validInventory(ALICE_UID, now), audience: "Toddlers" })
  );
  await assertFails(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("inventory")
      .doc("item_bad_price")
      .set({ ...validInventory(ALICE_UID, now), priceUsd: -10 })
  );
});

test("9. Outfit feedback allows owner create/update/list/delete and rejects invalid votes or cross-user access", async () => {
  const created = new Date(Date.now() - 5000);
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const feedbackRef = aliceDb
    .collection("users")
    .doc(ALICE_UID)
    .collection("outfit_feedback")
    .doc("2026_11_23_sage_linen");

  await assertSucceeds(feedbackRef.set(validFeedback(ALICE_UID, created)));
  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("outfit_feedback")
      .where("userId", "==", ALICE_UID)
      .get()
  );

  // Switch vote from UP to DOWN
  await assertSucceeds(
    feedbackRef.update({
      vote: "DOWN",
      trendHighlight: "Utility Longline Trench",
      matchedPieces: ["Sage Belted Longline Linen Trench Coat"],
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Invalid vote enum fails
  await assertFails(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("outfit_feedback")
      .doc("bad_vote_doc")
      .set({ ...validFeedback(ALICE_UID, created), vote: "MAYBE" })
  );

  // Cross-user read/write fails
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(
    bobDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("outfit_feedback")
      .doc("2026_11_23_sage_linen")
      .get()
  );

  // Owner can delete (clear feedback)
  await assertSucceeds(feedbackRef.delete());
});

test("10. Clothing size fields can be updated on StyleProfile and oversized values are rejected", async () => {
  const created = new Date(Date.now() - 5000);
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const profileRef = aliceDb.collection("users").doc(ALICE_UID);

  await assertSucceeds(profileRef.set(validProfile(ALICE_UID, created)));
  await assertSucceeds(
    profileRef.update({
      topSize: "L",
      bottomSize: "XL",
      dressAbayaLength: "56",
      girlsSize: "Teens M",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  await assertFails(
    profileRef.update({
      topSize: "A".repeat(30),
      updatedAt: new Date(Date.now() - 1000),
    })
  );
});
