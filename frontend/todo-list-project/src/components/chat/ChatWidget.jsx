import { useEffect, useRef, useState } from "react";
import { sendToAgent } from "../../api/api.js";

import "./chatWidget.css";
const ChatWidget = ({ setTodos, fetchTodos, api, isOpen, toggleChat }) => {
  const [messages, setMessages] = useState([{ role: "assistant", text: "Olá 👋" }]);
  const [input, setInput] = useState("");
  const bottomRef = useRef(null);
  const [isLoading, setIsLoading] = useState(false);

  const handleSend = async () => {
    if (!input.trim()) return;

    const userMessage = { role: "user", text: input };
    setMessages(prev => [...prev, userMessage]);

    setInput("");
    setIsLoading(true);

    try {
      const aiResponse = await sendToAgent(api, input);

      setMessages(prev => [
        ...prev,
        { role: "assistant", text: aiResponse }
      ]);

      const updatedTodos = await fetchTodos(api);
      setTodos(updatedTodos);

    } catch (error) {
      console.error(error);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  return (
    <div>
      <button
        className={`chat-toggle-button ${isOpen ? "open" : ""}`}
        onClick={toggleChat}
        aria-label={isOpen ? "Fechar chat" : "Abrir chat"}
      >
        <svg
          version="1.1"
          id="Layer_1"
          xmlns="http://www.w3.org/2000/svg"
          x="0px"
          y="0px"
          viewBox="0 0 43.3 61"
          className="chat-icon-svg"
          width="20px"
          height="20px"
        >
          <polygon
            fillRule="evenodd" // CORREÇÃO: fill-rule -> fillRule
            clipRule="evenodd" // CORREÇÃO: clip-rule -> clipRule
            fill="currentColor" // MELHORIA: usa a cor do texto do botão (mais fácil de estilizar no CSS)
            points="2.1,30.6 23.3,58.7 41.6,58.7 20.6,30.6 41.6,2.7 42,2.1 23.7,2.1 23.3,2.7 "
          />
        </svg>
      </button>
      <div className={`chat-widget ${isOpen ? "open" : "closed"}`}>
        <div className="chat-header">Assistente</div>

        <div className="chat-messages">
          {messages.map((message, index) => (
            <div key={index} className={`chat-bubble ${message.role}`}>
              {message.text}
            </div>
          ))}
          {isLoading && (
            <div className="chat-bubble assistant typing">digitando...</div>
          )}
          <div ref={bottomRef} />
        </div>

        <div className="chat-input-area">
          <input
            type="text"
            placeholder="Digite uma mensagem..."
            className="chat-input"
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === "Enter") handleSend();
            }}
            disabled={isLoading}
          />

          <button
            onClick={handleSend}
            disabled={isLoading}
            className="chat-send"
          ></button>
        </div>
      </div>
    </div>
  );
};
export default ChatWidget;
