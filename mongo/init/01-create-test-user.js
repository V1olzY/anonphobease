db = db.getSiblingDB('anonphobease-test');

db.createUser({
  user: "test",
  pwd: "testpass",
  roles: [
    { role: "readWrite", db: "anonphobease-test" }
  ]
});
