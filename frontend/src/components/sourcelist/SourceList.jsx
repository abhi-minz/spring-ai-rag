import SourceCard from "./../sourcecard/SourceCard"

import "./SourceList.css"

function SourceList({ sources }) {
    if (!sources || sources.length === 0) return null;

    return (
        <div className="source-list">
            <div className="source-list-title">
                Sources
            </div>
            {sources.map((s, i) => (<SourceCard key={i} card={s}/>))}
        </div>
    );
}

export default SourceList;
