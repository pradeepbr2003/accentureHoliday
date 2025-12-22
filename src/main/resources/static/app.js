const $ = (sel) => document.querySelector(sel);
 
// Derive base URL from current location (handles context path)
const base = (() => {
  const { origin, pathname } = window.location;
  // strip trailing /index.html if any
  const path = pathname.replace(/index\.html$/i, '').replace(/\/$/, '');
  return `${origin}${path}`;
})();
 
const API = {
  mandatory: `${base}/api/holidays/mandatory`,
  floating: `${base}/api/holidays/floating`,
  month: (type, m) => `${base}/api/holidays/${type}/month/${m}`,
  search: (q) => `${base}/api/holidays/search/${encodeURIComponent(q)}`,
};
 
const state = { lastEndpoint: null };
 
function setLoading(loading, label = '') {
  const spinner = $('#spinner');
  const meta = $('#resultMeta');
  spinner.classList.toggle('hidden', !loading);
  meta.textContent = loading ? `Loading ${label}…` : (state.lastEndpoint ? `${state.lastEndpoint}` : '');
}
 
function showError(err) {
  const el = $('#error');
  el.textContent = err?.message || String(err);
  el.classList.remove('hidden');
}
 
function clearError() {
  const el = $('#error');
  el.textContent = '';
  el.classList.add('hidden');
}
 
function render(rows) {
  const tbody = $('#resultsBody');
  tbody.innerHTML = '';
 
  if (!Array.isArray(rows) || rows.length === 0) {
    const tr = document.createElement('tr');
    const td = document.createElement('td');
    td.colSpan = 5;
    td.className = 'muted';
    td.textContent = 'No results';
    tr.appendChild(td);
    tbody.appendChild(tr);
    return;
  }
 
  for (const h of rows) {
    const tr = document.createElement('tr');
    const tds = [
      h.name,
      // date as ISO string if present
      (h.date ? h.date : ''),
      h.dayOfWeek,
      h.city,
      h.type
    ].map(v => {
      const td = document.createElement('td');
      td.textContent = v ?? '';
      return td;
    });
    tds.forEach(td => tr.appendChild(td));
    tbody.appendChild(tr);
  }
}
 
async function fetchJson(url, label) {
  setLoading(true, label);
  clearError();
  try {
    const res = await fetch(url, { headers: { 'Accept': 'application/json' } });
    if (!res.ok) throw new Error(`${res.status} ${res.statusText}`);
    const data = await res.json();
    state.lastEndpoint = `${url}`;
    render(data);
  } catch (e) {
    showError(e);
  } finally {
    setLoading(false);
  }
}
 
function init() {
  $('#btnMandatory').addEventListener('click', () => fetchJson(API.mandatory, 'mandatory holidays'));
  $('#btnFloating').addEventListener('click', () => fetchJson(API.floating, 'floating holidays'));
  $('#btnByMonth').addEventListener('click', () => {
    const m = $('#monthSelect').value;
    const t = $('#monthType').value;
    if (!m) return showError(new Error('Please choose a month'));
    fetchJson(API.month(t, m), `${t} month ${m}`);
  });
  $('#btnSearch').addEventListener('click', () => {
    const q = $('#searchKeyword').value.trim();
    if (!q) return showError(new Error('Enter a keyword to search'));
    fetchJson(API.search(q), `search "${q}"`);
  });
}
 
document.addEventListener('DOMContentLoaded', init);