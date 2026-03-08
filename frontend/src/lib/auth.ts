import { csrf, me } from "@/client";
import { client } from "@/client/client.gen";
import type { UserResponse } from "@/client/types.gen";

const CSRF_COOKIE_NAME = "XSRF-TOKEN";
const CSRF_HEADER_NAME = "X-XSRF-TOKEN";
const SAFE_METHODS = new Set(["GET", "HEAD", "OPTIONS", "TRACE"]);

let csrfInitializationPromise: Promise<void> | null = null;
let securityConfigured = false;

function getCookie(name: string): string | undefined {
  if (typeof document === "undefined") {
    return undefined;
  }

  const prefixedName = `${name}=`;
  const cookie = document.cookie
    .split(";")
    .map((entry) => entry.trim())
    .find((entry) => entry.startsWith(prefixedName));

  if (!cookie) {
    return undefined;
  }

  return decodeURIComponent(cookie.slice(prefixedName.length));
}

export async function ensureCsrfToken(): Promise<string | undefined> {
  const existing = getCookie(CSRF_COOKIE_NAME);
  if (existing) {
    return existing;
  }

  if (!csrfInitializationPromise) {
    csrfInitializationPromise = csrf()
      .then(() => undefined)
      .finally(() => {
        csrfInitializationPromise = null;
      });
  }

  await csrfInitializationPromise;
  return getCookie(CSRF_COOKIE_NAME);
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

    let csrfToken = getCookie(CSRF_COOKIE_NAME);
    if (!csrfToken) {
      csrfToken = await ensureCsrfToken();
    }

    if (!csrfToken) {
      return request;
    }

    const headers = new Headers(request.headers);
    headers.set(CSRF_HEADER_NAME, csrfToken);

    return new Request(request, { headers });
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
