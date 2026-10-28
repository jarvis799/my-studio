(async function () {
  var data = window.__DEVSTYLE_CONTENT__ || null;
  if (!data) {
    try {
      var res = await fetch('content.json', { cache: 'no-store' });
      if (res.ok) data = await res.json();
    } catch (e) {
      data = null;
    }
  }
  if (!data) return;
  var q = function (sel) { return document.querySelector(sel); };
  var all = function (sel) { return Array.prototype.slice.call(document.querySelectorAll(sel)); };

  var name = data.name || 'Portfolio';
  all('[data-name], [data-name-short], [data-name-copy]').forEach(function (el) {
    el.textContent = el.hasAttribute('data-name-short') ? name.split(' ')[0] : name;
  });
  if (data.role) q('[data-role]').textContent = data.role;
  if (data.tagline) q('[data-tagline]').textContent = data.tagline;
  if (data.about) q('[data-about]').textContent = data.about;
  if (data.location) q('[data-location]').textContent = data.location;
  if (data.photo) q('[data-avatar]').style.backgroundImage = 'url("' + data.photo + '")';
  document.title = name + (data.role ? ' · ' + data.role : '');

  if (data.email) {
    all('[data-email]').forEach(function (el) { el.href = 'mailto:' + data.email; });
    var mail = q('[data-email-text]');
    mail.textContent = data.email;
    mail.href = 'mailto:' + data.email;
  }
  if (data.phone) {
    q('[data-phone-wrap]').hidden = false;
    var phone = q('[data-phone-text]');
    phone.textContent = data.phone;
    phone.href = 'tel:' + data.phone.replace(/[^+0-9]/g, '');
  }
  var app = data.app || {};
  var download = q('[data-app-download]');
  if (app.repo) {
    download.hidden = false;
    download.href = 'https://github.com/' + app.repo + '/releases/latest';
    var note = download.querySelector('small');
    if (note && app.note) note.textContent = app.note;
  }

  var socials = q('[data-socials]');
  (data.socials || []).forEach(function (item) {
    if (!item || !item.url) return;
    var a = document.createElement('a');
    a.className = 'social';
    a.href = item.url;
    a.target = '_blank';
    a.rel = 'noreferrer';
    a.textContent = item.label || item.url;
    socials.appendChild(a);
  });

  var projects = q('[data-projects]');
  (data.projects || []).forEach(function (item) {
    var card = document.createElement('article');
    card.className = 'card';
    var title = document.createElement('h3');
    title.textContent = item.title || 'Project';
    var text = document.createElement('p');
    text.textContent = item.text || '';
    card.appendChild(title);
    card.appendChild(text);
    if (item.link) {
      var go = document.createElement('a');
      go.className = 'go';
      go.href = item.link;
      go.target = '_blank';
      go.rel = 'noreferrer';
      go.textContent = 'Open →';
      card.appendChild(go);
    }
    projects.appendChild(card);
  });
})();
