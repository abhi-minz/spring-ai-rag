import { useState } from 'react';
import './Header.css'

function Header() {
    const [session] = useState(0)
    return (
        <div className="header">
            <div className="header-model">
                <h1>Spring AI RAG</h1>
                <span>Ask questions about your documents</span>
            </div>
            <div className="header-session">
                <span>Session: {session}</span>
            </div>
        </div>
    );
}

export default Header