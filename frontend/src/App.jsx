import './App.css'
import Header from './components/header/Header'
import ChatArea from './components/chatarea/ChatArea';
import Sidebar from './components/sidebar/Sidebar';
import { useState } from 'react';

function App() {
  const [messages, setMessages] = useState([
    {role: 'assistant', text: 'Hi! I can answer questions about your documents'}
  ])
  const [sessionId] = useState('user-' + Math.random().toString(36).slice(2, 8));
  const [topK, setTopK] = useState(3);
  const [useRag, setUseRag] = useState(true);

  return (
    <div className="app">
      <div className="app-header">
        <Header />
      </div>
      <div className="app-chat-area">
        <div className="app-doc-area">
          <Sidebar topK={topK} setTopK={setTopK} useRag={useRag} setUseRag={setUseRag} />
        </div>
        <div className="app-msg-area">
          <ChatArea messages={messages} setMessages={setMessages} sessionId={sessionId} topK={topK} useRag={useRag} />
        </div>
      </div>
    </div>
  );
}

export default App
