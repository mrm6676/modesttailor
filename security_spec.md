# Modest Tailor — Firestore Security Specification (Phase 0 TDD)

## 1. Data Invariants & Authoritative Ownership
1. **Strict User Isolation**: Every `StyleProfile` (`/users/{userId}`), `InventoryItem` (`/users/{userId}/inventory/{itemId}`), and `SavedOutfit` (`/users/{userId}/saved_outfits/{outfitId}`) belongs exclusively to `request.auth.uid == userId`.
2. **Denormalized Ownership Alignment**: Every document stores `userId == request.auth.uid` matching the `{userId}` path wildcard, enabling zero-cost `allow list` verification (`resource.data.userId == request.auth.uid`) without cross-document `get()` calls.
3. **Immutable Provenance**: `userId` and `createdAt` can never be mutated during updates.
4. **Server-Side Temporal Integrity**: `createdAt` and `updatedAt` must be Firestore `timestamp` values `<= request.time`.
5. **Bounded Collections & Strings**: Every string has explicit `.size()` bounds; arrays (`trendTags`, `favoriteColors`, `matchedPieces`, `boutiqueSources`) have `.size() <= 10` or `<= 12`.

## 2. The "Dirty Dozen" Adversarial Payloads
1. **Unauthenticated Profile Read**: `GET /users/alice_123` with `auth == null` -> REJECT.
2. **Cross-User Inventory Read**: `bob_456` reading `/users/alice_123/inventory/item_1` -> REJECT.
3. **Unfiltered Inventory List**: `alice_123` listing `/users/alice_123/inventory` where `userId != alice_123` -> REJECT.
4. **Identity Spoofing on Create**: `alice_123` writing `{"userId": "bob_456"}` into `/users/alice_123/inventory/item_1` -> REJECT.
5. **Shadow / Ghost Field Injection**: Including `"isAdmin": true` or `"verified": true` in profile or inventory payload -> REJECT (`hasOnly` guard).
6. **Immutable `createdAt` Tampering**: Updating `createdAt` on an existing inventory item or saved outfit -> REJECT.
7. **Immutable `userId` Ownership Transfer**: Updating `userId` on an existing outfit to another UID -> REJECT.
8. **ID Poisoning Attack**: Creating `/users/alice_123/inventory/bad$id!@#` with invalid characters -> REJECT (`isValidId`).
9. **String Size Exhaustion (Denial of Wallet)**: Writing a 5,000-character string to `coverageSummary` or `stylingNotes` -> REJECT.
10. **Unbounded Array Injection**: Writing 25 items into `trendTags` or `matchedPieces` -> REJECT.
11. **Invalid Enum Value**: Writing `audience: "Everyone"` or `category: "Shoes"` outside allowed enum sets -> REJECT.
12. **Negative or Out-of-Bounds Price**: Writing `priceUsd: -15` or `priceUsd: 999999` on an inventory item -> REJECT.
