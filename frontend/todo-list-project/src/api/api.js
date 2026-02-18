import axios from "axios";

const BASE_URL = import.meta.env.VITE_API_URL || "http://localhost:8080";

export const createApi = (email, password) => {
  // BUG: A função btoa não suporta caracteres especiais em algumas situações ou acentuações.
  const token = btoa(`${email}:${password}`);
  return axios.create({
    baseURL: BASE_URL,
    headers: {
      "Content-Type": "application/json",
      Authorization: `Basic ${token}`,
    },
  });
};

export const registerUser = async (name, email, password) => {
  const response = await axios.post(`${BASE_URL}/users/register`, {
    name,
    email,
    password,
  });
  return response.data;
};

// ----------------------
// Funções que recebem a instância do axios
// ----------------------

export const fetchTodos = async (api) => {
  const response = await api.get("/todos");
  console.log("Fetched todos:", response.data);
  return response.data;
};

export const createTodo = async (api, todo) => {
  console.log("Creating Todo", todo);
  const response = await api.post("/todos", todo);
  return response.data;
};

export const alterTodo = async (api, id, todo) => {
  const response = await api.put(`/todos/${id}`, todo);
  return response.data;
};

export const deleteTodo = async (api, id) => {
  await api.delete(`/todos/${id}`);
};

export const completeTodo = async (api, id, todo) => {
  const updatedTodo = { ...todo, completed: !todo.completed };
  const response = await api.put(`/todos/${id}`, updatedTodo);
  return response.data;
};

export const sendToAgent = async (api, message) => {
  const response = await api.get(
    `/ai/chat?message=${encodeURIComponent(message)}`,
  );

  return response.data; // string pura
};
