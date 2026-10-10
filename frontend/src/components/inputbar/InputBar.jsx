import { useState } from 'react';
import './InputBar.css'

function InputBar({onSend, disabled}) {
    const [input, setInput] = useState('');

    function submit() {
        if (!input.trim() || disabled) return;
        onSend(input);
        setInput('');
    }

    function handleKeyDown(e) {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            submit();
        }
    }

    return (
        <div className="chat-input">
            <input type="text" name="chat-text" id="chat-text" value={input} placeholder={disabled ? 'Waiting for response...' : 'Type your question...'} onChange={(e)=>setInput(e.target.value)} onKeyDown={handleKeyDown} disabled={disabled}/>
            <button onClick={submit} disabled={disabled}>{disabled ? '...' : 'Send'}</button>
        </div>
    );
}

export default InputBar;