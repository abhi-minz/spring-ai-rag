import './Sidebar.css'

function Sidebar() {
    return (
        <div className="sidebar-main">
            <div className="sidebar-content">
                <div className="sidebar-upload">
                    <h3>UPLOAD</h3>
                    <input type="file" name="sidebar-docs" id="sidebar-docs"/>
                </div>
                <div className="sidebar-documents">
                    <h3>DOCUMENTS</h3>
                    <div className="documents">
                    </div>
                </div>
                <div className="sidebar-settings">
                    <h3>Top K:3</h3>
                    <input type="range" name="sidebar-range" id="sidebar-range" min={1} max={100} defaultValue={3}/>
                    <div className="sidebar-rag">
                        <input type="checkbox" name="sidebar-rag" id="sidebar-rag"/>
                        <label htmlFor="sidebar-rag">Use RAG</label>
                    </div>
                </div>
            </div>
            <div className="sidebar-clear-chat">
                <div className="sidebar-clear">
                    <button>Clear Chat</button>
                </div>
            </div>
        </div>
    );
}

export default Sidebar;