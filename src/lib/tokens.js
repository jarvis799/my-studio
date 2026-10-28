const KEY = 'devstyle.github.token';

export function getToken() {
  return localStorage.getItem(KEY) || '';
}

export function setToken(value) {
  if (value && value.trim()) localStorage.setItem(KEY, value.trim());
  else localStorage.removeItem(KEY);
}
