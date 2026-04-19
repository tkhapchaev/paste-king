const SESSION_KEY = "paste-king-session";
const MAX_TEXT_LENGTH = 65535;
const POST_PREVIEW_LENGTH = 520;

const state = {
  session: null,
  page: 0,
  limit: 10,
  totalPages: 0,
  isLoading: false,
};

const elements = {
  loginScreen: document.querySelector("#loginScreen"),
  dashboardScreen: document.querySelector("#dashboardScreen"),
  authPanel: document.querySelector("#authPanel"),
  composerPanel: document.querySelector("#composerPanel"),
  postsPanel: document.querySelector("#postsPanel"),
  sessionCard: document.querySelector("#sessionCard"),
  sessionLabel: document.querySelector("#sessionLabel"),
  loginForm: document.querySelector("#loginForm"),
  loginInput: document.querySelector("#loginInput"),
  passwordInput: document.querySelector("#passwordInput"),
  logoutButton: document.querySelector("#logoutButton"),
  postForm: document.querySelector("#postForm"),
  postText: document.querySelector("#postText"),
  textCounter: document.querySelector("#textCounter"),
  refreshButton: document.querySelector("#refreshButton"),
  limitSelect: document.querySelector("#limitSelect"),
  prevPageButton: document.querySelector("#prevPageButton"),
  nextPageButton: document.querySelector("#nextPageButton"),
  pageLabel: document.querySelector("#pageLabel"),
  postsList: document.querySelector("#postsList"),
  postTemplate: document.querySelector("#postTemplate"),
  toast: document.querySelector("#toast"),
};

function getAuthHeader() {
  if (!state.session?.token) {
    return {};
  }

  return {
    Authorization: `Bearer ${state.session.token}`,
  };
}

async function apiFetch(path, options = {}) {
  const response = await fetch(path, {
    ...options,
    credentials: "omit",
    headers: {
      ...getAuthHeader(),
      ...options.headers,
    },
  });

  if (response.status === 401) {
    clearSession();
    throw new Error("Сессия истекла или данные входа неверны");
  }

  if (!response.ok) {
    const error = await response.json().catch(() => null);
    throw new Error(error?.message || `Ошибка запроса: ${response.status}`);
  }

  if (response.status === 204) {
    return null;
  }

  return response.json();
}

function saveSession(session) {
  state.session = session;
  sessionStorage.setItem(SESSION_KEY, JSON.stringify(session));
  renderSession();
}

function restoreSession() {
  const rawSession = sessionStorage.getItem(SESSION_KEY);
  if (!rawSession) {
    renderSession();
    return;
  }

  try {
    const session = JSON.parse(rawSession);
    if (session?.login && session?.token) {
      state.session = session;
    } else {
      sessionStorage.removeItem(SESSION_KEY);
    }
  } catch {
    sessionStorage.removeItem(SESSION_KEY);
  }

  renderSession();
}

function clearSession() {
  state.session = null;
  state.page = 0;
  state.totalPages = 0;
  sessionStorage.removeItem(SESSION_KEY);
  renderSession();
  renderEmptyPosts("Войдите, чтобы увидеть свои посты");
}

function renderSession() {
  const isOnline = Boolean(state.session);
  elements.sessionCard.classList.toggle("is-online", isOnline);
  elements.loginScreen.classList.toggle("is-hidden", isOnline);
  elements.dashboardScreen.classList.toggle("is-hidden", !isOnline);
  elements.sessionLabel.textContent = isOnline ? state.session.login : "не активна";

  if (!isOnline) {
    elements.loginInput.focus();
  }
}

function renderEmptyPosts(message) {
  elements.postsList.innerHTML = `<div class="empty-state">${message}</div>`;
  elements.pageLabel.textContent = "0 / 0";
  elements.prevPageButton.disabled = true;
  elements.nextPageButton.disabled = true;
}

function renderPosts(pageData) {
  elements.postsList.innerHTML = "";
  state.totalPages = pageData.totalPages;

  const displayPage = pageData.totalPages === 0 ? 0 : pageData.page + 1;
  elements.pageLabel.textContent = `${displayPage} / ${pageData.totalPages}`;
  elements.prevPageButton.disabled = pageData.page <= 0;
  elements.nextPageButton.disabled = pageData.page >= pageData.totalPages - 1;

  if (pageData.content.length === 0) {
    renderEmptyPosts("Пока нет постов. Создайте первый текст выше");
    return;
  }

  for (const post of pageData.content) {
    const node = elements.postTemplate.content.firstElementChild.cloneNode(true);
    const postText = node.querySelector(".post-text");
    const expandButton = node.querySelector(".expand-button");
    const editForm = node.querySelector(".edit-form");
    const editTextarea = node.querySelector(".edit-textarea");

    node.querySelector(".post-date").textContent = formatDate(post.createdAt);
    postText.textContent = post.text;
    node.querySelector(".copy-button").addEventListener("click", () => copyPost(post.text));
    node.querySelector(".edit-button").addEventListener("click", () => startEditingPost(node, post.text));
    node.querySelector(".cancel-edit-button").addEventListener("click", () => stopEditingPost(node));
    editForm.addEventListener("submit", (event) => updatePost(event, post.id, editTextarea.value));
    node.querySelector(".delete-button").addEventListener("click", () => deletePost(post.id));

    if (isLongPost(post.text)) {
      postText.classList.add("is-collapsed");
      expandButton.classList.remove("is-hidden");
      expandButton.addEventListener("click", () => togglePostExpansion(postText, expandButton));
    }

    if (post.isEdited) {
      node.querySelector(".post-date").textContent += " (ред.)";
    }

    elements.postsList.appendChild(node);
  }
}

function startEditingPost(postCard, text) {
  postCard.classList.add("is-editing");
  const textarea = postCard.querySelector(".edit-textarea");
  textarea.value = text;
  textarea.focus();
}

function stopEditingPost(postCard) {
  postCard.classList.remove("is-editing");
}

async function copyPost(text) {
  try {
    if (navigator.clipboard?.writeText) {
      await navigator.clipboard.writeText(text);
    } else {
      copyPostFallback(text);
    }

    showToast("Пост скопирован");
  } catch {
    showToast("Не получилось скопировать пост", true);
  }
}

function copyPostFallback(text) {
  const textarea = document.createElement("textarea");
  textarea.value = text;
  textarea.setAttribute("readonly", "");
  textarea.style.position = "fixed";
  textarea.style.opacity = "0";
  document.body.appendChild(textarea);
  textarea.select();
  document.execCommand("copy");
  textarea.remove();
}

function isLongPost(text) {
  return text.length > POST_PREVIEW_LENGTH || text.split("\n").length > 8;
}

function togglePostExpansion(postText, expandButton) {
  const isCollapsed = postText.classList.toggle("is-collapsed");
  expandButton.textContent = isCollapsed ? "Показать полностью" : "Свернуть";
}

function formatDate(value) {
  return new Intl.DateTimeFormat("ru-RU", {
    dateStyle: "short",
    timeStyle: "short"
  }).format(new Date(value));
}

function updateCounter() {
  elements.textCounter.textContent = `${elements.postText.value.length} / ${MAX_TEXT_LENGTH}`;
}

function setLoading(isLoading) {
  state.isLoading = isLoading;
  elements.refreshButton.disabled = isLoading;
  elements.prevPageButton.disabled = isLoading || state.page <= 0;
  elements.nextPageButton.disabled = isLoading || state.page >= state.totalPages - 1;
}

function showToast(message, isError = false) {
  elements.toast.textContent = message;
  elements.toast.classList.toggle("is-error", isError);
  elements.toast.classList.add("is-visible");

  window.clearTimeout(showToast.timeoutId);
  showToast.timeoutId = window.setTimeout(() => {
    elements.toast.classList.remove("is-visible");
  }, 3200);
}

async function login(event) {
  event.preventDefault();

  const loginValue = elements.loginInput.value.trim();
  const password = elements.passwordInput.value;

  if (!loginValue || !password) {
    showToast("Введите логин и пароль", true);
    return;
  }

  try {
    const user = await apiFetch("/api/auth/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ login: loginValue, password }),
    });
    saveSession({ login: user.login, token: user.token, expiresAt: user.expiresAt });
    elements.passwordInput.value = "";
    state.page = 0;
    await loadPosts();
    showToast(`Вы вошли как ${user.login}`);
  } catch (error) {
    clearSession();
    showToast(error.message, true);
  }
}

async function createPost(event) {
  event.preventDefault();

  const text = elements.postText.value.trim();
  if (!text) {
    showToast("Пост не может быть пустым", true);
    return;
  }

  try {
    await apiFetch("/api/posts", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ text }),
    });
    elements.postText.value = "";
    updateCounter();
    state.page = 0;
    await loadPosts();
    showToast("Пост опубликован");
  } catch (error) {
    showToast(error.message, true);
  }
}

async function updatePost(event, id, textValue) {
  event.preventDefault();

  const text = textValue.trim();
  if (!text) {
    showToast("Пост не может быть пустым", true);
    return;
  }

  try {
    await apiFetch(`/api/posts/${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ text }),
    });
    await loadPosts();
    showToast("Пост обновлён");
  } catch (error) {
    showToast(error.message, true);
  }
}

async function loadPosts() {
  if (!state.session) {
    renderEmptyPosts("Войдите, чтобы увидеть свои посты");
    return;
  }

  setLoading(true);

  try {
    const pageData = await apiFetch(`/api/posts?page=${state.page}&limit=${state.limit}`);
    renderPosts(pageData);
  } catch (error) {
    showToast(error.message, true);
  } finally {
    setLoading(false);
  }
}

async function deletePost(id) {
  if (!window.confirm("Удалить этот пост?")) {
    return;
  }

  try {
    await apiFetch(`/api/posts/${id}`, { method: "DELETE" });
    if (elements.postsList.children.length === 1 && state.page > 0) {
      state.page -= 1;
    }
    await loadPosts();
    showToast("Пост удалён");
  } catch (error) {
    showToast(error.message, true);
  }
}

elements.loginForm.addEventListener("submit", login);
elements.postForm.addEventListener("submit", createPost);
elements.postText.addEventListener("input", updateCounter);
elements.refreshButton.addEventListener("click", loadPosts);
elements.logoutButton.addEventListener("click", () => {
  clearSession();
  showToast("Вы вышли из аккаунта");
});
elements.limitSelect.addEventListener("change", () => {
  state.limit = Number(elements.limitSelect.value);
  state.page = 0;
  loadPosts();
});
elements.prevPageButton.addEventListener("click", () => {
  if (state.page > 0) {
    state.page -= 1;
    loadPosts();
  }
});
elements.nextPageButton.addEventListener("click", () => {
  if (state.page < state.totalPages - 1) {
    state.page += 1;
    loadPosts();
  }
});

restoreSession();
updateCounter();
loadPosts();
