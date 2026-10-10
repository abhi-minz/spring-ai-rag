import './Message.css'

function Message({role, text, error}) {
    return (
        <div className={`message-area ${role}`}>
            <div className={`bubble ${error ? 'error' : ''}`}>
                {text}
            </div>
        </div>
    );
}

export default Message;