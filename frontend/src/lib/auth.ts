import { csrf, me } from "@/client";
import { client } from "@/client/client.gen";
import type { UserResponse } from "@/client/types.gen";

const CSRF_HEADER_NAME = "X-XSRF-TOKEN";
const AUTH_STATE_CHANGING_PATHS = new Set([
  "/api/auth/login",
  "/api/auth/logout",
  "/api/auth/signup",
  "/api/auth/verify",
]);
const SAFE_METHODS = new Set(["GET", "HEAD", "OPTIONS", "TRACE"]);

let csrfToken: string | undefined;
let csrfInitializationPromise: Promise<string | undefined> | null = null;
let securityConfigured = false;

function clearCsrfToken() {
  csrfToken = undefined;
  csrfInitializationPromise = null;
}

export async function ensureCsrfToken(): Promise<string | undefined> {
  if (csrfToken) {
    return csrfToken;
  }

  if (!csrfInitializationPromise) {
    csrfInitializationPromise = csrf()
      .then(({ data }) => {
        const token = data?.token;
        csrfToken = token;
        return token;
      })
      .finally(() => {
        csrfInitializationPromise = null;
      });
  }

  return await csrfInitializationPromise;
}

export function configureApiSecurity() {
  client.setConfig({
    credentials: "include",
  });

  if (securityConfigured) {
    return;
  }

  client.interceptors.request.use(async (request) => {
    const method = request.method.toUpperCase();
    if (SAFE_METHODS.has(method)) {
      return request;
    }

    let token = csrfToken;
    if (!token) {
      token = await ensureCsrfToken();
    }

    if (!token) {
      return request;
    }

    const headers = new Headers(request.headers);
    headers.set(CSRF_HEADER_NAME, token);

    return new Request(request, { headers });
  });

  client.interceptors.response.use(async (response, request) => {
    if (response.status === 403) {
      clearCsrfToken();
      return response;
    }

    if (!response.ok) {
      return response;
    }

    const pathname = new URL(request.url).pathname;
    if (AUTH_STATE_CHANGING_PATHS.has(pathname)) {
      clearCsrfToken();
    }

    return response;
  });

  securityConfigured = true;
}

export async function getCurrentUser(): Promise<UserResponse | null> {
  const { data, error } = await me();

  if (error || !data) {
    return null;
  }

  return data;
}
