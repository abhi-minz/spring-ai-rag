import SourceList from './../sourcelist/SourceList'

import './Message.css'

function Message({role, text, error, sources}) {
    return (
        <div className={`message-area ${role}`}>
            <div className="bubble-wrap">
                <div className={`bubble ${error ? 'error' : ''}`}>
                    {text}
                </div>
                {sources && sources.length > 0 &&
                    (<SourceList sources={sources} />)}
            </div>
        </div>
    );
}

export default Message;