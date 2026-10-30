import assert from "node:assert/strict";
import { readFile } from "node:fs/promises";
import { after, before, test } from "node:test";

import {
  assertFails,
  assertSucceeds,
  initializeTestEnvironment,
} from "@firebase/rules-unit-testing";

import { Timestamp, doc, getDoc, setDoc } from "firebase/firestore";

let testEnvironment;

before(async () => {
  const rules = await readFile(
    new URL("../firestore.rules", import.meta.url),
    "utf8",
  );

  testEnvironment = await initializeTestEnvironment({
    projectId: "mis-cosas-144ef",
    firestore: {
      host: "127.0.0.1",
      port: 8080,
      rules,
    },
  });
});

after(async () => {
  await testEnvironment?.cleanup();
});

function validUserProfile(displayName) {
  const createdAt = Timestamp.fromDate(new Date("2026-08-27T09:00:00.000Z"));

  return {
    displayName,
    createdAt,
    updatedAt: Timestamp.fromDate(new Date("2026-08-27T09:10:00.000Z")),
  };
}

test("cada UID solo puede acceder a su propio perfil", async () => {
  await testEnvironment.clearFirestore();

  const anonymousDatabase = testEnvironment.unauthenticatedContext().firestore();

  await assertFails(
    setDoc(
      doc(anonymousDatabase, "users", "uid-anonimo"),
      validUserProfile("Perfil anónimo"),
    ),
  );

  const databaseB = testEnvironment
    .authenticatedContext("uid-b")
    .firestore();

  await assertSucceeds(
    setDoc(doc(databaseB, "users", "uid-b"), validUserProfile("Perfil B")),
  );

  const databaseA = testEnvironment
    .authenticatedContext("uid-a")
    .firestore();

  await assertSucceeds(
    setDoc(doc(databaseA, "users", "uid-a"), validUserProfile("Perfil A")),
  );

  const ownProfile = doc(databaseA, "users", "uid-a");
  await assertSucceeds(getDoc(ownProfile));
  await assertSucceeds(
    setDoc(ownProfile, validUserProfile("Perfil A actualizado")),
  );

  const invalidProfiles = [
    { displayName: "Perfil sin fechas" },
    {
      ...validUserProfile("Fecha con tipo incorrecto"),
      createdAt: "2026-08-27T09:00:00.000Z",
    },
    validUserProfile(""),
    { ...validUserProfile("Campo adicional"), unexpected: true },
    {
      ...validUserProfile("Actualización anterior al alta"),
      updatedAt: Timestamp.fromDate(new Date("2026-08-27T08:00:00.000Z")),
    },
  ];

  for (const invalidProfile of invalidProfiles) {
    await assertFails(setDoc(ownProfile, invalidProfile));
  }

  await assertFails(getDoc(doc(databaseA, "users", "uid-b")));

  await assertFails(
    setDoc(
      doc(databaseA, "users", "uid-b"),
      validUserProfile("Intento de cambiar el perfil B"),
    ),
  );

  await assertFails(
    setDoc(doc(databaseA, "households", "hogar-prueba"), {
      name: "Hogar no permitido",
    }),
  );
});
