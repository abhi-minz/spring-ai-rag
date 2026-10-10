import { useState } from "react";
import InputBar from "./../inputbar/InputBar";
import Message from "./../message/Message";
import {chatDoc, askSimple} from "./../../services/api"

import './ChatArea.css'

function ChatArea({messages, setMessages, sessionId, topK, useRag}) {
    const [loading, setLoading] = useState(false);

    async function handleSend(text) {
        if (!text.trim() || loading)
            return

        const userMessage = { role: 'user', text };
        setMessages((prev) => [...prev, userMessage]);
        setLoading(true);

        try {
            const data = useRag ? await chatDoc({ sessionId, question: text, topK }) : await askSimple(text);

            setMessages((prev) => [...prev, { role: 'assistant', text: data.answer ?? data.content ?? '(no answer)' },]);
        }
        catch (err) {
            setMessages((prev) => [...prev, { role: 'assistant', text: `Error: ${err.message}`, error: true, }]);
        }
        finally {
            setLoading(false);
        }
    }

    return (
            <div className="chat-area">
            <div className="message-chat-area">
                {messages.map((msg, i) => (
                <Message key={i} role={msg.role} text={msg.text} error={msg.error} />
                ))}
                {loading && (
                    <div className="message assistant">
                        <div className="bubble thinking">Thinking...</div>
                    </div>
                )}
                </div>
                <div className="input-chat-bar">
                <InputBar onSend={handleSend} disabled={loading}/>
                </div>
            </div>
    );
}

export default ChatArea;