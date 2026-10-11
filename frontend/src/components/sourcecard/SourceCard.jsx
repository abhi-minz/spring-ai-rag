import './SourceCard.css'

function SourceCard({ card }) {
    const dist = typeof card.distance === 'number' ? card.distance.toFixed(3) : '?';

    return (
        <div className="source-card">
            <div className="source-card-header">
                <span className="source-label">
                    {card.citationLabel}
                </span>
                <span className="source-distance">
                    dist {dist}
                </span>
            </div>
            <div className="source-preview">
                {card.preview}
            </div>
        </div>
    );
}

export default SourceCard;