import assert from "node:assert/strict";
import { readFile } from "node:fs/promises";
import { after, before, test } from "node:test";

import {
  assertFails,
  assertSucceeds,
  initializeTestEnvironment,
} from "@firebase/rules-unit-testing";

import { doc, getDoc, setDoc } from "firebase/firestore";

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

test("cada UID solo puede acceder a su propio perfil", async () => {
  await testEnvironment.clearFirestore();

  const anonymousDatabase = testEnvironment.unauthenticatedContext().firestore();

  await assertFails(
    setDoc(doc(anonymousDatabase, "users", "uid-anonimo"), {
      displayName: "Perfil anónimo",
    }),
  );

  const databaseB = testEnvironment
    .authenticatedContext("uid-b")
    .firestore();

  await assertSucceeds(
    setDoc(doc(databaseB, "users", "uid-b"), {
      displayName: "Perfil B",
    }),
  );

  const databaseA = testEnvironment
    .authenticatedContext("uid-a")
    .firestore();

  await assertSucceeds(
    setDoc(doc(databaseA, "users", "uid-a"), {
      displayName: "Perfil A",
    }),
  );

  await assertFails(getDoc(doc(databaseA, "users", "uid-b")));

  await assertFails(
    setDoc(doc(databaseA, "users", "uid-b"), {
      displayName: "Intento de cambiar el perfil B",
    }),
  );

  await assertFails(
    setDoc(doc(databaseA, "households", "hogar-prueba"), {
      name: "Hogar no permitido",
    }),
  );
});
