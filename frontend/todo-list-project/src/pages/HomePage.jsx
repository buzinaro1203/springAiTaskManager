import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  alterTodo,
  completeTodo,
  createApi,
  createTodo,
  deleteTodo,
  fetchTodos,
} from "../api/api.js";
import "../App.css";
import ChatWidget from "../components/chat/ChatWidget.jsx";
import Modal from "../components/Modal/Modal.jsx";
import Filter from "../components/searchAndFilter/Filter.jsx";
import Search from "../components/searchAndFilter/Search.jsx";
import CreateTodo from "../components/todo/CreateTodo.jsx";
import Todo from "../components/todo/Todo.jsx";
import { hideLoading, showLoading } from "../utils/Loading.js";
function App() {
  const navigate = useNavigate();
  const email = localStorage.getItem("email");
  const password = localStorage.getItem("password");
  const api = email && password ? createApi(email, password) : null;
  const [search, setSearch] = useState("");
  const [sort, setSort] = useState("Asc");
  const [filter, setFilter] = useState("Incompleted");
  const [filterCategory, setFilterCategory] = useState("All");
  const [isChatOpen, setIsChatOpen] = useState(false);
  const [todos, setTodos] = useState([]);

  // dentro do componente App
  const handleLogout = () => {
    // Remove email e password do localStorage
    localStorage.removeItem("email");
    localStorage.removeItem("password");

    // Redireciona para login
    navigate("/login");
  };
  const toggleChat = () => {
    setIsChatOpen(!isChatOpen);
  };
  useEffect(() => {
    if (api == null) {
      navigate("/login");
    }
    const loadTodos = async () => {
      try {
        showLoading();
        fetchTodos(api).then(setTodos);
      } catch (error) {
        console.error("Error fetching todos:", error);
      } finally {
        hideLoading();
      }
    };

    loadTodos();
  }, []); // adiciona api como dependência

  const [isModalOpen, setIsModalOpen] = useState(false);

  const openModal = () => {
    setIsModalOpen(true);
  };

  const closeModal = () => {
    setIsModalOpen(false);
  };

  const updateTodo = async (todo) => {
    try {
      showLoading();
      const updatedTodo = await alterTodo(api, todo.id, todo);
      setTodos(todos.map((t) => (t.id === todo.id ? updatedTodo : t)));
    } catch (error) {
      console.error("Error updating todo:", error);
    } finally {
      hideLoading();
    }
  };

  const addTodo = async (todoData) => {
    try {
      showLoading();
      const savedTodo = await createTodo(api, todoData); // chama a API
      setTodos([...todos, savedTodo]); // adiciona o resultado do backend no estado
    } catch (error) {
      console.error("Error creating todo:", error);
    } finally {
      hideLoading();
    }
  };
  const handleComplete = async (todo) => {
    try {
      showLoading();
      const updatedTodo = await completeTodo(api, todo.id, todo); // chama API
      setTodos((prev) => prev.map((t) => (t.id === todo.id ? updatedTodo : t)));
    } catch (error) {
      console.error("Erro ao completar todo: ", error);
    } finally {
      hideLoading();
    }
  };
  const removeTodo = async (id) => {
    try {
      showLoading();
      await deleteTodo(api, id);
      setTodos(todos.filter((todo) => todo.id !== id));
    } catch (error) {
      console.error("Erro ao deletar todo:", error);
    } finally {
      hideLoading();
    }
  };
  return (
    <div>
      <div className={`app ${isChatOpen ? "with-chat" : ""}`}>
        <div className="header">
          <h1>Lista de tarefas</h1>
          <button onClick={handleLogout} className="logout-button"></button>
        </div>

        <Search search={search} setSearch={setSearch} />
        <Filter
          filter={filter}
          setFilter={setFilter}
          sort={sort}
          setSort={setSort}
          filterCategory={filterCategory}
          setFilterCategory={setFilterCategory}
        />
        <div className="todo-list">
          {todos
            .filter((todo) => {
              if (!todo) return false;
              return filter === "All"
                ? true
                : filter === "Completed"
                  ? todo.completed
                  : todo.completed === false;
            })
            .filter((todo) =>
              filterCategory === "All"
                ? true
                : todo.categoryName === filterCategory,
            )
            .filter((todo) =>
              todo.title.toLowerCase().includes(search.toLowerCase()),
            )
            .sort((a, b) =>
              sort === "Asc"
                ? a.title.localeCompare(b.title)
                : sort === "Desc"
                  ? b.title.localeCompare(a.title)
                  : sort === "CreatedDate"
                    ? new Date(a.createdAt) - new Date(b.createdAt)
                    : new Date(a.dueDate) - new Date(b.dueDate),
            )
            .map((todo) => (
              <Todo
                key={todo.id}
                todo={todo}
                removeTodo={removeTodo}
                completeTodo={handleComplete}
                updateTodo={updateTodo}
              />
            ))}

          <div className="create-todo-container">
            <button className="modal-button" onClick={openModal}></button>
            {isModalOpen && (
              <Modal onClose={closeModal}>
                <CreateTodo addTodo={addTodo} onClose={closeModal} />
              </Modal>
            )}
          </div>
        </div>
      </div>

      <ChatWidget api={api} isOpen={isChatOpen} toggleChat={toggleChat} />
    </div>
  );
}

export default App;
