import { useState } from 'react';
import './Header.css'

function Header({sessionId}) {
    return (
        <div className="header">
            <div className="header-model">
                <h1>Spring AI RAG</h1>
                <span>Ask questions about your documents</span>
            </div>
            <div className="header-session">
                <span>Session: {sessionId}</span>
            </div>
        </div>
    );
}

export default Header