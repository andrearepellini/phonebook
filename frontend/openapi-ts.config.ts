import { defineConfig } from "@hey-api/openapi-ts";

export default defineConfig({
  input: "../schema/openapi.yaml",
  output: {
    format: "prettier",
    lint: "eslint",
    path: "./src/client",
  },
});
